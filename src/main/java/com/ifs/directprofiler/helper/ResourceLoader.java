/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ifs.directprofiler.helper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;

/**
 * 
 * @author artur
 */
public class ResourceLoader {

	public static List<String> getAllowedElements() {
		List<String> result = new ArrayList<String>();
		BufferedReader br;
		try {
			URI uri = ClassLoader.getSystemResource("properties.list").toURI(); 
			File file=new File(uri);
			if (file != null && !file.exists()) {
				file = new File("properties.list");
			}
			if (file != null && !file.exists()) {
				file = new File(FileUtils.getUserDirectoryPath()
						+ "properties.list");
			}
			if (file != null && !file.exists()) {
				System.out
						.println("Could not find 'properties.list'. Using an empty list");
				return result;
			}
			br = new BufferedReader(new FileReader(file));
			String line;
			while ((line = br.readLine()) != null) {
				result.add(line);
			}
			br.close();
		} catch (FileNotFoundException ex) {
			Logger.getLogger(ResourceLoader.class.getName()).log(Level.SEVERE,
					null, ex);
		} catch (IOException ex) {
			Logger.getLogger(ResourceLoader.class.getName()).log(Level.SEVERE,
					null, ex);
		} catch (URISyntaxException ex) {
			Logger.getLogger(ResourceLoader.class.getName()).log(Level.SEVERE,
					null, ex);
		} 
		return result;
	}
}
