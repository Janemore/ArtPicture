<%@ page import="java.util.List" %>
<%@ page import="org.bson.Document" %>
<%@ page import="com.mongodb.client.MongoCollection" %>
<%@ page import="com.mongodb.client.FindIterable" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>Dashboard of ArtForEveryone</title>
</head>
<body>
    <form action="getDashboard" method="GET">
        <h2 style ="font-family:'Courier New'">Summarized Information about application usage...</h2>
        <% List<String> top3 = (List<String>) request.getAttribute("top3SearchTerm"); %>
        <% MongoCollection<Document> logCollection = (MongoCollection<Document>) request.getAttribute("allLogs"); %>
        <%FindIterable<Document> documentCursor = logCollection.find();%>
        <p style ="font-family:'Courier New'">Average search latency: <%=request.getAttribute("avgLatency")%> ms</p>
        <p style ="font-family:'Courier New'">Total Search Number: <%=request.getAttribute("totalSearch")%></p>
        <p style ="font-family:'Courier New'">The most searched term: <%=top3.get(0)%></p>
        <p style ="font-family:'Courier New'" >The second most searched term: <%=top3.get(1)%></p>
        <p style ="font-family:'Courier New'">The third most searched term: <%=top3.get(2)%></p>

        <h2 style ="font-family:'Courier New'" >Detailed logs...</h2>
        <table style="width:100%">
            <tr>
                <th>Client's IP address</th>
                <th>device model</th>
                <th>query latency</th>
                <th>Search Term</th>
                <th>mainPic url</th>
                <th>more info url</th>
            </tr>
        <% for (Document log : documentCursor){%>
        <tr>
            <td><%=log.get("ip")%></td>
            <td><%=log.get("model")%></td>
            <td><%=log.get("latency")%> ms</td>
            <td><%=log.get("searchTerm")%></td>
            <td><%=log.get("mainPic")%></td>
            <td><%=log.get("moreInfo")%></td>
        </tr>
        <% }%>
        </table>
    </form>
</body>
</html>