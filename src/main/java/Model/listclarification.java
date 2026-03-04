package Model;

public class listclarification {
	public String teamname;
	public int ClarificationId;
	public int ClarificationTime;
	public String Status;
	public String ProblemName;
	public String Question;
	public String Answer;
	public listclarification(String teamname, int clarificationId, int clarificationTime, String status,
			String problemName, String question, String answer) {
		this.teamname = teamname;
		ClarificationId = clarificationId;
		ClarificationTime = clarificationTime;
		Status = status;
		ProblemName = problemName;
		Question = question;
		Answer = answer;
	}


}
