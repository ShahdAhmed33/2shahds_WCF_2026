/*package helpers;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class AppWarmUp implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("[WARMUP] Pre-loading PC2 listener classes...");
        try {
            Class.forName("services.ConfigurationUpdates");
            Class.forName("edu.csus.ecs.pc2.api.listener.IConfigurationUpdateListener");
            Class.forName("edu.csus.ecs.pc2.api.listener.ContestEvent");
            Class.forName("websocket.ContestSocket");
            System.out.println("[WARMUP] PC2 classes loaded successfully.");
        } catch (ClassNotFoundException e) {
            System.err.println("[WARMUP] Warning: " + e.getMessage());
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // nothing needed here
    }
}*/