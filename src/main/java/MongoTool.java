import com.mongodb.*;

import java.util.HashMap;
import java.util.Map;

public class MongoTool {
    public DB db;

    public void configure(Map params) {
        db = new MongoClient("localhost").getDB("jtest");
    }

    public void insert(String collection, HashMap data) {
        DBCollection coll = db.getCollection(collection);
        coll.insert(new BasicDBObject(data));
    }

    public DBCursor find(String collection) {
        DBCollection coll = db.getCollection(collection);
        return coll.find();
    }
}
