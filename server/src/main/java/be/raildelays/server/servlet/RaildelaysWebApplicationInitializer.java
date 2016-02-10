package be.raildelays.server.servlet;

import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.XmlWebApplicationContext;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

/**
 * With this {@link WebApplicationInitializer} we don't need a {@code web.xml} anymore.
 *
 * @author Almex
 * @since 2.0
 */
public class RaildelaysWebApplicationInitializer implements WebApplicationInitializer {
    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        XmlWebApplicationContext appContext = new XmlWebApplicationContext();
        appContext.setConfigLocations(
                "classpath:/spring/bootstrap-servlet-context.xml",
                "classpath:/jobs/main-job-context.xml",
                "classpath:/jobs/steps/handle-max-months-job-context.xml",
                "classpath:/jobs/steps/handle-more-than-one-hour-delays-job-context.xml",
                "classpath:/jobs/steps/load-gtfs-into-database-job-context.xml");

        servletContext.addListener(new ContextLoaderListener(appContext));
    }
}
