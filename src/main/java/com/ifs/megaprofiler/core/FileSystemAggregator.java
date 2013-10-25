/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ifs.megaprofiler.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import org.apache.commons.io.FileUtils;

import com.ifs.megaprofiler.helper.FileExtractor;
import com.ifs.megaprofiler.helper.Message;
import com.ifs.megaprofiler.helper.MyLogger;
import java.io.BufferedInputStream;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 
 * @author artur
 */
public class FileSystemAggregator implements Runnable {

	Message message;
	protected BlockingQueue queue;
	private Iterator<File> iterator;
	private Iterator<File> iteratorArchive;
	private static final String[] ARCHIVE_EXTENSIONS = { ".zip", ".tar",
			".tar.gz", ".tgz", ".gz" };
	private volatile boolean running = true;
	String tmpDirPath;

	public FileSystemAggregator(String path, BlockingQueue queue,
			Message message) throws IOException {
		this.queue = queue;
		this.message = message;
		initialize(path);
		tmpDirPath = FileUtils.getTempDirectory().getPath() + File.separator
				+ "c3poarchives";
	}

	public boolean hasNext() {
		if (iteratorArchive != null && iteratorArchive.hasNext()) {
			return true;
		}
		return iterator.hasNext();
	}

	public void terminate() {
		running = false;
	}

	private void initialize(String path) throws IOException {
		if (path == null) {
			throw new IOException("No path specified");
		}
		File dir = new File(path);
		if (!dir.exists() || !dir.isDirectory()) {
			throw new IOException("Directory does not exist");
		}
		this.iterator = FileUtils.iterateFiles(dir, null, true);
	}

	public InputStream getNext() throws IOException  {
		if (iteratorArchive != null && iteratorArchive.hasNext()) {
			File tmpFile = iteratorArchive.next();
			// return new FileInputStream(tmpFile);
			return new BufferedInputStream(new FileInputStream(tmpFile),
					(int) tmpFile.length());
		}
		if (iterator == null) {
			throw new IOException("ERROR! Filepath specified incorrectly");
		}
		if (!iterator.hasNext()) {
			return null;
		}
		File file = iterator.next();
		if (isXML(file.getName())) {

			// return new FileInputStream(file);
			return new BufferedInputStream(new FileInputStream(file),
					(int) file.length());
		} else if (isArchive(file.getName())) {
			removeTmpDir();
			//MyLogger.print("Extraction started from " + file.getName());
			extractArchive(file.getAbsolutePath());
			//MyLogger.print("Extraction complete");
			return getNext();
		}
		return null;

	}

	private void removeTmpDir() throws IOException {
		File tmpDir = new File(tmpDirPath);
		FileUtils.deleteDirectory(tmpDir);

	}

	private void extractArchive(String filePath) throws IOException {

		File tmpDir = new File(tmpDirPath);
		if (!tmpDir.exists()) {
			tmpDir.mkdirs();
		}
		FileExtractor.extract(filePath, tmpDir);
		this.iteratorArchive = FileUtils.iterateFiles(tmpDir, null, true);
	}

	private boolean isArchive(String name) {
		for (String ext : ARCHIVE_EXTENSIONS) {
			if (name.endsWith(ext)) {
				return true;
			}
		}
		return false;
	}

	private boolean isXML(String name) {
		if (name.endsWith(".xml")) {
			return true;
		}
		return false;
	}

	@Override
	public void run() {
		InputStream is = null;
		while (hasNext() && running) {
			is = null;
			try {
				is = getNext();
				if (is != null) {
					queue.put(is);
				}
			} catch (Exception e) {
				MyLogger.print(Parser.class.getName() + ", exception:"
						+ e.getMessage());
			}
		}
		message.finishAggregation();
	}
}
