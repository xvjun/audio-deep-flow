package com.xujun.utils;

import java.io.*;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

public class FileUtils {

    public static ArrayList<String> readFromTextFile(String pathname) throws IOException {
        ArrayList<String> strArray = new ArrayList<String>();
        File filename = new File(pathname);
        InputStreamReader reader = new InputStreamReader(new FileInputStream(filename));
        BufferedReader br = new BufferedReader(reader);
        String line = "";
        line = br.readLine();
        while(line != null) {
            strArray.add(line);
            line = br.readLine();
        }
        return strArray;
    }

    public static long getLineNumber(File file) {
        if (file.exists()) {
            try {
                FileReader fileReader = new FileReader(file);
                LineNumberReader lineNumberReader = new LineNumberReader(fileReader);
                lineNumberReader.skip(Long.MAX_VALUE);
                long lines = lineNumberReader.getLineNumber() + 1;
                fileReader.close();
                lineNumberReader.close();
                return lines;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return 0;
    }

    public static long getFileSizeByKB(File file) {
        if (file.exists() && file.isFile()) {
            return file.length()/1024;
        }else{
            return 0;
        }
    }

    public static void copyFileUsingFileChannels(File source, File dest) throws IOException {
        FileChannel inputChannel = null;
        FileChannel outputChannel = null;
        try {
            inputChannel = new FileInputStream(source).getChannel();
            outputChannel = new FileOutputStream(dest).getChannel();
            outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
        } finally {
            inputChannel.close();
            outputChannel.close();
        }
    }

    public static String readTxtFile(File file) throws Exception {
        StringBuffer stringBuffer = new StringBuffer();
        InputStream inputStream= new FileInputStream(file);
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
        String line;
        while ((line = br.readLine()) != null) {
            stringBuffer.append(line+"\n");
        }
        return stringBuffer.toString();
    }


}
