package com.ifs.megaprofiler.core;

import com.ifs.megaprofiler.elements.Document;
import com.ifs.megaprofiler.elements.Record;
import com.ifs.megaprofiler.helper.ResourceLoader;
import junit.framework.TestCase;

import java.io.InputStream;
import java.util.List;

/**
 * Created by artur on 4/10/14.
 */
public class RecorderTest extends TestCase {
    Document doc;
    private List<String> LatticeProperties;
    public void setUp() throws Exception {
        super.setUp();
        Aggregator aggregator=new Aggregator();
        InputStream resourceAsStream = RecorderTest.class.getClassLoader().getResourceAsStream("jpeg.fits.xml");
        LatticeProperties = ResourceLoader.getLatticeProperties();
        doc = aggregator.parseDocument(resourceAsStream);

    }

    public void testReadDocument() throws Exception {
        Recorder recorder=new Recorder(LatticeProperties);
        Record record = recorder.readDocument(doc);
    }
}
