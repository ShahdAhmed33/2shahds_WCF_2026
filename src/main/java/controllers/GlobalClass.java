package controllers;

import edu.csus.ecs.pc2.api.implementation.Contest;
import services.ClarificationUpdates;
import services.ConfigurationUpdates;
public class GlobalClass {
	protected void registAllServices(Contest teamContest, String JWT) {
	    System.out.println(">>> REGISTERING PC2 LISTENERS NOW for JWT: " + JWT);
	    teamContest.addContestConfigurationUpdateListener(new ConfigurationUpdates(teamContest, JWT));
	    teamContest.addClarificationListener(new ClarificationUpdates(teamContest, JWT));

}
	}