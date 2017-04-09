package lcr;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

public class HelloWorld {

    /* this is only a class to test Apache Velocity
     * will be deleted soon */

	public static void main(String[] args) throws Exception {
		/* state list */
		ArrayList<Map<String, String>> list = new ArrayList<Map<String, String>>();
		Map<String, String> map1 = new HashMap<String, String>();
		map1.put("name", "Green");
		map1.put("body", "// here goes the body");
		list.add(map1);

		Map<String, String> map2 = new HashMap<String, String>();
		map2.put("name", "Yellow");
		map2.put("body", "// here goes the body");
		list.add(map2);

		Map<String, String> map3 = new HashMap<String, String>();
		map3.put("name", "Red");
		map3.put("body", "// here goes the body");
		list.add(map3);

		/* init an engine */
		VelocityEngine ve = new VelocityEngine();
		ve.init();
		
		/* get the template */
		Template t = ve.getTemplate("templates/module.vm");
		
		/* create context and add data */
		VelocityContext context = new VelocityContext();
		context.put("name", "TrafficLight");
		context.put("stateList", list);
		
		/* render the template */
		StringWriter writer = new StringWriter();
		t.merge(context, writer);
		
		/* show the world */
		System.out.println(writer.toString());
	}

}
