/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ifs.ultimatemetadataprofiler.elements;

/**
 *
 * @author artur
 */
public class Document {

    public String name;
    public Node root;

    public Document() {
    }

    public Document(String name, Node root) {
        this.name = name;
        this.root = root;
    }
}
