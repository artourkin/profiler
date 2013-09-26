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
import com.ifs.megaprofiler.helper.ResourceLoader;
import org.dom4j.DocumentException;

/**
 * 
 * @author artur
 */
public class Aggregator {

	List<String> Vocabulary;
	SAXReader reader;
	List<String> allowedElements;
	final String[] statsNodes = { "size", "min", "max", "sd", "avg", "var" };
	Map<String, String[]> statsMap;
	List<String> statsList;

	public Aggregator() {
		reader = new SAXReader();
		Vocabulary = new ArrayList<String>();
		allowedElements = ResourceLoader.getAllowedElements();
		statsMap = new HashMap<String, String[]>();
		statsMap.put("size", new String[] { "min", "max", "sd", "avg", "var" });

	}

	public com.ifs.megaprofiler.elements.Document parseDocument(
			InputStream input) throws DocumentException {
		Document doc = reader.read(input);
		com.ifs.megaprofiler.elements.Document result = new com.ifs.megaprofiler.elements.Document();
		Element root = doc.getRootElement();

		Node nodeRoot = new Node();
		nodeRoot.setName("root");

		Node main = mapElement(root, true);
		nodeRoot.addNode(main);
		nodeRoot.addNode(getStatsNode(root));
		result.setRoot(nodeRoot);
		return result;

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

	private Node mapElement(Element element, boolean strict) {

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
				result.add(new Property(s, "", Property.Type.String));
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
