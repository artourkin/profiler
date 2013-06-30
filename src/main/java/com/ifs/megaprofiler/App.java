package com.ifs.megaprofiler;

import java.util.ArrayList;
import java.util.List;

import com.ifs.megaprofiler.core.Aggregator;
import com.ifs.megaprofiler.core.FileSystemGatherer;
import com.ifs.megaprofiler.elements.Document;

/**
 * Hello world!
 *
 */
public class App {

    public static void main(String[] args) {
        String path = "/home/artur/rnd/data/conflicted/";

        FileSystemGatherer fsgatherer = new FileSystemGatherer(path);
        Aggregator aggregator=new Aggregator();
        long count = fsgatherer.getCount();
        System.out.println(count);
        List<Document> docs=new ArrayList<Document>();
        while (fsgatherer.getRemaining() > 0) {
            docs.add(aggregator.ParseDocument(fsgatherer.getNext(1).get(0)));
      //TODO DigesterContext mixes nodes :( . bad output  
        }

    }
}
