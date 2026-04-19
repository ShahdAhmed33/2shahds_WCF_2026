package services;

import edu.csus.ecs.pc2.api.IClarification;
import edu.csus.ecs.pc2.api.IContestClock;
import edu.csus.ecs.pc2.api.IProblem;
import edu.csus.ecs.pc2.api.implementation.Contest;
import edu.csus.ecs.pc2.api.listener.ContestEvent;
import edu.csus.ecs.pc2.api.listener.IConfigurationUpdateListener;
import websocket.ContestSocket;

public class ConfigurationUpdates implements IConfigurationUpdateListener {
        private static Contest teamContest;
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
      //  pushRealPc2TriggerClarification(arg0, "CLARIFICATION_SUBMITTED");

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
      //  pushRealPc2TriggerClarification(arg0, "CLARIFICATION_ANSWERED");   // <-- called here

        }
        public static void pushRealPc2Trigger(String source) {
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
        
        
        
    /*    private void pushRealPc2TriggerClarification(ContestEvent event, String type) {
            try {
                IClarification[] clarifications = teamContest.getClarifications();
                if (clarifications == null || clarifications.length == 0) return;

                IClarification clar = clarifications[clarifications.length - 1];
                if (clar == null) return;

                IProblem problem   = event.getProblem();
                String problemName = (problem != null) ? problem.getName() : "General";
                String question    = clar.getQuestion();
                String answer      = clar.getAnswer();
                boolean answered   = (answer != null && !answer.isEmpty());

                // ✅ Save to cache so REST endpoint can serve it
                ClarificationCache.store(
                    clar.getNumber(),
                    problemName,
                    question,
                    answer,
                    answered
                );

                String json =
                    "{"
                        + "\"type\":\"" + type + "\","
                        + "\"payload\":{"
                        + "\"problem\":\"" + escapeJson(problemName) + "\","
                        + "\"question\":\"" + escapeJson(question) + "\","
                        + "\"answered\":" + answered + ","
                        + "\"answer\":\"" + escapeJson(answered ? answer : "") + "\""
                        + "}"
                        + "}";

                ContestSocket.broadcast(json);
                System.out.println("[PC2] Clarification broadcast sent: " + type);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        private String escapeJson(String value) {
            if (value == null) return "";
            return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
        }*/
        
        
        
}