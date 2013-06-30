/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ifs.megaprofiler.elements;

/**
 *
 * @author artur
 */
public class Document {

    public String name;
    public Node root;

    public Document() {
    	name="";
    	root=new Node();
    }

    public Document(String name, Node root) {
        this.name = name;
        this.root = root;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
    public void setRoot(Node root) {
        this.root = root;
    }

    public Node getRoot() {
        return this.root;
    }
}
