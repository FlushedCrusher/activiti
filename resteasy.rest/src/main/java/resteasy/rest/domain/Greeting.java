package resteasy.rest.domain;

public class Greeting {

	private String hook;
	private String message;
	
	public String getHook() {
		return hook;
	}
	public void setHook(String hook) {
		this.hook = hook;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	@Override
	public String toString() {
		return "Greeting [hook=" + hook + ", message=" + message + "]";
	}
	
}
