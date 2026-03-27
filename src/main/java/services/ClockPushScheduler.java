/*package services;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import websocket.ClockMessage;

public class ClockPushScheduler {
    private final ContestClockService contestClockService;
    private final ScheduledExecutorService executorService;

    public ClockPushScheduler(ContestClockService contestClockService) {
        this.contestClockService = contestClockService;
        this.executorService = Executors.newSingleThreadScheduledExecutor();
    }

    public void start() {
        executorService.scheduleAtFixedRate(() -> {
            try {
                ClockMessage message = contestClockService.getClockMessage();
                ClockBroadcaster.broadcastClock(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 0, 1, TimeUnit.SECONDS);
    }

    public void stop() {
        executorService.shutdownNow();
    }
}*/