package com.xujun.utils;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;

import java.io.*;
import java.net.URI;

public class HdfsUtils {

    private static FileSystem fileSystem = null;

    private static FileSystem createFileSystemInstance(String hdfsUrl) throws Exception {
        Configuration conf = new Configuration();
        URI uri = new URI(hdfsUrl);
        return FileSystem.get(uri, conf, "root");
    }

    public static void copyFileFromHdfs(String hdfsSrc, String localDst) throws Exception {

        FileSystem hdfsFileSystem = createFileSystemInstance(hdfsSrc);
        Path srcPath = new Path(hdfsSrc);
        Path dstPath = new Path(localDst);
        hdfsFileSystem.copyToLocalFile(false,srcPath, dstPath, true);

        hdfsFileSystem.close();
    }

    public static String getFileNameFromHdfsUrl(String hdfsUrl) {
        return new org.apache.hadoop.fs.Path(hdfsUrl).getName();
    }

//    private static FileSystem getHdfsFileSystem(String hdfsUrl) throws IOException {
//        Configuration conf = new Configuration();
//        conf.set("fs.defaultFS", hdfsUrl);
//        return FileSystem.get(conf);
//    }

    public static void copyFromLocalFile(String localPath, String hdfsPath) throws Exception {
        FileSystem hdfsFileSystem = createFileSystemInstance(hdfsPath);
        Path localFilePath = new Path(localPath);
        Path hdfsFilePath = new Path(hdfsPath);
        if(!hdfsFileSystem.exists(hdfsFilePath)){
            hdfsFileSystem.mkdirs(hdfsFilePath);
        }
        hdfsFileSystem.copyFromLocalFile(localFilePath, hdfsFilePath);

        hdfsFileSystem.close();
    }

    public static boolean mvHdfsFile(String path,String newPath) throws Exception {
        FileSystem hdfsFileSystem = createFileSystemInstance(path);
        boolean result = false;
        if (!hdfsFileSystem.exists(new Path(newPath))){
            result=hdfsFileSystem.rename(new Path(path),new Path(newPath));
        }
        return result;
    }

    public static long getHdfsFileCap(String hdfsPath) throws Exception {
        FileSystem hdfsFileSystem = createFileSystemInstance(hdfsPath);
        Path hdfsFilePath = new Path(hdfsPath);
        FileStatus fileStatus = hdfsFileSystem.getFileStatus(hdfsFilePath);
        return fileStatus.getLen()/1024;
    }

    public static long getHdfsFileLength(String hdfsPath) throws Exception {
        FileSystem hdfsFileSystem = createFileSystemInstance(hdfsPath);
        Path hdfsFilePath = new Path(hdfsPath);
        FileStatus fileStatus = hdfsFileSystem.getFileStatus(hdfsFilePath);
        return fileStatus.getLen()/1024;
    }

    public static boolean deleteHdfsData(String hdfsPath) throws Exception {
        FileSystem hdfsFileSystem = createFileSystemInstance(hdfsPath);

        Path hdfsFilePath = new Path(hdfsPath);
        boolean flag = hdfsFileSystem.delete(hdfsFilePath, false);

        hdfsFileSystem.close();
        return flag;
    }

}
