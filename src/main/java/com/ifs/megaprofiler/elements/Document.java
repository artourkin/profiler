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

    private List<Integer> significantPropertyValueIDs;
    private List<Record> records;

    public void setRecords(List<Record> records) {
        this.records = records;
    }

    public List<Integer> getSignificantPropertyValueIDs() {
        if (significantPropertyValueIDs ==null){
            significantPropertyValueIDs =new ArrayList<Integer>();
        }
        return significantPropertyValueIDs;
    }

    public void setSignificantPropertyValueIDs(List<Integer> significantPropertyValueIDs) {
        this.significantPropertyValueIDs = significantPropertyValueIDs;
    }

    public List<Record> getRecords() {
        if (records==null) {
            records=new ArrayList<Record>();
        }
        return records;
    }
}
