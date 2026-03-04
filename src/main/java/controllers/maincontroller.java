package controllers;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.HashMap;

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
import exceptions.PC2ServiceUnavailableException;
import exceptions.UnauthorizedSessionException;
import helpers.CookiesHandlers;
import helpers.CookiesHandlers.CookieData;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@Path("/main")
@Tag(name = "Main", description = "Main controller endpoints")
public class maincontroller {
	 protected static Map<String, ServerConnection> sessions =
	            new ConcurrentHashMap<>();
	//we will use get method to retrieve the username and password that the user will enter
	
	//write http://localhost:8080/api/main/login/{username}/{password} ....> {username}=YOUR_USERNAME ,{password}=YOUR_PASSWORD
	//@Path("/login/{username}/{password}")
	
	
	//the request and the response are working with JSON format 
	
	  @Operation(
		        summary = "User login",
		        description = "Authenticates a user using username and password."
		    )
		    @RequestBody(
		        required = true,
		        description = "Login credentials",
		        content = @Content(
		            mediaType = "application/json",
		            schema = @Schema(implementation = LoginPage.class)
		        )
		    )
		    @ApiResponses({
		        @ApiResponse(
		            responseCode = "200",
		            description = "Login successful",
		            content = @Content(
		                mediaType = "application/json",
		                schema = @Schema(implementation = LoginResponse.class)
		            )
		        ),
		        @ApiResponse(
		            responseCode = "401",
		            description = "Invalid username or password"
		        )
		    })
	
	//method called loginApi and Response its data type ...> it takes username and password as parameters of type String
	@Path("/login")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@POST
	
	public Response loginApi(LoginPage req) {

		/*
		 -initialized res as null and res data type is Response
		 -this condition will return response code ok as long as the (IF condition) is false so it will execute the else part 
		 -if (IF condition) is true it will return unauthorized status code 401 and message will be USERNAME OR password is not correct
		 - we will create object from class LoginPage (the Model that carry the data) it carries the username and password data
		 -the else part returning response code ok and it will return login object as JSON format 
		 -after comparing we will return the res that carries the information inside the entity class
		*/
		Response res=null;
		 if (req == null ) {
		           res=Response.status(Response.Status.BAD_REQUEST)
		                .entity("Invalid media")
		                .type(MediaType.APPLICATION_JSON)
		                .build();
		           return res;
		    }
		 
		 if (req.username == null || req.password == null) {
	           res=Response.status(Response.Status.BAD_REQUEST)
	                .entity("Missing credentials")
	                .type(MediaType.APPLICATION_JSON)
	                .build();
	           return res;
	    }
		
		try {
			 ServerConnection serverconnection = new ServerConnection();
			 serverconnection.login(req.username, req.password);
			 
			 // Generate 64-char signed token
			 CookieData data = CookiesHandlers.createAuthCookie();
			 String token = data.getToken();

			 sessions.put(token, serverconnection);
			 
			 LoginResponse loginRes = new LoginResponse(req.username, token);
			 
			 String cookieHeader = CookiesHandlers.AUTH_COOKIE_NAME + "=" + token +
				        "; Path=/api" +
				        "; Max-Age=3600" +
				        "; Secure" +
				        "; HttpOnly" +
				        "; SameSite=Strict";//added samesite for mitigation against CSRF

				return Response.ok(loginRes)
				        .header("Set-Cookie", cookieHeader)
				        .type(MediaType.APPLICATION_JSON)
				        .build();
			
	           /* return Response.ok(loginRes)
	                           .cookie(data.getCookie())
	                           .type(MediaType.APPLICATION_JSON)
	                           .build();*/
		}
	   catch (NotLoggedInException e) {
		res= Response.status(Response.Status.UNAUTHORIZED)
				.entity("unable to execute api method")
				.type(MediaType.APPLICATION_JSON)
				.build();
	     return res;
	   }
		catch (Exception e) {
		    return Response.status(Response.Status.UNAUTHORIZED)
		            .entity("Invalid username/password or PC2 error")
		            .type(MediaType.APPLICATION_JSON)
		            .build();
		}
	} 
	  
	@GET
	@Path("/verify")
	@Produces(MediaType.APPLICATION_JSON)
	public Response verifySession(@Context HttpServletRequest req) {
		String token = CookiesHandlers.getCookie(req.getCookies(), CookiesHandlers.AUTH_COOKIE_NAME);

		// Verify signature and map existence
		if (!CookiesHandlers.verifyTokenSignature(token) || token == null || !sessions.containsKey(token)) {
			return Response.status(Response.Status.UNAUTHORIZED).entity("Invalid Session").build();
		}

		Map<String, String> success = new HashMap<>();
		success.put("status", "authenticated");
		return Response.ok(success).build();
	}

	
	 //handling the specific to the general exception for the get path for  list languages 
	
	@GET
	@Path("/listlanguages")
	@Produces(MediaType.APPLICATION_JSON)
	public Response listlanguages(@Context HttpServletRequest req) {
	    try {
	       
	        String token = CookiesHandlers.getCookie(req.getCookies(), CookiesHandlers.AUTH_COOKIE_NAME);
	        if (token == null || !CookiesHandlers.verifyTokenSignature(token) || !sessions.containsKey(token)) {
	         throw new UnauthorizedSessionException("Not logged in. Session is invalid or expired.");
	        }

	        ServerConnection userConn = sessions.get(token);

	        if (userConn == null) {
	            throw new PC2ServiceUnavailableException("The session exists, but the server connection object is missing.");
	        }

	        if (!userConn.isLoggedIn()) {
	            throw new NotLoggedInException("Your session has expired or the credentials are no longer valid.");
	        }

	        IContest contest = userConn.getContest();
	        if (contest == null) {
	            throw new PC2ServiceUnavailableException("Unable to retrieve contest data from the PC2 server.");
	        }

	        ILanguage[] languages = contest.getLanguages();
	        if (languages == null || languages.length == 0) {
	            throw new LanguageNotFoundException("No programming languages are defined for this contest.");
	        }

	        List<languageList> result = new ArrayList<>();
	        for (ILanguage lang : languages) {
	            result.add(new languageList(lang.getName(), lang.getCompilerCommandLine()));
	        }

	        return Response.ok(result).build();

	    } 
	    catch (MethodNotSupportedException e) {
	        return e.getResponse();
	    }
	    catch (NotLoggedInException e) {
	        return Response.status(Response.Status.UNAUTHORIZED)
	                .entity("error:" + e.getMessage())
	                .type(MediaType.APPLICATION_JSON)
	                .build();
	    }
	    catch (PC2ServiceUnavailableException e) {
	        return Response.status(Response.Status.SERVICE_UNAVAILABLE)
	                .entity("error:" + e.getMessage())
	                .type(MediaType.APPLICATION_JSON)
	                .build();
	    }
	    catch (LanguageNotFoundException e) {
	        return e.getResponse(); 
	    }
	    catch (WebApplicationException e) {
	        return Response.fromResponse(e.getResponse())
	                .type(MediaType.APPLICATION_JSON)
	                .build();
	    }
	    catch (Exception e) {
	        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
	                .entity("System Error: " + e.getLocalizedMessage())
	                .type(MediaType.APPLICATION_JSON)
	                .build();
	    }
	}
	
	//handling the wrong path for the list languages
	
	@POST
    @Path("/listlanguages")
    @Produces(MediaType.APPLICATION_JSON)
    public Response catchPost(@Context HttpServletRequest req) {
        throw new MethodNotSupportedException("Error: POST is not supported. Use GET.");
    }
	@PUT
    @Path("/listlanguages")
    @Produces(MediaType.APPLICATION_JSON)
    public Response catchPut(@Context HttpServletRequest req) {
        throw new MethodNotSupportedException("Error: PUT is not supported. Please use GET.");
    }
    @DELETE
    @Path("/listlanguages")
    @Produces(MediaType.APPLICATION_JSON)
    public Response catchDelete(@Context HttpServletRequest req) {
        throw new MethodNotSupportedException("Error: DELETE is not supported. Please use GET.");
    }
    @HEAD
    @Path("/listlanguages")
    @Produces(MediaType.APPLICATION_JSON)
    public Response catchHead(@Context HttpServletRequest req) {
        throw new MethodNotSupportedException("Error: HEAD is not supported. Please use GET.");
    }
    @PATCH
    @Path("/listlanguages")
    @Produces(MediaType.APPLICATION_JSON)
    public Response catchPatch(@Context HttpServletRequest req) {
        throw new MethodNotSupportedException("Error: PATCH is not supported. Please use GET.");
    }
    
    // handling exceptions from specific to general for the list problem
    
    @GET 
    @Path("/listProblem")
    @Produces(MediaType.APPLICATION_JSON)
    public Response listProblem(final @Context HttpServletRequest req) {
        try {
            String token = CookiesHandlers.getCookie(req.getCookies(), CookiesHandlers.AUTH_COOKIE_NAME);
            if (token == null || !CookiesHandlers.verifyTokenSignature(token) || !sessions.containsKey(token)) {
            throw new UnauthorizedSessionException("Not logged in. Session is invalid or expired.");
            }
            ServerConnection userConn = sessions.get(token);
            if (userConn == null) {
                throw new PC2ServiceUnavailableException("Server connection object is missing for this session.");
            }
            IContest contest = userConn.getContest();
            if (contest == null) {
                throw new PC2ServiceUnavailableException("Unable to retrieve contest data.");
            }

            List<listProblems> problem = new ArrayList<listProblems>();
            IProblem[] problems = contest.getProblems();
            
            if (problems == null || problems.length == 0) {
                throw new LanguageNotFoundException("No problems found for this contest.");
            }

            for (IProblem prob : problems) {
                listProblems li = new listProblems(prob.getName(), prob.getShortName(), prob.isDeleted());
                problem.add(li);
            }

            return Response.ok(problem).build();

        } 
        catch (MethodNotSupportedException e) {
            return e.getResponse();
        }
        catch (PC2ServiceUnavailableException e) {
            return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                    .entity("e.getMessage()")
                    .type(MediaType.APPLICATION_JSON).build();
        }
        catch (WebApplicationException e) {
            return Response.fromResponse(e.getResponse())
                    .type(MediaType.APPLICATION_JSON).build();
        }
        catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("System Error: " + e.getLocalizedMessage())
                    .type(MediaType.APPLICATION_JSON).build();
        }
    }
    
    
    //handling the error if we use the wrong path of list Problem
    
    @POST @Path("/listProblem") @Produces(MediaType.APPLICATION_JSON)
    public Response catchPostP(@Context HttpServletRequest req) { throw new MethodNotSupportedException("POST not supported. Use GET."); }
    @PUT @Path("/listProblem") @Produces(MediaType.APPLICATION_JSON)
    public Response catchPutP(@Context HttpServletRequest req) { throw new MethodNotSupportedException("PUT not supported. Use GET."); }
    @DELETE @Path("/listProblem") @Produces(MediaType.APPLICATION_JSON)
    public Response catchDeleteP(@Context HttpServletRequest req) { throw new MethodNotSupportedException("DELETE not supported. Use GET."); }
    @PATCH @Path("/listProblem") @Produces(MediaType.APPLICATION_JSON)
    public Response catchPatchP(@Context HttpServletRequest req) { throw new MethodNotSupportedException("PATCH not supported. Use GET."); }
    @HEAD @Path("/listProblem") @Produces(MediaType.APPLICATION_JSON)
    public Response catchHeadP(@Context HttpServletRequest req) { throw new MethodNotSupportedException("HEAD not supported. Use GET."); }
    
    
    
	@GET
	@Path("/sayhello/{name}")
	@Produces("text/plan")
	public String sayHelloName(@PathParam("name") String name) {
		return "Hello to jersey eng. " + name;
	}
	@GET
	@Path("/sayhello")
	@Produces("text/plain")
	public String sayHello() {
		return "Hello to jersy";
	}
}