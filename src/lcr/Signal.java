package lcr;

public class Signal {

	private String name;
	//private SignalType type;
	//private int length;
	private Direction direction;
	private String declaration;
	private DataType dataType;

	public DataType getDataType() {
		return dataType;
	}
	public void setDataType(DataType dataType) {
		this.dataType = dataType;
	}
	public String getDeclaration() {
		return declaration;
	}
	public void setDeclaration(String declaration) {
		this.declaration = declaration;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		//this.name = "v_" + name;
		this.name = name;
	}
	/*public SignalType getType() {
		return type;
	}
	public void setType(SignalType type) {
		this.type = type;
	}
	public int getLength() {
		return length;
	}
	public void setLength(int length) {
		this.length = length;
	}*/
	public Direction getDirection() {
		return direction;
	}
	public void setDirection(Direction direction) {
		this.direction = direction;
	}

}
