package com.ifs.megaprofiler.elements;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by artur on 4/10/14.
 */
public class Record {
    private List<Property> properties;
    private String uid;

    public Record(){
        this.properties=new ArrayList<Property>();
    }
    public Record(String uid, List<Property> properties) {
        this();
        this.uid = uid;
        addProperties(properties);
    }

    public String getUid(){
        return uid;
    }

    public void setUid(String uid){
        this.uid=uid;
    }

    public List<Property> getProperties() {
        return properties;
    }

    public void setProperties(List<Property> properties) {
        this.properties = properties;
    }

    public void addProperties(List<Property> properties){
        this.properties.addAll(properties);
    }
    public List<String> getPropertiesToString() {
        List<String> result = new ArrayList<String>();
        for (Property p: this.properties) {
            if (!result.contains(p.getKey())) {
                result.add(p.getKey());
            }
        }
        return result;
    }

    /* Extends propertyList with empty string to comply with dimensions */
    public List<String> complyToLatticeDimensions(List<String> dimensions){
        List<String> result = new ArrayList<String>();
        List<String> propertiesToString = getPropertiesToString();

        for (String s: dimensions) {
            if(propertiesToString.contains(s)) {
                result.add(s);
            } else {
                result.add("");
            }
        }
        return result;
    }

    public List<List<String>> getSourceIDs(List<String> propertyNames){
        List<List<String>> result= new ArrayList<List<String>>();
        for (String propertyName: propertyNames){
            List<String> sourceIDsbyProperty=new ArrayList<String>();   //TODO:check if null is the best value here. Maybe empty list is better
            for (Property p : properties){
                if (p.getKey().equals(propertyName)) {
                    sourceIDsbyProperty = p.getSourceIDs();
                    break;
                }
            }
            result.add(sourceIDsbyProperty);
        }
        return result;
    }

    public List<List<Source>> getSources(List<String> propertyNames){
        List<List<Source>> result= new ArrayList<List<Source>>();
        for (String propertyName: propertyNames){
            List<Source> sourcesByPropertyName=new ArrayList<Source>();   //TODO:check if null is the best value here. Maybe empty list is better
            for (Property p : properties){
                if (p.getKey().equals(propertyName)) {
                    sourcesByPropertyName = p.getSources();
                    break;
                }
            }
            result.add(sourcesByPropertyName);
        }
        return result;
    }


    public List<String> getPropertyValues(List<String> propertyNames){
        List<String> result = new ArrayList<String>();
        for (String propertyName: propertyNames){
            String propertyValue="";
            for (Property p : properties){
                if (p.getKey().equals(propertyName)) {
                    propertyValue=p.getValue();
                    break;
                }
            }
            result.add(propertyValue);
        }
        return result;
    }


}
