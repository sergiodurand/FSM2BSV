package lcr;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import com.mycila.xmltool.*;

public class XMLToolParser {
	
	/* TODO: Check if will be needed variable attribute 
	 *       Try to indent code inside a rule
	 *       It's possible to add restrictions to methods through Inner Elements/Body of a signal
	 *         ex: method Action input_myVector(Vector#(10, int) myVector) if (rst == 0);
	 *         for now, it's more simple use await function into testbench.
	 *       Add FIFO structure
	 *       Add class with name Debug for debugging purposes.
	 *           This class will be transformed in a rule without condition.
	 *       Check if user added an unknown type in Class Diagram
     *       Put System.err and Exit(-1) on every fatal error.
     *       ASAP: * Compact Symbol Table
     *             * Clean up this class because it's a mess.
	 */		
	
	final static String STATE = "subvertex";
	final static String TRANSITION = "transition";
	final static String PACKAGE = "packagedElement";
	final static String FSM = "region";

	public void findUmlActivity(XMLTag doc, FSM fsm) {
		try {
			if (doc.getAttribute("xmi:type").equals("uml:Activity")) {
				System.out.println("*** ACTIVITY DIAGRAM FOUND!");
				//fsm = ActivityParser.parser(doc, fsm);
			}			
		} catch (XMLDocumentException e) {
			// No problem... let it go
		}

		for (int i=1; i <= doc.getChildCount(); i++) {
			findUmlActivity(doc.gotoChild(i), fsm);
			doc.gotoParent();
		}
	}

	public void findUmlStateMachine(XMLTag doc, FSM fsm) {
		System.out.println("calling elvis");
		if (doc.getAttribute("xmi:type").equals("uml:StateMachine")) {
			//System.out.println("*** STATE DIAGRAM FOUND!");
			if (doc.getChildCount() == 1) {
				//System.out.println("only one region, ok... Now it is time to find states and transitions");
				doc.gotoChild();
				findUmlStates(doc, fsm);
				findUmlTransitions(doc, fsm);
				doc.gotoParent();
			}
		}
		for (int i=1; i <= doc.getChildCount(); i++) {
			findUmlStateMachine(doc.gotoChild(i), fsm);
			doc.gotoParent();
		}
		
		// Set FSM name
		/*System.out.println(doc.getChildCount());
		for (int i=1; i<= doc.getChildCount(); i++) {
			doc.gotoChild(i);
			System.out.println(doc.getCurrentTagName());
			if (doc.getCurrentTagName() == PACKAGE) {
				fsm.setName(doc.getAttribute("name"));
				System.out.println("got it");
			}
			if (doc.getCurrentTagName() == FSM) {
				System.out.println("got it");
				break;
			}
			doc.gotoParent();
		}*/
		
	}

	public void findUmlTransitions(XMLTag doc, FSM fsm) {
		for (int i=1; i<= doc.getChildCount(); i++) {
			doc.gotoChild(i);
			if (doc.getAttribute("xmi:type").equals("uml:Transition")) {
				Transition t = new Transition();
				t.setFrom(this.findByXmiId(fsm.getStates(), doc.getAttribute("source")));
				t.setTo(this.findByXmiId(fsm.getStates(), doc.getAttribute("target")));
				t.setXmiId(doc.getAttribute("xmi:id"));
				fsm.getTransitions().add(t);
			}
			doc.gotoParent();
		}
	}

	public void findUmlStates(XMLTag doc, FSM fsm) {
		for (int i=1; i<= doc.getChildCount(); i++) {
			doc.gotoChild(i);
			if (doc.getAttribute("xmi:type").equals("uml:State")) { //  doc.getCurrentTagName() == STATE) {
				State e = new State();
				e.setName(doc.getAttribute("name"));
				e.setEnumName(e.getName());
				e.setXmiId(doc.getAttribute("xmi:id"));
				fsm.getStates().add(e);
				System.out.println(e);
			}
			doc.gotoParent();
		}
	}

/*	public FSM Parser(FSM fsm) {
		fsm.setStates(new ArrayList<State>());
		fsm.setTransitions(new ArrayList<Transition>());
		fsm.setVariables(new ArrayList<Variable>());
		fsm.setSignals(new ArrayList<Signal>());
		//XMLTag doc = XMLDoc.from(new File("model/FinalTest.xml"), true);
		XMLTag doc = XMLDoc.from(new File("model/Max.xml"), true);
		findUmlActivity(doc, fsm); //findUmlStateMachine(doc);
		return fsm;
	}*/

	public FSM Parser(FSM fsm) {
		fsm.setStates(new ArrayList<State>());
		fsm.setTransitions(new ArrayList<Transition>());
		fsm.setDataTypes(new ArrayList<DataType>());
		fsm.setVariables(new ArrayList<Variable>());
		fsm.setSignals(new ArrayList<Signal>());
		fsm.setBRAMs(new ArrayList<BRAM>());
		//XMLTag doc = XMLDoc.from(new File("model/Sobel.xml"), true);
		XMLTag doc = XMLDoc.from(new File("model/Processor.xml"), true);
		
		// uncomment the line bellow to cleanup XML, removing 'xmi:Extension' tags before start parsing...
		//xmlCleanup(doc);

		// Data types
		DataType.findPrimitiveTypes(doc, fsm);
		DataType.findDataTypes(doc, fsm);
		BRAM.findBRAM(doc, fsm);

		// Activity Diagram
		ActivityParser.findActivityDiagram(doc, fsm);

		// Create Compact Symbol Table ********************************************************************************
		// At this point, it's ready to read all variables, vectors and memories declared in XML.
		//SymbolTable.createCompactSymbolTable(fsm);

		// State Diagram
		StateParser.findStateDiagram(doc, fsm);
		
		return fsm;
	}	

	public void GenBSV(FSM fsm, Boolean toFile) {
		VelocityEngine ve = new VelocityEngine();
		ve.init();
		Template t = ve.getTemplate("templates/module.vm");
		VelocityContext c = new VelocityContext();
		c.put("nl", "\n");
		c.put("fsmName", fsm.getName());
		c.put("thereIsVector", fsm.getThereIsVector());
		c.put("thereIsBRAM", fsm.getThereIsBRAM());
		c.put("brams", fsm.getBRAMs());
		c.put("states", fsm.getStates());
		c.put("transitions", fsm.getTransitions());
		c.put("resetState", fsm.getResetState().getEnumName());
		c.put("variables", fsm.getSignals());
		StringWriter sw = new StringWriter();
		t.merge(c, sw);
		if (toFile) {
			String filename = "output/" + fsm.getName() + ".bsv";
			System.out.print("Writing to file \"" + filename + "\"...");
			try {
				FileUtils.writeStringToFile(new File(filename), sw.toString());
				System.out.println(" Done!");
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			System.out.println("-------------------[cut here]-------------------");
			System.out.println(sw.toString());
			System.out.println("-------------------[cut here]-------------------");			
		}
	}
	
	public State findByXmiId(List<State> estados, String key) {
		for (State e : estados) {
			if (e.getXmiId().equals(key)) return e;
		}
		System.err.println("WARNING: State not found");
		return null;
	}
	
	public static void main(String[] args) {
		XMLToolParser xml = new XMLToolParser();
		FSM fsm = new FSM();
		//xml.Parser(fsm);
		xml.GenBSV(xml.Parser(fsm), false);
	}

	public static void xmlCleanup(XMLTag doc) {
		doc.forEachChild(new XMLCleanup());
	}
}
