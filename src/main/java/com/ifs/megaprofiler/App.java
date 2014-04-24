package com.ifs.megaprofiler;

import com.ifs.megaprofiler.core.Controller;

import java.io.Console;

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
        Console console = System.console();
        if (console == null) {
            System.out.println("Unable to fetch console");
            return;
        }
        Controller ctr = new Controller();
        if (args.length == 1) {
            ctr.Execute(args[0], "");
        } else {
          //  Filter f=new Filter();
          //  f.addFilterCondition(new FilterCondition("format", "Portable Document Format"));
          //  ctr.applyFilter(f);//Execute(args[0], args[1]);
        }
        String line = console.readLine();
        console.printf("I saw this line: %s", line);

	}
}
