package controllers;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import Model.LoginPage;
import Model.LoginResponse;
import Model.languageList;
import edu.csus.ecs.pc2.api.IContest;
import edu.csus.ecs.pc2.api.ILanguage;
import edu.csus.ecs.pc2.api.ServerConnection;
import edu.csus.ecs.pc2.api.exceptions.LoginFailureException;
import edu.csus.ecs.pc2.api.exceptions.NotLoggedInException;
import helpers.CookiesHandlers;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.util.HashMap;

@Path("/main")
@Tag(name = "Main", description = "Main controller endpoints")

public class maincontroller {
	  private static Map<String, ServerConnection> sessions =
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
	
	//@PathParam("username") String username , @PathParam("password") String password
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

			String username=req.username;
			String password=req.password; 	
			
	
		
		try {
			 ServerConnection serverconnection = new ServerConnection();
			 serverconnection.login(req.username, req.password);
			 IContest contest = serverconnection.getContest();
			// Generate a unique token for this specific user session
			 String token = UUID.randomUUID().toString();
			 sessions.put(token, serverconnection);
			// USE HELPER: Create the cookie
			 NewCookie jwtCookie = CookiesHandlers.createAuthCookie(token);
			 LoginResponse loginRes = new LoginResponse(req.username, token);
	            return Response.ok(loginRes)
	                           .cookie(jwtCookie)
	                           .type(MediaType.APPLICATION_JSON)
	                           .build();
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
	  
	
   	   ServerConnection serverconnection = new ServerConnection();

	    @GET
	    @Path("/listlanguages")
	    @Produces(MediaType.APPLICATION_JSON)
	    
	    public Response listlanguages(final @Context HttpServletRequest req) {
			 Response res=null;
			// USE HELPER: Get token from request cookies
			 String token = CookiesHandlers.getCookie(req.getCookies(), CookiesHandlers.AUTH_COOKIE_NAME);			
			// Validate session
	        if (serverconnection == null || token == null || !sessions.containsKey(token)) {
	             res=Response.status(Response.Status.UNAUTHORIZED)
	                    .entity("Not logged in")
	                    .type(MediaType.APPLICATION_JSON)
	                    .build();
	            return res;
	        }
	        if(req.getCookies()==null ) {
	        	res=Response.status(Response.Status.UNAUTHORIZED)
	        				.entity("Not logged in")
	        				.type(MediaType.APPLICATION_JSON)
	        				.build();
	        	return res;
	        }
	        if(CookiesHandlers.getCookie(req.getCookies(),"awt_jwt")== null){
	        	res=Response.status(Response.Status.UNAUTHORIZED)
    				.entity("Not logged in")
    				.type(MediaType.APPLICATION_JSON)
    				.build();
    	return res;
	        	
	        }
	     // Get the SPECIFIC connection for this user from the map
	        ServerConnection userConn = sessions.get(token);
	        
	        
	        try {

	            IContest contest = userConn.getContest();
	            List<languageList> result = new ArrayList<languageList>();
	            for (ILanguage lang : contest.getLanguages()) {
	                   languageList li=new languageList(lang.getName(),lang.getTitle());
	                   result.add(li);
	            }
	            

	            return Response.ok(result).build();

	        } catch (Exception e) {

	            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
	                    .entity("Failed to fetch languages from PC2")
	                    .build();
	        }
	    }
	
	
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
