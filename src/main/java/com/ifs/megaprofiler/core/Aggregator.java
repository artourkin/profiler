/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ifs.megaprofiler.core;

import com.ifs.megaprofiler.elements.Node;
import com.ifs.megaprofiler.elements.Property;
import com.ifs.megaprofiler.helper.ResourceLoader;
import com.ifs.megaprofiler.maths.Maths;
import com.sun.xml.internal.fastinfoset.vocab.Vocabulary;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.crypto.NodeSetData;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

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
			InputStream input) {
		try {
			Document doc = reader.read(input);
			com.ifs.megaprofiler.elements.Document result = new com.ifs.megaprofiler.elements.Document();
			Element root = doc.getRootElement();

			Node nodeRoot = new Node();
			nodeRoot.setName("root");

			Node main = parseElement(root);
			nodeRoot.addNode(main);
			nodeRoot.addNode(getStatsNode(main));
			result.setRoot(nodeRoot);
			return result;
		} catch (Exception ex) {
			Logger.getLogger(Aggregator.class.getName()).log(Level.SEVERE,
					null, ex);
			return null;
		}
	}

	private Node getStatsNode(Node main) throws Exception {
		Node stats = new Node();
		stats.setName("stats");
		for (String s : statsMap.keySet()) {
			Node node = main.getNode(s);
			main.getParent(node).nodes.remove(node);
			stats.addNode(node);
		}
		return stats;
	}

	private Node parseElement(Element element) {
		if (element == null || !isAllowed(element.getName())) {
			return null;
		}
		com.ifs.megaprofiler.elements.Node result = new Node();
		result.name = element.getName();
		List<Property> properties = parseAttributes(element);
		if (properties != null) {
			result.properties.addAll(properties);
		}
		List<Node> nodes = parseSubElements(element);
		if (nodes != null) {
			result.nodes.addAll(nodes);
		} else {
			result.value = element.getText();
		}
		return result;
	}

	private List<Property> parseAttributes(Element element) {
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
				Property property = parseAttribute(attribute);
				if (property != null) {
					result.add(property);
				}
			}
			return result;
		}
	}

	private Property parseAttribute(Attribute attribute) {
		if (attribute == null || !isAllowed(attribute.getName())) {
			return null;
		}
		Property result = new Property();
		result.setKey(attribute.getName());
		result.setValue(attribute.getValue());
		result.setType(Property.Type.String);
		return result;
	}

	private List<Node> parseSubElements(Element element) {
		if (element == null || element.elements() == null
				|| element.elements().isEmpty()) {
			return null;
		}
		List<Node> result = new ArrayList<Node>();
		List<Element> subElements = element.elements();
		for (Element subElement : subElements) {
			Node node = parseElement(subElement);
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
