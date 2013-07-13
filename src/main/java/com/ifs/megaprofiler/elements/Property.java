/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ifs.megaprofiler.elements;

import java.util.List;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import com.ifs.megaprofiler.maths.Maths;

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

    public void setKey(String key) {
        this.key = key;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Property(java.lang.String key, java.lang.String value, Type type) {
        this.key = key;
        this.value = value;
        this.type = type;
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
