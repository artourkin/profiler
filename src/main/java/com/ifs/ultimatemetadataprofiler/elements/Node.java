/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ifs.ultimatemetadataprofiler.elements;

import java.util.List;

import com.ifs.ultimatemetadataprofiler.maths.Maths;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 *
 * @author artur
 */
public class Node {

    public List<Property> properties;
    public String value;
    public List<Node> nodes;

    public Node() {
    }

    public Node(List<Property> properties, String value, List<Node> nodes) {
        this.properties = properties;
        this.value = value;
        this.nodes = nodes;
    }

    public Node[] toArray() {
        List<Node> tmpnodes = Maths.breadthFirstSearch(this);
        return tmpnodes.toArray(new Node[tmpnodes.size()]);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(13, 37).
                append(value).
                append(properties).
                toHashCode();
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

        Node n = (Node) obj;
        return new EqualsBuilder().
                append(this.value, n.value).
                append(properties, n.properties).
                isEquals();
    }

    public Node merge(Node n) {
        Node tmp = Maths.merge(this, n);
        this.value = tmp.value;
        this.properties = tmp.properties;
        this.nodes = tmp.nodes;
        return this;
    }
}
