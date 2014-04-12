package com.ifs.megaprofiler.elements;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by artur on 4/10/14.
 */
public class Endpoint {
    private List<List<String>> sourceIDs;
    private List<Property> payload;
    private String uid;

    public Endpoint(String uid, List<List<String>> sources) {
        this.sourceIDs = sources;
        this.uid=uid;
        this.payload =new ArrayList<Property>();
    }

    public List<List<String>> getSources() {
        return sourceIDs;
    }

    public void setSources(List<List<String>> sourceIDs) {
        this.sourceIDs = sourceIDs;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public List<Property> getPayload() {
        return payload;
    }

    public void setPayload(List<Property> payload) {
        this.payload = payload;
    }
}
