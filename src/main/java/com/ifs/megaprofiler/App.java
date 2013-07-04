package com.ifs.megaprofiler;

import com.ifs.megaprofiler.core.Aggregator;
import java.util.ArrayList;
import java.util.List;


import com.ifs.megaprofiler.core.FileSystemGatherer;
import com.ifs.megaprofiler.elements.Document;
import com.ifs.megaprofiler.helper.XmlSerialiser;
import com.ifs.megaprofiler.maths.Maths;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

/**
 * Hello world! Oh yes! Hello, world :D
 *
 */
public class App {

  public static void main(String[] args) {
    String path = "/home/artur/Data/1/";

    FileSystemGatherer fsgatherer = new FileSystemGatherer(path);
    Aggregator aggregator = new Aggregator();
    long count = fsgatherer.getCount();
    System.out.println(count);
    List<Document> docs = new ArrayList<Document>();
    while (fsgatherer.getRemaining() > 0) {
      docs.add(aggregator.parseDocument(fsgatherer.getNext(1).get(0)));
      //TODO DigesterContext mixes nodes :( . bad output  
    }
    //Document result = Maths.merge(docs);

    org.dom4j.Document output = XmlSerialiser.createDocument(docs.get(0));


    // lets write to a file
    XMLWriter writer;
    try {
      writer = new XMLWriter(
              new FileWriter("output.xml"));

      writer.write(output);
      writer.close();


      // Pretty print the document to System.out
      OutputFormat format = OutputFormat.createPrettyPrint();
      writer = new XMLWriter(System.out, format);
      writer.write(output);


    } catch (IOException ex) {
      Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
    }



    //    FileWriter out;
//    try {
//      out = new FileWriter("foo.xml");
//      output.write(out);
//    } catch (IOException ex) {
//      Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
//    }
  }
}
