import org.apache.velocity.Template;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.context.Context;
import org.apache.velocity.tools.view.VelocityViewServlet;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.AreaAveragingScaleFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

public class Main {
    public static void main(String[] args) throws Exception {
        // jetty
        Server server = new Server(8001);
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        server.setHandler(context);
        // velocity
        context.addServlet(new ServletHolder(new Servlet()), "/*");
        server.start();
        server.join();
    }

    public static class Servlet extends VelocityViewServlet {
        public void init(ServletConfig config) throws ServletException {
            super.init(config);
            Properties p = new Properties();
            p.setProperty("file.resource.loader.path", "C:\\templatecache-local\\templates\\test");
            p.setProperty("eventhandler.include.class", "org.apache.velocity.app.event.implement.IncludeRelativePath");
            Velocity.init(p);
        }

        protected void fillContext(Context context, HttpServletRequest request) {
            context.put("object", new ObjectTool());
        }

        protected Template getTemplate(HttpServletRequest request, HttpServletResponse response) {
            String path = request.getPathInfo().substring(1);
            return Velocity.getTemplate(path);
        }
    }

    public static class ObjectTool {
        public void extend(HashMap dest, HashMap data) {
            for (Object key : data.keySet()) {
                Object value = data.get(key);
                if (value instanceof HashMap) {
                    HashMap temp;
                    if (dest.containsKey(key)) {
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
                    ArrayList temp = (ArrayList) value;
                    for (int i = 0; i < temp.size(); ++i) {
                        if (temp.get(i) instanceof HashMap) {
                            ((ArrayList) dest.get(key)).add(new HashMap((HashMap) temp.get(i)));
                        }
                        else {
                            ((ArrayList) dest.get(key)).add(temp.get(i));
                        }
                    }
                }
                else {
                    dest.put(key, value);
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

        public ArrayList clone(ArrayList list) {
            ArrayList ret = new ArrayList();
            for (int i = 0; i < list.size(); ++i) {
                if (list.get(i) instanceof HashMap) {
                    ret.add(clone((HashMap) list.get(i)));
                }
                else if (list.get(i) instanceof ArrayList) {
                    ret.add((ArrayList) list.get(i));
                }
                else {
                    ret.add(list.get(i));
                }
            }
            return ret;
        }
    }
}
