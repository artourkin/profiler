package com.ifs.megaprofiler.core;

import com.ifs.megaprofiler.elements.Document;
import com.ifs.megaprofiler.elements.Property;
import com.ifs.megaprofiler.elements.Record;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by artur on 4/10/14.
 */
public class Recorder {
    private List<String> availableProperties;

    public Recorder(List<String> availableProperties) {
        this.availableProperties = availableProperties;
    }
    int i=0;
    public List<String> getAvailableProperties() {
        return availableProperties;
    }

    public void setAvailableProperties(List<String> availableProperties) {
        this.availableProperties = availableProperties;
    }

    public Record readDocument(Document document) {


        String uid=Integer.toString(i++);

        Record result=new Record(uid,new ArrayList<Property>());

        for (String propertyName:availableProperties){
            String propertyValue = document.getValue(propertyName);
            Property p =new Property(propertyName,propertyValue);
            result.getProperties().add(p);
        }


        return result;

    }
}
