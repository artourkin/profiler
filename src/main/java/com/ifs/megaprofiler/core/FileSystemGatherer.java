/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ifs.megaprofiler.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import org.apache.commons.io.FileUtils;

import com.ifs.megaprofiler.helper.FileExtractor;
import com.ifs.megaprofiler.helper.MyLogger;

/**
 * 
 * @author artur
 */
public class FileSystemGatherer {

	private Iterator<File> iterator;
	private Iterator<File> iteratorArchive;
	private static final String[] ARCHIVE_EXTENSIONS = { ".zip", ".tar",
			".tar.gz", ".tgz", ".gz" };

	public FileSystemGatherer(String path) {
		init(path);
	}

	public boolean hasNext() {
		if (iteratorArchive != null && iteratorArchive.hasNext()) {
			return true;
		}
		return iterator.hasNext();
	}

	private void init(String path) {
		if (path == null) {
			return;
		}
		File dir = new File(path);
		if (!dir.exists() || !dir.isDirectory()) {
			return;
		}
		this.iterator = FileUtils.iterateFiles(dir, null, true);

	}

	public InputStream getNext() throws IOException {
		if (iteratorArchive != null && iteratorArchive.hasNext()) {
			return new FileInputStream(iteratorArchive.next());
		}
		if (iterator == null) {
			throw new IOException("ERROR! Filepath specified incorrectly");
		}
		File file = iterator.next();
		if (isXML(file.getName())) {
			return new FileInputStream(file);
		} else if (isArchive(file.getName())) {
			clearTmpDir();
			MyLogger.print("Extraction started from " + file.getName());
			extractArchive(file.getAbsolutePath());
			MyLogger.print("Extraction complete");
			return getNext();
		}
		return null;

	}

	private void clearTmpDir() throws IOException {
		String tmp = FileUtils.getTempDirectory().getPath() + File.separator
				+ "c3poarchives";

		File tmpDir = new File(tmp);
		FileUtils.deleteDirectory(tmpDir);

	}

	private void extractArchive(String filePath) throws IOException {

		// TFile archive = new TFile(filePath);
		String tmp = FileUtils.getTempDirectory().getPath() + File.separator
				+ "c3poarchives";// + File.separator + folder++;

		File tmpDir = new File(tmp);
		if (!tmpDir.exists()) {
			tmpDir.mkdirs();
		}
		FileExtractor.extract(filePath, tmpDir);
		// TFile directory = new TFile(tmpDir);
		// TFile.cp_r(archive, directory, TArchiveDetector.NULL,
		// TArchiveDetector.NULL);
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
}
