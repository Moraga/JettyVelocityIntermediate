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
}
