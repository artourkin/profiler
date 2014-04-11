package com.ifs.megaprofiler.core;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import com.ifs.megaprofiler.elements.Document;
import com.ifs.megaprofiler.elements.Endpoint;
import com.ifs.megaprofiler.elements.Record;
import com.ifs.megaprofiler.helper.MyLogger;
import com.ifs.megaprofiler.helper.ResourceLoader;
import com.ifs.megaprofiler.helper.XmlSerializer;
import com.ifs.megaprofiler.maths.Lattice;

public class Controller {

    public long count;
    long start;
    int chunkmaxsize;
    long totalcount;
    long stopReduce;
    long stopMap;
    long timeMapTmp;
    long timeReduceTmp;
    long time;
    long timeReduce;
    long timeMap;
    Aggregator aggregator;
    Document result;
    FileSystemGatherer fsgatherer;
    List<Document> chunk;
    public Lattice<Endpoint> lattice;
    Recorder recorder;
    public Controller() {
        count = 0;
        lattice=new Lattice<Endpoint>(ResourceLoader.getLatticeProperties());
        recorder=new Recorder(ResourceLoader.getLatticeProperties()) ;
    }

    public void Execute(String path, String profilepath) {
        if (path == null || path == "") {
            System.out.println("Please provide a path to FITS results");
            return;
        }
        System.out.println("Process started");
        MyLogger.print("Process started");
        try {
            initialize(path);
        } catch (Exception e) {
            MyLogger.print(Aggregator.class.getName() + ", exception:"
                    + e.getMessage());
            return;
        }
        map();
        terminate();
        serializeResults(profilepath);
        System.out.println("Process finished");
        MyLogger.print("Process finished");
    }

    private void initialize(String path) throws IOException {
        MyLogger.print("Initialization...");
        aggregator = new Aggregator();
        result = null;
        chunk = new ArrayList<Document>();
        start = System.currentTimeMillis();
        stopReduce = System.currentTimeMillis();
        stopMap = System.currentTimeMillis();
        chunkmaxsize = 1000;
        totalcount = 0;
        timeMapTmp = 0;
        timeReduceTmp = 0;
        time = 0;
        timeReduce = 0;
        timeMap = 0;
        fsgatherer = new FileSystemGatherer(path);
        MyLogger.print("Initialization complete");
    }

    private void map() { // parses documents and maps results to a chunk
        while (true) {
            try {
                InputStream stream = fsgatherer.getNext();
                Document doc = aggregator.parseDocument(stream);
                stream.close();
                if (doc != null) {
                    totalcount++;
                    chunk.add(doc);
                }
            } catch (Exception e) {
                MyLogger.print(Aggregator.class.getName() + ", exception:"
                        + e.getMessage());
            }
            if (totalcount % chunkmaxsize == 0) {
                stopMap = System.currentTimeMillis();
                timeMapTmp = stopMap - stopReduce;
                timeMap += timeMapTmp;
                reduce();
            }
            if (!fsgatherer.hasNext()) {
                break;
            }
        }
        reduce();
    }

    private void reduce() { // reduces elements within a chunk to a single
        // result
        if (chunk.size() < 1) {
            return;
        }
        try {
            ListIterator<Document> documentListIterator = chunk.listIterator();
            while (documentListIterator.hasNext()){
                Record record = recorder.readDocument(documentListIterator.next());
                List<Endpoint> endpoints=new ArrayList<Endpoint>();
                endpoints.add(record.getEndpoint(lattice.getDimensionNames())) ;
                lattice.addEndpointsForSector(record.getCoordinates(lattice.getDimensionNames()),endpoints);
            }
            //result = Maths.reduce(result, Maths.reduce(chunk));
        } catch (Exception e) {
            MyLogger.print(Aggregator.class.getName() + ", exception:"
                    + e.getMessage());
        }
        stopReduce = System.currentTimeMillis();
        timeReduceTmp = stopReduce - stopMap;
        timeReduce += timeReduceTmp;
        System.out.print("\r" + totalcount + " files processed in "
                + (stopReduce - start) / 1000.0 + "s    ");
        MyLogger.print(totalcount + " files processed in "
                + (stopReduce - start) / 1000.0 + "s (map/reduce: "
                + timeMapTmp / 1000.0 + "/" + timeReduceTmp / 1000.0 + "s)");
        chunk.clear();

    }

    private void terminate() {
        long stop = System.currentTimeMillis();
        time = stop - start;

        if (totalcount == 0) {
            System.out.print("\r" + totalcount + " files processed in "
                    + (stopReduce - start) / 1000.0 + "s    \n");
            MyLogger.print(totalcount + " files processed in "
                    + (stopReduce - start) / 1000.0 + "s (map/reduce: "
                    + timeMapTmp / 1000.0 + "/" + timeReduceTmp / 1000.0 + "s)");

        } else {

            System.out.println("\nTotal elapsed time: " + time / 1000.0
                    + "s (map/reduce: " + timeMap / 1000.0 + "/" + timeReduce
                    / 1000.0 + "s)");
            MyLogger.print("[RESULT] Total elapsed time: " + time / 1000.0
                    + "s (map/reduce: " + timeMap / 1000.0 + "/" + timeReduce
                    / 1000.0 + "s)");

            float avgTime = (float) ((time * chunkmaxsize) / (1000.0 * totalcount));
            float timeMapAvg = (float) ((timeMap * chunkmaxsize) / (1000.0 * totalcount));
            float timeReduceAvg = (float) ((timeReduce * chunkmaxsize) / (1000.0 * totalcount));
            System.out.println("Average time: " + avgTime + "s per "
                    + chunkmaxsize + " files (map/reduce: " + timeMapAvg + "/"
                    + timeReduceAvg + "s)");
            MyLogger.print("[RESULT] Average time: " + avgTime + "s per "
                    + chunkmaxsize + " files (map/reduce: " + timeMapAvg + "/"
                    + timeReduceAvg + "s)");
        }
        this.count = totalcount;
    }

    private void serializeResults(String profilepath) {
        XmlSerializer.printDocument(result, profilepath + "profile.xml", false);
        TranslatorC3PO.printDocument(result, profilepath + "profilec3po.xml",
                false);
    }

}
