package com.ifs.megaprofiler.elements;

import com.google.gson.Gson;
import com.ifs.megaprofiler.helper.DOM4Reader;
import com.ifs.megaprofiler.helper.ResourceLoader;
import junit.framework.TestCase;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by artur on 4/15/14.
 */
public class DocumentTest extends TestCase {
    private com.ifs.megaprofiler.helper.DOM4Reader dom4Reader;

    public void testGetBasicDBObject() throws Exception {
        dom4Reader=new DOM4Reader();
        Gson gson=new Gson();
        List<String> significantPropertyNames = ResourceLoader.getLatticeProperties();
        File file=new File("src/test/resources/jpeg.fits.xml");
        List<Record> records = dom4Reader.readC(file);
        List<String> significantPropertyValues = records.get(0).getSignificantPropertyValues(significantPropertyNames);

        List<Integer> integers=new ArrayList<Integer>();
        integers.add(1);
        integers.add(2);
        integers.add(5);
        integers.add(7);
        Document document=new Document();
        document.setRecords(records);
        document.setSignificantPropertyValueIDs(integers);


    }
}
