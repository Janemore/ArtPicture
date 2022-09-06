/* Author: Zizhen Xian (zxian)
 * */
package com.example.artpicture;
import static com.mongodb.client.model.Filters.eq;

import com.mongodb.*;
import com.mongodb.client.*;
import com.mongodb.client.result.InsertOneResult;
import org.bson.Document;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ArtDashBoardModel {
    static MongoClient mongoClient = null;
    static MongoDatabase database = null;

    public void buildConnection() {
        String cString = "mongodb://zzxian:255231@cluster0-shard-00-00.un0cx.mongodb.net:27017,cluster0-shard-00-01.un0cx.mongodb.net:27017,cluster0-shard-00-02.un0cx.mongodb.net:27017/logDB?w=majority&retryWrites=true&tls=true&authMechanism=SCRAM-SHA-1";
//        "mongodb+srv://zzxian:255231@cluster0.un0cx.mongodb.net/logDB?retryWrites=true&w=majority");
        ConnectionString connectionString = new ConnectionString(cString);
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .serverApi(ServerApi.builder().version(ServerApiVersion.V1).build())
                .build();
        mongoClient = MongoClients.create(settings);
        database = mongoClient.getDatabase("logDB");

    }

    public MongoCollection<Document> getLogCollection(){
        return database.getCollection("logs");
    }

    public void insertLog(Document log){
        MongoCollection<Document> collection = database.getCollection("logs");
        InsertOneResult result = collection.insertOne(log);
    }

    /*top 3 picture search terms,
    *average search latency,
    *total query num
    */
    public long getTotalNumOfSearch(MongoCollection logs){
        long totalSearch = logs.countDocuments();
        return totalSearch;
    }
   //https://stackoverflow.com/questions/62077736/how-to-get-the-3-highest-values-in-a-hashmap
    public List<String>  getTop3SearchTerms(MongoCollection logs){
        HashMap<String, Integer> searchMap = new HashMap<>();
        FindIterable<Document> documentCursor = logs.find();
        for (Document dc : documentCursor){
            String st = (String) dc.get("searchTerm");
            if (searchMap.containsKey(st)){
                searchMap.put(st, searchMap.get(st) + 1);
            }
            else{
                searchMap.put(st,1);
            }
        }

        List<String> keys = searchMap.entrySet().stream().sorted(Map.Entry.<String, Integer>comparingByValue().reversed()).limit(3).map(Map.Entry::getKey).collect(Collectors.toList());

        return keys;
    }
    
    public double getAvgLatency(MongoCollection logs) {
        FindIterable<Document> documentCursor = logs.find();
        long sum = 0;
        int count = 0;
        for (Document dc : documentCursor){
            long latency = (long) dc.get("latency");
            sum += latency;
            count++;
        }
        double s = (double) sum;
        double avgLatency =  s/count;
        return avgLatency;
    }
}

