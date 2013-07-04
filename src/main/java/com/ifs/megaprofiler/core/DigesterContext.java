package com.ifs.megaprofiler.core;

import java.util.List;

import com.ifs.megaprofiler.elements.*;
import com.ifs.megaprofiler.elements.Property.Type;
import java.util.ArrayList;

public class DigesterContext {

  Document document;
  List<String> elementList;
  Node root;
  Node tmpnode;
  Property property;
  List<Node> nodes;

  public DigesterContext() {//List<String> elementList) {
    //this.elementList = elementList;
    nodes = new ArrayList<Node>();
  }

  public Document getDocument() {
    return document;
  }

  public void createDocument(String name, String path) {
    this.document = new Document();
    Node node = new Node();

    Property propn = new Property("filename", name, Type.String);
    Property propp = new Property("filepath", path, Type.String);

    node.addProperty(propp);
    node.addProperty(propn);
    node.setName("root");
    document.setRoot(node);
    root = node;

    root.nodes.addAll(nodes);
    nodes.clear();
  }

  public void createIdentification(String status) {
    Node identificationNode = new Node();

    identificationNode.setName("identification");
    Property props = new Property("status", status, Type.String);
    identificationNode.addProperty(props);

    //root.addNode(tmpnode);

    identificationNode.nodes.addAll(nodes);
    nodes.clear();
    nodes.add(identificationNode);
  }

  public void createIdentity(String format, String mimetype) {
    //Node identificationNode = root.findNode("identification");

    Node identityNode = new Node();

    Property propf = new Property("format", format, Type.String);
    Property propm = new Property("mimetype", mimetype, Type.String);

    identityNode.setName("identity");
    identityNode.addProperty(propf);
    identityNode.addProperty(propm);

    //identificationNode.addNode(identityNode);
    //tmpnode = identityNode;

    identityNode.nodes.addAll(nodes);
    nodes.clear();
    nodes.add(identityNode);

  }

  public void setIdentityTool(String toolname, String toolversion) {
    Node identityToolNode = new Node();

    Property propn = new Property("toolname", toolname, Type.String);
    Property propv = new Property("toolversion", toolversion, Type.String);

    identityToolNode.setName("tool");
    identityToolNode.addProperty(propn);
    identityToolNode.addProperty(propv);

    //tmpnode.addNode(identityToolNode);

    nodes.add(identityToolNode);
  }

  public void setIdentityVersion(String value, String status,
          String toolname, String version) {

    Node identityVersionNode = new Node();

    Property propstatus = new Property("status", status, Type.String);
    Property proptoolname = new Property("toolname", toolname, Type.String);
    Property propversion = new Property("version", version, Type.String);

    identityVersionNode.setName("version");
    identityVersionNode.setValue(value);
    identityVersionNode.addProperty(propstatus);
    identityVersionNode.addProperty(proptoolname);
    identityVersionNode.addProperty(propversion);

    //tmpnode.addNode(identityVersionNode);
    nodes.add(identityVersionNode);

  }

  public void setIdentityPuid(String value, String toolname, String version) {
    Node identityPuidNode = new Node();

    Property proptoolname = new Property("toolname", toolname, Type.String);
    Property propversion = new Property("version", version, Type.String);

    identityPuidNode.setName("externalIdentifier");
    identityPuidNode.setValue(value);
    identityPuidNode.addProperty(proptoolname);
    identityPuidNode.addProperty(propversion);

    //tmpnode.addNode(identityPuidNode);
    nodes.add(identityPuidNode);
  }
}
