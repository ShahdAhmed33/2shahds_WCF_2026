/*package services;

import edu.csus.ecs.pc2.api.IContestClock;
import edu.csus.ecs.pc2.api.implementation.Contest;
import websocket.ClockMessage;

public class ContestClockService {
	   private final Contest contest;

	    public ContestClockService(Contest contest) {
	        this.contest = contest;
	    }

	    public ClockMessage getClockMessage() {        
	        
	         * Replace these placeholders with the real PC2 methods
	         * available in your Contest / IContest object.
	         
	        boolean running = contest.isContestClockRunning();
	        IContestClock clock = contest.getContestClock();

	        boolean paused = false;
	        long remainingSeconds = 0L;
	        long elapsedSeconds = 0L;
	        long contestLengthSeconds = 0L;

	        return new ClockMessage(
	            "clock_update",
	            running,
	            paused,
	            remainingSeconds,
	            elapsedSeconds,
	            contestLengthSeconds
	        );
	    }
} */
