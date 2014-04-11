package com.ifs.megaprofiler.elements;

import com.ifs.megaprofiler.maths.Lattice;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by artur on 4/11/14.
 */
public class LatticeManager {
    private Lattice<Endpoint> lattice;
    private List<Source> availableSources;

    public LatticeManager(List<String> propertyNames) {
        this.lattice = new Lattice<Endpoint>(propertyNames);
    }

    public List<String> getPropertyNames()
    {
        if (lattice != null) {
            return lattice.getDimensionNames();
        }
        return new ArrayList<String>();
    }

    public List<Source> getAvailableSources() {
        return availableSources;
    }

    public void setAvailableSources(List<Source> availableSources) {
        this.availableSources = availableSources;
    }

    public boolean addRecord(Record record) {
        if (lattice != null){
            List<String> dimensionNames = lattice.getDimensionNames();
            List<Endpoint> endpoints=new ArrayList<Endpoint>();
            endpoints.add(new Endpoint(record.getUid(),record.getSourceIDs(dimensionNames)));
            List<String> propertyValues = record.getPropertyValues(dimensionNames);
            lattice.addEndpointsForSector(propertyValues,endpoints);
            return true;
        }
        return false;
    }

    private Source addSource(Source source) {
        if (availableSources != null && availableSources.size()>0){
            if (!availableSources.contains(source)){
                availableSources.add(source);
                return source;
            } else {
                int indexOf = availableSources.indexOf(source);
                return availableSources.get(indexOf);
            }
        } else {
            availableSources = new ArrayList<Source>();
            availableSources.add(source);
            return source;
        }
    }

    public Source getSource(String sourceName, String sourceVersion) {
        Source tmp=new Source(sourceName,sourceVersion);
        return addSource(tmp);
    }

    public Source getSource (String sourceID){
        if (availableSources != null && availableSources.size()>0){
            for (Source s : availableSources) {
                if (s.getId().equals(sourceID))
                    return s;
            }
        }
        return null;
    }

    public List<Source> getSources(List<String> sourceIDs) {
        List<Source> result=new ArrayList<Source>();
        for (String sourceID : sourceIDs){
            result.add(getSource(sourceID));
        }
        return result;
    }
    public List<Record> getRecords(List<String> propertyValues) {
        List<Record> result=new ArrayList<Record>();
        List<String> propertyNames = lattice.getDimensionNames();
        Collection<Endpoint> endpointsForSector = lattice.getEndpointsForSector(propertyValues);

        for (Endpoint e: endpointsForSector){
            List<List<String>> sources = e.getSources();
            Record r=new Record();
            r.setUid(e.getUid());
            for (int i=0; i< propertyValues.size(); i++){
                r.getProperties().add(new Property(propertyNames.get(i),propertyValues.get(i),getSources(sources.get(i))));
            }
            result.add(r);
        }
        return result;
    }
}
