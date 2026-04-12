package models;

public class LanguageModel {
	public String CompilerCommandLine;
	public String ExecutableMask;
	public String ExecutionCommandLine;
	public String Name;
	public String Title;
	public boolean isInterpreed;
	
	
	public LanguageModel() {}
	
	public LanguageModel(String CompilerCommandLine,String ExecutableMask,String ExecutionCommandLine,String Name,String Title,boolean isInterpreed) {
		this.CompilerCommandLine = CompilerCommandLine;
		this.ExecutableMask = ExecutableMask;
		this.ExecutionCommandLine = ExecutionCommandLine;
		this.Name = Name;
		this.Title = Title;
		this.isInterpreed = isInterpreed;	
	}
	
	public String getCompilerCommandLine() { return this.CompilerCommandLine; }
	public String getExecutionMask() { return this.ExecutableMask;}
	public String getExecutionCommandLine() { return this.ExecutionCommandLine;}
	public String getName() { return this.Name;}
	public String getTitle() { return this.Title;}
	public boolean getIsInterpreted() { return this.isInterpreed;}
}
