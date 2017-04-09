package lcr;

public class State {
	
	private String name;
	private String enumName;
	private String xmiId;
	private String language;
	private String doSource;
	private String doTarget;
	
	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}
	
	public String getDoSource() {
		return doSource;
	}

	public void setDoSource(String doSource) {
		this.doSource = doSource;
	}

	public String getDoTarget() {
		return doTarget;
	}

	public void setDoTarget(String doTarget) {
		this.doTarget = doTarget;
	}

	public String getXmiId() {
		return xmiId;
	}

	public void setXmiId(String xmiId) {
		this.xmiId = xmiId;
	}

	public String toString() {
		return "State: " + this.getName() + "(" + this.getEnumName() + ")";
	}

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name.substring(0,1).toLowerCase() + name.substring(1);
	}

	public String getEnumName() {
		return enumName;
	}
	
	public void setEnumName(String enumName) {
		this.enumName = enumName.toUpperCase();
	}	
}
