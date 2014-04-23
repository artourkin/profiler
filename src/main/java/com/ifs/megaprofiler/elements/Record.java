package com.ifs.megaprofiler.elements;

import com.google.gson.annotations.Expose;

import java.util.*;

/**
 * Created by artur on 4/10/14.
 */
public class Record {
    @Expose
    private List<Property> properties;
    @Expose
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
            if (!result.contains(p.getName())) {
                result.add(p.getName());
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

    public List<List<Integer>> getSourceIDsbyPropertyNames(List<String> propertyNames){
        List<List<Integer>> result= new ArrayList<List<Integer>>();
        for (String propertyName: propertyNames){
            List<Integer> sourceIDsbyProperty=new ArrayList<Integer>();   //TODO:check if null is the best value here. Maybe empty list is better
            for (Property p : properties){
                if (p.getName().equals(propertyName)) {
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
                if (p.getName().equals(propertyName)) {
                    sourcesByPropertyName = p.getSources();
                    break;
                }
            }
            result.add(sourcesByPropertyName);
        }
        return result;
    }


    public List<String> getSignificantPropertyValues(Map<String, List<String>> propertyValues){
        List<String> result = new ArrayList<String>();
        for (Property p : properties) {
            if (!propertyValues.keySet().contains(p.getName())) {
                continue;
            }
            String encodedP="";
            List<String> tmp_list= new ArrayList<String>(propertyValues.keySet());
            encodedP+= tmp_list.indexOf(p.getName())+"_";
            encodedP+= propertyValues.get(p.getName()).indexOf(p.getValue());
            result.add(encodedP);
        }

        return result;
    }



}
