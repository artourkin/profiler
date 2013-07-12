/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ifs.megaprofiler.maths;

import com.ifs.megaprofiler.elements.Document;
import com.ifs.megaprofiler.elements.Node;
import java.util.Iterator;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;

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
////            Node result = new Node(n1);
////            List<Node> n2List = depthFirstSearch(n2);
////            for (Node node : n2List) {
////                if (result.contains(node)) {
////                    Node another = result.get(node);
////                    another.count += node.count;
////                } else {
////                    Node nodeParent = n2.getParent(node);
////                    if (nodeParent==null)
////                    {
////                        int stop=0;
////                    
////                    }
////                    Node another = result.get(nodeParent);
////                    another.addNode(new Node(node));
////                }
////            }
////            return result;
        }
        return null;
    }

    public static Document merge(Document d1, Document d2) throws Exception {
        Document d;
        d = new Document();
        d.root = merge(d1.root, d2.root);
        d.name = "(" + d1.name + "+" + d2.name + ")";
        return d;
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
