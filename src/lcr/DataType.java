package lcr;

import com.mycila.xmltool.XMLDocumentException;
import com.mycila.xmltool.XMLTag;

public class DataType {
	
	private String name;
	private String bsvType;
	private Type type;
	private String xmiId;
	private int size;
	private DataType dataType;

	public String getBsvType() {
		return bsvType;
	}

	public void setBsvType(String bsvType) {
		this.bsvType = bsvType;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public String getXmiId() {
		return xmiId;
	}

	public void setXmiId(String xmiId) {
		this.xmiId = xmiId;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public DataType getDataType() {
		return dataType;
	}

	public void setDataType(DataType dataType) {
		this.dataType = dataType;
	}

	public enum Type {
		PRIMITIVE, VECTOR
	}
	
	public enum SignalType {
		BIT, UINT, INT  // Bool, int and bit will be implemented later.
	}

	public enum VariableType {
		INTEGER, STRING
	}
	
	public String toString() {
		if (this.type == Type.PRIMITIVE)
			return this.bsvType + "#(" + this.size + ")";
		else if (this.type == Type.VECTOR)
			return this.bsvType + "#(" + this.size + ", " + this.getDataType().toString()+")";
		else
			return "";
	}

	public String toStringReg() {
		if (this.type == Type.PRIMITIVE)
			return "Reg#(" + this.bsvType + "#(" + this.size + "))";
		else if (this.type == Type.VECTOR)
			return this.bsvType + "#(" + this.size + ", " + this.getDataType().toStringReg()+")";
		else
			return "";
	}
	
	public static void findPrimitiveTypes(XMLTag doc, FSM fsm) {
		try {
			if (doc.getAttribute("xmi:type").equals("uml:PrimitiveType")) {
				String name = doc.getAttribute("name");
				if (name.contains("#")) {
					String[] parts = name.split("#");
					DataType d = new DataType();
					d.setName(name);
					d.setBsvType(parts[0]);
					d.setType(Type.PRIMITIVE);
					d.setXmiId(doc.getAttribute("xmi:id"));
					d.setSize(Integer.parseInt(parts[1]));
					d.setDataType(null);
					fsm.getDataTypes().add(d);
					//System.out.println("MSG: Primitive type found: " + d.toString());					
				} else
					System.out.println("WRN: Type not supported: " + name);
			}			
		} catch (XMLDocumentException e) {/* No problem... let it go */}

		for (int i=1; i <= doc.getChildCount(); i++) {
			findPrimitiveTypes(doc.gotoChild(i), fsm);
			doc.gotoParent();
		}
	}
	
	public static DataType FindDataTypeByXmiId(FSM fsm, String xmiid) {
		for (DataType dt : fsm.getDataTypes()) {
			if (dt.getXmiId().equals(xmiid)) return dt;
		}
		System.out.println("ERR: Type not found");
		return null;
	}
	
	public static void findDataTypes(XMLTag doc, FSM fsm) {
		try {
			if (doc.getAttribute("xmi:type").equals("uml:DataType")) {
				DataType d = new DataType();
				d.setName(doc.getAttribute("name"));
				d.setBsvType("Vector");
				d.setType(Type.VECTOR);
				d.setXmiId(doc.getAttribute("xmi:id"));
				if (doc.getChildCount() == 2) {
					for (int i=1; i <= doc.getChildCount(); i++) {
						doc.gotoChild(i);
						if (doc.getAttribute("name").equals("size")) {
							doc.gotoChild();
							d.setSize(Integer.parseInt(doc.getAttribute("value")));
							doc.gotoParent();
						} else if (doc.getAttribute("name").equals("type")) d.setDataType(FindDataTypeByXmiId(fsm, doc.getAttribute("type")));
						else System.err.println("ERR: Missing parameters for [" + doc.getAttribute("name") + "] Vector");
						doc.gotoParent();
					}
				} else
					System.err.println("ERR: Missing parameters for [" + doc.getAttribute("name") + "] Vector");
				fsm.getDataTypes().add(d);
				//System.out.println("MSG: Vector type found: " + d.toString());
			}			
		} catch (XMLDocumentException e) {/* No problem... let it go */}

		for (int i=1; i <= doc.getChildCount(); i++) {
			findDataTypes(doc.gotoChild(i), fsm);
			doc.gotoParent();
		}		
	}

}