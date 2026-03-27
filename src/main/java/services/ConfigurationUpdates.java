package services;

import edu.csus.ecs.pc2.api.IContestClock;
import edu.csus.ecs.pc2.api.implementation.Contest;
import edu.csus.ecs.pc2.api.listener.ContestEvent;
import edu.csus.ecs.pc2.api.listener.IConfigurationUpdateListener;
import websocket.ContestSocket;

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
        pushRealPc2Trigger("configurationItemAdded");

	}

	@Override
	public void configurationItemRemoved(ContestEvent arg0) {
		// TODO Auto-generated method stub
		System.out.println("PC2 configuration removed .............");
        pushRealPc2Trigger("configurationItemRemoved");

	}

	@Override
	public void configurationItemUpdated(ContestEvent arg0) {
		// TODO Auto-generated method stub
		System.out.println("PC2 configuration updated .............");
        pushRealPc2Trigger("configurationItemUpdated");

	}
	private void pushRealPc2Trigger(String source) {
		 try {
	            /*
	             * REPLACE THESE WITH THE ACTUAL METHODS AVAILABLE
	             * ON YOUR teamContest OBJECT.
	             */
			    
	            boolean running = teamContest.isContestClockRunning();
		        IContestClock clock = teamContest.getContestClock();

	           // boolean paused = clock.;
	            long remainingSeconds = clock.getRemainingSecs();
	            long elapsedSeconds = clock.getElapsedSecs();
	            long contestLengthSeconds = clock.getContestLengthSecs();

	            String json =
	                "{"
	                    + "\"type\":\"CLOCK_UPDATE\","
	                    + "\"payload\":{"
	                    + "\"source\":\"" + source + "\","
	                    + "\"running\":" + running + ","
	              //      + "\"paused\":" + paused + ","
	                    + "\"remainingSeconds\":" + remainingSeconds + ","
	                    + "\"elapsedSeconds\":" + elapsedSeconds + ","
	                    + "\"contestLengthSeconds\":" + contestLengthSeconds
	                    + "}"
	                    + "}";

	            ContestSocket.broadcast(json);
	            System.out.println("[PC2] Real clock broadcast sent: " + source);
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }
}