package com.ifs.directprofiler.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

import com.ifs.directprofiler.elements.Document;
import com.ifs.directprofiler.helper.Message;
import com.ifs.directprofiler.helper.MyLogger;
import com.ifs.directprofiler.helper.MyPrinter;
import com.ifs.directprofiler.helper.XmlSerializer;
import com.ifs.directprofiler.maths.Maths;

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

		mapper = new Mapper(path, queueDocument, this.message);
		reducer = new Reducer(queueDocument, this.message);
		mapper.run();
		reducer.run();
		terminate();
		serializeResults(reducer.getDocument(), profilepath);
		MyPrinter.print("Process finished\n");
		MyLogger.print("Process finished");
	}

	private void initialize(String path) {
		MyPrinter.print("Initialization...\n");
		MyLogger.print("Initialization...");
		result = null;
		start = System.currentTimeMillis();
		MyPrinter.print("Initialization complete\n");
		MyLogger.print("Initialization complete");
	}

	private void terminate() {
		long stop = System.currentTimeMillis();
		time = stop - start;
		mapper.terminate();
		reducer.terminate();
	}

	private void serializeResults(Document document, String profilepath) {
		XmlSerializer.printDocument(document, profilepath + "profile.xml",
				false);
		TranslatorC3PO.printDocument(document, profilepath + "profilec3po.xml",
				false);
	}
}
