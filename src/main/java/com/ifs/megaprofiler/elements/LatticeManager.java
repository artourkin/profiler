package com.ifs.megaprofiler.elements;

import com.ifs.megaprofiler.maths.Lattice;

import java.util.*;

/**
 * Created by artur on 4/11/14.
 */
public class LatticeManager {
    private Lattice<Endpoint> lattice;
    private List<Source> availableSources;
    private  Map<String, List<Endpoint>> conflictedRecords;
    private int i=0;

    public LatticeManager(List<String> propertyNames) {
        this.lattice = new Lattice<Endpoint>(propertyNames);
        this.conflictedRecords=new HashMap<String, List<Endpoint>>();
    }

    public Lattice<Endpoint> getLattice() {
        return lattice;
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

    public void addRecords(List<Record> records) {
        if (records.size()==0){
            return;
        } else if (records.size()==1) {
            addRecord(records.get(0));
        } else {
            Set<String> recordUids=new HashSet<String>();
            for (Record r: records) {
                recordUids.add(r.getUid());
            }
            if (recordUids.size()==1) {
                addConflictedRecords(records);
            }
        }
    }
    public void addConflictedRecords(List<Record> records){
        String conflictedRecordName = records.get(0).getUid();
        //if (conflictedRecordName.equals("/home/petrov/taverna/tmp/006/006185.jpg")){
        //    System.out.print("HOLYCRAP");
       // }
        List<Endpoint> conflictedEndpoints=new ArrayList<Endpoint>();
        for(Record r: records) {
            Endpoint tmp=new Endpoint(conflictedRecordName, null);
            tmp.setPayload(r.getProperties());
            conflictedEndpoints.add(tmp);
        }
        conflictedRecords.put(conflictedRecordName,conflictedEndpoints);
    }
    public void addRecord(Record record) {
        if (lattice != null){
            List<String> dimensionNames = lattice.getDimensionNames();
            List<Endpoint> endpoints=new ArrayList<Endpoint>();
            List<Property> payload=new ArrayList<Property>();
            for (Property p: record.getProperties()) {
                p.setSources(addSources(p.getSources()));
                if (!dimensionNames.contains(p.getKey())) {
                    payload.add(p);
                }
            }
            Endpoint endpoint=new Endpoint(record.getUid(),record.getSourceIDs(dimensionNames));
            endpoint.setPayload(payload);
            endpoints.add(endpoint);
            List<String> propertyValues = record.getPropertyValues(dimensionNames);
            lattice.addEndpointsForSector(propertyValues,endpoints);
        }
    }

    private List<Source> addSources(List<Source> sources) {
        List<Source> result=new ArrayList<Source>();
        for (Source s: sources) {
            result.add(addSource(s));
        }
        return result;
    }

    private Source addSource(Source source) {
        if (availableSources != null && availableSources.size()>0){
            if (!availableSources.contains(source)){
                source.setId(Integer.toString(i++));
                availableSources.add(source);
                return source;
            } else {
                int indexOf = availableSources.indexOf(source);
                return availableSources.get(indexOf);
            }
        } else {
            availableSources = new ArrayList<Source>();
            source.setId(Integer.toString(i++));
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
