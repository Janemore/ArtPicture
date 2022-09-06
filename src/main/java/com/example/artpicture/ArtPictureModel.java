/* Author: Zizhen Xian (zxian)
 * */
package com.example.artpicture;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

//https://metmuseum.github.io/
public class ArtPictureModel {

    static String objUrl = "https://collectionapi.metmuseum.org/public/collection/v1/objects/";

    public JSONObject doPicSearch(String searchTag) throws IOException, JSONException {
        Set<String> imageIDs = getRandomArt(searchTag);
        // this is the url of a single object
        JSONObject jsonInfo = new JSONObject();
        int index = 0;
        Iterator<String > e = imageIDs.iterator();
        while(e.hasNext()) {
            String searchUrl = objUrl + e.next();
            System.out.println(searchUrl);
            JSONObject artJson = getRemoteJSON(searchUrl);
            String name = "objectImage" + index;
            String imageURL = (String) artJson.get("primaryImage");
            jsonInfo.put(name, imageURL);
            if(index == 0){
                jsonInfo.put("artistbio", artJson.get("artistDisplayBio"));
                jsonInfo.put("medium", artJson.get("medium"));
                jsonInfo.put("moreInfo", artJson.get("objectURL"));
                jsonInfo.put("date", artJson.get("objectDate"));
                jsonInfo.put("title", artJson.get("title"));
            }
            index ++;
        }
        return jsonInfo;
    }
    /* This method takes the term to search, and returns id of a random object in that category.
     * */
    private Set<String> getRandomArt(String searchTag) {
        Set<String> imageIDs = new HashSet<>(); // this is the set of three relevant images
        String searchUrl = "https://collectionapi.metmuseum.org/public/collection/v1/search?artistOrCulture=true&hasImages=true&q=" + searchTag;
//        System.out.println(searchUrl);
        try {
            JSONObject paintings = getRemoteJSON(searchUrl);
            if(!paintings.get("objectIDs").equals("null")) {
                JSONArray idArray = (JSONArray) paintings.get("objectIDs");
//            System.out.println(paintings);
                Set<Integer> setvisited = new HashSet<>();
                while (imageIDs.size() < 3 & setvisited.size() < idArray.length()) {
                    Random rd = new Random();
                    int rdN = rd.nextInt(idArray.length());
                    String rdID = Integer.toString((Integer) idArray.get(rdN));
                    // pre-search whether the random object has image url in the api json.
                    String objectUrl = objUrl + rdID;
                    JSONObject art = getRemoteJSON(objectUrl);
                    if (!art.get("primaryImage").equals("")) {
                        System.out.println(rdID);
                        imageIDs.add(rdID);
                    }
                    else{
                        setvisited.add(Integer.valueOf(rdID));
                    }

                }
            }
            return imageIDs;
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return null;
        } catch (JSONException e) {
            return null;
        }
    }

    // https://stackoverflow.com/questions/4308554/simplest-way-to-read-json-from-a-url-in-java

    private JSONObject getRemoteJSON(String url) throws IOException {
//        InputStream is = new URL(url).openStream();
        URL URL = new URL(url.replace("\"", "%22").replace(" ",  "%20"));
        HttpURLConnection conn = (HttpURLConnection) URL.openConnection();

        try{
//            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
//            String jsonText = rd.readLine();
            BufferedReader rd = new BufferedReader(new InputStreamReader((conn.getInputStream())));

            String jsonText = rd.readLine();
            JSONObject json = new JSONObject(jsonText);
            return json;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
//        } finally {
//            conn.close();
//        }
    }
}
