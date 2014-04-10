package com.ifs.megaprofiler.elements;

/**
 * Created by artur on 4/10/14.
 */
public class Source {
    private String id;

    private String name;

    public String getId() {
        return id;
    }

    public Source(String id,String name){
        this.id=id;
        this.name=name;
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
}
