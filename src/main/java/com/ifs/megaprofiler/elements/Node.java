/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ifs.megaprofiler.elements;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import com.ifs.megaprofiler.maths.Maths;
import javax.naming.spi.DirStateFactory;

/**
 * 
 * @author artur
 */
public class Node {

	public String name;
	public List<Property> properties;
	public String value;
	public List<Node> nodes;
	public long count;

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

	public void addNode(Node node) {
		if (this.nodes == null) {
			this.nodes = new ArrayList<Node>();
		}
		if (node != null) {
			this.nodes.add(node);
		}
	}

	public Node[] toArray() {
		List<Node> tmpnodes = Maths.breadthFirstSearch(this);
		return tmpnodes.toArray(new Node[tmpnodes.size()]);
	}

	public Node findNode(String nodeName) {
		Node[] nodes = this.toArray();
		for (Node node : nodes) {
			if (node.name.equals(nodeName)) {
				return node;
			}
		}
		return null;
	}

	public List<Node> findNodes(String nodeName) {
		Node[] nodes = this.toArray();
		List<Node> result = new ArrayList<Node>();
		for (Node node : nodes) {
			if (node.name.equals(nodeName)) {
				result.add(node);
			}
		}
		return result;

	}

	public List<Node> getNodesByProperty(Property property) {
		List<Node> result = new ArrayList<Node>();
		Node[] nodes = toArray();
		for (Node node : nodes) {
			if (!result.contains(node) && node.properties.contains(property)) {
				result.add(node);
			}
		}
		return result;
	}

	public List<Property> getProperties() {
		List<Property> result = new ArrayList<Property>();
		Node[] nodes = toArray();
		for (Node node : nodes) {
			for (Property property : node.properties) {
				if (!result.contains(property)) {
					result.add(property);
				}
			}
		}
		return properties;
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

		// return true;

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
		Node tmp = Maths.reduce(this, n);
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

	public Node getNode(Node n) {
		List<Node> list = Maths.depthFirstSearch(this);
		if (list != null) {
			int index = list.indexOf(n);
			if (index >= 0) {
				return list.get(index);
			}
		}
		return null;
	}

	public Node getNode(String name) {
		List<Node> list = Maths.depthFirstSearch(this);
		if (list != null) {
			for (Node node : list) {
				if (node.name.equals(name)) {
					return node;
				}
			}
		}
		return null;
	}

	public Property getProperty(String name) {
		for (Property prop : this.properties) {
			if (prop.key.equals(name)) {
				return prop;
			}
		}
		return null;

	}

	public List<Node> getList(String name) {
		List<Node> list = Maths.depthFirstSearch(this);
		List<Node> result = new ArrayList<Node>();
		if (list != null) {
			for (Node node : list) {
				if (node.name.equals(name)) {
					result.add(node);
				}
			}
		}
		return result;
	}

	public boolean isNull() {
		if (this.name == null || this.name.equals("")) {
			return true;
		}
		return false;
	}

	public Node getParent(Node n) {
		if (this.contains(n)) {
			List<Node> list = Maths.breadthFirstSearch(this);
			for (Node node : list) {
				for (Node subnode : node.nodes) {
					if (subnode == n) {
						return node;
					}
				}
			}
		}
		return null;

		// if (this.contains(n)) {
		// List<Node> list = Maths.depthFirstSearch(this);
		// int i = list.indexOf(n);
		// while (i >= 0) {
		// if (list.get(i).nodes.contains(n)) {
		// return list.get(i);
		// }
		// i--;
		// }
		// }
		// return null;
	}
}
