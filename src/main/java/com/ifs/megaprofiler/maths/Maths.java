/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ifs.megaprofiler.maths;

import com.ifs.megaprofiler.elements.Document;
import com.ifs.megaprofiler.elements.Node;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

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

    public static Node merge(Node n1, Node n2) {
        if (n1 == null || n2 == n1) {
            return null;
        }
        if (n1.name == null) {
            return new Node(n2);
        }
        if (n2.name == null) {
            return new Node(n1);
        }
        Node result = new Node();
        result.properties.addAll(n1.properties);
        result.setName(n1.name);
        result.nodes.addAll(n1.nodes);
        result.count = n1.count + n2.count;
        result.value = n1.value;
        for (Node n2sub : n2.nodes) {
            int n2subindex = result.nodes.indexOf(n2sub);
            if (n2subindex >= 0) { // we found a node   
                Node resultsub = result.nodes.get(n2subindex);
                resultsub = merge(resultsub, n2sub);
                result.nodes.set(n2subindex, resultsub);
            } else {
                result.nodes.add(n2sub);
            }
        }
        return result;
    }

    public static Document merge(Document d1, Document d2) {
        Document d;
        d = new Document();
        d.root = merge(d1.root, d2.root);
        d.name = "(" + d1.name + "+" + d2.name + ")";
        return d;
    }

    public static Document merge(List<Document> list) {
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
