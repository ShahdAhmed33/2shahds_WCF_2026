package services;

import edu.csus.ecs.pc2.api.implementation.Contest;
import edu.csus.ecs.pc2.api.listener.ContestEvent;
import edu.csus.ecs.pc2.api.listener.IConfigurationUpdateListener;

public class ConfigurationUpdates implements IConfigurationUpdateListener {
	private Contest teamContest;
	private String JWT;
   

	public ConfigurationUpdates(Contest teamContest, String jWT) {
		super();
		this.teamContest = teamContest;
		JWT = jWT;
	}

	

	@Override
	public void configurationItemAdded(ContestEvent arg0) {
		// TODO Auto-generated method stub
		System.out.println("PC2 configuration added .............");
	}

	@Override
	public void configurationItemRemoved(ContestEvent arg0) {
		// TODO Auto-generated method stub
		System.out.println("PC2 configuration removed .............");
	}

	@Override
	public void configurationItemUpdated(ContestEvent arg0) {
		// TODO Auto-generated method stub
		System.out.println("PC2 configuration updated .............");
	}

}