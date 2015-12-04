import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.io.FileTemplateLoader;
import com.github.jknack.handlebars.io.TemplateLoader;

import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.tools.config.DefaultKey;
import org.apache.velocity.tools.generic.SafeConfig;
import org.apache.velocity.tools.view.ViewToolContext;

import java.io.IOException;
import java.util.Map;

@DefaultKey("handlebars")
public class HandlebarsTool extends SafeConfig {
    protected ViewToolContext context;
    protected VelocityEngine ve;

    public void configure(Map params) {
        context = (ViewToolContext) params.get(ViewToolContext.CONTEXT_KEY);
    }

    public String render(String relpath, Object values) throws IOException {
        try {
            if (!context.getVelocityEngine().resourceExists(relpath))
                return "";
        }
        catch (ResourceNotFoundException rnfe) {
            return "";
        }

        String dir = context.getServletContext().getRealPath(relpath.substring(0, relpath.lastIndexOf("/")));
        String nam = relpath.substring(relpath.lastIndexOf("/") + 1, relpath.lastIndexOf("."));
        String ext = relpath.substring(relpath.lastIndexOf("."));

        TemplateLoader loader = new FileTemplateLoader(dir, ext);
        Handlebars handlebars = new Handlebars(loader);
        Template template = handlebars.compile(nam);
        return template.apply(values);
    }
}
