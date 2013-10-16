/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ifs.megaprofiler.core;

import com.ifs.megaprofiler.elements.Document;
import com.ifs.megaprofiler.helper.Message;
import com.ifs.megaprofiler.helper.MyLogger;
import com.ifs.megaprofiler.helper.MyPrinter;
import com.ifs.megaprofiler.maths.Maths;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 
 * @author artur
 */
public class Reducer implements Runnable {

	Document result;
	static LinkedBlockingQueue<Document> queueDocument;
	Message message;
	long totalcount;
	long chunkmaxsize;
	long start, stop, tmpStop;
	long startReduce, stopReduce, TotalReduce, startMap, time, timeReduce,
			timeReduceTmp, stopMap, timeMap, timeMapTmp;
	volatile boolean running = true;
	List<Document> listDocument;

	public Reducer(LinkedBlockingQueue<Document> queueDocument, Message message) {
		this.queueDocument = queueDocument;
		this.message = message;
		totalcount = 0;
		chunkmaxsize = 1000;
		start = 0;
		stop = 0;
		tmpStop = 0;
		listDocument = new ArrayList<Document>();
	}

	Document tmpDocument;

	@Override
	public void run() {
		try {
			start = System.currentTimeMillis();
			stopReduce = System.currentTimeMillis();
			stopMap = System.currentTimeMillis();
			int listSize = 0;
			int count = 0;
			while ((!queueDocument.isEmpty() || !message.isTrue()) && running) {
				queueDocument.drainTo(listDocument);
				listSize = listDocument.size();
				if (listSize > chunkmaxsize) {
					totalcount += listSize;
					count += listSize;
					if (count > 100000) {
						mapStats();
					}
					result = Maths.reduce(result, Maths.reduce(listDocument));
					listDocument.clear();
					if (count > 100000) {
						reduceStats();
						count = 0;
					}
				}
			}
			if (listSize > 0) {
				totalcount += listSize;
				mapStats();
				result = Maths.reduce(result, Maths.reduce(listDocument));
				listDocument.clear();
				reduceStats();
			}
			finalStats();
		} catch (Exception e) {
			MyLogger.print(Parser.class.getName() + ", exception:"
					+ e.getMessage());
		}
	}

	private void finalStats() {
		stop = System.currentTimeMillis();
		time = stop - start;
		MyPrinter.print("\nTotal elapsed time: " + time / 1000.0
				+ "s (map/reduce: " + timeMap / 1000.0 + "/" + timeReduce
				/ 1000.0 + "s)\n");
		MyLogger.print("[RESULT] Total elapsed time: " + time / 1000.0
				+ "s (map/reduce: " + timeMap / 1000.0 + "/" + timeReduce
				/ 1000.0 + "s)");
	}

	private void mapStats() {
		stopMap = System.currentTimeMillis();
		timeMapTmp += stopMap - stopReduce;
	}

	private void reduceStats() {
		stopReduce = System.currentTimeMillis();
		timeReduceTmp += stopReduce - stopMap;
		timeReduce += timeReduceTmp;
		timeMap += timeMapTmp;

		MyPrinter.print("\r" + totalcount + " files processed in "
				+ (stopReduce - start) / 1000.0 + "s    ");
		MyLogger.print(totalcount + " files processed in "
				+ (stopReduce - start) / 1000.0 + "s (map/reduce: "
				+ timeMapTmp / 1000.0 + "/" + timeReduceTmp / 1000.0 + "s)");
		timeReduceTmp = 0;
		timeMapTmp = 0;
		stopReduce = System.currentTimeMillis();
	}

	public Document getDocument() {
		return result;
	}

	public void terminate() {
		running = false;
	}
}
