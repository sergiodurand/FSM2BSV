package lcr;

import java.io.IOException;

import nu.xom.*;

public class ExampleXMIParser {
	
	public void xomParser(String arquivo) {
		try {
			Builder parser = new Builder(false);   // when true validity is checked.
			Document doc = parser.build(arquivo);
			Element root = doc.getRootElement();
			
			System.out.println("Document Resume:");
			System.out.println("Parent Node: " + root.getParent());
			System.out.println("Parent Node: " + root.getParent());
			System.out.println("Child Count: " + root.getChildCount());
			System.out.println("Child Count: " + root.getChildCount());
			
			Element fsm = root.getFirstChildElement("packagedElement");
			if (fsm == null) {
				System.out.println("Nulo...");
			} else {
				System.out.println(fsm.toString());
			}
			
			
		} catch (ValidityException ex) {
			System.err.println("this document has " + ex.getErrorCount() + " warnings.");
			for (int i = 0; i < ex.getErrorCount(); i++) {
				System.err.println("Warning " + i + ": " + ex.getValidityError(i));
			}
		} catch (ParsingException ex) {
			System.err.println("malformed file.");
		} catch (IOException ex) {
			System.err.println("io error.");
		}
	}

	private static void printSpaces(int n) {
		for (int i = 0; i < n; i++) {
			System.out.print(' ');
		}
	}
	public static void listChildren(Node current, int depth) {
		printSpaces(depth);
		String data = "";
		if (current instanceof Element) {
			Element temp = (Element) current;
		    data = ": (getQualifiedName) " + temp.getQualifiedName();   
		} else if (current instanceof ProcessingInstruction) {
			ProcessingInstruction temp = (ProcessingInstruction) current;
		    data = ": (getTarget) " + temp.getTarget();   
		} else if (current instanceof DocType) {
			DocType temp = (DocType) current;
		    data = ": (getRootElement) " + temp.getRootElementName();   
		} else if (current instanceof Text || current instanceof Comment) {
			String value = "(Text or Comment) " + current.getValue();
			value = value.replace('\n', ' ').trim();
		    if (value.length() <= 20)
		    	data = ": " + value;
		    else
		    	data = ": " + current.getValue().substring(0, 17) + "...";   
		}
		
		// Attributes are never returned by getChild()
		System.out.println(current.getClass().getName() + data);
		for (int i = 0; i < current.getChildCount(); i++) {
			listChildren(current.getChild(i), depth+4);
		}
	}	

	public static void main(String[] args) {

		// XMI browsing...

		// simple example
		//XMIParser xmi = new XMIParser();
		//xmi.xomParser("TrafficLight.xmi");
		
		/* another example
		Builder builder = new Builder();
		try {
			Document doc = builder.build("TrafficLight.xmi");
			Element root = doc.getRootElement();
			listChildren(root, 0);
		} catch (Exception ex) {
			System.err.println("buggy: " + ex.getMessage());
		}*/
		
	}
}
