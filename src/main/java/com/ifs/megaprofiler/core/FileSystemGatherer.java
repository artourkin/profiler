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
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

/**
 *
 * @author artur
 */
public class FileSystemGatherer implements Gatherer {

    private Map<String, Object> config;
    private List<String> files;
    private long count;
    private long remaining;
    private int pointer;

    public FileSystemGatherer(String path) {
        init(path);
    }

    public long getRemaining() {
        return this.remaining;
    }

    public long getCount() {
        return this.count;
    }

    private void init(String path) {
        this.files = new ArrayList<String>();
        this.pointer = 0;
        this.count = -1;
        this.remaining = -1;

        boolean recursive = true;

        if (path == null) {
            return;
        }

        File dir = new File(path);

        if (!dir.exists() || !dir.isDirectory()) {
            return;
        }

        final XMLFileFilter filter = new XMLFileFilter(recursive);

        this.count = this.traverseFiles(dir, filter);
        this.remaining = this.count;

    }

    public List<InputStream> getNext(int nr) {
        List<InputStream> next = new ArrayList<InputStream>();

        if (nr <= 0) {
            return next;
        }

        while (this.pointer < this.files.size() && nr > 0) {
            try {
                nr--;
                this.remaining--;
                next.add(new FileInputStream(this.files.get(pointer++)));
            } catch (FileNotFoundException e) {
            }
        }

        return next;
    }

    private long traverseFiles(File file, FileFilter filter) {
        long sum = 0;

        if (file.isDirectory()) {
            File[] files = file.listFiles(filter);
            for (File f : files) {
                sum += traverseFiles(f, filter);
            }
        } else {
            this.files.add(file.getAbsolutePath());
            sum++;
        }

        return sum;
    }

    private class XMLFileFilter implements FileFilter {

        private boolean recursive;

        public XMLFileFilter(boolean recursive) {
            this.recursive = recursive;
        }

        @Override
        public boolean accept(File pathname) {
            boolean accept = false;

            if ((pathname.isDirectory() && this.recursive) || pathname.getName().endsWith(".xml")) {
                accept = true;
            }

            return accept;
        }
    }

    private void Extract(File file) throws Exception {
        //String s;
        try {

            Process p = Runtime.getRuntime().exec("tar -xzf " + file.getPath() + " -C " + file.getParent());
            //    BufferedReader br = new BufferedReader(
            //       new InputStreamReader(p.getInputStream()));
            //   while ((s = br.readLine()) != null)
            //       System.out.println("line: " + s);
            System.out.println("Extracting: " + file.getPath());
            p.waitFor();
            // System.out.println ("exit: " + p.exitValue());
            p.destroy();
        } catch (Exception ignored) {
        }
    }

    public void Extract(File file, String Destination) {
        //String s;
        try {
            Process p = Runtime.getRuntime().exec("tar -xzf " + file.getPath() + " -C " + Destination);
            //    BufferedReader br = new BufferedReader(
            //       new InputStreamReader(p.getInputStream()));
            //   while ((s = br.readLine()) != null)
            //       System.out.println("line: " + s);
            System.out.println("Extracting: " + file.getPath());
            p.waitFor();
            // System.out.println ("exit: " + p.exitValue());
            p.destroy();
        } catch (Exception ignored) {
        }
    }
}
