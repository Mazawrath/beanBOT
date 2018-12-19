package com.mazawrath.beanbot.utilities.jersey;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

public class RestServer implements Runnable {
    public void run()
    {
        try
        {
            // Displaying the thread that is running
            System.out.println ("Thread " +
                    Thread.currentThread().getId() +
                    " is running");
            ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
            context.setContextPath("/");

            Server jettyServer = new Server(8081);
            jettyServer.setHandler(context);

            ServletHolder jerseyServlet = context.addServlet(
                    org.glassfish.jersey.servlet.ServletContainer.class, "/*");
            jerseyServlet.setInitOrder(0);

            jerseyServlet.setInitParameter(
                    "jersey.config.server.provider.classnames",
                    Calculator.class.getCanonicalName());

            try {
                jettyServer.start();
                jettyServer.join();
            } catch (Exception e) {
                jettyServer.destroy();
            }
        }
        catch (Exception e)
        {
            // Throwing an exception
            System.out.println ("Exception is caught");
        }
    }
}
