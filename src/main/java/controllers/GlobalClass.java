package controllers;

import edu.csus.ecs.pc2.api.implementation.Contest;
import services.ConfigurationUpdates;

public class GlobalClass {
	protected void registAllServices(Contest teamContest, String JWT) {
	    teamContest.addContestConfigurationUpdateListener(new ConfigurationUpdates(teamContest, JWT));
	}
}