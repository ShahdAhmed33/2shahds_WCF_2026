package controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.WebApplicationException;
import Model.listclarification;
import edu.csus.ecs.pc2.api.IClarification;
import edu.csus.ecs.pc2.api.IClient;
import edu.csus.ecs.pc2.api.ServerConnection;
import edu.csus.ecs.pc2.core.model.Clarification;
import edu.csus.ecs.pc2.api.IContest;
import edu.csus.ecs.pc2.api.IContestClock;
import edu.csus.ecs.pc2.api.IProblem;
import helpers.CookiesHandlers;
import services.ClarificationCache;
import exceptions.MethodNotSupportedException;
import exceptions.PC2ServiceUnavailableException;
import exceptions.UnauthorizedSessionException;
import exceptions.NoCookieException;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.ContestInformation;
import edu.csus.ecs.pc2.core.model.ContestTime;
@Path("/team")
public class teamcontroller extends maincontroller {

    @GET
    @Path("/listClarification")
    @Produces(MediaType.APPLICATION_JSON)
    public Response listClarification(@Context HttpServletRequest req) {
        try {
            // 1. Extract and Validate Token from Cookie
            String token = CookiesHandlers.getCookie(req.getCookies(), CookiesHandlers.AUTH_COOKIE_NAME);
            
            // Check if token is cryptographically valid AND exists in our active sessions map
            if (!isValidToken(token) || !sessions.containsKey(token)) {
                throw new UnauthorizedSessionException("Not logged in. Session is invalid or expired.");
            }

            ServerConnection userConn = sessions.get(token);
            
            // 2. Validate Server Connection
            if (userConn == null || !userConn.isLoggedIn()) {
                throw new PC2ServiceUnavailableException("The PC2 server connection is lost for this session.");
            }

            IContest contest = userConn.getContest();
            if (contest == null) {
                throw new PC2ServiceUnavailableException("Unable to retrieve contest data.");
            }

            // 3. Process Clarifications
            IClarification[] clarifications = contest.getClarifications();
            List<listclarification> result = new ArrayList<>();
            
            if (clarifications != null) {
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
            }
            
            
            // 2. ✅ Read directly from cache instead of asking PC2
           /* List<ClarificationCache.ClarificationEntry> cached = ClarificationCache.getAll();

            System.out.println("[DEBUG] Clarifications in cache: " + cached.size());

            // 3. Map cache entries to your model
            List<listclarification> result = new ArrayList<>();
            for (ClarificationCache.ClarificationEntry entry : cached) {
                listclarification li = new listclarification(
                    "",                  // team login name (not available from event)
                    entry.number,
                    0,                   // submission time (not available from event)
                    entry.answered ? "Answered" : "New",
                    entry.problem,
                    entry.question,
                    entry.answer
                );
                result.add(li);
            }*/

            return Response.ok(result).build();

        } 
        // --- CATCH BLOCKS: Specific to General ---
        catch (NoCookieException | UnauthorizedSessionException e) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}")
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }
        catch (PC2ServiceUnavailableException e) {
            return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}")
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }
        catch (MethodNotSupportedException e) {
            return e.getResponse(); // Assuming this returns a properly built JSON Response
        }
        catch (Exception e) {
            // Final fallback to prevent HTML 500 error pages
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"System Error: " + e.getLocalizedMessage() + "\"}")
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }
    }
    @POST
    @Path("/submitClarification")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response submitClarification(@Context HttpServletRequest req, Map<String, String> payload) {
        try {
            // 1. Extract and Validate Token
            String token = CookiesHandlers.getCookie(req.getCookies(), CookiesHandlers.AUTH_COOKIE_NAME);
            
            if (token == null || !isValidToken(token) || !sessions.containsKey(token)) {
                throw new UnauthorizedSessionException("Not logged in. Session is invalid or expired.");
            }

            ServerConnection userConn = sessions.get(token);
            
            // 2. Validate Server Connection
            if (userConn == null || !userConn.isLoggedIn()) {
                throw new PC2ServiceUnavailableException("The PC2 server connection is lost for this session.");
            }

            IContest contest = userConn.getContest();
            if (contest == null) {
                throw new PC2ServiceUnavailableException("Unable to retrieve contest data.");
            }

            // --- FIX FOR ISSUE: Verify Contest State ---
            // This prevents "General" or problem-specific clarifications 
            // from being submitted if the contest is stopped or paused.
            IContestClock contestClock = contest.getContestClock();
            if (contestClock == null || !contestClock.isContestClockRunning()) {
                return Response.status(Response.Status.FORBIDDEN)
                        .entity("{\"error\": \"Clarifications cannot be submitted while the contest is stopped or paused.\"}")
                        .build();
            }
            // --------------------------------------------

            // 3. Extract Payload Data
            String problemName = payload.getOrDefault("ProblemName", "").trim();
            String questionText = payload.getOrDefault("Question", "");

            if (questionText.isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"error\": \"Question text cannot be empty.\"}")
                        .build();
            }

            IProblem selectedProblem = null;

            // 4. Search in standard Problems
            IProblem[] problems = contest.getProblems();
            if (problems != null) {
                for (IProblem prob : problems) {
                    if (prob.getName().trim().equalsIgnoreCase(problemName)) {
                        selectedProblem = prob;
                        break;
                    }
                }
            }

            // 5. Search in Clarification Categories (e.g., "General")
            // This is the specific area where "General" clarifications were slipping through
            if (selectedProblem == null) {
                IProblem[] categories = contest.getClarificationCategories();
                if (categories != null) {
                    for (IProblem cat : categories) {
                        if (cat.getName().trim().equalsIgnoreCase(problemName)) {
                            selectedProblem = cat;
                            break;
                        }
                    }
                }
            }

            // 6. Final Validation and Submission
            if (selectedProblem == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"error\": \"Category/Problem '" + problemName + "' not found.\"}")
                        .build();
            }

            // Final submission call to the PC2 Server
            userConn.submitClarification(selectedProblem, questionText);

            return Response.ok("{\"status\": \"Success\", \"message\": \"Clarification submitted\"}")
                           .build();

        } catch (UnauthorizedSessionException e) {
            return Response.status(Response.Status.UNAUTHORIZED)
                           .entity("{\"error\": \"" + e.getMessage() + "\"}")
                           .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                           .entity("{\"error\": \"An unexpected error occurred: " + e.getMessage() + "\"}")
                           .build();
        }
    }
    // --- UNSUPPORTED METHOD CATCHERS ---
    // These ensure that if a user tries POST/PUT on a GET endpoint, they get a JSON error

    @POST @Path("/listClarification") @Produces(MediaType.APPLICATION_JSON)
    public Response catchPostCl() { throw new MethodNotSupportedException("POST not supported. Use GET."); }

    @PUT @Path("/listClarification") @Produces(MediaType.APPLICATION_JSON)
    public Response catchPutCl() { throw new MethodNotSupportedException("PUT not supported. Use GET."); }

    @DELETE @Path("/listClarification") @Produces(MediaType.APPLICATION_JSON)
    public Response catchDeleteCl() { throw new MethodNotSupportedException("DELETE not supported. Use GET."); }
    @GET
    @Path("/submitClarification")
    @Produces(MediaType.APPLICATION_JSON)
    public Response catchGetSubmit() { 
        throw new MethodNotSupportedException("GET not supported for submission. Use POST."); 
    }

    @PUT
    @Path("/submitClarification")
    @Produces(MediaType.APPLICATION_JSON)
    public Response catchPutSubmit() { 
        throw new MethodNotSupportedException("PUT not supported for submission. Use POST."); 
    }

    @DELETE
    @Path("/submitClarification")
    @Produces(MediaType.APPLICATION_JSON)
    public Response catchDeleteSubmit() { 
        throw new MethodNotSupportedException("DELETE not supported for submission. Use POST."); 
    }
}