/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ifs.ultimatemetadataprofiler.maths;

import com.ifs.ultimatemetadataprofiler.elements.Document;
import com.ifs.ultimatemetadataprofiler.elements.Node;

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
        if (!n1.equals(n2)) {
            return null;
        }
        for (Node tmp : n2.nodes) {
            if (n1.nodes.contains(tmp)) {
                Node n = n1.nodes.get(n1.nodes.indexOf(tmp));
                merge(tmp, n);
            } else {
                n1.nodes.add(tmp);
            }
        }
       // n2 = n1;
        return n1;

    }
     public static Document merge(Document d1, Document d2) {
        Document d;
        d = new Document();
        d.root=merge(d1.root, d2.root);
        d.name= "("+d1.name+"+" + d2.name + ")";
        return d;
    }
}
