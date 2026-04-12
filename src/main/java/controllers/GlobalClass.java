package controllers;

import java.util.HashMap;

import edu.csus.ecs.pc2.api.ServerConnection;
import edu.csus.ecs.pc2.api.implementation.Contest;
import services.ConfigurationUpdates;

public class GlobalClass {

	protected static HashMap<String,ServerConnection> Logins = new HashMap<>();


	public GlobalClass() {


	}



	public void addLogin(String c, ServerConnection s) {
		Logins.putIfAbsent(c, s);
		/*
		 * Scenario:
		 * 	put(c,s);		//C: team1, S
		 *  put(c,s1);		//C: team1, s1
		 */
	}


	public HashMap<String,ServerConnection> getLogins() {
		return Logins;
	}

	public ServerConnection getLogin(String c) {
		ServerConnection s = null;
		s = Logins.get(c);
		return s;
	}
	
	protected  void registAllServices(Contest teamContest,String JWT) {
		teamContest.addContestConfigurationUpdateListener(new ConfigurationUpdates(teamContest,JWT));
		
		
	}

}
