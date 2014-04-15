package com.ifs.megaprofiler.elements;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by artur on 4/10/14.
 */
public class Endpoint {
    private List<Integer> propertyValues;
    private Record record;

    public List<Integer> getPropertyValues() {
        return propertyValues;
    }

    public void setCoordinates(List<Integer> propertyValues) {
        this.propertyValues = propertyValues;
    }

    public Record getRecord() {
        return record;
    }

    public void setRecord(Record record) {
        this.record = record;
    }
}

