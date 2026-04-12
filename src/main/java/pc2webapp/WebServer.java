package pc2webapp;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.servlet.ServletContainer;

public class WebServer {
	
	private Integer portNumber;
	
	//Default empty  constructor initialize the class attributes to defualt values
	public WebServer() {
		
		this.portNumber = 8080;
		
	}
	
	//Overrloaded constructor with port number
	public WebServer(Integer portNumber) {
		
		this.portNumber = portNumber;
		
	}
	
	//Method to start the web server
	public void startServer() {
		
		//Create a server
				Server server = new Server(this.portNumber);
				
				
				//Create a handler list
				HandlerList handlers = new HandlerList();
				
				//Adding handler to the html files
				handlers.addHandler(getFrontEndHandler());
				
				
				////////Adding handler to API
				handlers.addHandler(getAPIServletHandler());
				
				//Add all handlers to the server
				server.setHandler(handlers);
				
				
				//Start the server
				try {
				    server.start();
				    server.join(); // Block until server stops
				} catch (Exception e) {
				    e.printStackTrace();
				} finally {
				    server.destroy(); // Clean up resources
				}
	}
	
	public Handler getFrontEndHandler() {
		
		ResourceHandler webContent = new ResourceHandler();
		webContent.setResourceBase("./html");
		ContextHandler frontend = new ContextHandler("/*");
		frontend.setHandler(webContent);

		return frontend;
	}
	
	/**
	 * A Class return a handler to the servlet responsible for the Controller
	 * @return a {@link ServletContextHandler}
	 */
	public Handler getAPIServletHandler() {
		
		ServletContextHandler api = new ServletContextHandler();
		api.setContextPath("/api");
		ServletHolder servletHolder = api.addServlet(ServletContainer.class, "/*");			
		servletHolder.setInitParameter("jersey.config.server.provider.classnames", 
				"org.glassfish.jersey.jackson.internal.jackson.jaxrs.json.JacksonJaxbJsonProvider, "
				+ "controllers.TeamController,"
				+ "controllers.MainContestController,"
				+ "org.glassfish.jersey.jackson.JacksonFeature");	
		
		servletHolder.setInitOrder(1);

		return api;
	}
	
	
}
