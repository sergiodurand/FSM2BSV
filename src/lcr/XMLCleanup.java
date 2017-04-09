package lcr;

import com.mycila.xmltool.CallBack;
import com.mycila.xmltool.XMLTag;

public class XMLCleanup implements CallBack {

	@Override
	public void execute(XMLTag doc) {
		XMLToolParser.xmlCleanup(doc);
		if (doc.getCurrentTagName().equalsIgnoreCase("xmi:Extension")) doc.delete();
	}

}
