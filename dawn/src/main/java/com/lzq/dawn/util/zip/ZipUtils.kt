package com.lzq.dawn.util.zip;

import android.util.Log;

import com.lzq.dawn.DawnBridge;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/**
 * @Name : ZipUtils
 * @Time : 2022/12/21  16:33
 * @Author :  Lzq
 * @Desc : zip
 */
public class ZipUtils {

    private static final int BUFFER_LEN = 8192;

    private ZipUtils() {
    }


    /**
     * 压缩文件
     *
     * @param srcFiles    文件的来源。
     * @param zipFilePath 压缩后文件的路径。
     * @return {@code true}: success<br>{@code false}: fail
     * @throws IOException 如果发生 IO 错误
     */
    public static boolean zipFiles(final Collection<String> srcFiles,
                                   final String zipFilePath)
            throws IOException {
        return zipFiles(srcFiles, zipFilePath, null);
    }

    /**
     * 压缩文件
     *
     * @param srcFilePaths 源文件的路径。
     * @param zipFilePath  压缩后文件的路径。
     * @param comment      评论
     * @return {@code true}: success<br>{@code false}: fail
     * @throws IOException 如果发生 IO 错误
     */
    public static boolean zipFiles(final Collection<String> srcFilePaths,
                                   final String zipFilePath,
                                   final String comment)
            throws IOException {
        if (srcFilePaths == null || zipFilePath == null) {
            return false;
        }
        ZipOutputStream zos = null;
        try {
            zos = new ZipOutputStream(new FileOutputStream(zipFilePath));
            for (String srcFile : srcFilePaths) {
                if (!zipFile(DawnBridge.getFileByPath(srcFile), "", zos, comment)) {
                    return false;
                }
            }
            return true;
        } finally {
            if (zos != null) {
                zos.finish();
                zos.close();
            }
        }
    }

    /**
     * 压缩文件
     *
     * @param srcFiles 文件的来源。
     * @param zipFile  压缩后文件的路径。
     * @return {@code true}: success<br>{@code false}: fail
     * @throws IOException 如果发生 IO 错误
     */
    public static boolean zipFiles(final Collection<File> srcFiles, final File zipFile)
            throws IOException {
        return zipFiles(srcFiles, zipFile, null);
    }

    /**
     * 压缩文件
     *
     * @param srcFiles 文件的来源。
     * @param zipFile  压缩后文件的路径。
     * @param comment  评论
     * @return {@code true}: success<br>{@code false}: fail
     * @throws IOException 如果发生 IO 错误
     */
    public static boolean zipFiles(final Collection<File> srcFiles,
                                   final File zipFile,
                                   final String comment)
            throws IOException {
        if (srcFiles == null || zipFile == null) {
            return false;
        }
        ZipOutputStream zos = null;
        try {
            zos = new ZipOutputStream(new FileOutputStream(zipFile));
            for (File srcFile : srcFiles) {
                if (!zipFile(srcFile, "", zos, comment)) {
                    return false;
                }
            }
            return true;
        } finally {
            if (zos != null) {
                zos.finish();
                zos.close();
            }
        }
    }

    /**
     * 压缩文件
     *
     * @param srcFilePath 源文件的路径。
     * @param zipFilePath 压缩后文件的路径
     * @return {@code true}: success<br>{@code false}: fail
     * @throws IOException 如果发生 IO 错误
     */
    public static boolean zipFile(final String srcFilePath,
                                  final String zipFilePath)
            throws IOException {
        return zipFile(DawnBridge.getFileByPath(srcFilePath), DawnBridge.getFileByPath(zipFilePath), null);
    }

    /**
     * 压缩文件
     *
     * @param srcFilePath 源文件的路径。
     * @param zipFilePath 压缩后文件的路径
     * @param comment  评论
     * @return {@code true}: success<br>{@code false}: fail
     * @throws IOException 如果发生 IO 错误
     */
    public static boolean zipFile(final String srcFilePath,
                                  final String zipFilePath,
                                  final String comment)
            throws IOException {
        return zipFile(DawnBridge.getFileByPath(srcFilePath), DawnBridge.getFileByPath(zipFilePath), comment);
    }

    /**
     * 压缩文件
     *
     * @param srcFile 源文件的路径。
     * @param zipFile 压缩后文件的路径
     * @return {@code true}: success<br>{@code false}: fail
     * @throws IOException 如果发生 IO 错误
     */
    public static boolean zipFile(final File srcFile,
                                  final File zipFile)
            throws IOException {
        return zipFile(srcFile, zipFile, null);
    }

    /**
     * 压缩文件
     *
     * @param srcFile 源文件的路径。
     * @param zipFile 压缩后文件的路径
     * @param comment  评论
     * @return {@code true}: success<br>{@code false}: fail
     * @throws IOException 如果发生 IO 错误
     */
    public static boolean zipFile(final File srcFile,
                                  final File zipFile,
                                  final String comment)
            throws IOException {
        if (srcFile == null || zipFile == null) {
            return false;
        }
        ZipOutputStream zos = null;
        try {
            zos = new ZipOutputStream(new FileOutputStream(zipFile));
            return zipFile(srcFile, "", zos, comment);
        } finally {
            if (zos != null) {
                zos.close();
            }
        }
    }

    private static boolean zipFile(final File srcFile,
                                   String rootPath,
                                   final ZipOutputStream zos,
                                   final String comment)
            throws IOException {
        rootPath = rootPath + (DawnBridge.isSpace(rootPath) ? "" : File.separator) + srcFile.getName();
        if (srcFile.isDirectory()) {
            File[] fileList = srcFile.listFiles();
            if (fileList == null || fileList.length <= 0) {
                ZipEntry entry = new ZipEntry(rootPath + '/');
                entry.setComment(comment);
                zos.putNextEntry(entry);
                zos.closeEntry();
            } else {
                for (File file : fileList) {
                    if (!zipFile(file, rootPath, zos, comment)) {
                        return false;
                    }
                }
            }
        } else {
            InputStream is = null;
            try {
                is = new BufferedInputStream(new FileInputStream(srcFile));
                ZipEntry entry = new ZipEntry(rootPath);
                entry.setComment(comment);
                zos.putNextEntry(entry);
                byte buffer[] = new byte[BUFFER_LEN];
                int len;
                while ((len = is.read(buffer, 0, BUFFER_LEN)) != -1) {
                    zos.write(buffer, 0, len);
                }
                zos.closeEntry();
            } finally {
                if (is != null) {
                    is.close();
                }
            }
        }
        return true;
    }



    /**
     * 解压缩文件。
     *
     * @param zipFilePath ZIP 文件的路径。
     * @param destDirPath 目标目录
     * @return 解压缩的文件
     * @throws IOException 如果解压缩不成功
     */
    public static List<File> unzipFile(final String zipFilePath,
                                       final String destDirPath)
            throws IOException {
        return unzipFileByKeyword(zipFilePath, destDirPath, null);
    }

    /**
     * 解压缩文件。
     *
     * @param zipFile ZIP 文件。
     * @param destDir 目标目录。
     * @return 解压缩的文件
     * @throws IOException 如果解压缩不成功
     */
    public static List<File> unzipFile(final File zipFile,
                                       final File destDir)
            throws IOException {
        return unzipFileByKeyword(zipFile, destDir, null);
    }

    /**
     * 解压缩文件 有密码
     *
     * @param zipFilePath ZIP 文件的路径。
     * @param destDirPath 目标目录。
     * @param keyword    密码
     * @return 解压缩的文件
     * @throws IOException 如果解压缩不成功
     */
    public static List<File> unzipFileByKeyword(final String zipFilePath,
                                                final String destDirPath,
                                                final String keyword)
            throws IOException {
        return unzipFileByKeyword(DawnBridge.getFileByPath(zipFilePath), DawnBridge.getFileByPath(destDirPath), keyword);
    }

    /**
     * 解压缩文件 有密码
     *
     * @param zipFile ZIP 文件
     * @param destDir 目标目录。
     * @param keyword 密码
     * @return 解压缩的文件
     * @throws IOException 如果解压缩不成功
     */
    public static List<File> unzipFileByKeyword(final File zipFile,
                                                final File destDir,
                                                final String keyword)
            throws IOException {
        if (zipFile == null || destDir == null) {
            return null;
        }
        List<File> files = new ArrayList<>();
        ZipFile zip = new ZipFile(zipFile);
        Enumeration<?> entries = zip.entries();
        try {
            if (DawnBridge.isSpace(keyword)) {
                while (entries.hasMoreElements()) {
                    ZipEntry entry = ((ZipEntry) entries.nextElement());
                    String entryName = entry.getName().replace("\\", "/");
                    if (entryName.contains("../")) {
                        Log.e("ZipUtils", "entryName: " + entryName + " is dangerous!");
                        continue;
                    }
                    if (!unzipChildFile(destDir, files, zip, entry, entryName)) {
                        return files;
                    }
                }
            } else {
                while (entries.hasMoreElements()) {
                    ZipEntry entry = ((ZipEntry) entries.nextElement());
                    String entryName = entry.getName().replace("\\", "/");
                    if (entryName.contains("../")) {
                        Log.e("ZipUtils", "entryName: " + entryName + " is dangerous!");
                        continue;
                    }
                    if (entryName.contains(keyword)) {
                        if (!unzipChildFile(destDir, files, zip, entry, entryName)) {
                            return files;
                        }
                    }
                }
            }
        } finally {
            zip.close();
        }
        return files;
    }

    private static boolean unzipChildFile(final File destDir,
                                          final List<File> files,
                                          final ZipFile zip,
                                          final ZipEntry entry,
                                          final String name) throws IOException {
        File file = new File(destDir, name);
        files.add(file);
        if (entry.isDirectory()) {
            return DawnBridge.createOrExistsDir(file);
        } else {
            if (!DawnBridge.createOrExistsFile(file)) {
                return false;
            }
            InputStream in = null;
            OutputStream out = null;
            try {
                in = new BufferedInputStream(zip.getInputStream(entry));
                out = new BufferedOutputStream(new FileOutputStream(file));
                byte buffer[] = new byte[BUFFER_LEN];
                int len;
                while ((len = in.read(buffer)) != -1) {
                    out.write(buffer, 0, len);
                }
            } finally {
                if (in != null) {
                    in.close();
                }
                if (out != null) {
                    out.close();
                }
            }
        }
        return true;
    }

    /**
     * 返回 ZIP 文件中的文件路径。
     *
     * @param zipFilePath ZIP 文件的路径。
     * @return ZIP 文件中的文件路径
     * @throws IOException 如果发生 IO 错误
     */
    public static List<String> getFilesPath(final String zipFilePath)
            throws IOException {
        return getFilesPath(DawnBridge.getFileByPath(zipFilePath));
    }

    /**
     * 返回 ZIP 文件中的文件路径。
     *
     * @param zipFile 压缩文件。
     * @return ZIP 文件中的文件路径
     * @throws IOException 如果发生 IO 错误
     */
    public static List<String> getFilesPath(final File zipFile)
            throws IOException {
        if (zipFile == null) {
            return null;
        }
        List<String> paths = new ArrayList<>();
        ZipFile zip = new ZipFile(zipFile);
        Enumeration<?> entries = zip.entries();
        while (entries.hasMoreElements()) {
            String entryName = ((ZipEntry) entries.nextElement()).getName().replace("\\", "/");
            if (entryName.contains("../")) {
                Log.e("ZipUtils", "entryName: " + entryName + " is dangerous!");
                paths.add(entryName);
            } else {
                paths.add(entryName);
            }
        }
        zip.close();
        return paths;
    }

    /**
     * 以 ZIP 文件返回文件的注释。
     *
     * @param zipFilePath ZIP 文件的路径。
     * @return ZIP 文件中的文件注释
     * @throws IOException 如果发生 IO 错误
     */
    public static List<String> getComments(final String zipFilePath)
            throws IOException {
        return getComments(DawnBridge.getFileByPath(zipFilePath));
    }

    /**
     *返回文件的注释。
     *
     * @param zipFile 压缩文件。
     * @return 文件的注释
     * @throws IOException 如果发生 IO 错误
     */
    public static List<String> getComments(final File zipFile)
            throws IOException {
        if (zipFile == null) {
            return null;
        }
        List<String> comments = new ArrayList<>();
        ZipFile zip = new ZipFile(zipFile);
        Enumeration<?> entries = zip.entries();
        while (entries.hasMoreElements()) {
            ZipEntry entry = ((ZipEntry) entries.nextElement());
            comments.add(entry.getComment());
        }
        zip.close();
        return comments;
    }

}
