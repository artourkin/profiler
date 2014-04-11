package com.ifs.megaprofiler.helper;

/**
 * Created by artur on 4/11/14.
 */

        import com.ifs.megaprofiler.elements.Record;
        import org.dom4j.Attribute;
        import org.dom4j.Document;
        import org.dom4j.DocumentException;
        import org.dom4j.Element;
        import org.dom4j.io.OutputFormat;
        import org.dom4j.io.SAXReader;
        import org.dom4j.io.XMLWriter;
        import org.dom4j.tree.DefaultAttribute;
        import org.dom4j.tree.DefaultDocument;
        import org.dom4j.tree.DefaultElement;

        import java.io.InputStream;
        import java.io.StringReader;
        import java.util.*;

/**
 * Created by artur on 4/1/14.
 */
public class DOM4Reader {
    final SAXReader reader=new SAXReader();
    public DOM4Reader()    {
    }
    public  List<Record> read(InputStream is){
        List<Record> result=null;
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
        result.addAll(identify(document.getRootElement()));
        result.addAll(extractFeatures(document.getRootElement())) ;
        return result;
    }

    public  List<Record> read(Document document){
        List<Record> result= new ArrayList<Record>();
        result.addAll(identify(document.getRootElement()));
        result.addAll(extractFeatures(document.getRootElement())) ;
        return result;
    }


    private List<Record> extractFeatures(Element rootElement) {
        List<Record> result= new ArrayList<Record>();

        result.addAll(extractFeaturesFrom(rootElement.element("filestatus")));
        result.addAll(extractFeaturesFrom(rootElement.element("fileinfo")));

        Element metadata = rootElement.element("metadata");
        Iterator iterator = metadata.elementIterator();
        while (iterator.hasNext()){
            result.addAll(extractFeaturesFrom((Element) iterator.next()));
        }
        return result;
    }

    private List<Record> extractFeaturesFrom(Element element){
        if (element==null)
            return null;
        List<Record> result=new ArrayList<Record>();
        Iterator iterator = element.elementIterator();
        while (iterator.hasNext()){
            result.add(extractFeatureCommon((Element) iterator.next()));
        }
        return result;
    }

    private Record extractFeatureCommon(Element element) {
        return new Record(adaptor.getProperty(element.getName()),
                element.getStringValue(), getStatus(element), getSources(element)) ;
    }

    public List<Record> identify(Element rootElement) {
        List<Record> result= new ArrayList<Record>();
        Element identification = rootElement.element("identification");
        String status = getStatus(identification);
        Iterator identityIterator = identification.elementIterator("identity");

        while (identityIterator.hasNext()){
            Element identity = (Element) identityIterator.next();

            List<Record> versions = getVersions(identity);
            List<Record> formatMimetypes = getFormatMimetypes(identity, status);
            Record puid = getPuid(identity);
            for ( Record v: versions){
                List<Record> identityRecord=new ArrayList<Record>();
                identityRecord.addAll(formatMimetypes);
                identityRecord.add(v);
                identityRecord.add(puid);
                result.add(new Record(adaptor.getProperty("identity"),identityRecord,status));
            }
        }
        return result;
    }

    private List<Record> getFormatMimetypes(Element identity, String status) {
        List<Record> result=new ArrayList<Record>();
        result.add(new Record(adaptor.getProperty("format"),
                identity.attribute("format").getValue(), status, getSources(identity)));
        result.add(new Record(adaptor.getProperty("mimetype"),
                identity.attribute("mimetype").getValue(), status, getSources(identity)));
        return result;
    }

    private Record getPuid(Element identity) {
        Element externalIdentifier = identity.element("externalIdentifier");
        String status = getStatus(externalIdentifier);
        return new Record(adaptor.getProperty("puid"),
                externalIdentifier.getStringValue(), status, getSources(externalIdentifier));

    }
    private Record getFeature(Element element, String feature) {
        return new Record(adaptor.getProperty(feature),
                element.getStringValue(), getStatus(element), getSources(element));

    }

    private List<Record> getVersions(Element identity) {
        List<Record> result=new ArrayList<Record>();
        Iterator versionIterator = identity.elementIterator("version");
        while (versionIterator.hasNext()){
            Element version = (Element) versionIterator.next();
            String status = getStatus(version);
            result.add(new Record(adaptor.getProperty("format_version"),
                    version.getStringValue(), status, getSources(version)));
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
    private List<String> getSources(Element element){
        List<String> result=new ArrayList<String>();
        if (element.getName().equals("identity")){
            Iterator sourceIterator = element.elementIterator("tool");
            while (sourceIterator.hasNext()){
                Element source = (Element) sourceIterator.next();
                String toolname= source.attribute("toolname").getValue();
                String toolversion= source.attribute("toolversion").getValue();
                result.add( adaptor.getSource( toolname, toolversion ).getId())   ;
            }
        } else {
            String toolname= element.attribute("toolname").getValue();
            String toolversion= element.attribute("toolversion").getValue();
            result.add(adaptor.getSource( toolname, toolversion ).getId());
        }
        return result;
    }



}
