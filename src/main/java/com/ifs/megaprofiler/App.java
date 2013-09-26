package com.ifs.megaprofiler;

import com.ifs.megaprofiler.core.Controller;

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
		Controller ctr = new Controller();
		ctr.Execute(args[0]);

	}
}
