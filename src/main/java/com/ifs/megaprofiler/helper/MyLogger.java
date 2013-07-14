package com.ifs.megaprofiler.helper;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import org.apache.commons.io.FileUtils;

public class MyLogger {
	static File file;
	static String path = "logs/megaprofiler.log";
	static Date date;

	public static void print(String text) {
		if (file == null){
			file = new File(path);
			date=new Date();
		
		}
		
		try {
			FileUtils.writeStringToFile(file, new Date().toString() +": "+ text +"\n",true);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
