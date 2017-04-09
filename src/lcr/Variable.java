package lcr;

import lcr.DataType.VariableType;

public class Variable {

	private String name;
	private VariableType type;
	private int length;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public VariableType getType() {
		return type;
	}
	public void setType(VariableType type) {
		this.type = type;
	}
	public int getLength() {
		return length;
	}
	public void setLength(int length) {
		this.length = length;
	}
	
}
