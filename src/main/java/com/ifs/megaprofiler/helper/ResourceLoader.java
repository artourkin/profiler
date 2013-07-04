/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ifs.megaprofiler.helper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author artur
 */
public class ResourceLoader {

  public static List<String> getAllowedElements() {
    List<String> result = new ArrayList<String>();
    BufferedReader br;
    try {
      File file = new File("src/main/resources/properties.list");
      br = new BufferedReader(new FileReader(file));
      String line;
      while ((line = br.readLine()) != null) {
        result.add(line);
      }
      br.close();
    } catch (FileNotFoundException ex) {
      Logger.getLogger(ResourceLoader.class.getName()).log(Level.SEVERE, null, ex);
    } catch (IOException ex) {
      Logger.getLogger(ResourceLoader.class.getName()).log(Level.SEVERE, null, ex);
    }
    return result;
  }
  
}
