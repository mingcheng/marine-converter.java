package com.gracecode.java.marine;

import org.apache.commons.cli.*;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    private static final int THREAD_NUM = 10;
    private static File NMEADirectory;
    private static File JSONDirectory;

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
        ExecutorService threadPool = Executors.newFixedThreadPool(THREAD_NUM);

        Options options = new Options();
        options.addOption("i", "input-dir", true, "input directory");
        options.addOption("o", "output-dir", true, "output directory");
        options.addOption("f", "focus", false, "focus to create/overwrite file");

        try {
            CommandLine commandLine = new DefaultParser().parse(options, args);
            boolean focus = commandLine.hasOption("f");
            try {
                NMEADirectory = new File(commandLine.getOptionValue("i"));
                JSONDirectory = new File(commandLine.getOptionValue("o"));
            } catch (NullPointerException e) {
                throw new ParseException("");
            }

            if (!NMEADirectory.canRead() || !NMEADirectory.isDirectory()) {
                throw new IOException(NMEADirectory.getAbsolutePath() + " is NOT readable.");
            }

            if ((!JSONDirectory.exists() && !JSONDirectory.mkdirs())
                    || !JSONDirectory.canWrite() || !JSONDirectory.isDirectory()) {
                throw new IOException(JSONDirectory.getAbsolutePath() + " is NOT writable.");
            }

            File[] NMEAFiles = NMEADirectory.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return name.endsWith(".LOG");
                }
            });

            for (File NMEAFile : NMEAFiles) {
                try {
                    File JSONFile = getJSONFileFromNMEAFile(NMEAFile);
                    if (!JSONFile.exists()) {
                        threadPool.execute(new ConverterRunnable(NMEAFile, JSONFile));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (ParseException e) {
            new HelpFormatter().printHelp(Main.class.getName(), options);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private static File getJSONFileFromNMEAFile(File NMEAFile) {
        String basename = NMEAFile.getName().replaceAll("[.][^.]+$", "");
        return new File(JSONDirectory.getAbsolutePath() + File.separator + basename + ".json");
    }
}
