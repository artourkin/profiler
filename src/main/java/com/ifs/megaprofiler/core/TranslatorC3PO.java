/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ifs.megaprofiler.core;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.dom4j.tree.DefaultAttribute;
import org.dom4j.tree.DefaultDocument;
import org.dom4j.tree.DefaultElement;

import com.ifs.megaprofiler.App;
import com.ifs.megaprofiler.elements.Node;
import com.ifs.megaprofiler.helper.MyLogger;

/**
 * 
 * @author artur
 */
public class TranslatorC3PO {

	private static String[] C3PO_PROFILE_PROPERTIES = { "format", "mimetype",
			"puid", "format_version" };

	public static Document createDocument(
			com.ifs.megaprofiler.elements.Document document) {
		// Document result = DocumentHelper.createDocument();
		// Element root = result.addElement(document.root.name);
		// parseNode(document.root, root);

		return run(document);
	}

	public static void printDocument(
			com.ifs.megaprofiler.elements.Document document, String filename,
			boolean consoleOutput) {
		if (document == null) {
			return;
		}
		Document output = createDocument(document);
		XMLWriter writer;
		try {
			OutputFormat format = OutputFormat.createPrettyPrint();
			if (consoleOutput) {
				System.out.println("\nPrinting a profile:----\n");
				writer = new XMLWriter(System.out, format);
				writer.write(output);
			}
			System.out.println("Writing a c3po profile to file: " + filename);
			MyLogger.print("Writing a c3po profile to file: " + filename);
			writer = new XMLWriter(new FileWriter(filename), format);
			writer.write(output);
			writer.close();
		} catch (IOException ex) {
			Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	public static Document run(com.ifs.megaprofiler.elements.Document document) {
		Document result = new DefaultDocument();
		Element root = new DefaultElement("profile");
		root.add(new DefaultAttribute("xmlns",
				"http://ifs.tuwien.ac.at/dp/c3po"));
		root.add(new DefaultAttribute("xmlns:xsi",
				"http://www.w3.org/2001/XMLSchema-instance"));

		root.add(new DefaultAttribute("collection", "test"));
		root.add(new DefaultAttribute("date", new Date().toString()));
		root.add(new DefaultAttribute("count",
				Long.toString(document.getRoot().count)));
		result.setRootElement(root);
		root.add(getPartition(document.getRoot()));
		return result;
	}

	static Element getPartition(com.ifs.megaprofiler.elements.Node root) {
		Element result = new DefaultElement("partition");
		result.add(new DefaultAttribute("count", Long.toString(root.count)));
		Element filter = new DefaultElement("filter");
		result.add(filter);
		filter.add(new DefaultAttribute("id", "extern"));
		Element parameters = new DefaultElement("parameters");
		filter.add(parameters);
		Element parameter = new DefaultElement("parameter");
		parameters.add(parameter);
		Element name = new DefaultElement("name");
		name.addText("collection");
		Element value = new DefaultElement("value");
		value.addText("test");
		parameter.add(name);
		parameter.add(value);
		result.add(getProperties(root));
		return result;
	}

	static Element processIdentity(Node root, String identityName) {
		TreeMap<String, Long> dictionary = new TreeMap<String, Long>();
		Element property = new DefaultElement("property");
		List<Node> nodes = root.findNodes("identity");
		property.add(new DefaultAttribute("id", identityName));
		property.add(new DefaultAttribute("type", "STRING"));
		long itemCount = 0;
		long conflictCount = 0;
		for (Node node : nodes) {
			if (isIdentificationConflicted(node, root)) {
				conflictCount += node.count;
			} else {
				String key = node.getProperty(identityName).getValue();
				if (dictionary.containsKey(key)) {
					Entry<String, Long> entry = dictionary.floorEntry(key);
					dictionary.remove(key);
					dictionary.put(entry.getKey(), entry.getValue()
							+ node.count);
				} else {
					dictionary.put(node.getProperty(identityName).getValue(),
							node.count);
				}
				itemCount += node.count;
			}
		}
		if (conflictCount > 0) {
			conflictCount = root.count - itemCount;
			dictionary.put("Conficted", conflictCount);
		}
		itemCount += conflictCount;
		property.add(new DefaultAttribute("count", String.valueOf(itemCount)));

		updatePropertyFromMap(property, dictionary);
		return property;
	}

	static void updatePropertyFromMap(Element property, Map dictionary) {
		Map map = sortByValue(dictionary);
		for (Iterator it = map.entrySet().iterator(); it.hasNext();) {
			Entry entry = (Entry) it.next();
			Element item = new DefaultElement("item");
			String keyString = entry.getKey().toString();
			String valueString = entry.getValue().toString();
			if (!(keyString.isEmpty() || valueString.isEmpty())) {
				item.add(new DefaultAttribute("id", entry.getKey().toString()));
				item.add(new DefaultAttribute("value", entry.getValue()
						.toString()));
				property.add(item);
			}
		}
	}

	static Element processValueProperty(Node root, String propertyName) {
		TreeMap<String, Long> dictionary = new TreeMap<String, Long>();
		List<Node> nodes;
		if (propertyName.equals("puid")) {
			nodes = root.findNodes("externalIdentifier");
		} else if (propertyName.equals("format_version")) {
			nodes = root.findNodes("version");
		} else if (propertyName.equals("compression_scheme")) {
			nodes = root.findNodes("compressionScheme");
		} else if (propertyName.equals("colorspace")) {
			nodes = root.findNodes("colorSpace");
		} else if (propertyName.equals("byteorder")) {
			nodes = root.findNodes("byteOrder");
		} else {
			nodes = root.findNodes("identity");
		}
		Element property = new DefaultElement("property");
		property.add(new DefaultAttribute("id", propertyName));
		property.add(new DefaultAttribute("type", "STRING"));
		long itemCount = 0;
		long conflictCount = 0;
		for (Node node : nodes) {
			if (isIdentificationConflicted(node, root)
					|| (propertyName.equals("format_version") && isVersionConflicted(
							node, root))) {
				conflictCount += node.count;
			} else {
				String key = node.getValue();
				if (dictionary.containsKey(key)) {
					Entry<String, Long> entry = dictionary.floorEntry(key);
					dictionary.remove(key);
					dictionary.put(entry.getKey(), entry.getValue()
							+ node.count);
				} else {
					dictionary.put(node.getValue(), node.count);
				}

				itemCount += node.count;
			}
		}
		if (conflictCount > 0) {
			conflictCount = root.count - itemCount;
			dictionary.put("Conficted", conflictCount);
		}
		itemCount += conflictCount;
		property.add(new DefaultAttribute("count", String.valueOf(itemCount)));

		updatePropertyFromMap(property, dictionary);
		return property;
	}

	static Element processNumericProperty(Node root, String propertyName) {
		Node node;
		if (propertyName.equals("size")) {
			node = root.findNode("size");
		} else {
			node = null;
		}

		Element property = new DefaultElement("property");
		property.add(new DefaultAttribute("id", propertyName));
		property.add(new DefaultAttribute("type", "INTEGER"));

		property.add(new DefaultAttribute("count", String.valueOf(node.count)));
		property.add(new DefaultAttribute("sum", node.value));
		property.add(new DefaultAttribute("min", node.getProperty("min").getValue()));
		property.add(new DefaultAttribute("max", node.getProperty("max").getValue()));
		property.add(new DefaultAttribute("avg", node.getProperty("avg").getValue()));
		property.add(new DefaultAttribute("var", node.getProperty("var").getValue()));
		property.add(new DefaultAttribute("sd", node.getProperty("sd").getValue()));
		return property;
	}

	static Element getProperties(Node root) {
		Element result = new DefaultElement("properties");

		result.add(processIdentity(root, "format"));
		result.add(processIdentity(root, "mimetype"));
		result.add(processNumericProperty(root, "size"));
		result.add(processValueProperty(root, "puid"));
		result.add(processValueProperty(root, "format_version"));
		result.add(processValueProperty(root, "byteorder"));
		result.add(processValueProperty(root, "compression_scheme"));
		result.add(processValueProperty(root, "colorspace"));
		return result;
	}

	static boolean isIdentificationConflicted(Node node, Node root) { // checking
		// whether
		// node
		// a
		// conflicted
		// identification
		// or a
		// conflicted
		// version
		while (node != null) {
			if (node.name.equals("identification")) {
				break;
			}
			node = root.getParent(node);
		}
		if (node != null && node.getProperty("status") != null
				&& node.getProperty("status").getValue().equals("CONFLICT")) {
			return true;
		}
		return false;
	}

	static boolean isVersionConflicted(Node node, Node root) { // checking
		// whether node
		// a conflicted
		// identification
		// or a
		// conflicted
		// version
		if (node != null && node.getProperty("status") != null
				&& node.getProperty("status").getValue().equals("CONFLICT")) {
			return true;
		}
		return false;
	}

	static Map sortByValue(Map map) {
		List list = new LinkedList(map.entrySet());
		Collections.sort(list, new Comparator() {
			public int compare(Object o1, Object o2) {
				return ((Comparable) ((Map.Entry) (o2)).getValue())
						.compareTo(((Map.Entry) (o1)).getValue());
			}
		});

		Map result = new LinkedHashMap();
		for (Iterator it = list.iterator(); it.hasNext();) {
			Map.Entry entry = (Map.Entry) it.next();
			result.put(entry.getKey(), entry.getValue());
		}
		return result;
	}
}
