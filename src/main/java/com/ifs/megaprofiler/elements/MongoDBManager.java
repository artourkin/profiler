package com.ifs.megaprofiler.elements;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mongodb.*;
import com.mongodb.util.JSON;

import java.net.UnknownHostException;
import java.util.*;

/**
 * Created by artur on 4/11/14.
 */
public class MongoDBManager {
    private DB db;


    private List<Source> availableSources;
    private  Map<String, List<Endpoint>> conflictedRecords;
    protected List<String> significantPropertyNames;
    protected Map<Integer, Set<String>> significantPropertyValuesByID;
    Gson gson= new GsonBuilder().create();
    private int i=0;

    public MongoDBManager(List<String> significantPropertyNames) throws UnknownHostException {
        Mongo mongodb=new Mongo();
        db=mongodb.getDB("mydb");
        db.getCollection("documents").ensureIndex(new BasicDBObject("significantPropertyValueIDs", 1).append("name", true).append("unique", true));
        db.getCollection("sources").ensureIndex(new BasicDBObject("id", 1).append("name", true).append("unique", true));
        db.getCollection("properties").ensureIndex(new BasicDBObject("id", 1).append("name", true).append("unique", true));
        this.conflictedRecords=new HashMap<String, List<Endpoint>>();
        this.significantPropertyNames = significantPropertyNames;
        addPropertyNames();
        significantPropertyValuesByID = new HashMap<Integer, Set<String>>(significantPropertyNames.size());
        for (int i=0; i<significantPropertyNames.size();i++)
        {
            this.significantPropertyValuesByID.put(i, new HashSet<String>());
        }
    }

    public List<String> getSignificantPropertyNames()
    {
        return this.significantPropertyNames;
    }

    public List<Source> getAvailableSources() {
        return availableSources;
    }

    public void setAvailableSources(List<Source> availableSources) {
        this.availableSources = availableSources;
    }

    public Document createDocument(Coordinate coordinate) {

        Document result=new Document();
        result.setSignificantPropertyValueIDs(coordinate);

        return result;
    }
//    public void createDocument(Record record) {
//        List<String> significantPropertyValues = record.getSignificantPropertyValueIDs(significantPropertyNames);
//
//        DBCollection documents = db.getCollection("documents");
//        DBObject dbObject = getDBObject(result);
//        documents.insert(dbObject);
//    }


    public DBObject getDBObject(Object pojo){
        String string =  gson.toJson(pojo);
        //System.out.print(string);
        DBObject result= (DBObject) JSON.parse(string);
        return result;
    }


//    public void addEndpointsForSector(List<String> sectorCoordinates, Collection<T> endpoints) {
//        if (sectorCoordinates.size() != significantPropertyNames.size()) {
//            throw new IllegalArgumentException("Mismatch between dimensions of lattice and sector");
//        }
//
//         /* Add the coordinate values to our list of values by dimension */
//        for (int i = 0; i < significantPropertyNames.size(); i++) {
//            this.valuesByDimension.get(significantPropertyNames.get(i)).add(sectorCoordinates.get(i));
//        }
//
//        ArrayList<T> toBeAdded = new ArrayList<T>(endpoints);
//        Collection<T> existing = endpointsByCoordinate.get(new Coordinate(sectorCoordinates, valuesByDimension));
//        if (existing != null) {
//            toBeAdded.addAll(existing);
//        }
//
//        endpointsByCoordinate.put(new Coordinate(sectorCoordinates, valuesByDimension), toBeAdded);
//
//
//    }



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
        List<Endpoint> conflictedEndpoints=new ArrayList<Endpoint>();
        for(Record r: records) {
            Endpoint tmp=new Endpoint(conflictedRecordName, null);
            tmp.setPayload(r.getProperties());
            conflictedEndpoints.add(tmp);
        }
        conflictedRecords.put(conflictedRecordName,conflictedEndpoints);
    }

    public void addRecord(Record record) {
        for (Property p: record.getProperties()) {
            p.setSources(addSources(p.getSources()));
        }

        List<String> significantPropertyValues = record.getSignificantPropertyValues(significantPropertyNames);
        Document document=new Document();

        document.getRecords().add(record);
        document.setSignificantPropertyValueIDs(getCoordinates(record.getSignificantPropertyValues(significantPropertyNames)));

        DBObject dbObject = getDBObject(document);
        DBCollection documentsDB = db.getCollection("documents");
        documentsDB.insert(dbObject);

       //TODO: temporary call. Should be removed
        updateSignificantPropertyValuesByID();
    }

    private void addProperty(Property property) {
        if (significantPropertyNames.contains(property.getName())) {
            DBCollection propertyValuesDB = db.getCollection("propertyValues");
            DBCursor dbCursor = propertyValuesDB.find();
            if (dbCursor.hasNext()) {
                int size = dbCursor.size();
                significantPropertyValuesByID=new HashMap<Integer, Set<String>>(size);
                while (dbCursor.hasNext())
                {
                    DBObject next = dbCursor.next();
                    Integer id = (Integer) next.get("id");
                    String name =(String) next.get("name");
                    significantPropertyNames.add(id, name);

                }
            }




            significantPropertyValuesByID.get(significantPropertyNames.indexOf(property.getName())).add(property.getValue());

        }
    }

    private void updateSignificantPropertyValuesByID()
    {
        DBCollection propertyValuesDB = db.getCollection("propertyValues");
        Iterator<Map.Entry<Integer, Set<String>>> iterator = significantPropertyValuesByID.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Integer, Set<String>> entry = iterator.next();
            DBObject dbObject = getDBObject(entry);
            propertyValuesDB.insert(dbObject);
        }
    }

    private void addPropertyNames(){
        DBCollection propertiesDB = db.getCollection("properties");
        DBCursor dbCursor = propertiesDB.find();
        if (dbCursor.hasNext()) {
            int size = dbCursor.size();
            significantPropertyNames=new ArrayList<String>(size);
            while (dbCursor.hasNext())
            {
                DBObject next = dbCursor.next();
                Integer id = (Integer) next.get("id");
                String name =(String) next.get("name");
                significantPropertyNames.add(id, name);

            }
        }
        for (int i=0; i<significantPropertyNames.size();i++) {
            String propertyname = significantPropertyNames.get(i);
            DBObject dbObject=new BasicDBObject("id", i).append("name", propertyname);
            propertiesDB.insert(dbObject);
        }
    }

    private Integer getPropertyIDByName(String propertyName) {
        return significantPropertyNames.indexOf(propertyName);
    }

    private List<Source> addSources(List<Source> sources) {
        List<Source> result=new ArrayList<Source>();
        for (Source s: sources) {
            result.add(addSource(s));
        }
        return result;
    }

    private Source addSource(Source source) {
        if (availableSources != null){
            if (!availableSources.contains(source)){
                source.setId(i++);
                availableSources.add(source);
                DBCollection sourcesDB = db.getCollection("sources");
                DBObject dbObject = getDBObject(source);
                sourcesDB.insert(dbObject);
                return source;
            } else {
                int indexOf = availableSources.indexOf(source);
                return availableSources.get(indexOf);
            }
        } else {
            availableSources = new ArrayList<Source>();
            DBCollection sourcesDB = db.getCollection("sources");
            DBCursor cursor = sourcesDB.find();
            while (cursor.hasNext()){
                DBObject dbObject = cursor.next();
                Source tmp_source = gson.fromJson(dbObject.toString(), Source.class);
                availableSources.add(tmp_source);
            }
            return addSource(source);
        }
    }

    private Source getSource(String sourceName, String sourceVersion) {
        Source tmp=new Source(sourceName,sourceVersion);
        return addSource(tmp);
    }

    private Source getSource (String sourceID){
        if (availableSources != null && availableSources.size()>0){
            for (Source s : availableSources) {
                if (s.getId().equals(sourceID))
                    return s;
            }
        }
        return null;
    }

    private Integer getIDBySource(Source source)
    {
        return addSource(source).getId();
    }

    private List<Source> getSources(List<String> sourceIDs) {
        List<Source> result=new ArrayList<Source>();
        for (String sourceID : sourceIDs){
            result.add(getSource(sourceID));
        }
        return result;
    }
//    public List<Record> getRecords(List<String> propertyValues) {
//        List<Record> result=new ArrayList<Record>();
//        List<String> propertyNames = lattice.getSignificantPropertyNames();
//        Collection<Endpoint> endpointsForSector = lattice.getEndpointsForSector(propertyValues);
//
//        for (Endpoint e: endpointsForSector){
//            List<List<String>> sources = e.getSources();
//            Record r=new Record();
//            r.setUid(e.getUid());
//            for (int i=0; i< propertyValues.size(); i++){
//                r.getProperties().add(new Property(propertyNames.get(i),propertyValues.get(i),getSources(sources.get(i))));
//            }
//            result.add(r);
//        }
//        return result;
//    }


    Coordinate getCoordinates(List<String> significantPropertyValues){
        return new Coordinate(significantPropertyValues, significantPropertyValuesByID);
    }

    protected class Coordinate extends ArrayList<Integer> implements Comparable<Coordinate> {

        public Coordinate(List<Integer> sectorCoordinates) {
            super(sectorCoordinates);
        }

        public Coordinate(List<String> sectorCoordinates,Map<Integer, Set<String>> valuesByDimension) {
            ArrayList<Integer> integers = ToNumericCoordinates(sectorCoordinates, valuesByDimension);
            this.addAll(integers);
        }

        public ArrayList<Integer> ToNumericCoordinates(List<String> coordinates, Map<Integer, Set<String>> valuesByDimension){
            ArrayList<Integer> result=new ArrayList<Integer>();

            for (int i = 0; i < significantPropertyNames.size(); i++) {
                valuesByDimension.get(i).add(coordinates.get(i));
            }

            for (int i=0; i<coordinates.size();i++)
            {
                List<String> strings =  new ArrayList<String>();
                strings.addAll(valuesByDimension.get(i));
                int indexOf = strings.indexOf(coordinates.get(i));
                result.add(indexOf);
            }
            return result;
        }

        @Override
        public int compareTo(Coordinate other) {

            for (int i = 0; i < this.size(); i++) {
                if (other.size() < i) {
                    return -1;
                }
                if (!other.get(i).equals(this.get(i))) {
                    return this.get(i).compareTo(other.get(i));
                }
            }

            return 0;
        }

    }

}
