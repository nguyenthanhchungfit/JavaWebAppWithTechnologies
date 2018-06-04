/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data_server;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import static com.mongodb.client.model.Filters.eq;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import models.Singer;
import models.Song;
import models.SongResult;
import org.bson.Document;

/**
 *
 * @author Nguyen Thanh Chung
 */
public class SongDB {
    private static MongoClient mongo = null;
    private static MongoCredential credential = null;
    private static MongoDatabase mongo_db = null;
    private static MongoCollection<Document> collectionSongs = null;
    
    static{
        mongo = new MongoClient(DBContracts.HOST, DBContracts.PORT);
        credential = MongoCredential.createCredential(DBContracts.USERNAME
                , DBContracts.DATABASE_NAME, DBContracts.PASSWORD.toCharArray());
        mongo_db = mongo.getDatabase(DBContracts.DATABASE_NAME);
        collectionSongs = mongo_db.getCollection(DBContracts.COLLECTION_SONGS);
    }
    
    public static SongResult getSongByName(String name){
        SongResult sr = new SongResult();
        
        System.out.println(name);
        FindIterable<Document> docs = collectionSongs.find(eq("name", name));
        //Iterator it = docs.iterator();
        
//        while(it.hasNext()){
//            Document doc = (Document) it.next();
//            System.out.println(doc.getString("id"));
//        }
      
        Document doc = docs.first();
        if(doc != null){
            sr.result = 0;
            sr.song = new Song();
            sr.song.id = doc.getString("id");
            sr.song.name = doc.getString("name");
            sr.song.album = doc.getString("album");
            sr.song.lyrics = (List<String>) doc.get("lyrics");
            sr.song.composers  = (List<String>) doc.get("composers");
            sr.song.kinds = (List<String>) doc.get("kinds");
            List<Document> singers = (List<Document>) doc.get("singers");
            sr.song.singers = new ArrayList<>();
            for(Document docSinger : singers){
                String sId = docSinger.getString("id");
                String sName = docSinger.getString("name");
                Singer singer = new Singer();
                singer.id = sId;
                singer.name = sName;
                sr.song.singers.add(singer);
            }
        }else{
            sr.result = -1;
            sr.song = null;
        }
        return sr;
    } 
}