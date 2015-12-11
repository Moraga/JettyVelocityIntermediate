import com.mongodb.*;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.apache.velocity.tools.config.DefaultKey;
import org.bson.Document;

import java.util.HashMap;
import java.util.Map;

@DefaultKey("mongo")
public class MongoTool {
    public String connect;
    public String db;
    public String defaultCollection;
    private MongoDatabase database;

    public void configure(Map params) {
        // get tool configuration
        connect = (String) params.get("connect");
        db = (String) params.get("db");
        defaultCollection = (String) params.get("defaultCollection");
        // connects to server and gets the database
        database = new MongoClient(new MongoClientURI(connect)).getDatabase(db);
    }

    public void insert(HashMap data) {
        insert(defaultCollection, data);
    }

    public void insert(String collection, HashMap data) {
        database.getCollection(collection).insertOne(new Document(data));
    }

    public MongoCursor<Document> find() {
        return find(defaultCollection, new HashMap());
    }

    public MongoCursor<Document> find(HashMap query) {
        return find(defaultCollection, query);
    }

    public MongoCursor<Document> find(String collection, HashMap query) {
        return database.getCollection(defaultCollection).find(new Document(query)).iterator();
    }
}
