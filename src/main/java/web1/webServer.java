package web1;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.servlet.ServletHolder.JspContainer;
import org.glassfish.jersey.servlet.ServletContainer;
import org.eclipse.jetty.util.resource.Resource;

public class webServer {
	private Integer portNum;
	
	public webServer() {
		this.portNum=8080;
	}
	public webServer(Integer portNum) {
		this.portNum=portNum;
	}
	
	public void start() {
		Server server = new Server(this.portNum);
		HandlerList handlers = new HandlerList();
		handlers.addHandler(getFrontendHandler());
		handlers.addHandler(getApiServletHandler());
        handlers.addHandler(getSwaggerUIHandler());

		server.setHandler(handlers);
		try {
			server.start();
			server.join();
		} catch (Exception e ) {
			e.printStackTrace();
		}finally {
			server.destroy();
		}
	}
	public Handler getFrontendHandler() {
		ResourceHandler webcontent = new ResourceHandler();
		webcontent.setResourceBase("./html");
		ContextHandler frontend = new ContextHandler("/html/*");
		frontend.setHandler(webcontent);
		
		return frontend;
		
	}
	
	/*public Handler getapiSerlitHandler() {
		ServletContextHandler api = new ServletContextHandler();
		api.setContextPath("/api");
		ServletHolder servletHolder = api.addServlet(ServletContainer.class, "/*");
		servletHolder.setInitParameter("jersey.config.server.provider.packages", "controllers");
		servletHolder.setInitOrder(0);

		return api;
	}*/
	 public Handler getApiServletHandler() {

	        ServletContextHandler api = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);
	        api.setContextPath("/api");

	        ServletHolder servletHolder = api.addServlet(ServletContainer.class, "/*");

	        //  ENABLE CONTROLLERS + SWAGGER + CONFIG
	        servletHolder.setInitParameter(
	                "jersey.config.server.provider.packages",
	                "controllers,config,io.swagger.v3.jaxrs2.integration.resources"
	        );

	        // ENABLE JSON MAPPING
	        servletHolder.setInitParameter(
	                "jersey.config.server.provider.classnames",
	                "org.glassfish.jersey.jackson.internal.jackson.jaxrs.json.JacksonJaxbJsonProvider"
	                
	        );
	        
	       


	        servletHolder.setInitOrder(1);

	        return api;
	    }
	 
	 
	 
	 
	 
	 public Handler getSwaggerUIHandler() {
	        // Serve Swagger UI static files from the WebJar on the classpath
	        ResourceHandler swaggerResources = new ResourceHandler();
	        swaggerResources.setBaseResource(
	                Resource.newClassPathResource("/META-INF/resources/webjars/swagger-ui/5.11.8")
	        );
	        swaggerResources.setWelcomeFiles(new String[] { "index.html" });

	        // Map them under /swagger
	        ContextHandler swaggerContext = new ContextHandler("/swagger");
	        swaggerContext.setHandler(swaggerResources);

	        return swaggerContext;
	    }
	 
	 
	 
	 
	 
	 
}
