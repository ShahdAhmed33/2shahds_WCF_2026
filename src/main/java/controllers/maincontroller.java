package controllers;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import Model.LoginPage;
import Model.LoginResponse;


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
	//we will use get method to retrieve the username and password that the user will enter
	@POST
	
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
	
	//@PathParam("username") String username , @PathParam("password") String password
	public Response loginApi(LoginPage req) {
		String username=req.username;
		String password=req.password;
		
		
		/*
		 -initialized res as null and res data type is Response
		 
		 
		 -this condition will return response code ok as long as the (IF condition) is false so it will execute the else part 
		 -if (IF condition) is true it will return unauthorized status code 401 and message will be USERNAME OR password is not correct
		 - we will create object from class LoginPage (the Model that carry the data) it carries the username and password data
		 -the else part returning response code ok and it will return login object as JSON format 
		 -after comparing we will return the res that carries the information inside the entity class
		*/
		Response res=null;
		if (!"admin".equals(username) || !"admin".equals(password)) {
			res= Response.status(Response.Status.UNAUTHORIZED)
					.entity("USERNAME and password is not correct ")		
					.type(MediaType.APPLICATION_JSON)
					.build();	
		}
		
		else {
			 	
			
			String token="aast";
			LoginResponse login=new LoginResponse(req.username,token);
			res=Response.ok()
					.entity(login)
					.type(MediaType.APPLICATION_JSON)
					.build();
	}
		return res;
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
