import java.util.ArrayList;
import java.util.HashMap;

public class ObjectTool {
    public void extend(HashMap dest, HashMap data) {
        for (Object key: data.keySet()) {
            Object value = data.get(key);
            if (value instanceof HashMap) {
                HashMap temp;
                if (dest.get(key) instanceof HashMap) {
                    temp = (HashMap) dest.get(key);
                }
                else {
                    temp = new HashMap();
                    dest.put(key, temp);
                }
                extend(temp, (HashMap) value);
            }
            else if (value instanceof ArrayList) {
                if (!(dest.get(key) instanceof ArrayList)) {
                    dest.put(key, new ArrayList());
                }
                extend((ArrayList) dest.get(key),(ArrayList) value);
            }
            else {
                dest.put(key, value);
            }
        }
    }

    public void extend(ArrayList dest, ArrayList data) {
        for (Object item: data) {
            if (item instanceof HashMap) {
                HashMap temp = new HashMap();
                extend(temp, (HashMap) item);
                dest.add(temp);
            }
            else if (item instanceof ArrayList) {
                ArrayList temp = new ArrayList();
                extend(temp, (ArrayList) item);
                dest.add(temp);
            }
            else {
                dest.add(item);
            }
        }
    }

    public HashMap clone(HashMap data) {
        HashMap dest = new HashMap();
        for (Object key: data.keySet()) {
            if (data.get(key) instanceof HashMap) {
                dest.put(key, clone((HashMap) data.get(key)));
            }
            else if (data.get(key) instanceof ArrayList) {
                dest.put(key, clone((ArrayList) data.get(key)));
            }
            else {
                dest.put(key, data.get(key));
            }
        }
        return dest;
    }

    public ArrayList clone(ArrayList data) {
        ArrayList dest = new ArrayList();
        for (Object item: data) {
            if (item instanceof HashMap) {
                dest.add(clone((HashMap) item));
            }
            else if (item instanceof ArrayList) {
                dest.add(clone((ArrayList) item));
            }
            else {
                dest.add(item);
            }
        }
        return dest;
    }

    public String implode(String glue, ArrayList list) {
        String ret = "";
        String str = "";
        for (Object item: list) {
            ret += str + item;
            str = glue;
        }
        return ret;
    }
}