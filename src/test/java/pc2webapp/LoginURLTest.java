package pc2webapp;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.URISyntaxException;
import org.junit.Before;
import org.junit.Test;

import com.google.gson.JsonSyntaxException;

import testModels.TestLoginRequestModel;
import testUtils.Constants;
import testUtils.HttpConnect;
/*
 * 
 * 1-Missing JSON request in login, fix to return status code 415 "Invalid media"
 * 2-Any connection with "Unable to contact server" returns status code 504, means Bad gateway
 */
public class LoginURLTest {
	private String LOGIN_URL;
	
	@Before
	public void InitTest() {
		
		this.LOGIN_URL =  Constants.SERVER_URL+Constants.LOGIN_URL;
		//System.out.println("Testing APIs at " + Constants.SERVER_URL);
	}

	
	@Test
	public void testLoginGet() throws URISyntaxException, IOException, InterruptedException, JsonSyntaxException {
		
			//Test wrong method, using any other method than post
			//The http returned status is 405 "Method not allowed"
			assertEquals(405,HttpConnect.doConnectGet(this.LOGIN_URL).statusCode());

	}
	
	
	@Test
	public void testLoginNoJSON() throws URISyntaxException, IOException, InterruptedException, JsonSyntaxException {
		
			//Test json format for login, by setting the request model to null
			TestLoginRequestModel LoginReq = null;
			
			assertEquals(401,HttpConnect.doConnectPost(this.LOGIN_URL, LoginReq).statusCode());
	}
	
	
	@Test
	public void testLoginInvalidLogin() throws URISyntaxException, IOException, InterruptedException, JsonSyntaxException {
		
			//Test login with dummy account
			TestLoginRequestModel LoginReq = new TestLoginRequestModel("dummyuser","dummypass");
			
			assertEquals(401,HttpConnect.doConnectPost(this.LOGIN_URL, LoginReq).statusCode());
	}
	
	@Test
	public void testLoginValidLogin() throws URISyntaxException, IOException, InterruptedException, JsonSyntaxException {
		
			//Test login with real account
			TestLoginRequestModel LoginReq = new TestLoginRequestModel("team1001","^!2HQJNQC");
			
			assertEquals(200,HttpConnect.doConnectPost(this.LOGIN_URL, LoginReq).statusCode());
	}

	@Test
	public void testLoginConnectError() throws URISyntaxException, IOException, InterruptedException, JsonSyntaxException {
		
			//Test login with dummy account, but pc2 server is not running/connection problem
			TestLoginRequestModel LoginReq = new TestLoginRequestModel("dummuser","dummypassword");
			
			assertEquals(504,HttpConnect.doConnectPost(this.LOGIN_URL, LoginReq).statusCode());
	}
	
	
	
	

}
