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
        this.uid = uid;
        this.properties = properties;
    }

    public List<Property> getProperties() {
        return properties;
    }

    public void setProperties(List<Property> properties) {
        this.properties = properties;
    }

    public List<String> getPropertiesToString() {
        List<String> result = new ArrayList<String>();
        for (Property p: this.properties) {
            if (!result.contains(p.key)) {
                result.add(p.key);
            }
        }
        return result;
    }


    /* Extends propertyList with empty string to comply with dimensions */
    public List<String> complyToDimensions(List<String> dimensions){
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

    public List<List<String>> getSources(List<String> dimensions){
        List<List<String>> result= new ArrayList<List<String>>();
        for (String dimension: dimensions){
            List<String> sourceIDsbyProperty=null;   //TODO:check if null is the best value here. Maybe empty list is better
            for (Property p : properties){
                if (p.key.equals(dimension)) {
                    sourceIDsbyProperty = p.getSourceIDs();
                    break;
                }
            }
            result.add(sourceIDsbyProperty);
        }
        return result;
    }

    public List<String> getCoordinates(List<String> dimensions){
        List<String> result = new ArrayList<String>();
        for (String dimension: dimensions){
            String coordinate="";
            for (Property p : properties){
                if (p.key.equals(dimension)) {
                    coordinate=p.value;
                    break;
                }
            }
            result.add(coordinate);
        }
        return result;
    }

    public Endpoint getEndpoint(List<String> dimensions){
        return new Endpoint(uid, getSources(dimensions));
    }


}
