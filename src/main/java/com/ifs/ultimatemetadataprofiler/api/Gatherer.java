/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ifs.ultimatemetadataprofiler.api;

import java.io.InputStream;
import java.util.List;

/**
 *
 * @author artur
 */
public interface Gatherer {

    long getCount();

    long getRemaining();

    List<InputStream> getNext(int count);
}
