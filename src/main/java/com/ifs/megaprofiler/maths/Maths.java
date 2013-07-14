/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ifs.megaprofiler.maths;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import com.ifs.megaprofiler.elements.Document;
import com.ifs.megaprofiler.elements.Node;
import com.ifs.megaprofiler.elements.Property;

/**
 * 
 * @author artur
 */
public class Maths {

	public static List<Node> breadthFirstSearch(Node node) {
		List<Node> result = new LinkedList<Node>();
		Queue<Node> q = new LinkedList<Node>();
		q.add(node);

		while (!q.isEmpty()) {
			Node n = q.poll();
			result.add(n);
			for (Node n2 : n.nodes) {
				q.add(n2);
			}
		}
		return result;
	}

	public static List<Node> depthFirstSearch(Node node) {
		List<Node> result = new LinkedList<Node>();
		result.add(node);
		for (Node subn : node.nodes) {
			result.addAll(depthFirstSearch(subn));
		}
		return result;
	}

	public static Node merge(Node n1, Node n2) throws Exception {
		if (n1 == n2) {
			return n1;
		}
		if (n1.isNull() && !n2.isNull()) {
			return n2;
		}
		if (n2.isNull() && !n1.isNull()) {
			return n1;
		}
		if (!n1.isNull() && !n1.isNull()) {
			Node result = n1;
			result.count += n2.count;
			for (Node n2sub : n2.nodes) {
				int n2subindex = result.nodes.indexOf(n2sub);
				if (n2subindex >= 0) {
					Node resultsub = new Node(result.nodes.get(n2subindex));
					resultsub = merge(resultsub, n2sub);
					result.nodes.set(n2subindex, resultsub);
				} else {
					result.nodes.add(n2sub);
				}
			}
			return result;

			// SECOND APPROACH
			// // Node result = new Node(n1);
			// // List<Node> n2List = depthFirstSearch(n2);
			// // for (Node node : n2List) {
			// // if (result.contains(node)) {
			// // Node another = result.get(node);
			// // another.count += node.count;
			// // } else {
			// // Node nodeParent = n2.getParent(node);
			// // if (nodeParent==null)
			// // {
			// // int stop=0;
			// //
			// // }
			// // Node another = result.get(nodeParent);
			// // another.addNode(new Node(node));
			// // }
			// // }
			// // return result;
		}
		return null;
	}

	public static Document merge(Document d1, Document d2) throws Exception {
		Document d;
		d = new Document();
		d.root = merge(d1.root, d2.root);
		d.root = updateStats(d.root);
		return d;
	}

	private static Node updateStats(Node root) throws Exception {
		Node stats = root.getNode("stats");
		List<Node> size = stats.getList("size");
		Node result = new Node();
		result.setName("size");

		long value = 0;
		long min = Long.MAX_VALUE;
		long max = -1;
		for (Node node : size) {
			if (node.getProperty("min").value.equals("")) {
				node.getProperty("min").value = node.value;
				node.getProperty("max").value = node.value;
				node.getProperty("avg").value = node.value;
			}
			long tmp = Long.parseLong(node.getProperty("max").value);
			if (tmp > max)
				max = tmp;
			tmp = Long.parseLong(node.getProperty("min").value);
			if (tmp < min)
				min = tmp;

			value += Long.parseLong(node.value);
			
		}
		result.count = stats.count;
		result.value = String.valueOf(value);
		result.addProperty(new Property("max", String.valueOf(max),
				Property.Type.String));
		result.addProperty(new Property("min", String.valueOf(min),
				Property.Type.String));
		float avg = (float) (value / (stats.count * 1.0));
		result.addProperty(new Property("avg", String.valueOf(avg),
				Property.Type.String));
		stats.nodes = new ArrayList<Node>();
		stats.addNode(result);
		return root;
	}

	public static Document merge(List<Document> list) throws Exception {
		if (list.isEmpty()) {
			return null;
		}
		if (list.size() == 1) {
			return list.get(0);
		}
		Document result = list.get(0);
		list.remove(0);
		for (Document document : list) {
			result = merge(result, document);
		}
		return result;
	}
}
