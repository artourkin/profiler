package com.ifs.megaprofiler.core;

import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.List;

import com.ifs.megaprofiler.elements.Filter;
import com.ifs.megaprofiler.elements.MongoDBManager;
import com.ifs.megaprofiler.elements.Record;
import com.ifs.megaprofiler.helper.DOM4Reader;
import com.ifs.megaprofiler.helper.MyLogger;
import com.ifs.megaprofiler.helper.ResourceLoader;

public class Controller {

    public long count;
    long start;
    int chunkmaxsize;
    long totalcount;
    long stopReduce;
    long timeReduceTmp;
    long time;
    long timeReduce;
    FileSystemGatherer fsgatherer;
    public MongoDBManager latticeManager;
    DOM4Reader dom4Reader;
    public Controller() {
        count = 0;
        try {
            latticeManager=new MongoDBManager(ResourceLoader.getSignificantProperties());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
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
            MyLogger.print("exception:"
                    + e.getMessage());
            return;
        }
        reduce();
        terminate();
        //serializeResults(profilepath);
        System.out.println("Process finished");
        MyLogger.print("Process finished");
    }

    public void applyFilter(Filter filter) {
        latticeManager.applyFilter(filter);
    }

    private void initialize(String path) throws IOException {
        MyLogger.print("Initialization...");
        start = System.currentTimeMillis();
        stopReduce = System.currentTimeMillis();
        totalcount = 0;
        time = 0;
        fsgatherer = new FileSystemGatherer(path);
        MyLogger.print("Initialization complete");
    }


    private void reduce() { // reduces elements within a chunk to a single
        File file=null;
        while (fsgatherer.hasNext()){
            try {
                file = fsgatherer.getNext();
                List<Record> records = dom4Reader.readC(file);
                latticeManager.addRecords(records);
                totalcount++;
                if (totalcount%1000==0) {
                    System.out.println("Processed files: " + totalcount + " in " + (System.currentTimeMillis() - start) / 1000.0 );
                }
            } catch (Exception e1) {
                System.out.println("Problem occured with file: " + file.getPath());
                e1.printStackTrace();
            }
        }
        latticeManager.updateDB();
    }

    private void terminate() {
        long stop = System.currentTimeMillis();
        time = stop - start;

        System.out.println("\nTotal elapsed time: " + time / 1000.0  );

        float avgTime = (float) ((time * chunkmaxsize) / (1000.0 * totalcount));
        float timeReduceAvg = (float) ((timeReduce * chunkmaxsize) / (1000.0 * totalcount));
    }

}
