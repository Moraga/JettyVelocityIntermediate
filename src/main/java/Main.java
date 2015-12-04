import org.apache.velocity.Template;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.tools.view.VelocityViewServlet;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;

public class Main {
    public static void main(String[] args) throws Exception {
        // jetty
        Server server = new Server(8001);
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        context.setResourceBase("templates");
        server.setHandler(context);
        // velocity
        context.addServlet(new ServletHolder(new Servlet()), "/*");
        server.start();
        server.join();
    }

    public static class Servlet extends VelocityViewServlet {
        public void init(ServletConfig config) throws ServletException {
            super.init(config);
            Velocity.init("templates/WEB-INF/velocity.properties");
        }

        protected Template getTemplate(HttpServletRequest request, HttpServletResponse response) {
            String path = request.getPathInfo().substring(1);
            return Velocity.getTemplate(path);
        }
    }

    public static class ObjectTool {
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
    }
}
