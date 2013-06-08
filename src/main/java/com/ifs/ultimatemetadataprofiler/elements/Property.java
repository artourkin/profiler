/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ifs.ultimatemetadataprofiler.elements;

/**
 *
 * @author artur
 */
public class Property {

    public String key;
    public String value;

    public enum Type {

        Float, String, Integer
    };
    public Type type;

    public Property() {
    }

    public Property(java.lang.String key, java.lang.String value, Type type) {
        this.key = key;
        this.value = value;
        this.type = type;
    }
}
