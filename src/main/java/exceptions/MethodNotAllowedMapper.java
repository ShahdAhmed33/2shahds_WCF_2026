/*package exceptions;

import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.NotAllowedException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.MediaType;

@Provider
public class MethodNotAllowedMapper implements ExceptionMapper<NotAllowedException> {

    @Override
    public Response toResponse(NotAllowedException ex) {

        // Get allowed methods
        String allowed = String.join(", ", ex.getResponse().getAllowedMethods());

        // Extract used method from exception message (fallback)
        String message = ex.getMessage(); // contains something like "HTTP 405 Method Not Allowed"

        return Response.status(Response.Status.METHOD_NOT_ALLOWED)
                .entity("{\"error\": \"" + message + ". Allowed: " + allowed + "\"}")
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}*/