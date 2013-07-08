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
        long stopMerge = System.currentTimeMillis();;
        long stopGather = System.currentTimeMillis();;
        long timeGather = 0;
        long timeMerge = 0;

        while (true) {
            totalcount++;
            chunk.add(aggregator.parseDocument(fsgatherer.getNext()));
            if (totalcount % chunkmaxsize == 0) {
                stopGather = System.currentTimeMillis();
                timeGather = stopGather - stopMerge;
                Document tmp = Maths.merge(chunk);
                result = Maths.merge(result, tmp);
                stopMerge = System.currentTimeMillis();
                timeMerge = stopMerge - stopGather;
                System.out.println(totalcount + " files processed in " + (stopMerge - start) / 1000.0 + "s (" + timeGather / 1000.0 + "s - read, " + timeMerge / 1000.0 + "s - merge)");
                chunk.clear();
            }
            if (!fsgatherer.hasNext()) {
                break;
            }
        }
        if (chunk.size() > 0) {
            result = Maths.merge(result, Maths.merge(chunk));
            long stop = System.currentTimeMillis();
            long time = stop - start;
            System.out.println(totalcount + " objects processed in " + time / 1000.0 + "s");
        }

        long stop = System.currentTimeMillis();
        long time = stop - start;
        System.out.println("Total elapsed time: " + time / 1000.0 + "s");
        System.out.println("Average time: " + time / (totalcount * 1.0) + "s per " + chunkmaxsize + " files");

        XmlSerialiser.printDocument(result, "output.xml", false);
    }
}
