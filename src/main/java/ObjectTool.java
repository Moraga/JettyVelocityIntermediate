import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

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

    public void remodel(HashMap data, HashMap rules) {
        for (Object key: rules.keySet()) {
            if (data.containsKey(key)) {
                HashMap rule = (HashMap) rules.get(key);

                if ("int".equals(rule.get("TYPE"))) {
                    data.put(key, Integer.parseInt((String) data.get(key)));
                }

                if (rule.containsKey("MOVETO")) {
                    String moveto = (String) rule.get("MOVETO");
                    HashMap base = data;
                    Scanner scan = new Scanner(moveto);
                    scan.useDelimiter("\\.");
                    String node;
                    while (true) {
                        node = scan.next();
                        if (!scan.hasNext()) {
                            break;
                        }
                        if (!base.containsKey(node) || !(base.get(node) instanceof HashMap)) {
                            HashMap temp = new HashMap();
                            base.put(node, temp);
                            base = temp;
                        } else {
                            base = (HashMap) base.get(node);
                        }
                    }
                    base.put(node, data.get(key));
                }

                if (rule.containsKey("REMOVE")) {
                    data.remove(key);
                    continue;
                }

                // N+n
                for (Object prop: rule.keySet()) {
                    if (prop.toString().matches("N\\+\\d+")) {
                        int n = Integer.parseInt(prop.toString().substring(2));
                        ArrayList list = (ArrayList) data.get(key);
                        for (int i = 0; i < list.size(); ++i) {
                            if (i % n == 0) {
                                remodel((HashMap) list.get(i), (HashMap) rule.get(prop));
                            }
                        }
                    }
                }

                if (rule.containsKey("RENAME")) {
                    data.put((String) rule.get("RENAME"), data.get(key));
                    data.remove(key);
                }
            }
            else if ("ADD".equals(key)) {
                data.putAll((HashMap) rules.get(key));
            }
            else if ("DEFAULT".equals(key)) {

            }
        }
    }

    public void remodel(ArrayList data, HashMap rules) {

    }
}