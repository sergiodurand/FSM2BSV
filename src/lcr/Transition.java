package lcr;

public class Transition {
	
	private State from;
	private State to;
	private String condition;
	private String xmiId;

	public String getXmiId() {
		return xmiId;
	}

	public void setXmiId(String xmiId) {
		this.xmiId = xmiId;
	}

	public String toString() {
		return this.getFrom().getName() + " -> " + this.getTo().getName();
	}
	
	public State getFrom() {
		return from;
	}
	
	public void setFrom(State from) {
		this.from = from;
	}
	
	public State getTo() {
		return to;
	}
	
	public void setTo(State to) {
		this.to = to;
	}
	
	public String getCondition() {
		return condition;
	}
	
	public void setCondition(String condition) {
		this.condition = condition;
	}
}
