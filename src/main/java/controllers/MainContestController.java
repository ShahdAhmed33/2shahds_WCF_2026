package controllers;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Singleton;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;

import edu.csus.ecs.pc2.api.ServerConnection;
import edu.csus.ecs.pc2.api.exceptions.LoginFailureException;
import edu.csus.ecs.pc2.api.exceptions.NotLoggedInException;
import edu.csus.ecs.pc2.core.transport.TransportException;
import exceptions.NoCookieException;
import exceptions.NoCookiesExceptions;
import exceptions.NoHttpRequestExcepion;
import exceptions.NoServerConnectionException;
import exceptions.NotLoggedIn;
import exceptions.NotVerifiedCookieException;
import helps.Checkers;
import helps.CookiesOps;
import helps.HttpResponseStatusEnums;
import models.ErrorResponseModel;
import models.LanguageModel;
import models.LoginRequestModel;
import models.LoginResponseModel;
import edu.csus.ecs.pc2.api.IContest;
import edu.csus.ecs.pc2.api.ILanguage;



@Singleton
@Path("/contest")
public class MainContestController extends GlobalClass {

	public MainContestController() {
		super();
	
	}

	@GET
	@Path("/listlanguages")
	@Produces(MediaType.APPLICATION_JSON)
	public Response ListLanguages(final @Context HttpServletRequest req) {
		Response res = null;
		
		try {
			Checkers.authHeader(req);
			Checkers.checkCookies(req.getCookies());		
			Cookie[] myCookie = req.getCookies();
			String CookieID = CookiesOps.getCookieValue(myCookie, "awt-jwt");
			CookiesOps.verifyCookie(CookieID);
			
			ServerConnection sc = getLogin(CookieID);
			Checkers.isConnectionExist(sc);
			Checkers.isConnected(sc);
			
			IContest contest = sc.getContest();
			List<LanguageModel> lms = new ArrayList<LanguageModel>();
			
			for ( ILanguage lang: contest.getLanguages()) {
				LanguageModel lm = new LanguageModel(lang.getCompilerCommandLine(),lang.getExecutableMask(),lang.getExecutionCommandLine(),lang.getName(),lang.getTitle(),lang.isInterpreted());
				lms.add(lm);
			}
			res= Response.ok().entity(lms).type(MediaType.APPLICATION_JSON).build();	
			return res;
		} catch (RuntimeException e) {
			e.printStackTrace();
			res = Response.status(Response.Status.UNAUTHORIZED)
					.entity(new ErrorResponseModel(Response.Status.UNAUTHORIZED,e.getMessage()))
					.type(MediaType.APPLICATION_JSON).build();
			return res;
		} catch (NoHttpRequestExcepion    | NoServerConnectionException | NotLoggedIn | NotLoggedInException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			res = Response.status(Response.Status.UNAUTHORIZED)
					.entity(new ErrorResponseModel(Response.Status.UNAUTHORIZED,e.getMessage()))
					.type(MediaType.APPLICATION_JSON).build();
			return res;
		} catch (NotVerifiedCookieException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			res = Response.status(HttpResponseStatusEnums.NOTVALIDCOOKIEID)
					.entity(new ErrorResponseModel(HttpResponseStatusEnums.NOTVALIDCOOKIEID,e.getMessage()))
					.type(MediaType.APPLICATION_JSON).build();
			return res;
		} catch (NoCookiesExceptions e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			res = Response.status(HttpResponseStatusEnums.NOCOOCKIESFOUND)
					.entity(new ErrorResponseModel(HttpResponseStatusEnums.NOCOOCKIESFOUND,e.getMessage()))
					.type(MediaType.APPLICATION_JSON).build();
			return res;
		} catch (NoCookieException e) {
			e.printStackTrace();
			res = Response.status(HttpResponseStatusEnums.NOCOOKIEFIELDFOUND)
					.entity(new ErrorResponseModel(HttpResponseStatusEnums.NOCOOKIEFIELDFOUND,e.getMessage()))
					.type(MediaType.APPLICATION_JSON).build();
			return res;
		}
	}
	
	/*
	@GET
	@Path("/listlanguages")
	@Produces(MediaType.APPLICATION_JSON)
	public Response ListLanguages(final @Context HttpServletRequest req) {
		Response res = null;
		
		try {
			Checkers.authHeader(req);
			Checkers.checkCookies(req.getCookies());		
			Cookie[] myCookie = req.getCookies();
			String CookieID = CookiesOps.getCookieValue(myCookie, "awt-jwt");
			CookiesOps.verifyCookie(CookieID);
			
			ServerConnection sc = getLogin(CookieID);
			Checkers.isConnectionExist(sc);
			Checkers.isConnected(sc);
			
			IContest contest = sc.getContest();
			List<LanguageModel> lms = new ArrayList<LanguageModel>();
			
			for ( ILanguage lang: contest.getLanguages()) {
				LanguageModel lm = new LanguageModel(lang.getCompilerCommandLine(),lang.getExecutableMask(),lang.getExecutionCommandLine(),lang.getName(),lang.getTitle(),lang.isInterpreted());
				lms.add(lm);
			}
			res= Response.ok().entity(lms).type(MediaType.APPLICATION_JSON).build();	
			return res;
		} catch (NoHttpRequestExcepion | NoCookiesExceptions | NoCookieException | NotVerifiedCookieException | NoServerConnectionException | NotLoggedIn | NotLoggedInException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			res = Response.status(Response.Status.UNAUTHORIZED)
					.entity(new ErrorResponseModel(Response.Status.UNAUTHORIZED,e.getMessage()))
					.type(MediaType.APPLICATION_JSON).build();
			return res;
		}
	}
	*/
	
	/*
	@GET
	@Path("/listlanguages")
	@Produces(MediaType.APPLICATION_JSON)
	public Response ListLanguages(final @Context HttpServletRequest req) throws NotLoggedInException {
		Response res = null;
		
		if ( req == null ) { 	//No request sent
			res = Response.status(Response.Status.UNAUTHORIZED)
					.entity(new ErrorResponseModel(Response.Status.UNAUTHORIZED,"Missing authentication header"))
					.type(MediaType.APPLICATION_JSON).build();
			return res;
		}
		
		
		if ( req.getCookies() == null ) {
			res = Response.status(Response.Status.UNAUTHORIZED)
					.entity(new ErrorResponseModel(Response.Status.UNAUTHORIZED,"No cookies, enable it"))
					.type(MediaType.APPLICATION_JSON).build();
			return res;
		}
		
		Cookie[] myCookie = req.getCookies();
		String CookieID = CookiesOps.getCookieValue(myCookie, "awt-jwt");
		if (  CookieID == null) {
			res = Response.status(Response.Status.UNAUTHORIZED)
					.entity(new ErrorResponseModel(Response.Status.UNAUTHORIZED,"Cookie not found"))
					.type(MediaType.APPLICATION_JSON).build();
			return res;
		}
		
		if ( ! CookiesOps.verifyCookie(CookieID) ) {
			res = Response.status(Response.Status.UNAUTHORIZED)
					.entity(new ErrorResponseModel(Response.Status.UNAUTHORIZED,"Not valid cookie"))
					.type(MediaType.APPLICATION_JSON).build();
			return res;
		}
		
		
		ServerConnection sc = getLogin(CookieID);
		
		if ( sc == null ) {
			res = Response.status(Response.Status.UNAUTHORIZED)
					.entity(new ErrorResponseModel(Response.Status.UNAUTHORIZED,"Server connection not found"))
					.type(MediaType.APPLICATION_JSON).build();
			return res;
		}
		
		
		if ( sc.isLoggedIn() == false ) {
			res = Response.status(Response.Status.UNAUTHORIZED)
					.entity(new ErrorResponseModel(Response.Status.UNAUTHORIZED,"User not logged in to PC2"))
					.type(MediaType.APPLICATION_JSON).build();
			return res;
		}
		
		IContest contest = sc.getContest();
		List<LanguageModel> lms = new ArrayList<LanguageModel>();
		
		for ( ILanguage lang: contest.getLanguages()) {
			LanguageModel lm = new LanguageModel(lang.getCompilerCommandLine(),lang.getExecutableMask(),lang.getExecutionCommandLine(),lang.getName(),lang.getTitle(),lang.isInterpreted());
			lms.add(lm);
		}
		res= Response.ok().entity(lms).type(MediaType.APPLICATION_JSON).build();	
		return res;
	}
	*/
	
	
	//API Endpoint recieves LoginRequestModel, authenticates and return LoginResponseModel
	@POST
	@Path("/login")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response login(LoginRequestModel lr) {
		Response res = null;
		
		if ( lr == null ) { 	//No request snt

			res = Response.status(Response.Status.UNSUPPORTED_MEDIA_TYPE)
					.entity(new ErrorResponseModel(Response.Status.UNSUPPORTED_MEDIA_TYPE,"Missing authentication header"))
					.type(MediaType.APPLICATION_JSON).build();
			return res;
		
		}
		
		if ( lr.getUsername() == null || lr.getPassword() == null) {
		
			res = Response.status(Response.Status.UNAUTHORIZED)
					.entity(new ErrorResponseModel(Response.Status.UNAUTHORIZED,"Invalid username/password"))
					.type(MediaType.APPLICATION_JSON).build();
			return res;
		
		}
		
		//Start PC2 Authentication
		ServerConnection teamConnection = new ServerConnection();
		

			try {
				teamConnection.login(lr.getUsername(),lr.getPassword());
				String CookieID;
				CookieID = CookiesOps.genCookieID("team",lr.getUsername(),lr.getUsername());
				NewCookie jwtCookie;
				jwtCookie = CookiesOps.createCookie(CookieID);
				//Add the connection to the hashmap defined in the super class GlobalClass
				addLogin(CookieID, teamConnection);
				LoginResponseModel lres = new LoginResponseModel(CookieID , lr.getUsername());
				registAllServices(teamConnection.getContest(),CookieID);
				res = Response.status(Response.Status.OK)
						.entity(lres)
						.cookie(jwtCookie)
						.type(MediaType.APPLICATION_JSON).build();

				return res;
			
			}catch (LoginFailureException | NotLoggedInException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				res = Response.status(Response.Status.GATEWAY_TIMEOUT)
						.entity(new ErrorResponseModel(Response.Status.GATEWAY_TIMEOUT,e.getMessage()))
						.type(MediaType.APPLICATION_JSON).build();

				return res;
			}  

			
	

	}

}
