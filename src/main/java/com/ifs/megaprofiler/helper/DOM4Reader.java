package com.ifs.megaprofiler.helper;

/**
 * Created by artur on 4/11/14.
 */

import com.google.caliper.memory.ObjectGraphMeasurer;
import com.ifs.megaprofiler.elements.Property;
import com.ifs.megaprofiler.elements.Record;
import com.ifs.megaprofiler.elements.Source;
import com.ifs.megaprofiler.maths.Maths;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by artur on 4/1/14.
 */
public class DOM4Reader {
    final SAXReader reader=new SAXReader();
    public DOM4Reader()    {
    }
    public  List<Record> read(InputStream is) throws DocumentException {
        Document document=null;
        document = reader.read(is);
        return read(document);
    }

    public  List<Record> read(Document document){
        List<Record> result = identify(document.getRootElement());
        List<Property> properties = extractFeatures(document.getRootElement());
        if (result != null){
            for (Record r: result){
                r.getProperties().addAll(properties);
            }
        }
        return result;
    }

    public  List<Record> readC(File file) throws DocumentException {
        Document document=null;
        document = reader.read(file);
        return readC(document);
    }


    public  List<Record> readC(Document document){
        List<Record> result=new ArrayList<Record>();
        String uid = document.getRootElement().element("fileinfo").element("filepath").getStringValue();
        List<List<Property>> identifyC = identifyC(document.getRootElement());
        List<List<Property>> extractFeaturesC = extractFeaturesC(document.getRootElement());

        for (List<Property> identity : identifyC) {
            for (List<Property> features : extractFeaturesC ) {
                Record r=new Record(uid,identity);
                r.getProperties().addAll(features);
                result.add(r);
            }
        }
        return result;
    }


    private List<Property> extractFeatures(Element rootElement) {
        List<Property> result= new ArrayList<Property>();

        result.addAll(extractFeaturesFrom(rootElement.element("filestatus")));
        result.addAll(extractFeaturesFrom(rootElement.element("fileinfo")));

        Element metadata = rootElement.element("metadata");
        Iterator iterator = metadata.elementIterator();
        while (iterator.hasNext()){
            result.addAll(extractFeaturesFrom((Element) iterator.next()));
        }
        return result;
    }

    private List<List<Property>> extractFeaturesC(Element rootElement) {
        List<List<Property>> result= new ArrayList<List<Property>>();
        List<List<Property>> tmp = new ArrayList<List<Property>>();
        tmp.addAll(extractFeaturesFromC(rootElement.element("filestatus")));
        tmp.addAll(extractFeaturesFromC(rootElement.element("fileinfo")));

        Element metadata = rootElement.element("metadata");
        Iterator iterator = metadata.elementIterator();
        while (iterator.hasNext()){
            tmp.addAll(extractFeaturesFromC((Element) iterator.next()));
        }
        result= Maths.cartesianProduct(tmp);
        return result;
    }

    private List<Property> extractFeaturesFrom(Element element){
        if (element==null)
            return null;
        List<Property> result=new ArrayList<Property>();
        Iterator iterator = element.elementIterator();
        while (iterator.hasNext()){
            result.add(extractFeatureCommon((Element) iterator.next()));
        }
        return result;
    }

    private List<List<Property>> extractFeaturesFromC(Element element){
        if (element==null)
            return null;
        List<List<Property>> result= new ArrayList<List<Property>>();
        Iterator iterator = element.elementIterator();
        List<String> propertyNames=new ArrayList<String>();
        while (iterator.hasNext()) {
            Element e = (Element) iterator.next();
            String name = e.getName();
            if (!propertyNames.contains(name)) {
                propertyNames.add(name);
            }
        }

        for (String propertyName : propertyNames) {
            List<Property> properties=new ArrayList<Property>();
            List elements = element.elements(propertyName);
            for (Object o : elements) {
                Element e = (Element)o;
                Property featureCommon = extractFeatureCommon(e);
                if (featureCommon!=null) {
                    properties.add(extractFeatureCommon(e));
                }
            }
            result.add(properties);
        }

        return result;
    }

    private Property extractFeatureCommon(Element element) {
        if (element.getName().equals("message")) {
            return null;
        }
        Property result=new Property();
        result.setKey(element.getName());
        result.setValue(element.getStringValue());
        result.setSources(getSources(element));
        return result;
    }

    private List<Record> identify(Element rootElement) {
        List<Record> result= new ArrayList<Record>();

        String uid = rootElement.element("fileinfo").element("filepath").getStringValue();
        Element identification = rootElement.element("identification");
        String status = getStatus(identification);
        Iterator identityIterator = identification.elementIterator("identity");

        while (identityIterator.hasNext()){
            List<Record> records=new ArrayList<Record>();

            Element identity = (Element) identityIterator.next();
            List<Property> versions = getVersions(identity);
            List<Property> mimetypes = getMimetypes(identity);
            List<Property> formats = getFormats(identity);
            List<Property> puids = getPuid(identity);
            if (versions.size()>0) {
                for ( Property v: versions){
                    List<Property> toBeAdded=new ArrayList<Property>();
                    toBeAdded.addAll(mimetypes);
                    toBeAdded.addAll(formats);
                    toBeAdded.addAll(puids);
                    toBeAdded.add(v);

                    Record r = new Record(uid,toBeAdded);
                    records.add(r);
                }

            } else {
                List<Property> toBeAdded=new ArrayList<Property>();
                toBeAdded.addAll(mimetypes);
                toBeAdded.addAll(formats);
                toBeAdded.addAll(puids);
                Record r = new Record(uid,toBeAdded);
                records.add(r);
            }
            result.addAll(records);
        }
        return result;
    }

    private List<List<Property>> identifyC(Element rootElement) {
        List<List<Property>> result= new ArrayList<List<Property>>();


        Element identification = rootElement.element("identification");
        String status = getStatus(identification);
        Iterator identityIterator = identification.elementIterator("identity");

        while (identityIterator.hasNext()){
            Element identity = (Element) identityIterator.next();
            List<List<Property>> tmp=  new ArrayList<List<Property>>();

            List<Property> versions = getVersions(identity);
            List<Property> puids = getPuid(identity);
            tmp.add(versions);
            tmp.add(puids);
            tmp=Maths.cartesianProduct(tmp);

            List<Property> mimetypes = getMimetypes(identity);
            List<Property> formats = getFormats(identity);

            for (List<Property> list : tmp) {
                list.addAll(mimetypes);
                list.addAll(formats);
            }

            result.addAll(tmp);
        }
        return result;
    }

    private List<Property> getFormats(Element identity) {
        List<Property> result=new ArrayList<Property>();
        List<Source> sources=getSources(identity);

        Property tmp=new Property();
        tmp.setKey("format");
        tmp.setValue(identity.attribute("format").getValue());
        tmp.setSources(getSources(identity));

        result.add(tmp);
        return result;
    }

    private List<Property> getMimetypes(Element identity) {
        List<Property> result=new ArrayList<Property>();
        List<Source> sources=getSources(identity);

        Property tmp=new Property();
        tmp.setKey("mimetype");
        tmp.setValue(identity.attribute("mimetype").getValue());
        tmp.setSources(getSources(identity));

        result.add(tmp);
        return result;
    }

    private List<Property> getPuid(Element identity) {
        List<Property> result=new ArrayList<Property>();
        List externalIdentifiers = identity.elements("externalIdentifier");
        for (Object externalIdentifier: externalIdentifiers) {
            Element e = (Element) externalIdentifier;
            result.add(new Property("puid",e.getStringValue(),getSources(e)));
        }
        return result;
    }

    private List<Property> getVersions(Element identity) {
        List<Property> result=new ArrayList<Property>();
        Iterator versionIterator = identity.elementIterator("version");
        while (versionIterator.hasNext()){
            Element version = (Element) versionIterator.next();
            result.add(new Property("format_version",version.getStringValue(),getSources(version)));
        }
        return result;
    }

    private String getStatus(Element element){
        String value=null;
        for ( int i = 0, size = element.attributeCount(); i < size; i++ ) {
            Attribute attribute = element.attribute("status");
            if (attribute != null)  {
                value= attribute.getValue();
            }
        }
        return (value == null) ? "OK" : value;
    }

    private List<Source> getSources(Element element){
        List<Source> result=new ArrayList<Source>();
        if (element.getName().equals("identity")){
            Iterator sourceIterator = element.elementIterator("tool");
            while (sourceIterator.hasNext()){
                Element source = (Element) sourceIterator.next();
                String toolname= source.attribute("toolname").getValue();
                String toolversion= source.attribute("toolversion").getValue();
                result.add(new Source(toolname, toolversion));
            }
        } else {
            String toolname= element.attribute("toolname").getValue();
            String toolversion= element.attribute("toolversion").getValue();
            result.add(new Source( toolname, toolversion ));
        }
        return result;
    }



}
