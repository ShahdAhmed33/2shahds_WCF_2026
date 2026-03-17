package web1;

import java.io.IOException;

import org.eclipse.jetty.server.Handler;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.servlet.ServletHolder.JspContainer;
import org.glassfish.jersey.servlet.ServletContainer;
import org.eclipse.jetty.util.resource.Resource;
//import org.eclipse.jetty.websocket.server.config.JettyWebSocketServletContainerInitializer;
//import org.eclipse.jetty.websocket.server.config.JettyWebSocketServletContainerInitializer;
//import org.eclipse.jetty.websocket.server.config.JettyWebSocketServletContainerInitializer;
import org.eclipse.jetty.servlet.DefaultServlet;
//import org.eclipse.jetty.websocket.server.config.JettyWebSocketServletContainerInitializer;
import org.eclipse.jetty.websocket.server.NativeWebSocketServletContainerInitializer;
import org.eclipse.jetty.websocket.server.WebSocketHandler;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;
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
		ContextHandlerCollection contexts = new ContextHandlerCollection();
		HandlerList handlers = new HandlerList();
		/*handlers.addHandler(getApiServletHandler());
	    handlers.addHandler(getWebSocketHandler());
        handlers.addHandler(getSwaggerUIHandler());
		handlers.addHandler(getFrontendHandler()); */
	/*	contexts.setHandlers(new Handler[] {
		        //getWebSocketHandler(),
				getApiServletHandler(),
		        getSwaggerUIHandler(),
		        getFrontendHandler()
		    }); */
		 WebSocketHandler wsHandler = new WebSocketHandler() {
		        @Override
		        public void configure(WebSocketServletFactory factory) {
		            System.out.println("[WS] Factory configure called");
		            factory.setCreator((req, resp) -> {
		                System.out.println("[WS] CREATOR FIRED");
		                return new websocket.ContestSocket();
		            });
		        }

		        @Override
		        public void handle(String target, org.eclipse.jetty.server.Request baseRequest,
		                           javax.servlet.http.HttpServletRequest request,
		                           javax.servlet.http.HttpServletResponse response)
		                           throws java.io.IOException, javax.servlet.ServletException {
		            System.out.println("[WS] handle() called for: " + target);
		            // Only handle WebSocket upgrade requests
		            
		                super.handle(target, baseRequest, request, response);
		            
		            // Otherwise let it fall through to next handler
		        }
		    };

		    // WebSocket handler wraps everything — WS requests handled inside, 
		    // all other requests passed to the rest of the chain via setHandler
		    HandlerList restHandlers = new HandlerList();
		    restHandlers.addHandler(getApiServletHandler());
		    restHandlers.addHandler(getSwaggerUIHandler());
		    restHandlers.addHandler(getFrontendHandler());

		    // THIS IS THE KEY — wsHandler wraps the rest
		    wsHandler.setHandler(restHandlers);
		   // server.setHandler(restHandlers);
		 //   wsHandler.setHandler(wsHandler);
		    server.setHandler(wsHandler);
		//server.setHandler(contexts);
		//server.setHandler(handlers);
		try {
			server.start();
			server.join();
		} catch (Exception e ) {
			e.printStackTrace();
		}finally {
			server.destroy();
		}
	}
	/*public Handler getFrontendHandler() {
		ResourceHandler webcontent = new ResourceHandler();
		webcontent.setResourceBase("./html");
		ContextHandler frontend = new ContextHandler("/*");
		frontend.setHandler(webcontent);
		
		return frontend;
		
	}*/
	public Handler getFrontendHandler() {
	    // 1. Create the ResourceHandler to point to your folder
	    ResourceHandler resourceHandler = new ResourceHandler();
	    resourceHandler.setResourceBase("./html");
	    resourceHandler.setDirectoriesListed(false); // Security: don't show file list
	    resourceHandler.setWelcomeFiles(new String[]{"index.html"});

	    // 2. Wrap it in a ServletContextHandler
	    ServletContextHandler frontendContext = new ServletContextHandler(ServletContextHandler.SESSIONS);
	    frontendContext.setContextPath("/"); 
	    frontendContext.setHandler(resourceHandler);
	    
	    return frontendContext;
	}
	
	
	
	/*public Handler getWebSocketHandler() {

	    System.out.println("[WS] Building websocket handler");

	    ServletContextHandler wsContext =
	        new ServletContextHandler(ServletContextHandler.NO_SESSIONS);
	    
	   // wsContext.addServlet(DefaultServlet.class, "/test");
	    wsContext.setContextPath("/ws");

	    // Add a dummy servlet so the context can receive requests
	   wsContext.addServlet(DefaultServlet.class, "/");
	// Force the initializer to run (sometimes needed in embedded Jetty 9.4)

	    NativeWebSocketServletContainerInitializer.configure(
	        wsContext,
	        (servletContext, wsContainer) -> {

	            System.out.println("[WS] Configuring websocket mappings");

	            wsContainer.addMapping("/contest", (req, resp) -> {
	                System.out.println("[WS] Creator invoked for " + req.getRequestPath());
	                return new websocket.ContestSocket();
	            });

	        });

	    return wsContext;
	} */
	
	
	
	public Handler getWebSocketHandler() {
	    System.out.println("[WS] Building websocket handler");

	    WebSocketHandler wsHandler = new WebSocketHandler() {
	        @Override
	        public void configure(WebSocketServletFactory factory) {
	            System.out.println("[WS] Configuring websocket factory");
	            factory.setCreator((req, resp) -> {
	                String path = req.getRequestPath();
	                System.out.println("[WS] Creator invoked for: " + path);

	                // Manual path filtering
	                if (path.equals("/ws/contest") || path.equals("/contest")) {
	                    return new websocket.ContestSocket();
	                }

	                System.out.println("[WS] Rejected path: " + path);
	                return null; // reject non-matching paths
	            });
	        }
	    };

	    return wsHandler;
	}
	
	
	/*public Handler getWebSocketHandler() {
	    ServletContextHandler ws = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);
	    ws.setContextPath("/ws");

	    JettyWebSocketServletContainerInitializer.configure(ws, (servletContext, wsContainer) -> {
	        wsContainer.addMapping("/contest", (req, resp) -> new websocket.ContestSocket());
	    });

	    return ws;
	}*/
	
	
	
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
