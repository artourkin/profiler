package com.ifs.megaprofiler.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

import com.ifs.megaprofiler.elements.Document;
import com.ifs.megaprofiler.helper.Message;
import com.ifs.megaprofiler.helper.MyLogger;
import com.ifs.megaprofiler.helper.MyPrinter;
import com.ifs.megaprofiler.helper.XmlSerializer;
import com.ifs.megaprofiler.maths.Maths;
import java.io.InputStream;

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
	Parser aggregator;
	Document result;
	FileSystemAggregator fsgatherer;
	List<Document> chunk;
	Message message;
	Mapper mapper;
	Reducer reducer;
	static LinkedBlockingQueue<Document> queueDocument;

	public Controller() {
		count = 0;
		queueDocument = new LinkedBlockingQueue<Document>();
		message = new Message();
	}

	public void Execute(String path, String profilepath) throws IOException {
		if (path == null || path == "") {
			MyPrinter.print("Please provide a path to FITS results\n");
			return;
		}
		MyPrinter.print("Process started\n");
		MyLogger.print("Process started");
		try {
			initialize(path);
		} catch (Exception e) {
			MyLogger.print(Parser.class.getName() + ", exception:"
					+ e.getMessage());
			return;
		}
		// map();
		// terminate();
		mapper = new Mapper(path, queueDocument, this.message);
		reducer = new Reducer(queueDocument, this.message);
		mapper.run();
		reducer.run();
		serializeResults(reducer.getDocument(), profilepath);
		MyPrinter.print("Process finished\n");
		MyLogger.print("Process finished");
	}

	private void initialize(String path) {
		MyPrinter.print("Initialization...\n");
		MyLogger.print("Initialization...");
		// aggregator = new Aggregator(queueIS);
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
		// fsgatherer = new FileSystemGatherer(path,queueIS);
		MyPrinter.print("Initialization complete\n");
		MyLogger.print("Initialization complete");
	}

	private void terminate() {
		long stop = System.currentTimeMillis();
		time = stop - start;
		mapper.terminate();
		reducer.terminate();
		if (totalcount == 0) {
			MyPrinter.print("\r" + totalcount + " files processed in " + time
					/ 1000.0 + "s    \n");
			MyLogger.print(totalcount + " files processed in " + time / 1000.0
					+ "s (map/reduce: " + timeMapTmp / 1000.0 + "/"
					+ timeReduceTmp / 1000.0 + "s)");
		} else {
			MyPrinter.print("\nTotal elapsed time: " + time / 1000.0
					+ "s (map/reduce: " + timeMap / 1000.0 + "/" + timeReduce
					/ 1000.0 + "s)");
			MyLogger.print("[RESULT] Total elapsed time: " + time / 1000.0
					+ "s (map/reduce: " + timeMap / 1000.0 + "/" + timeReduce
					/ 1000.0 + "s)");

			float avgTime = (float) ((time * chunkmaxsize) / (1000.0 * totalcount));
			float timeMapAvg = (float) ((timeMap * chunkmaxsize) / (1000.0 * totalcount));
			float timeReduceAvg = (float) ((timeReduce * chunkmaxsize) / (1000.0 * totalcount));
			System.out.println("Average time: " + avgTime + "s per "
					+ chunkmaxsize + " files");
			MyLogger.print("[RESULT] Average time: " + avgTime + "s per "
					+ chunkmaxsize + " files");
		}
		this.count = totalcount;
	}

	private void serializeResults(Document document, String profilepath) {
		XmlSerializer.printDocument(document, profilepath + "profile.xml",
				false);
		TranslatorC3PO.printDocument(document, profilepath + "profilec3po.xml",
				false);
	}
}
