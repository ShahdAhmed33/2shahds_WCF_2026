package exceptions;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response; // Fixed Import


public class PC2ServiceUnavailableException extends WebApplicationException {
	public PC2ServiceUnavailableException(String message) {
        super(Response.status(Response.Status.SERVICE_UNAVAILABLE)
            .entity(message).type(MediaType.APPLICATION_JSON).build());
    }
}
