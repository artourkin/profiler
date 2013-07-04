/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ifs.megaprofiler.core;

import com.ifs.megaprofiler.elements.Node;
import com.ifs.megaprofiler.elements.Property;
import com.ifs.megaprofiler.helper.ResourceLoader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.dom4j.Attribute;
import org.xml.sax.SAXException;
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

  public Aggregator() {
    reader = new SAXReader();

    Vocabulary = new ArrayList<String>();
    allowedElements = ResourceLoader.getAllowedElements();
  }

  public com.ifs.megaprofiler.elements.Document parseDocument(InputStream input) {
    try {
      Document doc = reader.read(input);
      com.ifs.megaprofiler.elements.Document result = new com.ifs.megaprofiler.elements.Document();
      Element root = doc.getRootElement();
      Node nodeRoot = parseElement(root);
      if (nodeRoot != null) {
        result.setRoot(nodeRoot);
      }
      return result;
    } catch (DocumentException ex) {
      Logger.getLogger(Aggregator.class.getName()).log(Level.SEVERE, null, ex);
      return null;
    }
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
    if (element == null || element.elements() == null || element.elements().isEmpty()) {
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
    return false;
  }
}
