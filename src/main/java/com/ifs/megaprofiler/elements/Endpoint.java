package com.ifs.megaprofiler.elements;

import java.util.List;

/**
 * Created by artur on 4/10/14.
 */
public class Endpoint {
    private List<List<String>> sources;
    private String uid;

    public Endpoint(String uid, List<List<String>> sources) {
        this.sources = sources;
        this.uid=uid;
    }

    public List<List<String>> getSources() {
        return sources;
    }

    public void setSources(List<List<String>> sources) {
        this.sources = sources;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
