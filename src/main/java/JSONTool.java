import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import org.apache.velocity.tools.config.DefaultKey;

import java.util.Map;

@DefaultKey("json")
public class JSONTool {
    public Gson gson;

    public void configure(Map params) {
        gson = new Gson();
    }

    public LinkedTreeMap parse(String str) {
        return gson.fromJson(str, LinkedTreeMap.class);
    }

    public String stringify(Object obj) {
        return gson.toJson(obj);
    }
}