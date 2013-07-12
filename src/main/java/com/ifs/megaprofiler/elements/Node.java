/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ifs.megaprofiler.elements;

import java.util.ArrayList;
import java.util.List;

import com.ifs.megaprofiler.maths.Maths;
import static com.ifs.megaprofiler.maths.Maths.depthFirstSearch;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 *
 * @author artur
 */
public class Node {

    public String name;
    public List<Property> properties;
    public String value;
    public List<Node> nodes;
    public int count;

    public Node() {
        this.properties = new ArrayList<Property>();
        this.nodes = new ArrayList<Node>();
        this.count = 1;
    }

    public Node(Node original) {
        this.properties = new ArrayList<Property>();
        this.properties.addAll(original.properties);
        this.value = original.value;
        this.nodes = new ArrayList<Node>();
        this.nodes.addAll(original.nodes);
        this.count = original.count;
        this.name = original.name;
    }

    public void increaseCount() {
        this.count++;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }

    public void addProperty(Property property) {
        if (this.properties == null) {
            this.properties = new ArrayList<Property>();
        }
        this.properties.add(property);
    }

    public void addNode(Node node) throws Exception {
        if (this.nodes == null) {
            this.nodes = new ArrayList<Node>();
        }
        if (node != null) {
            this.nodes.add(node);
        } else {
            throw new Exception("Can not add a null node");
        }
    }

    public Node[] toArray() {
        List<Node> tmpnodes = Maths.breadthFirstSearch(this);
        return tmpnodes.toArray(new Node[tmpnodes.size()]);
    }

    public Node findNode(String nodeValue) {
        Node[] nodes = this.toArray();
        for (Node node : nodes) {
            if (node.value.equals(nodeValue)) {
                return node;
            }
        }
        return null;

    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(13, 37).append(value).append(properties)
                .toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Node)) {
            return false;
        }

        //return true;

        Node n = (Node) obj;
        if (!this.name.equals(n.name)) {
            return false;
        }
        EqualsBuilder eqBuilder = new EqualsBuilder();
        eqBuilder.append(this.value, n.value);
        if (this.properties.size() != n.properties.size()) {
            return false;
        }
        for (int i = 0; i < this.properties.size(); i++) {
            eqBuilder.append(this.properties.get(i), n.properties.get(i));
        }
        return eqBuilder.isEquals();
    }

    public Node merge(Node n) throws Exception {
        Node tmp = Maths.merge(this, n);
        this.value = tmp.value;
        this.properties = tmp.properties;
        this.nodes = tmp.nodes;
        return this;
    }

    public boolean contains(Node n) {
        List<Node> list = Maths.depthFirstSearch(this);
        if (list == null) {
            return false;
        }
        int index = list.indexOf(n);
        if (index >= 0) {
            return true;
        }
        return false;
    }

    public Node get(Node n) {
        List<Node> list = Maths.depthFirstSearch(this);
        if (list != null) {
            int index = list.indexOf(n);
            if (index >= 0) {
                return list.get(index);
            }
        }
        return null;
    }

    public boolean isNull() {
        if (this.name == null || this.name.equals("")) {
            return true;
        }
        return false;
    }

    public Node getParent(Node n) {
        if (this.contains(n)) {
            List<Node> list = Maths.depthFirstSearch(this);
            int i = list.indexOf(n);
            while (i >= 0) {
                if (list.get(i).nodes.contains(n)) {
                    return list.get(i);
                }
                i--;
            }
        }
        return null;
    }
}
