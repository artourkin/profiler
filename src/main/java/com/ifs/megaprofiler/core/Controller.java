package com.ifs.megaprofiler.core;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import com.ifs.megaprofiler.elements.Endpoint;
import com.ifs.megaprofiler.elements.LatticeManager;
import com.ifs.megaprofiler.elements.Record;
import com.ifs.megaprofiler.helper.DOM4Reader;
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
    long timeReduceTmp;
    long time;
    long timeReduce;
    Aggregator aggregator;
    FileSystemGatherer fsgatherer;
    LatticeManager latticeManager;
    DOM4Reader dom4Reader;
    public Controller() {
        count = 0;
        latticeManager=new LatticeManager(ResourceLoader.getLatticeProperties());
        dom4Reader=new DOM4Reader();
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
        reduce();
        terminate();
        //serializeResults(profilepath);
        System.out.println("Process finished");
        MyLogger.print("Process finished");
    }

    private void initialize(String path) throws IOException {
        MyLogger.print("Initialization...");
        aggregator = new Aggregator();
        start = System.currentTimeMillis();
        stopReduce = System.currentTimeMillis();
        totalcount = 0;
        time = 0;
        fsgatherer = new FileSystemGatherer(path);
        MyLogger.print("Initialization complete");
    }


    private void reduce() { // reduces elements within a chunk to a single
        // result
        while (fsgatherer.hasNext()){
            try {
                InputStream inputStream = fsgatherer.getNext();
                List<Record> records = dom4Reader.readC(inputStream);
                inputStream.close();
                latticeManager.addRecords(records);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    private void terminate() {
        long stop = System.currentTimeMillis();
        time = stop - start;

        System.out.println("\nTotal elapsed time: " + time / 1000.0  );

        float avgTime = (float) ((time * chunkmaxsize) / (1000.0 * totalcount));
        float timeReduceAvg = (float) ((timeReduce * chunkmaxsize) / (1000.0 * totalcount));
    }

}
