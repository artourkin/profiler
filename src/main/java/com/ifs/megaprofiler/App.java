package com.ifs.megaprofiler;

import com.ifs.megaprofiler.core.Controller;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Hello world! Oh yes! Hello, world :D
 * 
 */
public class App {

	public static void main(String[] args) {
		if (args.length < 1 || args[0] == null) {
			System.out
					.println("Please provide paths to FITS results and a profile output (optional)");
			System.out
					.println("e.g.: java -jar profiler.jar /home/user/fits /home/user/profiles/");
			return;
		}
		Controller ctr = new Controller();
		try {
			if (args.length == 1) {

				ctr.Execute(args[0], "");

			} else {
				ctr.Execute(args[0], args[1]);
			}
		} catch (IOException ex) {
			Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
}
