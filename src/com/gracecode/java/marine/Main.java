package com.gracecode.java.marine;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    private static final String SOURCE_DIR = "/Users/mingcheng/Desktop/NMEA/";
    private static final String OUTPUT_DIR = "/Users/mingcheng/Sites/NMEA/json/";
    private static final int THREAD_NUM = 10;

    private static ExecutorService threadPool;

    private static class ConverterRunnable implements Runnable {
        private final SentenceConverter converter;

        public ConverterRunnable(File from, File to) throws IOException {
            converter = new SentenceConverter(from, to);
        }

        @Override
        public void run() {
            converter.start();
        }
    }

    public static void main(String[] args) {
        threadPool = Executors.newFixedThreadPool(THREAD_NUM);

        File NMEADirectory = new File(SOURCE_DIR);
        File[] NMEAFiles = NMEADirectory.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".LOG");
            }
        });

        for (File NMEAFile : NMEAFiles) {
            File JSONFile = getJSONFileFromNMEAFile(NMEAFile);
            try {
                threadPool.submit(new ConverterRunnable(NMEAFile, JSONFile));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static File getJSONFileFromNMEAFile(File NMEAFile) {
        String basename = NMEAFile.getName().replaceAll("[.][^.]+$", "");
        return new File(OUTPUT_DIR + basename + ".json");
    }
}
