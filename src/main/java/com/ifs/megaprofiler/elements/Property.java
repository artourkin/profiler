/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ifs.megaprofiler.elements;

import com.google.gson.annotations.Expose;
import org.apache.commons.lang.builder.HashCodeBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author artur
 */
public class Property {
    @Expose
    private String name;
    @Expose
    private String value;
    @Expose
    private List<Source> sources;

    public enum Type {
        Float, String, Integer
    };

    public Type type;

    public Property() {
    }

    public Property(String name, String value) {
        this.name = name;
        this.value = value;
        this.sources=new ArrayList<Source>();
    }

    public Property(String name, String value, List<Source> sources) {
        this.name = name;
        this.value = value;
        this.sources = sources;
    }

    public String getName() {
        return name;
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

    public void setName(String name) {
        this.name = name;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setType(Type type) {
        this.type = type;
    }


    public List<Integer> getSourceIDs(){
        List<Integer> result=new ArrayList<Integer>();
        for(Source s: sources){
            result.add(s.getId());
        }
        return result;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(13, 37).append(value).append(name)
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
        if (this.name.equals(p.name) && this.value.equals(p.value)) {
            return true;
        }
        return false;
    }
}
