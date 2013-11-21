/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ifs.directprofiler.core;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.LinkedBlockingQueue;

import com.ifs.directprofiler.elements.Document;
import com.ifs.directprofiler.helper.Message;

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
	Thread aggThread,parseThread; 
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
		aggThread = new Thread(fsAggregator);
		parseThread = new Thread(parser);

		aggThread.start();
		parseThread.start();
	}

	public void terminate() {
		running = false;
		fsAggregator.terminate();
		parser.terminate();
		aggThread.interrupt();
		parseThread.interrupt();
	}
}
