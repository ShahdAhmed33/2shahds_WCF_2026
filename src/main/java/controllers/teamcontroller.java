package controllers;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.WebApplicationException;
import Model.listclarification;
import edu.csus.ecs.pc2.api.IClarification;
import edu.csus.ecs.pc2.api.ServerConnection;
import edu.csus.ecs.pc2.api.IContest;
import helpers.CookiesHandlers;
import exceptions.MethodNotSupportedException;
import exceptions.PC2ServiceUnavailableException;
import exceptions.UnauthorizedSessionException;

@Path("/team")
public class teamcontroller extends maincontroller {

	//handling the exception from specific to the general in list clarification
    @GET
    @Path("/listClarification")
    @Produces(MediaType.APPLICATION_JSON)
    public Response listClarification(@Context HttpServletRequest req) {
        try {
            // 1. SPECIFIC: Session Validation
            String token = CookiesHandlers.getCookie(req.getCookies(), CookiesHandlers.AUTH_COOKIE_NAME);      
            if (token == null || !CookiesHandlers.verifyTokenSignature(token) || !sessions.containsKey(token)) {
                throw new UnauthorizedSessionException("Not logged in. Session is invalid or expired.");
            }

            ServerConnection userConn = sessions.get(token);
            
            // 2. SPECIFIC: Connection Check
            if (userConn == null) {
                throw new PC2ServiceUnavailableException("The PC2 server connection is lost for this session.");
            }

            IContest contest = userConn.getContest();
            if (contest == null) {
                throw new PC2ServiceUnavailableException("Unable to retrieve contest data.");
            }

            IClarification[] clarifications = contest.getClarifications();
            List<listclarification> result = new ArrayList<>();
            
            for (IClarification clar : clarifications) {
                String status = clar.isAnswered() ? "Answered" : "New";
                String problemName = (clar.getProblem() != null) ? clar.getProblem().getName() : "General";
                
                listclarification li = new listclarification(
                    clar.getTeam().getLoginName(),        
                    clar.getNumber(),                    
                    (int) clar.getSubmissionTime(),      
                    status, 
                    problemName, 
                    clar.getQuestion(),                  
                    clar.getAnswer()                     
                );
                result.add(li);
            }

            return Response.ok(result).build();

        } 
        // --- CATCH BLOCKS: Ordered Specific to General ---
        catch (MethodNotSupportedException e) {
            // Custom 405
            return e.getResponse(); 
        }
        catch (UnauthorizedSessionException e) {
            // Custom 401
            return e.getResponse();
        }
        catch (PC2ServiceUnavailableException e) {
            // Custom 503
            return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                    .entity("error:" + e.getMessage())
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }
        catch (WebApplicationException e) {
            // Other JAX-RS errors
            return Response.fromResponse(e.getResponse())
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }
        catch (Exception e) {
            // 3. GENERAL: Final Fallback
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("System Error:" + e.getLocalizedMessage())
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }
    }

    // --- UNSUPPORTED METHOD CATCHERS (Forces JSON for all HTTP verbs) ---

    @POST @Path("/listClarification") @Produces(MediaType.APPLICATION_JSON)
    public Response catchPostCl(@Context HttpServletRequest req) { 
        throw new MethodNotSupportedException("POST not supported. Use GET."); 
    }

    @PUT @Path("/listClarification") @Produces(MediaType.APPLICATION_JSON)
    public Response catchPutCl(@Context HttpServletRequest req) { 
        throw new MethodNotSupportedException("PUT not supported. Use GET."); 
    }

    @DELETE @Path("/listClarification") @Produces(MediaType.APPLICATION_JSON)
    public Response catchDeleteCl(@Context HttpServletRequest req) { 
        throw new MethodNotSupportedException("DELETE not supported. Use GET."); 
    }

    @PATCH @Path("/listClarification") @Produces(MediaType.APPLICATION_JSON)
    public Response catchPatchCl(@Context HttpServletRequest req) { 
        throw new MethodNotSupportedException("PATCH not supported. Use GET."); 
    }

    @HEAD @Path("/listClarification") @Produces(MediaType.APPLICATION_JSON)
    public Response catchHeadCl(@Context HttpServletRequest req) { 
        throw new MethodNotSupportedException("HEAD not supported. Use GET."); 
    }
}