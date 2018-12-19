package com.mazawrath.beanbot.utilities.jersey;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ServerProperties;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RestServer implements Runnable {
	private static final Logger logger = LoggerFactory.getLogger(RestServer.class);
    public void run()
    {
        //try
        //{
            // Displaying the thread that is running
            /*System.out.println ("Thread " +
                    Thread.currentThread().getId() +
                    " is running");            */

            Server server = new Server(8081);
			
			final ResourceConfig application = new ResourceConfig()
                .packages("com.mazawrath.beanbot.utilities.jersey")
                .register(JacksonFeature.class);
			
			ServletContextHandler servletContextHandler = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);			
			
			servletContextHandler.setContextPath("/");
			server.setHandler(servletContextHandler);
			
			ServletHolder servletHolder = new ServletHolder(new ServletContainer(application));			
			servletHolder.setInitOrder(0);
			servletContextHandler.addServlet(servletHolder, "/api/*");

            try {
				server.start();
				server.join();
			} catch (Exception ex) {
				logger.error("Error occurred while starting Jetty", ex);
				System.exit(1);
			}

			finally {
				server.destroy();
			}
        //}
        //catch (Exception e)
        //{
            // Throwing an exception
            //System.out.println ("Exception is caught");
        //}
    }
}
