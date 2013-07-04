/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ifs.megaprofiler.helper;

import com.ifs.megaprofiler.core.Aggregator;
import com.ifs.megaprofiler.elements.Node;
import com.ifs.megaprofiler.elements.Property;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

/**
 *
 * @author artur
 */
public class XmlSerialiser {

  public static Document createDocument(com.ifs.megaprofiler.elements.Document document) {
    Document result = DocumentHelper.createDocument();
    Element root = result.addElement(document.root.name);
    parseNode(document.root, root);

    return result;
  }

  private static void parseNode(Node node, Element element) {
    if (node.value != null) {
      element.addText(node.value);
    }
    parseProperties(node, element);
    parseNodes(node, element);
    element.addAttribute("count", Integer.toString(node.count));
  }

  private static void parseProperties(Node node, Element element) {
    if (node.properties == null) {
      return;
    }
    for (Property property : node.properties) {
      element.addAttribute(property.key, property.value);
    }
  }

  private static void parseNodes(Node node, Element element) {
    if (node.nodes == null) {
      return;
    }
    for (Node subNode : node.nodes) {
      if (node.nodes != null) {
        Element subElement = element.addElement(subNode.name);
        parseNode(subNode, subElement);
      }
    }
  }
}
