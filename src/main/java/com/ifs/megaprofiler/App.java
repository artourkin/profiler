package com.ifs.megaprofiler;

import com.ifs.megaprofiler.core.Aggregator;
import java.util.ArrayList;
import java.util.List;


import com.ifs.megaprofiler.core.FileSystemGatherer;
import com.ifs.megaprofiler.elements.Document;
import com.ifs.megaprofiler.helper.XmlSerialiser;
import com.ifs.megaprofiler.maths.Maths;

/**
 * Hello world! Oh yes! Hello, world :D
 *
 */
public class App {

    public static void main(String[] args) {
        if (args.length < 1 || args[0] == null) {
            System.out.println("Please provide a path to FITS results");
            return;
        }
        long start = System.currentTimeMillis();
        String path = args[0];
        FileSystemGatherer fsgatherer = new FileSystemGatherer(path);
        Aggregator aggregator = new Aggregator();
        long count = fsgatherer.getCount();
        System.out.println(count);
        Document result = new Document();
        List<Document> chunk = new ArrayList<Document>();
        int chunkmaxsize = 1000;
        int totalcount = 0;
        while (true) {
            //             result = Maths.merge(result, aggregator.parseDocument(fsgatherer.getNext()));
            totalcount++;
            chunk.add(aggregator.parseDocument(fsgatherer.getNext()));
            if (totalcount % chunkmaxsize == 0) {
                System.out.println("Processed objects: " + totalcount);
                Document tmp = Maths.merge(chunk);
                result = Maths.merge(result, tmp);
                chunk.clear();
            }
            if (!fsgatherer.hasNext()) {
                break;
            }
        }
        if (chunk.size() > 0) {
            result = Maths.merge(result, Maths.merge(chunk));
            System.out.println("Processed objects: " + totalcount);
        }

        long stop = System.currentTimeMillis();
        long time = stop - start;
        System.out.println("Total elapsed time: " + time / 1000.0 + " sec");

        XmlSerialiser.printDocument(result, "output.xml", false);
    }
}
