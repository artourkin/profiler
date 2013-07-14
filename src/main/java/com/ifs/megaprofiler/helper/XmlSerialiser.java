/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ifs.megaprofiler.helper;

import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import com.ifs.megaprofiler.App;
import com.ifs.megaprofiler.elements.Node;
import com.ifs.megaprofiler.elements.Property;

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

    public static void printDocument(com.ifs.megaprofiler.elements.Document document, String filename, boolean consoleOutput) {
        Document output = XmlSerialiser.createDocument(document);
        XMLWriter writer;
        try {
            OutputFormat format = OutputFormat.createPrettyPrint();
            if (consoleOutput) {
                System.out.println("\nPrinting a profile:----\n");
                writer = new XMLWriter(System.out, format);
                writer.write(output);
            }
            writer = new XMLWriter(
                    new FileWriter(filename), format);
            writer.write(output);
            writer.close();
        } catch (IOException ex) {
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void parseNode(Node node, Element element) {
        if (node.value != null) {
            element.addText(node.value);
        }
        parseProperties(node, element);
        parseNodes(node, element);
        element.addAttribute("count", Long.toString(node.count));
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
