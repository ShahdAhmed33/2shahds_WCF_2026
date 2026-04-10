package services;

import edu.csus.ecs.pc2.api.IClarification;
import edu.csus.ecs.pc2.api.IClarificationEventListener;
import edu.csus.ecs.pc2.api.IProblem;
import edu.csus.ecs.pc2.api.implementation.Contest;
import websocket.ContestSocket;

public class ClarificationUpdates implements IClarificationEventListener {

    private Contest teamContest;
    private String JWT;

    public ClarificationUpdates(Contest teamContest, String JWT) {
        this.teamContest = teamContest;
        this.JWT = JWT;
    }

    @Override
    public void clarificationAdded(IClarification clar) {
        System.out.println("[PC2] NEW clarification arrived!");
        pushClarification(clar, "CLARIFICATION_SUBMITTED");
    }

    @Override
    public void clarificationAnswered(IClarification clar) {
        System.out.println("[PC2] Clarification ANSWERED by judge!");
        pushClarification(clar, "CLARIFICATION_ANSWERED");
    }

    @Override
    public void clarificationUpdated(IClarification clar) {
        System.out.println("[PC2] Clarification UPDATED!");
        pushClarification(clar, "CLARIFICATION_UPDATED");
    }

    @Override
    public void clarificationRemoved(IClarification clar) {
        System.out.println("[PC2] Clarification removed.");
        // nothing to broadcast
    }

    private void pushClarification(IClarification clar, String type) {
        try {
            // ✅ No need to fetch from teamContest — clar is passed directly!
            if (clar == null) return;

            IProblem problem   = clar.getProblem();
            String problemName = (problem != null) ? problem.getName() : "General";
            String question    = clar.getQuestion();
            String answer      = clar.getAnswer();
            boolean answered   = (answer != null && !answer.isEmpty());

            // ✅ Store in cache for REST endpoint
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
    }
}