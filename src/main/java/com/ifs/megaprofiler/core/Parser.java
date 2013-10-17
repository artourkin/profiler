/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ifs.megaprofiler.core;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.ifs.megaprofiler.elements.Node;
import com.ifs.megaprofiler.elements.Property;
import com.ifs.megaprofiler.helper.MyLogger;
import com.ifs.megaprofiler.helper.ResourceLoader;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import org.dom4j.DocumentException;

/**
 * 
 * @author artur
 */
public class Parser implements Runnable {

	protected BlockingQueue<InputStream> queueIS;
	protected BlockingQueue<com.ifs.megaprofiler.elements.Document> queueDocument;
	List<String> Vocabulary;
	SAXReader reader;
	List<String> allowedElements;
	final String[] statsNodes = { "min", "max", "sd", "avg", "var" };
	Map<String, String[]> statsMap;
	List<String> statsList;
	com.ifs.megaprofiler.elements.Document Document;
	volatile boolean running = true;

	public Parser(BlockingQueue<InputStream> queueIS,
			BlockingQueue<com.ifs.megaprofiler.elements.Document> queueDocument) {
		this.queueIS = queueIS;
		this.queueDocument = queueDocument;
		reader = new SAXReader();
		Vocabulary = new ArrayList<String>();
		allowedElements = ResourceLoader.getAllowedElements();
		statsMap = new HashMap<String, String[]>();
		statsMap.put("size", statsNodes);
	}

	long totalcount = 0;

	@Override
	public void run() {
		InputStream is=null;
		while (running) {
			try {
				is = queueIS.take();
				Document = parseDocument(is);
				is.close();
				if (Document != null) {
					queueDocument.put(Document);
				}
			} catch (Exception e) {
				MyLogger.print(Parser.class.getName() + ", exception:"
						+ e.getMessage());
			}
		}

	}

	public void terminate() {
		running = false;
	}

	public com.ifs.megaprofiler.elements.Document parseDocument(
			InputStream input) throws DocumentException, IOException {
		com.ifs.megaprofiler.elements.Document result = new com.ifs.megaprofiler.elements.Document();
		if (input == null) {
			return null;
		}
		Document doc = reader.read(input);

		input.close();
		Element root = doc.getRootElement();

		Node nodeRoot = new Node();
		nodeRoot.setName("root");

		if (root.getName().equals("fits")) { // parse a fits file
			Node main = mapElement(root, true);
			nodeRoot.addNode(main);
			nodeRoot.addNode(getStatsNode(root));
			result.setRoot(nodeRoot);
			return result;
		} else if (root.getName().equals("root")) { // parse an existing profile
			nodeRoot = mapElement(root, false);
			result.setRoot(nodeRoot);
			return result;
		}
		return null;

	}

	private Node getStatsNode(Element element) {
		Node tmpTree = mapElement(element, false);
		Node stats = new Node();
		stats.setName("stats");
		for (String elementName : statsMap.keySet()) {
			Node tmpNode = tmpTree.findNode(elementName);
			if (tmpNode != null) {
				stats.addNode(tmpNode);
			}
		}
		return stats;
	}

	private Node mapElement(Element element, boolean strict) { // strict - usage
		// of
		// properties.list
		// file

		if (strict && (element == null || !isAllowed(element.getName()))) {
			return null;
		}
		if (!strict && element == null) {
			return null;
		}
		com.ifs.megaprofiler.elements.Node result = new Node();
		result.name = element.getName();
		List<Property> properties = mapAttributes(element, strict);
		if (properties != null) {
			for (Property property : properties) {
				if (property.key.equals("count")) {
					result.count = Long.parseLong(property.value);
					properties.remove(property);
					break;
				}
			}
			result.properties.addAll(properties);
		}
		List<Node> nodes = mapSubElements(element, strict);
		if (nodes != null) {
			result.nodes.addAll(nodes);
		} else {
			result.value = element.getText();
		}
		return result;
	}

	private List<Property> mapAttributes(Element element, boolean strict) {
		if (element == null) {
			return null;
		}
		if (statsMap.keySet().contains(element.getName())) {
			List<Property> result = new ArrayList<Property>();
			for (String s : statsMap.get(element.getName())) {
				Attribute a = element.attribute(s);
				if (a != null) {
					result.add(new Property(s, a.getValue(),
							Property.Type.String));
				} else {
					result.add(new Property(s, "", Property.Type.String));
				}
			}
			return result;
		} else {
			List<Property> result = new ArrayList<Property>();
			List<Attribute> attributes = element.attributes();
			for (Attribute attribute : attributes) {
				Property property = mapAttribute(attribute, strict);
				if (property != null) {
					result.add(property);
				}
			}
			return result;
		}
	}

	private Property mapAttribute(Attribute attribute, boolean strict) {
		if (strict && (attribute == null || !isAllowed(attribute.getName()))) {
			return null;
		}
		if (!strict && attribute == null) {
			return null;
		}
		Property result = new Property();
		result.setKey(attribute.getName());
		result.setValue(attribute.getValue());
		result.setType(Property.Type.String);
		return result;
	}

	private List<Node> mapSubElements(Element element, boolean strict) {
		if (element == null || element.elements() == null
				|| element.elements().isEmpty()) {
			return null;
		}
		List<Node> result = new ArrayList<Node>();
		List<Element> subElements = element.elements();
		for (Element subElement : subElements) {
			Node node = mapElement(subElement, strict);
			if (node != null) {
				result.add(node);
			}
		}
		return result;
	}

	private boolean isAllowed(String string) {
		if (allowedElements.contains(string)) {
			return true;
		}
		if (statsMap.keySet().contains(string)) {
			return true;
		}
		return false;
	}
}
