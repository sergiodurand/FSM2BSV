package lcr;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

import lcr.c2bsv.parser.C2BSVParser;
import lcr.c2bsv.parser.ParseException;
import lcr.c2bsv.parser.SimpleNode;

import com.mycila.xmltool.XMLDocumentException;
import com.mycila.xmltool.XMLTag;

public class StateParser {

	public static void findStateDiagram(XMLTag doc, FSM fsm) {
		try {
			if (doc.getAttribute("xmi:type").equals("uml:StateMachine")) {
				//System.out.println("***  STATE DIAGRAM FOUND!");
				fsm.setName(doc.getAttribute("name"));
				findRegion(doc, fsm);
			}			
		} catch (XMLDocumentException e) {/* No problem... let it go */}

		for (int i=1; i <= doc.getChildCount(); i++) {
			findStateDiagram(doc.gotoChild(i), fsm);
			doc.gotoParent();
		}
	}
	
	public static void findRegion(XMLTag doc, FSM fsm) {
		for (int i=1; i <= doc.getChildCount(); i++) {
			doc.gotoChild(i);
			try {
				if (doc.getAttribute("xmi:type").equals("uml:Region")) {
					//System.out.println("***   REGION FOUND!");
					findStates(doc, fsm);
					findPseudostate(doc, fsm);
					findTransitions(doc, fsm);
					setInitialState(doc, fsm);
					putConditionIntoRule(fsm);
				}
				doc.gotoParent();						
			} catch (Exception e) {
				//System.err.println(doc.getCurrentTagName() + "] " + e.getMessage());
				doc.gotoParent();
			}
		}
	}
	
	public static void putConditionIntoRule(FSM fsm) {
		for (Transition t : fsm.getTransitions()) {
			if (!t.getFrom().getName().equalsIgnoreCase("pseudostate")) {
				String target = t.getFrom().getDoTarget();
				t.getFrom().setDoTarget(t.getCondition() + "\n" + target);
			}
		}
	}
	
	public static void findStates(XMLTag doc, FSM fsm) {
		for (int i=1; i <= doc.getChildCount(); i++) {
			doc.gotoChild(i);
			if (doc.getAttribute("xmi:type").equals("uml:State")) {
				State s = new State();
				s.setName(doc.getAttribute("name"));
				s.setEnumName(s.getName());
				s.setXmiId(doc.getAttribute("xmi:id"));
				s.setLanguage(getDoLanguage(doc));
				s.setDoSource(getDoCode(doc));
				if (s.getLanguage().equalsIgnoreCase("BSV")) { s.setDoTarget(s.getDoSource()); }
				else if (s.getLanguage().equalsIgnoreCase("C")) { s.setDoTarget(callC2BSV(s.getDoSource())); } // Call C2BSV compiler
				else {
					System.out.println("WARNING: [state "+ s.getEnumName() +"] Tag 'language' is missing from XMI file. Using BSV as default language.");
					s.setDoTarget(s.getDoSource());
				}
				fsm.getStates().add(s);
			}
			doc.gotoParent();
		}
	}

	public static String callC2BSV(String code) {
		InputStream is = new ByteArrayInputStream(code.getBytes());
		C2BSVParser parser = new C2BSVParser(is);
		try {
			SimpleNode root = parser.TranslationUnit();
			return root.toBSV().toString().trim();
		} catch (ParseException e) {
			System.err.println("C2BSV Compiler ERROR: " + e.getMessage());
			e.printStackTrace();
		}
		return "";
	}
	
	public static String getDoLanguage(XMLTag doc) {
		String lang = "";

		if (doc.getChildCount() == 0) return lang;  // if state has no code, then return empty string

		for (int i=1; i <= doc.getChildCount(); i++) {   // search for tags like 'doActivity'
			doc.gotoChild(i);
			if (doc.getCurrentTagName().equalsIgnoreCase("doActivity")) { // in this first version, check only for code of 'doActivity' type
				for (int j=1; j <= doc.getChildCount(); j++) {   // search for 'body' and 'language' tags
					doc.gotoChild(j);
					if (doc.getCurrentTagName().equalsIgnoreCase("language")) {
						lang = doc.getText();
					}
					doc.gotoParent();
				}				
			}
			doc.gotoParent();
		}
		return lang;
	}
	
	public static String getDoCode(XMLTag doc) {
		String code = "";

		if (doc.getChildCount() == 0) return "";  // if state has no code, then return empty string

		for (int i=1; i <= doc.getChildCount(); i++) {   // search for tags like 'doActivity'
			doc.gotoChild(i);
			if (doc.getCurrentTagName().equalsIgnoreCase("doActivity")) { // in this first version, check only for code of 'doActivity' type
				for (int j=1; j <= doc.getChildCount(); j++) {   // search for 'body' and 'language' tags
					doc.gotoChild(j);
					if (doc.getCurrentTagName().equalsIgnoreCase("body")) {
						code = doc.getText();
					}
					doc.gotoParent();
				}				
			}
			doc.gotoParent();
		}

		return code;
	}
	
	public static void findPseudostate(XMLTag doc, FSM fsm) {
		for (int i=1; i <= doc.getChildCount(); i++) {
			doc.gotoChild(i);
			if (doc.getAttribute("xmi:type").equals("uml:Pseudostate")) {
				State s = new State();
				s.setName("Pseudostate");
				s.setEnumName(s.getName());
				s.setXmiId(doc.getAttribute("xmi:id"));
				fsm.getStates().add(s);
			}
			doc.gotoParent();
		}
	}
	
	public static void findTransitions(XMLTag doc, FSM fsm) {
		for (int i=1; i <= doc.getChildCount(); i++) {
			doc.gotoChild(i);
			if (doc.getAttribute("xmi:type").equals("uml:Transition")) {
				Transition t = new Transition();
				t.setFrom(findByXmiId(fsm.getStates(), doc.getAttribute("source")));
				t.setTo(findByXmiId(fsm.getStates(), doc.getAttribute("target")));
				t.setXmiId(doc.getAttribute("xmi:id"));
				t.setCondition(getCondition(doc, t.getTo()));
				fsm.getTransitions().add(t);
			}
			doc.gotoParent();
		}		
	}
	
	public static String getCondition(XMLTag doc, State to) {
		String cond = "";

		if (doc.getChildCount() == 0) return "state <= " + to.getEnumName() + ";";
		
		for (int i=1; i <= doc.getChildCount(); i++) {
			doc.gotoChild(i);
			if ((doc.getCurrentTagName().equalsIgnoreCase("ownedRule")) && doc.getAttribute("xmi:type").equalsIgnoreCase("uml:Constraint")) {
				for (int j=1; j <= doc.getChildCount(); j++) {
					doc.gotoChild(j);
					if ((doc.getCurrentTagName().equalsIgnoreCase("specification")) && doc.getAttribute("xmi:type").equalsIgnoreCase("uml:OpaqueExpression")) {
						for (int k=1; k <= doc.getChildCount(); k++) {
							doc.gotoChild(k);
							if (doc.getCurrentTagName().equalsIgnoreCase("body")) {
								cond = "if (" + doc.getText() + ") state <= " + to.getEnumName() +";";
							}
							doc.gotoParent();
						}
					}
					doc.gotoParent();
				}
			}
			doc.gotoParent();
		}
		return cond;
	}
	
	public static void setInitialState(XMLTag doc, FSM fsm) {
		State pseudo = null;
		State initial = null;
		for (State s : fsm.getStates()) {
			if (s.getName().equalsIgnoreCase("pseudostate")) {
				pseudo = s;
				break;
			}
		}
		
		for (Transition t : fsm.getTransitions()) {
			if (t.getFrom().equals(pseudo)) {
				initial = t.getTo();
			}
		}
		
		fsm.setResetState(initial);
		fsm.getStates().remove(pseudo);
	}

	public static State findByXmiId(List<State> states, String key) {
		for (State s : states) {
			if (s.getXmiId().equals(key)) return s;
		}
		System.err.println("WARNING: State not found");
		return null;
	}	
}
