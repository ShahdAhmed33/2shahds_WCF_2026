package controllers;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.CookieParam;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.PATCH;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import Model.LoginPage;
import Model.LoginResponse;
import Model.languageList;
import Model.listProblems;
import edu.csus.ecs.pc2.api.IClient;
import edu.csus.ecs.pc2.api.IClient.ClientType;
import edu.csus.ecs.pc2.api.IContest;
import edu.csus.ecs.pc2.api.ILanguage;
import edu.csus.ecs.pc2.api.IProblem;
import edu.csus.ecs.pc2.api.ServerConnection;
import exceptions.LanguageNotFoundException;
import edu.csus.ecs.pc2.api.exceptions.NotLoggedInException;
import edu.csus.ecs.pc2.api.implementation.Contest;
import exceptions.MethodNotSupportedException;
import exceptions.NoCookieException;
import exceptions.NotVerifiedCookieException;
import exceptions.PC2ServiceUnavailableException;
import exceptions.UnauthorizedSessionException;
import helpers.CookiesHandlers;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import websocket.ContestSocket;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.util.UUID;
	
@Path("/main")
@Tag(name = "Main", description = "Main controller endpoints")
public class maincontroller extends GlobalClass { 

    protected static Map<String, ServerConnection> sessions = new ConcurrentHashMap<>();
    protected static Map<String, String> csrfTokens = new ConcurrentHashMap<>();
    protected static Map<String, Integer> loginAttempts = new ConcurrentHashMap<>();
    protected static Map<String, Long> lockoutTime = new ConcurrentHashMap<>();
    private static final int MAX_ATTEMPTS = 5;
    private static final long LOCK_TIME = 5 * 60 * 1000; // 5 minutes

    // --- HELPER METHODS ---
    public boolean isValidToken(String token) {
        try {
            return token != null 
                && CookiesHandlers.verifyCookie(token) 
                && sessions.containsKey(token);
        } catch (Exception e) {
            return false;
        }
    }
    

    // --- AUTHENTICATION ---
    @Operation(summary = "User login", description = "Authenticates a user using username and password.")
    @RequestBody(required = true, description = "Login credentials", 
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = LoginPage.class)))
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Login successful", 
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = LoginResponse.class))),
        @ApiResponse(responseCode = "401", description = "Invalid username or password")
    })
    
    
    

    @Path("/login")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response loginApi(LoginPage req) {
        // 1. Validate Input
    	String csrfToken = UUID.randomUUID().toString();
    	String username = req.username;

    	// Check if user is locked
    	if (lockoutTime.containsKey(username)) {
    	    long lockTime = lockoutTime.get(username);

    	    if (System.currentTimeMillis() - lockTime < LOCK_TIME) {
    	        return Response.status(Response.Status.TOO_MANY_REQUESTS)
    	            .entity("{\"error\": \"Account locked. Try again later.\"}")
    	            .build();
    	    } else {
    	        // Unlock after time passes
    	        lockoutTime.remove(username);
    	        loginAttempts.remove(username);
    	    }
    	}

        if (req == null || req.username == null || req.password == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"Missing credentials\"}")
                    .build();
        }
        try {
            // 2. Establish PC2 Connection
        	System.out.print("fuck");
            ServerConnection serverconnection = new ServerConnection();
            serverconnection.login(req.username, req.password);
           
            IClient myClient = serverconnection.getMyClient();
            if (myClient.getType() != ClientType.TEAM_CLIENT) {
                serverconnection.logoff(); // Clean up the connection
                return Response.status(Response.Status.FORBIDDEN)
                        .entity("{\"error\": \"Access denied: Only team accounts are allowed\"}")
                        .type(MediaType.APPLICATION_JSON)
                        .build();
            }
            loginAttempts.remove(username);
            lockoutTime.remove(username);

            
            // 3. Generate Session Token (JWT)
            String cookieID = CookiesHandlers.genCookieID("team", req.username, req.username);
            csrfTokens.put(cookieID, csrfToken);
            
            // 4. Thread-Safe Session Storage
            sessions.put(cookieID, serverconnection);
            
            // 5. Service Registration
            registAllServices(serverconnection.getContest(), cookieID); 
            
            // --- THE FIX IS HERE ---
            // 6. Create the NewCookie object using your helper
            NewCookie authCookie = CookiesHandlers.createCookie(cookieID);
            
            // 7. Response - Attach the cookie to the response
            LoginResponse loginRes = new LoginResponse(req.username , cookieID);
            loginRes.csrfToken= csrfToken;
            
            return Response.ok(loginRes)
                .cookie(authCookie) // This adds the 'Set-Cookie' header
                .type(MediaType.APPLICATION_JSON)
                .build();

        } catch (Throwable t) { 
            // Catch Throwable to see class-loading or initialization errors
            t.printStackTrace(); 
            int attempts = loginAttempts.getOrDefault(username, 0) + 1;
            loginAttempts.put(username, attempts);

            if (attempts >= MAX_ATTEMPTS) {
                lockoutTime.put(username, System.currentTimeMillis());
            }
            
            String msg = t.getMessage() != null ? t.getMessage() : "Unknown Initialization Error";
            
            if (msg.contains("Login denied") || msg.contains("No such account")) {
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity("{\"error\": \"Login denied: Invalid credentials\"}")
                        .build();
            }

            // Return a JSON error instead of an HTML 500 page
            return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                    .entity("{\"error\": \"Server warm-up error: " + msg + ". Please try again.\"}")
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }
    }
    
    
 // --- CONTEST DATA: LANGUAGES ---
    @GET
    @Path("/listlanguages")
    @Produces(MediaType.APPLICATION_JSON)
    public Response listlanguages(@Context HttpServletRequest req) {
        try {
        	validateOrigin();
            // 1. Extract cookie using the constant
            String token = CookiesHandlers.getCookie(req.getCookies(), CookiesHandlers.AUTH_COOKIE_NAME);
            validateCSRF(req, token); // 🔥 ADD THIS
            
            // 2. Validate token & Server-side Session
            if (!isValidToken(token)) {
                throw new UnauthorizedSessionException("Invalid or expired token.");
            }

            ServerConnection userConn = sessions.get(token);
            if (userConn == null || !userConn.isLoggedIn()) {
                throw new NotVerifiedCookieException("Session expired on server. Please login again.");
            }

            // 3. Fetch PC2 Data
            IContest contest = userConn.getContest();
            if (contest == null) {
                throw new PC2ServiceUnavailableException("PC2 Contest data not found.");
            }

            ILanguage[] languages = contest.getLanguages();
            if (languages == null || languages.length == 0) {
                throw new LanguageNotFoundException("No languages available for this contest.");
            }

            List<languageList> result = new ArrayList<>();
            for (ILanguage lang : languages) {
                result.add(new languageList(lang.getName(), lang.getCompilerCommandLine()));
            }
            return Response.ok(result).build();

        } catch (NoCookieException | UnauthorizedSessionException | NotVerifiedCookieException e) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}")
                    .build();
        } catch (LanguageNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}")
                    .build();
        } catch (PC2ServiceUnavailableException e) {
            return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}")
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"System Error: " + e.getMessage() + "\"}")
                    .build();
        }
    }
    // --- CONTEST DATA: PROBLEMS ---
    @GET
    @Path("/listProblem")
    @Produces(MediaType.APPLICATION_JSON)
    public Response listProblem(@Context HttpServletRequest req) {
        try {
        	validateOrigin();

            // 1. Get Token from Cookies
            String token = CookiesHandlers.getCookie(req.getCookies(), CookiesHandlers.AUTH_COOKIE_NAME);
            validateCSRF(req, token); // 🔥 ADD THIS
            
            // 2. Validate Token and Session
            if (!isValidToken(token)) {
                throw new UnauthorizedSessionException("Invalid or expired token.");
            }

            ServerConnection userConn = sessions.get(token);
            if (userConn == null || !userConn.isLoggedIn()) {
                throw new NotVerifiedCookieException("Session not found. Please log in again.");
            }

            // 3. Get Contest and Problems
            IContest contest = userConn.getContest();
            if (contest == null) {
                throw new PC2ServiceUnavailableException("Unable to reach PC2 contest data.");
            }

            IProblem[] problems = contest.getProblems();
            if (problems == null || problems.length == 0) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"error\": \"No problems found in this contest.\"}")
                        .build();
            }

            List<listProblems> problemList = new ArrayList<>();
            for (IProblem prob : problems) {
                problemList.add(new listProblems(prob.getName(), prob.getShortName(), prob.isDeleted()));
            }

            return Response.ok(problemList).build();

        } catch (NoCookieException | UnauthorizedSessionException | NotVerifiedCookieException e) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}")
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"System Error: " + e.getMessage() + "\"}")
                    .build();
        }
    }

    // --- WEBSOCKET TEST ---
    @GET
    @Path("/ws-test")
    @Produces(MediaType.APPLICATION_JSON)
    public Response wsTest(@Context HttpServletRequest req) {
        try {
            // 1. Extract & Validate
            String token = CookiesHandlers.getCookie(req.getCookies(), CookiesHandlers.AUTH_COOKIE_NAME);

            if (!isValidToken(token) || !sessions.containsKey(token)) {
                throw new UnauthorizedSessionException("WS Auth failed: Session not found.");
            }

            // 2. Broadcast
            ContestSocket.broadcast("{\"type\":\"TEST\",\"payload\":{\"message\":\"hello\"}}");

            // 3. Success Response
            Map<String, Object> result = new HashMap<>();
            result.put("status", "broadcast sent");
            result.put("connectedClients", ContestSocket.getConnectedClientsCount());

            return Response.ok(result).build();

        } catch (NoCookieException | UnauthorizedSessionException e) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}")
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"Unexpected error during WS test: " + e.getMessage() + "\"}")
                    .build();
        }
    }
    @GET
	@Path("/sayhello/{name}")
	@Produces("text/plain")
	public String sayHelloName(@PathParam("name") String name) {
		return "Hello to jersey ENG. " + name;
	}

	@GET
	@Path("/sayhello")
	@Produces("text/plain")
	public String sayHello() {
		return "Hello to jersey";
	}
	
	@Context
	HttpServletRequest request;

	private void validateOrigin() {
	    String origin = request.getHeader("Origin");

	    // Allow if no origin (non-browser or safe case)
	    if (origin != null && !origin.equals("http://localhost:8080")) {
	        throw new WebApplicationException(
	        		Response.status(Response.Status.FORBIDDEN)
	                .entity("{\"error\":\"CSRF protection: Invalid origin\"}")
	                .build()
	        );
	    }
	}
	private void validateCSRF(HttpServletRequest req, String token) {
	    String csrfHeader = req.getHeader("X-CSRF-Token");
	    String expected = csrfTokens.get(token);

	    if (csrfHeader == null || expected == null || !csrfHeader.equals(expected)) {
	        throw new WebApplicationException(
	            Response.status(Response.Status.FORBIDDEN)
	                .entity("{\"error\":\"Invalid CSRF token\"}")
	                .type(MediaType.APPLICATION_JSON)
	                .build()
	        );
	    }
	}
	
	@POST 
	@Path("/logout") 
	public Response logout(@CookieParam("awt_jwt") String token) 
	{ sessions.remove(token); 
	csrfTokens.remove(token); 
	return Response.ok("Logged out").build(); 
	}
	
	

	// --- CATCHERS FOR INVALID METHODS (PREVENTS DEFAULT HTML 405) --
	
	@GET @Path("/login") public Response catchGetM() { throw new MethodNotSupportedException("GET not supported. Use POST."); }
	@PUT @Path("/login") public Response catchPutM() { throw new MethodNotSupportedException("PUT not supported. Use POST."); }
	@DELETE @Path("/login") public Response catchDeleteM() { throw new MethodNotSupportedException("DELETE not supported. Use POST."); }
	@PATCH @Path("/login") public Response catchPatchM() { throw new MethodNotSupportedException("PATCH not supported. Use POST."); }
	@HEAD @Path("/login") public Response catchHeadM() { throw new MethodNotSupportedException("HEAD not supported. Use POST."); }
	
	@GET @Path("/logout") public Response catchGetB() { throw new MethodNotSupportedException("GET not supported. Use POST."); }
	@PUT @Path("/logout") public Response catchPutB() { throw new MethodNotSupportedException("PUT not supported. Use POST."); }
	@DELETE @Path("/logout") public Response catchDeleteB() { throw new MethodNotSupportedException("DELETE not supported. Use POST."); }
	@PATCH @Path("/logout") public Response catchPatchB() { throw new MethodNotSupportedException("PATCH not supported. Use POST."); }
	@HEAD @Path("/logout") public Response catchHeadB() { throw new MethodNotSupportedException("HEAD not supported. Use POST."); }

	@POST @Path("/listlanguages") public Response catchPostL() { throw new MethodNotSupportedException("POST not supported. Use GET."); }
	@PUT @Path("/listlanguages") public Response catchPutL() { throw new MethodNotSupportedException("PUT not supported. Use GET."); }
	@DELETE @Path("/listlanguages") public Response catchDeleteL() { throw new MethodNotSupportedException("DELETE not supported. Use GET."); }
	@PATCH @Path("/listlanguages") public Response catchPatchL() { throw new MethodNotSupportedException("PATCH not supported. Use GET."); }
	@HEAD @Path("/listlanguages") public Response catchHeadL() { throw new MethodNotSupportedException("HEAD not supported. Use GET."); }

	@POST @Path("/listProblem") public Response catchPostP() { throw new MethodNotSupportedException("POST not supported. Use GET."); }
	@PUT @Path("/listProblem") public Response catchPutP() { throw new MethodNotSupportedException("PUT not supported. Use GET."); }
	@DELETE @Path("/listProblem") public Response catchDeleteP() { throw new MethodNotSupportedException("DELETE not supported. Use GET."); }
	@PATCH @Path("/listProblem") public Response catchPatchP() { throw new MethodNotSupportedException("PATCH not supported. Use GET."); }
	@HEAD @Path("/listProblem") public Response catchHeadP() { throw new MethodNotSupportedException("HEAD not supported. Use GET."); }
}