package com.ifs.megaprofiler.helper;

/**
 * Created by artur on 4/11/14.
 */

import com.ifs.megaprofiler.elements.Property;
import com.ifs.megaprofiler.elements.Record;
import com.ifs.megaprofiler.elements.Source;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

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
    public  List<Record> read(InputStream is){
        List<Record> result=null;
        List<Property> propertyList=new ArrayList<Property>();
        Document document=null;
        try {
            document = reader.read(is);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        if (document==null)
        {
            return null;
        }

        identify(document.getRootElement());
        extractFeatures(document.getRootElement()) ;
        return result;
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

    private Property extractFeatureCommon(Element element) {
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

            for ( Property v: versions){
                List<Property> toBeAdded=new ArrayList<Property>();
                toBeAdded.addAll(mimetypes);
                toBeAdded.addAll(formats);
                toBeAdded.addAll(puids);
                toBeAdded.add(v);

                Record r = new Record(uid,toBeAdded);
                records.add(r);
            }
            result.addAll(records);
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
        Element externalIdentifier = identity.element("externalIdentifier");
        result.add(new Property("puid",externalIdentifier.getStringValue(),getSources(externalIdentifier)));
        return result;
    }

    private List<Property> getVersions(Element identity) {
        List<Property> result=new ArrayList<Property>();
        Iterator versionIterator = identity.elementIterator("version");
        while (versionIterator.hasNext()){
            Element version = (Element) versionIterator.next();
            result.add(new Property("format_versin",version.getStringValue(),getSources(version)));
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
