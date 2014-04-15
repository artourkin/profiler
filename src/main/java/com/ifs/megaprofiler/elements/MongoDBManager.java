package com.ifs.megaprofiler.elements;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mongodb.*;
import com.mongodb.util.JSON;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.*;

/**
 * Created by artur on 4/11/14.
 */
public class MongoDBManager {
    protected List<String> propertyNames;
    protected Map<Integer, Set<String>> propertyValuesByID;
    private Gson gson = new GsonBuilder().create();
    private DB db;
    private DBCollection recordsDB;
    private DBCollection conflictedRecordsDB;
    private DBCollection propertiesDB;
    private DBCollection propertyValuesDB;
    private DBCollection sourcesDB;
    private List<Source> availableSources;
   // private Map<String, List<Endpoint>> conflictedRecords;
    private int i = 0;

    public MongoDBManager(List<String> significantPropertyNames) throws UnknownHostException {
        //this.conflictedRecords = new HashMap<String, List<Endpoint>>();
        initialize(significantPropertyNames);
    }

    private void initialize(List<String> significantPropertyNames) {
        MongoClient mongodb = null;
        try {
            mongodb = new MongoClient();
        } catch (UnknownHostException e) {
            e.printStackTrace();
            System.exit(0);
        }
        db = mongodb.getDB("mydb");
        propertiesDB = db.getCollection("properties");
        propertyValuesDB = db.getCollection("propertyValues");
        recordsDB = db.getCollection("records");
        conflictedRecordsDB = db.getCollection("conflictedRecords");
        sourcesDB = db.getCollection("sources");

        try {
            fetchPropertyNames(significantPropertyNames);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(0);
        }
        fetchSources();
        fetchPropertyValuesByID();
    }

    private void fetchPropertyValuesByID() {
        propertyValuesByID = new HashMap<Integer, Set<String>>(propertyNames.size());
        for (int i = 0; i < propertyNames.size(); i++) {
            this.propertyValuesByID.put(i, new HashSet<String>());
        }

        DBCursor dbCursor = propertyValuesDB.find();
        if (dbCursor.size()>0) {
            while (dbCursor.hasNext()) {
                DBObject dbObject = dbCursor.next();
                Integer key=(Integer) dbObject.get("key");
                List<String> values =(List<String>) dbObject.get("value");
                propertyValuesByID.put(key, new HashSet<String>(values));
            }
        }
    }

    private void fetchPropertyNames(List<String> significantPropertyNames) throws IOException {
        if (significantPropertyNames==null || significantPropertyNames.size()==0){
            throw new IOException("significant properties are not defined");
        }
        DBCursor dbCursor = propertiesDB.find();
        if (dbCursor.size()>0) {
            int size = dbCursor.size();
            significantPropertyNames = new ArrayList<String>(size);
            while (dbCursor.hasNext()) {
                DBObject next = dbCursor.next();
                Integer id = (Integer) next.get("id");
                String name = (String) next.get("name");
                significantPropertyNames.add(id, name);
            }
        }
        this.propertyNames = significantPropertyNames;
    }

    private void fetchSources() {
        availableSources = new ArrayList<Source>();

        DBCursor dbCursor = sourcesDB.find();
        if (dbCursor.size()>0) {
            while (dbCursor.hasNext()) {
                DBObject dbObject = dbCursor.next();
                Source tmp_source = gson.fromJson(dbObject.toString(), Source.class);
                availableSources.add(tmp_source);
            }
        }
    }

    public void updateDB()
    {
        updatePropertyNamesDB();
        updateSources();
        updatePropertyValuesByID();
    }

    private void updatePropertyNamesDB() {
        propertiesDB.drop();
        for (int i = 0; i < propertyNames.size(); i++) {
            String propertyname = propertyNames.get(i);
            DBObject dbObject = new BasicDBObject("id", i).append("name", propertyname);
            propertiesDB.insert(dbObject);
        }
    }
    private void updatePropertyValuesByID() {
        propertyValuesDB.drop();
        Iterator<Map.Entry<Integer, Set<String>>> iterator = propertyValuesByID.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Integer, Set<String>> entry = iterator.next();
            DBObject dbObject = getDBObject(entry);
            propertyValuesDB.insert(dbObject);
        }
    }
    private void updateSources()   {
        sourcesDB.drop();
        for (int i = 0; i < availableSources.size(); i++) {
            DBObject dbObject = getDBObject(availableSources.get(i));
            sourcesDB.insert(dbObject);
        }
    }




    public DBObject getDBObject(Object pojo) {
        String string = gson.toJson(pojo);
        //System.out.print(string);
        DBObject result = (DBObject) JSON.parse(string);
        return result;
    }





    public void addRecords(List<Record> records) {
        if (records.size() == 0) {
            return;
        } else if (records.size() == 1) {
            addRecord(records.get(0));
        } else {
            Set<String> recordUids = new HashSet<String>();
            for (Record r : records) {
                recordUids.add(r.getUid());
            }
            if (recordUids.size() == 1) {
                addConflictedRecords(records);
            }
        }
    }

    public void addConflictedRecords(List<Record> records) {
        String conflictedRecordName = records.get(0).getUid();
        List<Endpoint> conflictedEndpoints = new ArrayList<Endpoint>();
        for (Record r : records) {
            Endpoint tmp = new Endpoint();
            tmp.setRecord(r);
            tmp.setCoordinates(getCoordinates(r));
            conflictedEndpoints.add(tmp);
        }
        DBObject dbObject = new BasicDBObject("uid", conflictedRecordName).append("records",getDBObject(conflictedEndpoints));
        conflictedRecordsDB.insert(dbObject);
    }

    public void addRecord(Record record) {
        for (Property p : record.getProperties()) {
            p.setSources(addSources(p.getSources()));
        }

        Endpoint endpoint=new Endpoint();
        endpoint.setCoordinates(getCoordinates(record));
        endpoint.setRecord(record);

        DBObject dbObject = getDBObject(endpoint);
        recordsDB.insert(dbObject);


    }

    private void addPropertyValue(Property property) {
        if (propertyNames.contains(property.getName())) {
            propertyValuesByID.get(propertyNames.indexOf(property.getName())).add(property.getValue());
        }
    }

    private void addPropertyValues(List<Property> properties) {
        for(Property p: properties) {
            addPropertyValue(p);
        }
    }

    private List<Source> addSources(List<Source> sources) {
        List<Source> result = new ArrayList<Source>();
        for (Source s : sources) {
            result.add(addSource(s));
        }
        return result;
    }

    private Source addSource(Source source) {
        if (availableSources != null) {
            if (!availableSources.contains(source)) {
                source.setId(i++);
                availableSources.add(source);
                return source;
            } else {
                int indexOf = availableSources.indexOf(source);
                return availableSources.get(indexOf);
            }
        } else {
            availableSources = new ArrayList<Source>();
            return addSource(source);
        }
    }

    private Source getSource(String sourceID) {
        if (availableSources != null && availableSources.size() > 0) {
            for (Source s : availableSources) {
                if (s.getId().equals(sourceID))
                    return s;
            }
        }
        return null;
    }

    public void clear() {
        db.dropDatabase();
    }

    private Coordinate getCoordinates(Record record) {
        addPropertyValues(record.getProperties());
        return new Coordinate(record.getSignificantPropertyValues(propertyNames));
    }



    protected class Coordinate extends ArrayList<Integer> implements Comparable<Coordinate> {

        public Coordinate(List<String> sectorCoordinates) {
            ArrayList<Integer> integers = ToNumericCoordinates(sectorCoordinates);
            this.addAll(integers);
        }

        public ArrayList<Integer> ToNumericCoordinates(List<String> coordinates) {
            ArrayList<Integer> result = new ArrayList<Integer>();

            for (int i = 0; i < propertyNames.size(); i++) {
                propertyValuesByID.get(i).add(coordinates.get(i));
            }

            for (int i = 0; i < coordinates.size(); i++) {
                List<String> strings = new ArrayList<String>();
                strings.addAll(propertyValuesByID.get(i));
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
