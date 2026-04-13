package exceptions;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response; // Fixed Import

public class MethodNotSupportedException extends WebApplicationException{
	
	public MethodNotSupportedException(String message) {
		super(Response.status(Response.Status.METHOD_NOT_ALLOWED)
            .entity(message).type(MediaType.APPLICATION_JSON).build());
    }
}
