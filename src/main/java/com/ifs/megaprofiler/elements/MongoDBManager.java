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
    protected Map<String, List<String>> propertyValuesByName;
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
        Mongo mongodb = null;
        try {
            mongodb = new Mongo();
        } catch (UnknownHostException e) {
            e.printStackTrace();
            System.exit(0);
        }
        db = mongodb.getDB("mydb");
        propertiesDB = db.getCollection("properties");
        propertyValuesDB = db.getCollection("propertyValues");
        recordsDB = db.getCollection("records");
        recordsDB.ensureIndex(new BasicDBObject("coordinates", 1).append("name", true));
        conflictedRecordsDB = db.getCollection("conflictedRecords");
        sourcesDB = db.getCollection("sources");

        try {
            fetchPropertyValuesByName(significantPropertyNames);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(0);
        }
        fetchSources();

    }

    private void fetchPropertyValuesByName(List<String> propertyNames) throws IOException {
        if (propertyNames==null || propertyNames.size()==0){
            throw new IOException("significant properties are not defined");
        }
        propertyValuesByName = new HashMap<String, List<String>>(propertyNames.size());
        for (int i = 0; i < propertyNames.size(); i++) {
            this.propertyValuesByName.put(propertyNames.get(i), new ArrayList<String>());
        }
        DBCursor dbCursor = propertyValuesDB.find();
        if (dbCursor.size()>0) {
            while (dbCursor.hasNext()) {
                DBObject dbObject = dbCursor.next();
                String key=(String) dbObject.get("key");
                List<String> values =(List<String>) dbObject.get("value");
                propertyValuesByName.put(key,values);
            }
        }
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
        updateSources();
        updatePropertyValuesByID();
    }


    private void updatePropertyValuesByID() {
        propertyValuesDB.drop();
        Iterator<Map.Entry<String, List<String>>> iterator = propertyValuesByName.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, List<String>> entry = iterator.next();
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
        if (propertyValuesByName.keySet().contains(property.getName())) {
            List<String> strings = propertyValuesByName.get(property.getName());
            if (!strings.contains(property.getValue())) {
                strings.add(property.getValue());
            }
            //propertyValuesByName.get(property.getName()).add(property.getValue());
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

    public void applyFilter(Filter filter) {
        List<FilterCondition> conditions = filter.getConditions();
        List<Property> result=new ArrayList<Property>();
        for(FilterCondition fc: conditions){
            result.add(new Property(fc.getField(), (String)fc.getValue()));
        }
        Coordinate coordinates = getCoordinates(result);

        DBObject query = new BasicDBObject("$in", getDBObject(coordinates));
        DBObject dbObject=new BasicDBObject();
        dbObject.put("coordinates",query);

        DBCursor dbCursor = recordsDB.find(dbObject);
        int count = dbCursor.count();

    }

    public List<String> getSignificantPropertyValues(List<Property> properties){
        List<String> result = new ArrayList<String>();
        for (Property p : properties) {
            if (!propertyValuesByName.keySet().contains(p.getName())){
                continue;
            }
            String encodedP="";
            int i=0;
            List<String> tmp_list= new ArrayList<String>(propertyValuesByName.keySet());
            encodedP+= tmp_list.indexOf(p.getName())+"_";
            tmp_list= new ArrayList<String>(propertyValuesByName.get(p.getName()));
            encodedP+= tmp_list.indexOf(p.getValue());
            result.add(encodedP);
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
        List<String> significantPropertyValues = record.getSignificantPropertyValues(propertyValuesByName);
        return new Coordinate(significantPropertyValues);
    }

    private Coordinate getCoordinates(List<Property> properties) {
        List<String> significantPropertyValues     = getSignificantPropertyValues(properties);
        return new Coordinate(significantPropertyValues);
    }

}
