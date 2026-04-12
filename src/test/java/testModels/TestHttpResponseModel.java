package testModels;

public class TestHttpResponseModel {
	private Object status;
	private String message;
	
	public TestHttpResponseModel(Object status, String message) {
		this.setMessage(message);
		this.setStatus(status);
	}
	
	public Object getStatus() {
		return status;
	}
	
	public void setStatus(Object status) {
		this.status = status;
	}
	
	public String getMessage() {
		return message;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}
	
	
}
