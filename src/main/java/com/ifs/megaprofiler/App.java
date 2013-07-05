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
import java.io.InputStream;
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
    Document result = null;
    List<Document> chunk = new ArrayList<Document>();
    int chunkmaxsize = 1000;
    while (fsgatherer.hasNext()) {
      if (chunk.size() < chunkmaxsize) {
        chunk.add(aggregator.parseDocument(fsgatherer.getNext()));
      } else {
        if (result == null) {
          result = Maths.merge(chunk);
        } else {
          result = Maths.merge(result, Maths.merge(chunk));
        }
        chunk.clear();
      }
    }
    org.dom4j.Document output = XmlSerialiser.createDocument(result);
    // lets write to a file
    XMLWriter writer;
    try {



      // Pretty print the document to System.out
      OutputFormat format = OutputFormat.createPrettyPrint();
      writer = new XMLWriter(System.out, format);
      writer.write(output);
      writer = new XMLWriter(
              new FileWriter("output.xml"), format);

      writer.write(output);
      writer.close();


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
