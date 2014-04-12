package com.ifs.megaprofiler.helper;

import com.ifs.megaprofiler.elements.Record;
import junit.framework.TestCase;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;

/**
 * Created by artur on 4/11/14.
 */
public class DOM4ReaderTest extends TestCase {
    public void setUp() throws Exception {
        super.setUp();

    }

    public void testRead() throws Exception {


        InputStream resourceAsStream = new FileInputStream("/home/artur/rnd/data/govdocs_subset/006/006185.jpg.fits.xml");// DOM4Reader.class.getClassLoader().getResourceAsStream("jpeg.fits.xml");
        SAXReader reader=new SAXReader();
        Document doc =reader.read(resourceAsStream);
        DOM4Reader dom4Reader=new DOM4Reader();
        List<Record> read = dom4Reader.readC(doc);
    }



}

