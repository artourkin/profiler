/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ifs.directprofiler.helper;

import java.util.concurrent.Semaphore;

import com.ifs.directprofiler.core.Parser;

/**
 * 
 * @author artur
 */
public class Message {

	boolean value;
	Semaphore semaphoreParsing, semaphoreAggregation;

	public Message() {
		value = false;
		semaphoreAggregation = new Semaphore(1);
		semaphoreParsing = new Semaphore(1);
		try {
			semaphoreAggregation.acquire();
			semaphoreParsing.acquire();
		} catch (Exception e) {
			MyLogger.print(Parser.class.getName() + ", exception:"
					+ e.getMessage());
			return;
		}
	}

	public boolean aggregationIsFinished() {
		if (semaphoreAggregation.availablePermits() > 0)
			return true;
		return false;
	}

	public boolean parsingIsFinished() {
		if (semaphoreParsing.availablePermits() > 0)
			return true;
		return false;
	}

	public void finishAggregation() {
		semaphoreAggregation.release();
	}

	public void finishParsing() {
		semaphoreParsing.release();
	}

}
