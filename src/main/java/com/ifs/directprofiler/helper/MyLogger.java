package com.ifs.directprofiler.helper;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import org.apache.commons.io.FileUtils;

public class MyLogger {

	static File file;
	static String path = "logs/directprofiler.log";
	static Object lock = new Object();

	public static void print(String text) {
		synchronized (lock) {
			if (file == null) {
				file = new File(path);
			}
			try {
				FileUtils.writeStringToFile(file, new Date().toString() + ": "
						+ text + "\n", true);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
