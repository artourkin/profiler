/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ifs.directprofiler.helper;

/**
 * 
 * @author artur
 */
public class MyPrinter {

	static Object lock = new Object();

	public static void print(String text) {
		synchronized (lock) {
			System.out.print(text);
		}

	}
}
