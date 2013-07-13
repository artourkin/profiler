/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ifs.megaprofiler.core;

import com.ifs.megaprofiler.api.Gatherer;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;
import de.schlichtherle.truezip.file.TArchiveDetector;
import de.schlichtherle.truezip.file.TFile;

/**
 * 
 * @author artur
 */
public class FileSystemGatherer {

	private Iterator<File> iterator;
	private Iterator<File> iteratorArchive;

	private static final String[] ARCHIVE_EXTENSIONS = { ".zip", ".tar",
			".bzip2", ".tar.bz2", ".bz2", ".tb2", ".tbz", ".tar.gz", ".tgz",
			".gz", ".tar.xz", ".txz", ".xz" };

	private static int folder = 0;

	public FileSystemGatherer(String path) {
		init(path);
	}

	public boolean hasNext() {
		if (iteratorArchive != null  && iteratorArchive.hasNext())
			return true;
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
		if (iteratorArchive != null && iteratorArchive.hasNext())
			return new FileInputStream(iteratorArchive.next());
		File file = iterator.next();
		if (isXML(file.getName()))
			return new FileInputStream(file);
		else if (isArchive(file.getName())) {
			clearTmpDir();
			extractArchive(file.getAbsolutePath());
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
		TFile archive = new TFile(filePath);
		String tmp = FileUtils.getTempDirectory().getPath() + File.separator
				+ "c3poarchives" ;//+ File.separator + folder++;
		File tmpDir = new File(tmp);
		if (!tmpDir.exists())
			tmpDir.mkdirs();

		TFile directory = new TFile(tmpDir);
		TFile.cp_r(archive, directory, TArchiveDetector.NULL,
				TArchiveDetector.NULL);
		this.iteratorArchive = FileUtils.iterateFiles(tmpDir, null, true);
	}

	private boolean isArchive(String name) {
		for (String ext : ARCHIVE_EXTENSIONS)
			if (name.endsWith(ext))
				return true;
		return false;
	}

	private boolean isXML(String name) {
		if (name.endsWith(".xml"))
			return true;
		return false;
	}

}
