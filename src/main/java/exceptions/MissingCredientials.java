package exceptions;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response; // Fixed Import

/*
 * this exception class throws exception when there is missing credientials from user input with 400 bad request status code
 */
	public class MissingCredientials extends WebApplicationException{
		public MissingCredientials(String message) {
			  super(Response.status(Response.Status.BAD_REQUEST)
			            .entity(message).type(MediaType.APPLICATION_JSON).build());
				
			
		}
		
	}

