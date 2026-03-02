package exceptions;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class LanguageNotFoundException extends WebApplicationException {

    public LanguageNotFoundException(String message) {
        super(Response.status(Response.Status.NOT_FOUND)
            .entity(message)
            .type(MediaType.APPLICATION_JSON)
            .build());
    }
}