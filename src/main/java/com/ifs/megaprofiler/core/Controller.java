package com.ifs.megaprofiler.core;

import java.util.ArrayList;
import java.util.List;

import com.ifs.megaprofiler.elements.Document;
import com.ifs.megaprofiler.helper.MyLogger;
import com.ifs.megaprofiler.helper.XmlSerializer;
import com.ifs.megaprofiler.maths.Maths;

public class Controller {
	public Controller() {

	}

	public void Execute(String Filepath) {
		if (Filepath == null || Filepath == "") {
			System.out.println("Please provide a path to FITS results");
			return;
		}
		try {
			long start = System.currentTimeMillis();
			int chunkmaxsize = 1000;
			long totalcount = 0;
			long stopReduce, stopMap;
			long timeMapTmp = 0;
			long timeReduceTmp = 0;
			long time = 0;
			long timeReduce = 0;
			long timeMap = 0;
			System.out.println("Process started");
			MyLogger.print("Process started");
			String path = Filepath;
			MyLogger.print("Initialization...");
			FileSystemGatherer fsgatherer = new FileSystemGatherer(path);
			Aggregator aggregator = new Aggregator();
			Document result = new Document();
			List<Document> chunk = new ArrayList<Document>();
			stopReduce = System.currentTimeMillis();
			stopMap = System.currentTimeMillis();
			MyLogger.print("Initialization complete");
			while (true) {
				try {
					totalcount++;
					chunk.add(aggregator.parseDocument(fsgatherer.getNext()));
				} catch (Exception e) {
					MyLogger.print(Aggregator.class.getName() + ", exception:"
							+ e.getMessage());
					// System.out.print(e.getMessage());
				}
				if (totalcount % chunkmaxsize == 0) {
					stopMap = System.currentTimeMillis();
					timeMapTmp = stopMap - stopReduce;
					timeMap += timeMapTmp;
					try {
						result = Maths.reduce(result, Maths.reduce(chunk));
					} catch (Exception e) {
						MyLogger.print(Aggregator.class.getName()
								+ ", exception:" + e.getMessage());
						// System.out.print(e.getMessage());
					}
					stopReduce = System.currentTimeMillis();
					timeReduceTmp = stopReduce - stopMap;
					timeReduce += timeReduceTmp;
					System.out.print("\r" + totalcount + " files processed in "
							+ (stopReduce - start) / 1000.0 + "s    ");
					MyLogger.print(totalcount + " files processed in "
							+ (stopReduce - start) / 1000.0 + "s (map/reduce: "
							+ timeMapTmp / 1000.0 + "/" + timeReduceTmp
							/ 1000.0 + "s)");
					chunk.clear();
				}
				if (!fsgatherer.hasNext()) {
					break;
				}

			}
			if (chunk.size() > 0) {

				try {
					result = Maths.reduce(result, Maths.reduce(chunk));
				} catch (Exception e) {
					MyLogger.print(Aggregator.class.getName() + ", exception:"
							+ e.getMessage());
					// System.out.print(e.getMessage());
				}
				stopReduce = System.currentTimeMillis();
				timeReduceTmp = stopReduce - stopMap;
				timeReduce += timeReduceTmp;
				System.out.print("\r" + totalcount + " files processed in "
						+ (stopReduce - start) / 1000.0 + "s    ");
				MyLogger.print(totalcount + " files processed in "
						+ (stopReduce - start) / 1000.0 + "s (map/reduce: "
						+ timeMapTmp / 1000.0 + "/" + timeReduceTmp / 1000.0
						+ "s)");
				chunk.clear();
			}
			long stop = System.currentTimeMillis();
			time = stop - start;
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

			XmlSerializer.printDocument(result, "output.xml", false);
			TranslatorC3PO.printDocument(result, "outputc3po.xml", false);
			System.out.println("Process finished");
			MyLogger.print("Process finished");

		} catch (Exception e) {
			MyLogger.print(Aggregator.class.getName() + ", exception:"
					+ e.getMessage());
			// System.out.print(e.getMessage());
		}
	}

}
