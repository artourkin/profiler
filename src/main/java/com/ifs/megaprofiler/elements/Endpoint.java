package com.ifs.megaprofiler.elements;

import com.ifs.megaprofiler.maths.Lattice;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by artur on 4/10/14.
 */
public class Endpoint {
    private Coordinate coordinates;
    private Record record;

    public Coordinate getPropertyValues() {
        return coordinates;
    }

    public void setCoordinates(Coordinate propertyValues) {
        this.coordinates = propertyValues;
    }

    public Record getRecord() {
        return record;
    }

    public void setRecord(Record record) {
        this.record = record;
    }
}

