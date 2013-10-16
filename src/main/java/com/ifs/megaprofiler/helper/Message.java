/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ifs.megaprofiler.helper;

/**
 * 
 * @author artur
 */
public class Message {

	boolean value;

	public Message() {
		value = false;
	}

	public boolean isTrue() {
		return value;
	}

	public void makeItTrue() {
		value = true;
	}

	public void makeItFalse() {
		value = false;
	}
}
