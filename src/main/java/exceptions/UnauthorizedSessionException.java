package exceptions;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response; // Fixed Import


public class UnauthorizedSessionException extends WebApplicationException {
	public UnauthorizedSessionException(String message) {
        super(Response.status(Response.Status.UNAUTHORIZED)
            .entity(message).type(MediaType.APPLICATION_JSON).build());
    }
}
