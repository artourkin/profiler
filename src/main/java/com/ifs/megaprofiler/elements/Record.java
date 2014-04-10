package com.ifs.megaprofiler.elements;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by artur on 4/10/14.
 */
public class Record {
    private List<Property> properties;

    public Record(){
        this.properties=new ArrayList<Property>();
    }
    public Record(List<Property> properties) {
        this.properties = properties;
    }

    public List<Property> getProperties() {
        return properties;
    }

    public void setProperties(List<Property> properties) {
        this.properties = properties;
    }

    public List<String> getProperties2String() {
        List<String> result = new ArrayList<String>();
        for (Property p: this.properties) {
            if (!result.contains(p.key)) {
                result.add(p.key);
            }
        }
        return result;
    }

    public List<Endpoint> getEndpoints(){
        List<Endpoint> result=new ArrayList<Endpoint>();
        for (Property p: this.properties) {
            result.add(new Endpoint(p.getSourceIDs()));
        }
        return result;
    }


}
