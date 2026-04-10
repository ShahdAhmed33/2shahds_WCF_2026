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
		        // ✅ Creator stays simple — path is already filtered in handle()
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

		        String upgradeHeader = request.getHeader("Upgrade");
		        if (upgradeHeader != null && upgradeHeader.equalsIgnoreCase("websocket")) {
		            // ✅ Filter the path HERE — before super.handle()
		            if ("/ws/contest".equals(target)) {
		                System.out.println("[WS] WebSocket upgrade accepted for /ws/contest");
		                super.handle(target, baseRequest, request, response);
		            } else {
		                System.out.println("[WS] WebSocket upgrade REJECTED for path: " + target);
		                response.sendError(403, "Invalid WebSocket path");
		                baseRequest.setHandled(true);
		            }
		            return;
		        }

		        // ✅ All normal HTTP requests pass to Jersey/Swagger/Frontend
		        System.out.println("[WS] HTTP request — passing to next handler");
		        if (getHandler() != null) {
		            getHandler().handle(target, baseRequest, request, response);
		        }
		    }
		}; // WebSocket handler wraps everything — WS requests handled inside, 
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
		    
	/*	try {
			server.start();
			server.join();
		} catch (Exception e ) {
			e.printStackTrace();
		}finally {
			server.destroy();
		} */
		    try {
		        server.start();

		        // ✅ WARM-UP Step 1 — Wake up Jersey with GET
		        System.out.println("[WARMUP] Step 1 - GET warm-up...");
		        try {
		            java.net.HttpURLConnection con = (java.net.HttpURLConnection)
		                new java.net.URL("http://localhost:" + this.portNum + "/api/main/sayhello")
		                    .openConnection();
		            con.setRequestMethod("GET");
		            con.setConnectTimeout(3000);
		            con.setReadTimeout(3000);
		            con.getResponseCode();
		            con.disconnect();
		            System.out.println("[WARMUP] Step 1 done");
		        } catch (Exception e) {
		            System.out.println("[WARMUP] Step 1 failed: " + e.getMessage());
		        }

		        // ✅ WARM-UP Step 2 — Wake up Jackson JSON deserializer with POST
		        System.out.println("[WARMUP] Step 2 - POST JSON warm-up...");
		        try {
		            java.net.HttpURLConnection con = (java.net.HttpURLConnection)
		                new java.net.URL("http://localhost:" + this.portNum + "/api/main/login")
		                    .openConnection();
		            con.setRequestMethod("POST");
		            con.setRequestProperty("Content-Type", "application/json");
		            con.setDoOutput(true);
		            con.setConnectTimeout(3000);
		            con.setReadTimeout(8000);
		            String body = "{\"username\":\"warmup\",\"password\":\"warmup\"}";
		            con.getOutputStream().write(body.getBytes("UTF-8"));
		            int status = con.getResponseCode();
		            System.out.println("[WARMUP] Step 2 done - status: " + status);
		            con.disconnect();
		        } catch (Exception e) {
		            System.out.println("[WARMUP] Step 2 failed: " + e.getMessage());
		        }

		        System.out.println("[WARMUP] Jersey is fully ready!");
		        server.join();

		    } catch (Exception e) {
		        e.printStackTrace();
		    } finally {
		        server.destroy();
		    }
		    
		  /*  try {
		        server.start();

		        // ✅ WARM-UP: Force Jersey to fully initialize before first real request
		        System.out.println("[WARMUP] Sending warm-up request to Jersey...");
		        try {
		            java.net.HttpURLConnection con = (java.net.HttpURLConnection)
		                new java.net.URL("http://localhost:" + this.portNum + "/api/main/sayhello")
		                    .openConnection();
		            con.setRequestMethod("GET");
		            con.setConnectTimeout(3000);
		            con.setReadTimeout(3000);
		            int status = con.getResponseCode();
		            System.out.println("[WARMUP] Warm-up response: " + status);
		            con.disconnect();
		        } catch (Exception warmupEx) {
		            System.out.println("[WARMUP] Warm-up request failed (ignored): " + warmupEx.getMessage());
		        }

		        server.join();

		    } catch (Exception e) {
		        e.printStackTrace();
		    } finally {
		        server.destroy();
		    }
		    
		 // First warm-up — GET
		    System.out.println("[WARMUP] Step 1 - warming up GET...");
		    try {
		        java.net.HttpURLConnection con = (java.net.HttpURLConnection)
		            new java.net.URL("http://localhost:" + this.portNum + "/api/main/sayhello")
		                .openConnection();
		        con.setRequestMethod("GET");
		        con.setConnectTimeout(3000);
		        con.setReadTimeout(3000);
		        int status = con.getResponseCode();
		        System.out.println("[WARMUP] Step 1 response: " + status);
		        con.disconnect();
		    } catch (Exception e) {
		        System.out.println("[WARMUP] Step 1 failed: " + e.getMessage());
		    }

		    // Second warm-up — POST JSON to login with dummy credentials
		    System.out.println("[WARMUP] Step 2 - warming up POST JSON...");
		    try {
		        java.net.HttpURLConnection con = (java.net.HttpURLConnection)
		            new java.net.URL("http://localhost:" + this.portNum + "/api/main/login")
		                .openConnection();
		        con.setRequestMethod("POST");
		        con.setRequestProperty("Content-Type", "application/json");
		        con.setDoOutput(true);
		        con.setConnectTimeout(3000);
		        con.setReadTimeout(5000);

		        // Send dummy credentials — will fail login but warms up Jackson + Jersey
		        String body = "{\"username\":\"warmup\",\"password\":\"warmup\"}";
		        con.getOutputStream().write(body.getBytes("UTF-8"));

		        int status = con.getResponseCode();
		        System.out.println("[WARMUP] Step 2 response: " + status + " — POST JSON ready!");
		        con.disconnect();
		    } catch (Exception e) {
		        System.out.println("[WARMUP] Step 2 failed: " + e.getMessage());
		    }*/
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
	
	
	
	/*public Handler getWebSocketHandler() {
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
	}*/
	
	
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