/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ifs.megaprofiler.core;

import com.ifs.megaprofiler.elements.Document;
import com.ifs.megaprofiler.helper.Message;
import com.ifs.megaprofiler.helper.MyLogger;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 
 * @author artur
 */
public class Mapper implements Runnable {

	static LinkedBlockingQueue<InputStream> queueIS;
	static LinkedBlockingQueue<Document> queueDocument;
	Parser parser;
	FileSystemAggregator fsAggregator;
	Message message;
	volatile boolean running = true;

	public Mapper(String path, LinkedBlockingQueue<Document> queueDocument,
			Message message) throws IOException {
		this.queueDocument = queueDocument;
		this.message = message;
		queueIS = new LinkedBlockingQueue<InputStream>(1000);
		fsAggregator = new FileSystemAggregator(path, queueIS, this.message);
		parser = new Parser(queueIS, this.queueDocument, this.message);

	}

	public boolean isFinished() {
		if (fsAggregator != null) {
			return fsAggregator.hasNext();
		}
		terminate();
		return true;
	}

	@Override
	public void run() {
		new Thread(fsAggregator).start();
		new Thread(parser).start();

	}

	public void terminate() {
		running = false;
		fsAggregator.terminate();
		parser.terminate();
	}
}
