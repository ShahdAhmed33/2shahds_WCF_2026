package websocket;

public class ClockMessage {
	private String type;
    private boolean running;
    private boolean paused;
    private long remainingSeconds;
    private long elapsedSeconds;
    private long contestLengthSeconds;

    public ClockMessage() {
    }

    public ClockMessage(
        String type,
        boolean running,
        boolean paused,
        long remainingSeconds,
        long elapsedSeconds,
        long contestLengthSeconds
    ) {
        this.type = type;
        this.running = running;
        this.paused = paused;
        this.remainingSeconds = remainingSeconds;
        this.elapsedSeconds = elapsedSeconds;
        this.contestLengthSeconds = contestLengthSeconds;
    }

    public String getType() {
        return type;
    }

    public boolean isRunning() {
        return running;
    }

    public boolean isPaused() {
        return paused;
    }

    public long getRemainingSeconds() {
        return remainingSeconds;
    }

    public long getElapsedSeconds() {
        return elapsedSeconds;
    }

    public long getContestLengthSeconds() {
        return contestLengthSeconds;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
    }

    public void setRemainingSeconds(long remainingSeconds) {
        this.remainingSeconds = remainingSeconds;
    }

    public void setElapsedSeconds(long elapsedSeconds) {
        this.elapsedSeconds = elapsedSeconds;
    }

    public void setContestLengthSeconds(long contestLengthSeconds) {
        this.contestLengthSeconds = contestLengthSeconds;
    }
    
}
