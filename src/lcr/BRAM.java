package lcr;

import com.mycila.xmltool.XMLDocumentException;
import com.mycila.xmltool.XMLTag;

public class BRAM {
	
	public enum BRAMFormat {
		None, Hex, Binary
	}

	private String name;
	private DataType addrWidth;
	private DataType dataWidth;
	private int dualPort;
	private BRAMFormat format;
	private String xmiId;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public DataType getAddrWidth() {
		return addrWidth;
	}

	public void setAddrWidth(DataType addrWidth) {
		this.addrWidth = addrWidth;
	}

	public DataType getDataWidth() {
		return dataWidth;
	}

	public void setDataWidth(DataType dataWidth) {
		this.dataWidth = dataWidth;
	}

	public int getDualPort() {
		return dualPort;
	}

	public void setDualPort(int dualPort) {
		this.dualPort = dualPort;
	}

	public BRAMFormat getFormat() {
		return format;
	}

	public void setFormat(BRAMFormat format) {
		this.format = format;
	}

	public String getXmiId() {
		return xmiId;
	}

	public void setXmiId(String xmiId) {
		this.xmiId = xmiId;
	}

	public String toString() {
		String s = "    ";
		if (this.format == BRAMFormat.None)   s += "cfg.loadFormat = tagged " + this.format + ";\n";
		if (this.format == BRAMFormat.Hex)    s += "cfg.loadFormat = tagged " + this.format + " \"" + this.name + ".hex\";\n";
		if (this.format == BRAMFormat.Binary) s += "cfg.loadFormat = tagged " + this.format + " \"" + this.name + ".bin\";\n";
		if (this.dualPort == 0) s += "    BRAM1Port#(" + this.addrWidth.toString() + ", " + this.dataWidth.toString() + ") " + this.name + " <- mkBRAM1Server(cfg);";
		if (this.dualPort == 1) s += "    BRAM2Port#(" + this.addrWidth.toString() + ", " + this.dataWidth.toString() + ") " + this.name + " <- mkBRAM2Server(cfg);";
		return s;
	}
	
	public String toStringFunction() {
		String s = "    function BRAMRequest#(" + this.addrWidth.toString() + ", " + this.dataWidth.toString() + ") " + this.name + "Request(Bool write, " + this.addrWidth.toString() + " addr, " + this.dataWidth.toString() + " data);\n";
	    s += "        return BRAMRequest { write: write, responseOnWrite: False, address: addr, datain: data };\n";
	    s += "    endfunction";
	    return s;
	}
	
	public static void findBRAM(XMLTag doc, FSM fsm) {
		try {
			if (doc.getAttribute("xmi:type").equals("uml:Class")) {
				BRAM b = new BRAM();
				b.setName(doc.getAttribute("name"));
				b.setXmiId(doc.getAttribute("xmi:id"));
				
				if (doc.getChildCount() != 4) System.err.println("ERR: Wrong number of parameters for [" + doc.getAttribute("name") + "] Memory");
				for (int i=1; i <= doc.getChildCount(); i++) {
					doc.gotoChild(i);
					if (doc.getAttribute("name").equals("addrWidth")) {
						b.setAddrWidth(DataType.FindDataTypeByXmiId(fsm, doc.getAttribute("type")));
					} else if (doc.getAttribute("name").equals("dataWidth")) {
						b.setDataWidth(DataType.FindDataTypeByXmiId(fsm, doc.getAttribute("type")));
					} else if (doc.getAttribute("name").equals("dualPort")) {
						doc.gotoChild();
						b.setDualPort(Integer.parseInt(doc.getAttribute("value")));
						doc.gotoParent();
					} else if (doc.getAttribute("name").equals("format")) {
						doc.gotoChild();
						b.setFormat(BRAMFormat.valueOf(doc.getAttribute("value")));
						doc.gotoParent();
					} else System.err.println("ERR: Unknown parameter [" + doc.getAttribute("name") +"] found in ["+ b.getName() +"] Memory");
					doc.gotoParent();
				}
				fsm.getBRAMs().add(b);
				fsm.setThereIsBRAM(true);
			}			
		} catch (XMLDocumentException e) {/* No problem... let it go */}

		for (int i=1; i <= doc.getChildCount(); i++) {
			findBRAM(doc.gotoChild(i), fsm);
			doc.gotoParent();
		}		
	}
}
