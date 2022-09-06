/* Author: Zizhen Xian (zxian)
* */
package com.example.artpicture;

import java.io.IOException;
import java.io.PrintWriter;

import com.mongodb.client.MongoCollection;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import org.bson.Document;
import org.json.JSONException;
import org.json.JSONObject;

@WebServlet(name = "ArtPictureServlet",
        urlPatterns = {"/getAnArtPicture","/getDashboard"})
public class ArtPictureServlet extends HttpServlet {
    ArtPictureModel ipm = null;
    ArtDashBoardModel adm = null;
    // Initiate this servlet by instantiating the model that it will use.
    @Override
    public void init() {
        ipm = new ArtPictureModel();
        adm = new ArtDashBoardModel();
        adm.buildConnection();
    }

    // This servlet will reply to HTTP GET requests via this doGet method
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // logdata a : search

        String path = request.getServletPath();


        if(!path.contains("getDashboard")){

        String search = request.getParameter("q");
        System.out.println(search);
        if (search != null) search = search.toLowerCase();
        if (search != null) {
            /* collect log data!*/
            long st = System.currentTimeMillis();
            JSONObject picInfo = ipm.doPicSearch(search);
            long et = System.currentTimeMillis();
            // c. latency
            long lag = et - st;
            // d. device model
            String model = request.getHeader("User-Agent");
            // e. request address
            String ipAddress = request.getRemoteAddr();
            // f. picInfo
            String moreInfo;
            try{
                moreInfo = (String) picInfo.get("moreInfo");
            } catch (JSONException e) {
                e.printStackTrace();
                moreInfo = "result not found";
            }

            // b. main pic url
            String mainPic;
            try{
                mainPic = (String) picInfo.get("objectImage0");
            } catch (JSONException e) {
                e.printStackTrace();
                mainPic = "result not found";
            }

            Document aLog = new Document()
                    .append("ip", ipAddress)
                    .append("model", model)
                    .append("latency", lag) // lag is a long
                    .append("searchTerm", search)
                    .append("mainPic", mainPic)
                    .append("moreInfo", moreInfo);

            adm.insertLog(aLog);

            PrintWriter out = response.getWriter();
            out.println(picInfo.toString());
//            out.flush();
            }
        }

        else{
            MongoCollection<Document>  logCollection = adm.getLogCollection();
            request.setAttribute("avgLatency", adm.getAvgLatency(logCollection));
            request.setAttribute("top3SearchTerm", adm.getTop3SearchTerms(logCollection));
            request.setAttribute("totalSearch", adm.getTotalNumOfSearch(logCollection));
            request.setAttribute("allLogs", logCollection);
            String nextView ="getDashboard.jsp";
            RequestDispatcher view = request.getRequestDispatcher(nextView);
            view.forward(request, response);
        }
    }
}

