package com.ifs.megaprofiler.maths;

import com.ifs.megaprofiler.elements.Endpoint;
import com.ifs.megaprofiler.elements.Property;
import com.ifs.megaprofiler.elements.Record;
import com.ifs.megaprofiler.elements.Source;
import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by artur on 4/10/14.
 */
public class LatticeTest extends TestCase {

    List<Record> recordList;
    Lattice<Endpoint> lattice;
    public void setUp() throws Exception {
        super.setUp();
        recordList=new ArrayList<Record>();
        Source s1=new Source("1", "JHove");
        Source s2=new Source("2", "Tika");
        Source s3=new Source("3", "file");
        Source s4=new Source("4", "DROID");
        Source s5=new Source("5", "Exiftool");

        List<Source> sourceList1=new ArrayList<Source>();
        sourceList1.add(s1);
        sourceList1.add(s2);

        List<Source> sourceList2=new ArrayList<Source>();
        sourceList2.add(s3);
        sourceList2.add(s5);

        List<Source> sourceList3=new ArrayList<Source>();
        sourceList3.add(s4);

        List<Property> propertyList1=new ArrayList<Property>();
        propertyList1.add(new Property("Format","PDF", sourceList1));
        propertyList1.add(new Property("Format_version","1.1", sourceList2));
        propertyList1.add(new Property("Mimetype","application/pdf", sourceList3));
        propertyList1.add(new Property("puid","fmt/1", sourceList1));
        propertyList1.add(new Property("OS","win", sourceList1));
        recordList.add(new Record("File1",propertyList1));

        List<Property> propertyList2=new ArrayList<Property>();
        propertyList2.add(new Property("Format","PDF", sourceList1));
        propertyList2.add(new Property("Format_version","1.5", sourceList3));
        propertyList2.add(new Property("Mimetype","application/pdf", sourceList2));
        propertyList2.add(new Property("puid","fmt/4", sourceList3));
       // propertyList2.add(new Property("OS","MacOS", sourceList2));
        recordList.add(new Record("File2",propertyList2));

        List<Property> propertyList3=new ArrayList<Property>();
        propertyList3.add(new Property("Format","PNG", sourceList3));
        propertyList3.add(new Property("Format_version","87a", sourceList1));
        propertyList3.add(new Property("Mimetype","image/png", sourceList2));
        propertyList3.add(new Property("puid","fmt/99", sourceList3));
        propertyList3.add(new Property("OS","win", sourceList2));
        recordList.add(new Record("File3",propertyList3));

        List<String> dimension_names= new ArrayList<String>();

        for (Record r: recordList){
            List<String> properties = r.getPropertiesToString();
            for (String p: properties){
                if (!dimension_names.contains(p)) {
                    dimension_names.add(p);
                }
            }
        }
        lattice=new Lattice<Endpoint>(dimension_names);
    }

    public void testAddEndpointsForSector() throws Exception {
        for (Record r: recordList){
            List<Endpoint> endpoints=new ArrayList<Endpoint>();
         //   endpoints.add(r.getEndpoint(lattice.dimensionNames)) ;
         //   lattice.addEndpointsForSector(r.getCoordinates(lattice.dimensionNames),endpoints);
        }
        System.out.print(lattice.toString());
    }

    public void testGetEndpointsForSector() throws Exception {

    }

    public void testGetAllEndpoints() throws Exception {

    }

    public void testGetAllCoordinates() throws Exception {
        testAddEndpointsForSector();
        Set<List<String>> allCoordinates = lattice.getAllCoordinates();


    }
}
