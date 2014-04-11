/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ifs.megaprofiler.elements;

import org.apache.commons.lang.builder.HashCodeBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author artur
 */
public class Property {

    private String key;
    private String value;

    private List<Source> sources;

    public enum Type {
        Float, String, Integer
    };

    public Type type;

    public Property() {
        this.type=Type.String;
    }

    public Property(String key, String value, Type type) {
        this.key = key;
        this.value = value;
        this.type = type;
    }
    public Property(String key, String value) {
        this.key = key;
        this.value = value;
        this.type = Type.String;
        this.sources=new ArrayList<Source>();
    }

    public Property(String key, String value, List<Source> sources) {
        this.key = key;
        this.value = value;
        this.type = Type.String;
        this.sources = sources;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public List<Source> getSources() {
        return sources;
    }

    public void setSources(List<Source> sources) {
        this.sources = sources;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setType(Type type) {
        this.type = type;
    }


    public List<String> getSourceIDs(){
        List<String> result=new ArrayList<String>();
        for(Source s: sources){
            result.add(s.getId());
        }
        return result;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(13, 37).append(value).append(key)
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
        if (!(obj instanceof Property)) {
            return false;
        }
        Property p = (Property) obj;
        if (this.key.equals(p.key) && this.value.equals(p.value)) {
            return true;
        }
        return false;
    }
}
