package controllers;

import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Path("/team")
public class TeamController extends GlobalClass{

	public TeamController() {
		super();
	}
	
	@Path("/getruns")
	public Response getAllRuns() {
		Response res = null;
		
		return res;
	}
	
	
}
