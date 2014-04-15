/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ifs.megaprofiler.elements;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author artur
 */
public class Document {

    private List<Integer> propertyValueIDs;
    private List<Record> records;

    public void setRecords(List<Record> records) {
        this.records = records;
    }

    public List<Integer> getPropertyValueIDs() {
        if (propertyValueIDs ==null){
            propertyValueIDs =new ArrayList<Integer>();
        }
        return propertyValueIDs;
    }

    public void setPropertyValueIDs(List<Integer> propertyValueIDs) {
        this.propertyValueIDs = propertyValueIDs;
    }

    public List<Record> getRecords() {
        if (records==null) {
            records=new ArrayList<Record>();
        }
        return records;
    }
}
