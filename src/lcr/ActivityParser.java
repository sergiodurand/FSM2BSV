package lcr;

import lcr.DataType.Type;

import com.mycila.xmltool.XMLDocumentException;
import com.mycila.xmltool.XMLTag;

public class ActivityParser {
	
	public static void findActivityDiagram(XMLTag doc, FSM fsm) {
		try {
			if (doc.getAttribute("xmi:type").equals("uml:Activity")) {
				System.out.println("*** ACTIVITY DIAGRAM FOUND!");
				// set all elements from activity diagram to FSM object
				findAction(doc, fsm);
				//return;
			}			
		} catch (XMLDocumentException e) {/* No problem... let it go */}

		for (int i=1; i <= doc.getChildCount(); i++) {
			findActivityDiagram(doc.gotoChild(i), fsm);
			doc.gotoParent();
		}		
	}
	
	public static void findAction(XMLTag doc, FSM fsm) {
		for (int i=1; i <= doc.getChildCount(); i++) {
			doc.gotoChild(i);
			try {
				if (doc.getAttribute("xmi:type").equals("uml:CallBehaviorAction")) {
					System.out.println("***   ACTION FOUND!");
					findSignalsAndVariables(doc, fsm);
				}
				doc.gotoParent();						
			} catch (Exception e) {
				//System.err.println(doc.getCurrentTagName() + "] " + e.getMessage());
				doc.gotoParent();
			}
		}		
	}
	
	public static void findSignalsAndVariables(XMLTag doc, FSM fsm) {
		for (int i=1; i <= doc.getChildCount(); i++) {
			doc.gotoChild(i);
			Signal s = new Signal();
			s.setName(doc.getAttribute("name"));
			s.setDataType(DataType.FindDataTypeByXmiId(fsm, doc.getAttribute("type")));
			
			if (doc.getAttribute("xmi:type").equals("uml:InputPin")) { s.setDirection(Direction.INPUT); }
			else if (doc.getAttribute("xmi:type").equals("uml:OutputPin")) { s.setDirection(Direction.OUTPUT); }
			else if (doc.getAttribute("xmi:type").equals("uml:ValuePin")) { s.setDirection(Direction.VALUE); }
			else System.err.println("ERR: Found an unsupported signal: " + doc.getAttribute("xmi:type") + "/" + doc.getAttribute("name"));

			// Initialize registers
			if (s.getDataType().getType() == Type.PRIMITIVE) s.setDeclaration(s.getDataType().toStringReg() + " " + s.getName() + " <- mkReg(0);");
			else if (s.getDataType().getType() == Type.VECTOR) {
				s.setDeclaration(s.getDataType().toStringReg() + " " + s.getName() + " <- replicateM(mkReg(0));");
				fsm.setThereIsVector(true);
			}

			//System.out.println(s.getDirection().toString() + " " + s.getDeclaration());
			fsm.getSignals().add(s);
			doc.gotoParent();
		}
	}
}
