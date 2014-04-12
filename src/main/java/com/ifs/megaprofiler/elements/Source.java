package com.ifs.megaprofiler.elements;

import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * Created by artur on 4/10/14.
 */
public class Source {
    private String id;

    private String name;

    private String version;

    private int i=0;

    public Source(String name, String version){
        this.id= Integer.toString(i++);
        this.name=name;
        this.version=version;
    }

    public Source(String name){
        this.id= Integer.toString(i++);
        this.name=name;
        this.version= "" ;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(13, 37).append(name).append(version)
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
        if (!(obj instanceof Source)) {
            return false;
        }
        Source s = (Source) obj;
        if (this.name.equals(s.getName()) && this.version.equals(s.getVersion())) {
            return true;
        }
        return false;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
