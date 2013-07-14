package com.ifs.megaprofiler;

import java.util.ArrayList;
import java.util.List;

import com.ifs.megaprofiler.core.Aggregator;
import com.ifs.megaprofiler.core.FileSystemGatherer;
import com.ifs.megaprofiler.elements.Document;
import com.ifs.megaprofiler.helper.MyLogger;
import com.ifs.megaprofiler.helper.XmlSerialiser;
import com.ifs.megaprofiler.maths.Maths;

/**
 * Hello world! Oh yes! Hello, world :D
 * 
 */
public class App {

	public static void main(String[] args) {
		if (args.length < 1 || args[0] == null) {
			System.out.println("Please provide a path to FITS results");
			return;
		}

		long start = System.currentTimeMillis();
		System.out.println("Process started");
		MyLogger.print("Process started");
		String path = args[0];
		MyLogger.print("Initialisation...");
		FileSystemGatherer fsgatherer = new FileSystemGatherer(path);
		Aggregator aggregator = new Aggregator();
		Document result = new Document();
		List<Document> chunk = new ArrayList<Document>();
		int chunkmaxsize = 1000;
		int totalcount = 0;
		long stopMerge = System.currentTimeMillis();
		long stopGather = System.currentTimeMillis();
		long timeGather = 0;
		long timeMerge = 0;
		MyLogger.print("Initialisation complete");
		while (true) {
			try {
				totalcount++;
				chunk.add(aggregator.parseDocument(fsgatherer.getNext()));
				if (totalcount % chunkmaxsize == 0) {

					stopGather = System.currentTimeMillis();
					timeGather = stopGather - stopMerge;
					result = Maths.merge(result, Maths.merge(chunk));
					stopMerge = System.currentTimeMillis();
					timeMerge = stopMerge - stopGather;
					System.out.print("\r" + totalcount + " files processed in "
							+ (stopMerge - start) / 1000.0 + "s    ");
					MyLogger.print(totalcount + " files processed in "
							+ (stopMerge - start) / 1000.0 + "s (" + timeGather
							/ 1000.0 + "s - read, " + timeMerge / 1000.0
							+ "s - merge)");
					chunk.clear();
				}
				if (!fsgatherer.hasNext()) {
					break;
				}
			} catch (Exception e) {
				MyLogger.print(Aggregator.class.getName() + ", exception:"
						+ e.getMessage());
			}
		}
		if (chunk.size() > 0) {
			try {
				result = Maths.merge(result, Maths.merge(chunk));
				stopMerge = System.currentTimeMillis();
				timeMerge = stopMerge - stopGather;
				System.out.print("\r" + totalcount + " files processed in "
						+ (stopMerge - start) / 1000.0 + "s    ");
				MyLogger.print(totalcount + " files processed in "
						+ (stopMerge - start) / 1000.0 + "s (" + timeGather
						/ 1000.0 + "s - read, " + timeMerge / 1000.0
						+ "s - merge)");
				chunk.clear();
			} catch (Exception e) {
				MyLogger.print(Aggregator.class.getName() + ", exception:"
						+ e.getMessage());
			}

		}
		long stop = System.currentTimeMillis();
		long time = stop - start;
		System.out.println("\nTotal elapsed time: " + time / 1000.0 + "s");
		System.out.println("Average time: " + time / (float) (totalcount)
				+ "s per " + chunkmaxsize + " files");
		MyLogger.print("Total elapsed time: " + time / 1000.0 + "s");
		MyLogger.print("Average time: " + time / (float) (totalcount)
				+ "s per " + chunkmaxsize + " files");

		XmlSerialiser.printDocument(result, "output.xml", false);
		System.out.println("Process finished");
		MyLogger.print("Process finished");
	}
}
