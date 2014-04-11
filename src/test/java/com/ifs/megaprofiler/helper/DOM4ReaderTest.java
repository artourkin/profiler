package com.ifs.megaprofiler.helper;

import junit.framework.TestCase;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.InputStream;

/**
 * Created by artur on 4/11/14.
 */
public class DOM4ReaderTest extends TestCase {
    public void setUp() throws Exception {
        super.setUp();

    }

    public void testRead() throws Exception {


        InputStream resourceAsStream = DOM4Reader.class.getClassLoader().getResourceAsStream("fits.xml");
        SAXReader reader=new SAXReader();
        Document doc =reader.read(resourceAsStream);
        DOM4Reader dom4Reader=new DOM4Reader(fitsAdaptor);
        dom4Reader.read(doc);
    }

    public void testIdentify() throws Exception {
        InputStream resourceAsStream = DOM4Reader.class.getClassLoader().getResourceAsStream("fits.xml");
        SAXReader reader=new SAXReader();
        Document doc =reader.read(resourceAsStream);
        DOM4Reader dom4Reader=new DOM4Reader(fitsAdaptor);
        Element root=doc.getRootElement();
        dom4Reader.identify(root);

    }

}

