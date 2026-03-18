package controllers;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
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
import javax.ws.rs.core.Response;
import Model.LoginPage;
import Model.LoginResponse;
import Model.languageList;
import Model.listProblems;
import edu.csus.ecs.pc2.api.IContest;
import edu.csus.ecs.pc2.api.ILanguage;
import edu.csus.ecs.pc2.api.IProblem;
import edu.csus.ecs.pc2.api.ServerConnection;
import edu.csus.ecs.pc2.api.exceptions.NotLoggedInException;
import exceptions.LanguageNotFoundException;
import exceptions.MethodNotSupportedException;
import exceptions.MissingCredientials;
import exceptions.PC2ServiceUnavailableException;
import exceptions.UnauthorizedSessionException;
import helpers.CookiesHandlers;
import helpers.CookiesHandlers.CookieData;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import websocket.ContestSocket;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
@Path("/main")
@Tag(name = "Main", description = "Main controller endpoints")
public class maincontroller {

	protected static Map<String, ServerConnection> sessions = new ConcurrentHashMap<>();

	// --- HELPER METHODS ---

	public static boolean isValidToken(String token) {
		return token != null 
				&& CookiesHandlers.verifyTokenSignature(token) 
				&& sessions.containsKey(token);
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
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@POST
	public Response loginApi(LoginPage req) {
		if (req == null) {
			return Response.status(Response.Status.BAD_REQUEST).entity("Invalid media").type(MediaType.APPLICATION_JSON).build();
		}

		if (req.username == null || req.password == null) {
			throw new MissingCredientials("Please add username/password");//thorws exceptions when there is missing credientials 
		}

		try {
			ServerConnection serverconnection = new ServerConnection();
			serverconnection.login(req.username, req.password);

			CookieData data = CookiesHandlers.createAuthCookie();
			String token = data.getToken();

			sessions.put(token, serverconnection);

			LoginResponse loginRes = new LoginResponse(req.username, token);

			String cookieHeader = CookiesHandlers.AUTH_COOKIE_NAME + "=" + token + 
					"; Path=/api" + 
					"; Max-Age=3600" + 
					"; Secure" + 
					"; HttpOnly" + 
					"; SameSite=Strict";

			return Response.ok(loginRes)
					.header("Set-Cookie", cookieHeader)
					.type(MediaType.APPLICATION_JSON)
					.build();

		} catch (NotLoggedInException e) {
			return Response.status(Response.Status.UNAUTHORIZED).entity("unable to execute api method").type(MediaType.APPLICATION_JSON).build();
		} catch (Exception e) {
			throw new PC2ServiceUnavailableException("PC2Server is not available");// throws exception if the server is not working and tried logging in 
		}
	}

	

	// --- CONTEST DATA: LANGUAGES ---

	@GET
	@Path("/listlanguages")
	@Produces(MediaType.APPLICATION_JSON)
	public Response listlanguages(@Context HttpServletRequest req) {
		try {
			String token = CookiesHandlers.getCookie(req.getCookies(), CookiesHandlers.AUTH_COOKIE_NAME);
			if (!isValidToken(token)) {
				throw new UnauthorizedSessionException("Not logged in. Session is invalid or expired.");//throws exception when session is invalid 
			}

			ServerConnection userConn = sessions.get(token);
			if (userConn == null) {
				throw new PC2ServiceUnavailableException("Session exists, but server connection is missing.");//Throws exception if session exists but no server connection 
			}

			if (!userConn.isLoggedIn()) {
				throw new NotLoggedInException("Your session has expired on the PC2 server.");//throws exception when session expires
			}

			IContest contest = userConn.getContest();
			if (contest == null) {
				throw new PC2ServiceUnavailableException("Unable to retrieve contest data.");// throws exception if the server is not working
			}

			ILanguage[] languages = contest.getLanguages();
			if (languages == null || languages.length == 0) {
				throw new LanguageNotFoundException("No programming languages defined.");//throws exceptions when no languages are added yet 
			}

			List<languageList> result = new ArrayList<>();
			for (ILanguage lang : languages) {
				result.add(new languageList(lang.getName(), lang.getCompilerCommandLine()));
			}

			return Response.ok(result).build();

		} catch (MethodNotSupportedException | LanguageNotFoundException e) {
			return e.getResponse();
		} catch (NotLoggedInException e) {
			return Response.status(Response.Status.UNAUTHORIZED).entity("error:" + e.getMessage()).type(MediaType.APPLICATION_JSON).build();
		} catch (PC2ServiceUnavailableException e) {
			return Response.status(Response.Status.SERVICE_UNAVAILABLE).entity("error:" + e.getMessage()).type(MediaType.APPLICATION_JSON).build();
		} catch (WebApplicationException e) {
			return Response.fromResponse(e.getResponse()).type(MediaType.APPLICATION_JSON).build();
		} catch (Exception e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("System Error: " + e.getLocalizedMessage()).type(MediaType.APPLICATION_JSON).build();
		}
	}

	// --- CONTEST DATA: PROBLEMS ---

	@GET
	@Path("/listProblem")
	@Produces(MediaType.APPLICATION_JSON)
	public Response listProblem(@Context HttpServletRequest req) {
		try {
			String token = CookiesHandlers.getCookie(req.getCookies(), CookiesHandlers.AUTH_COOKIE_NAME);
			if (!isValidToken(token)) {
				throw new UnauthorizedSessionException("Not logged in. Session is invalid or expired.");//throws exception when session is invalid
			}

			ServerConnection userConn = sessions.get(token);
			if (userConn == null) {
				throw new PC2ServiceUnavailableException("Server connection object is missing.");// throws exception if the server is not working
			}

			IContest contest = userConn.getContest();
			if (contest == null) {
				throw new PC2ServiceUnavailableException("Unable to retrieve contest data.");// throws exception if the server is not working	
			}

			IProblem[] problems = contest.getProblems();
			if (problems == null || problems.length == 0) {
				throw new LanguageNotFoundException("No problems found for this contest.");//throws exception when no problems are added yet 
			}

			List<listProblems> problemList = new ArrayList<>();
			for (IProblem prob : problems) {
				problemList.add(new listProblems(prob.getName(), prob.getShortName(), prob.isDeleted()));
			}

			return Response.ok(problemList).build();

		} catch (MethodNotSupportedException e) {
			return e.getResponse();
		} catch (PC2ServiceUnavailableException e) {
			return Response.status(Response.Status.SERVICE_UNAVAILABLE).entity(e.getMessage()).type(MediaType.APPLICATION_JSON).build();
		} catch (WebApplicationException e) {
			return Response.fromResponse(e.getResponse()).type(MediaType.APPLICATION_JSON).build();
		} catch (Exception e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("System Error: " + e.getLocalizedMessage()).type(MediaType.APPLICATION_JSON).build();
		}
	}

	// --- WEBSOCKET TEST ---

	@GET
	@Path("/ws-test")
	@Produces(MediaType.APPLICATION_JSON)
	public Response wsTest(@Context HttpServletRequest req) {
		String token = CookiesHandlers.getCookie(req.getCookies(), CookiesHandlers.AUTH_COOKIE_NAME);

		if (!isValidToken(token)) {
			return Response.status(Response.Status.UNAUTHORIZED).entity("Not logged in").type(MediaType.APPLICATION_JSON).build();
		}

		ContestSocket.broadcast("{\"type\":\"TEST\",\"payload\":{\"message\":\"hello from websocket test endpoint\"}}");

		Map<String, Object> result = new HashMap<>();
		result.put("status", "broadcast sent");
		result.put("connectedClients", ContestSocket.getConnectedClientsCount());

		return Response.ok(result).build();
	}

	// --- GREETINGS ---

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

	// --- CATCHERS FOR INVALID METHODS (PREVENTS DEFAULT HTML 405) ---
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