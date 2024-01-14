package com.lzq.dawn.util.file;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.net.Uri;
import android.os.Build;
import android.os.StatFs;
import android.text.TextUtils;

import com.lzq.dawn.DawnBridge;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

/**
 * @Name :FileUtils
 * @Time :2022/7/22 14:05
 * @Author :  Lzq
 * @Desc : 文件操作
 */
public final class FileUtils {

    private FileUtils() {
    }

    private static final String LINE_SEP = System.getProperty("line.separator");

    /**
     * 按路径返回文件。
     *
     * @param filePath 文件的路径。
     * @return file
     */
    public static File getFileByPath(final String filePath) {
        return DawnBridge.isSpace(filePath) ? null : new File(filePath);
    }

    /**
     * 返回文件是否存在。
     *
     * @param file file.
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isFileExists(final File file) {
        if (file == null) {
            return false;
        }
        if (file.exists()) {
            return true;
        }
        return isFileExists(file.getAbsolutePath());
    }

    /**
     * 返回文件是否存在。
     *
     * @param filePath 文件的路径
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isFileExists(final String filePath) {
        File file = getFileByPath(filePath);
        if (file == null) {
            return false;
        }
        if (file.exists()) {
            return true;
        }
        return isFileExistsApi29(filePath);
    }

    private static boolean isFileExistsApi29(String filePath) {
        if (Build.VERSION.SDK_INT >= 29) {
            try {
                Uri uri = Uri.parse(filePath);
                ContentResolver cr = DawnBridge.getApp().getContentResolver();
                AssetFileDescriptor afd = cr.openAssetFileDescriptor(uri, "r");
                if (afd == null) {
                    return false;
                }
                try {
                    afd.close();
                } catch (IOException ignore) {
                }
            } catch (FileNotFoundException e) {
                return false;
            }
            return true;
        }
        return false;
    }

    /**
     * 重命名文件。
     *
     * @param filePath 文件的路径
     * @param newName  文件的新名称
     * @return {@code true}: success<br>{@code false}: fail
     */
    public static boolean rename(final String filePath, final String newName) {
        return rename(getFileByPath(filePath), newName);
    }

    /**
     * 重命名文件。
     *
     * @param file    file.
     * @param newName 文件的新名称
     * @return {@code true}: success<br>{@code false}: fail
     */
    public static boolean rename(final File file, final String newName) {
        // file is null then return false
        if (file == null) {
            return false;
        }
        // file doesn't exist then return false
        if (!file.exists()) {
            return false;
        }
        // the new name is space then return false
        if (DawnBridge.isSpace(newName)) {
            return false;
        }
        // the new name equals old name then return true
        if (newName.equals(file.getName())) {
            return true;
        }
        File newFile = new File(file.getParent() + File.separator + newName);
        // the new name of file exists then return false
        return !newFile.exists()
                && file.renameTo(newFile);
    }

    /**
     * 返回是否为目录。
     *
     * @param dirPath 目录的路径
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isDir(final String dirPath) {
        return isDir(getFileByPath(dirPath));
    }

    /**
     * 返回是否为目录
     *
     * @param file file.
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isDir(final File file) {
        return file != null && file.exists() && file.isDirectory();
    }

    /**
     * 返回是否为文件。
     *
     * @param filePath 返回是否为文件。
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isFile(final String filePath) {
        return isFile(getFileByPath(filePath));
    }

    /**
     * 返回是否为文件
     *
     * @param file file.
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isFile(final File file) {
        return file != null && file.exists() && file.isFile();
    }

    /**
     * 如果目录不存在，请创建一个目录，否则什么也不做。
     *
     * @param dirPath 目录的路径。
     * @return {@code true}: exists or creates successfully<br>{@code false}: otherwise
     */
    public static boolean createOrExistsDir(final String dirPath) {
        return createOrExistsDir(getFileByPath(dirPath));
    }

    /**
     * 如果目录不存在，请创建一个目录，否则什么也不做。
     *
     * @param file file.
     * @return {@code true}: exists or creates successfully<br>{@code false}: otherwise
     */
    public static boolean createOrExistsDir(final File file) {
        return file != null && (file.exists() ? file.isDirectory() : file.mkdirs());
    }

    /**
     * 如果文件不存在，请创建一个文件，否则什么也不做。
     *
     * @param filePath 文件的路径。
     * @return {@code true}: exists or creates successfully<br>{@code false}: otherwise
     */
    public static boolean createOrExistsFile(final String filePath) {
        return createOrExistsFile(getFileByPath(filePath));
    }

    /**
     * 如果文件不存在，请创建一个文件，否则什么也不做。
     *
     * @param file file.
     * @return {@code true}: exists or creates successfully<br>{@code false}: otherwise
     */
    public static boolean createOrExistsFile(final File file) {
        if (file == null) {
            return false;
        }
        if (file.exists()) {
            return file.isFile();
        }
        if (!createOrExistsDir(file.getParentFile())) {
            return false;
        }
        try {
            return file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }


    /**
     * 如果文件不存在，请创建一个文件，否则请在创建之前删除旧文件。
     *
     * @param filePath 文件的路径
     * @return {@code true}: success<br>{@code false}: fail
     */
    public static boolean createFileByDeleteOldFile(final String filePath) {
        return createFileByDeleteOldFile(getFileByPath(filePath));
    }

    /**
     * 如果文件不存在，请创建一个文件，否则请在创建之前删除旧文件。
     *
     * @param file file.
     * @return {@code true}: success<br>{@code false}: fail
     */
    public static boolean createFileByDeleteOldFile(final File file) {
        if (file == null) {
            return false;
        }
        // file exists and unsuccessfully delete then return false
        if (file.exists() && !file.delete()) {
            return false;
        }
        if (!createOrExistsDir(file.getParentFile())) {
            return false;
        }
        try {
            return file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 复制目录或文件。
     *
     * @param srcPath  源路径。
     * @param destPath 目的地路径。
     * @return {@code true}: success<br>{@code false}: fail
     */
    public static boolean copy(final String srcPath,
                               final String destPath) {
        return copy(getFileByPath(srcPath), getFileByPath(destPath), null);
    }

    /**
     * 复制目录或文件
     *
     * @param srcPath  源路径。
     * @param destPath 目的地路径。
     * @param listener 替换侦听器。
     * @return {@code true}: success<br>{@code false}: fail
     */
    public static boolean copy(final String srcPath,
                               final String destPath,
                               final OnReplaceListener listener) {
        return copy(getFileByPath(srcPath), getFileByPath(destPath), listener);
    }

    /**
     * 复制目录或文件
     *
     * @param src  源。
     * @param dest 目的地。
     * @return {@code true}: success<br>{@code false}: fail
     */
    public static boolean copy(final File src,
                               final File dest) {
        return copy(src, dest, null);
    }

    /**
     * 复制目录或文件
     *
     * @param src      源。
     * @param dest     目的地。
     * @param listener 替换侦听器
     * @return {@code true}: success<br>{@code false}: fail
     */
    public static boolean copy(final File src,
                               final File dest,
                               final OnReplaceListener listener) {
        if (src == null) {
            return false;
        }
        if (src.isDirectory()) {
            return copyDir(src, dest, listener);
        }
        return copyFile(src, dest, listener);
    }

    /**
     * 复制目录
     *
     * @param srcDir   源目录
     * @param destDir  c目录
     * @param listener 替换侦听器.
     * @return {@code true}: success<br>{@code false}: fail
     */
    private static boolean copyDir(final File srcDir,
                                   final File destDir,
                                   final OnReplaceListener listener) {
        return copyOrMoveDir(srcDir, destDir, listener, false);
    }

    /**
     * 复制file.
     *
     * @param srcFile  源 file.
     * @param destFile 源 file.
     * @param listener 替换侦听器
     * @return {@code true}: success<br>{@code false}: fail
     */
    private static boolean copyFile(final File srcFile,
                                    final File destFile,
                                    final OnReplaceListener listener) {
        return copyOrMoveFile(srcFile, destFile, listener, false);
    }

    /**
     * 移动目录或文件。
     *
     * @param srcPath  源的路径。
     * @param destPath 目的地的路径。
     * @return {@code true}: success<br>{@code false}: fail
     */
    public static boolean move(final String srcPath,
                               final String destPath) {
        return move(getFileByPath(srcPath), getFileByPath(destPath), null);
    }

    /**
     * 移动目录或文件。
     *
     * @param srcPath  源的路径。
     * @param destPath 目的地的路径。
     * @param listener 替换侦听器
     * @return {@code true}: success<br>{@code false}: fail
     */
    public static boolean move(final String srcPath,
                               final String destPath,
                               final OnReplaceListener listener) {
        return move(getFileByPath(srcPath), getFileByPath(destPath), listener);
    }

    /**
     * 移动目录或文件
     *
     * @param src  源
     * @param dest 目的
     * @return {@code true}: success<br>{@code false}: fail
     */
    public static boolean move(final File src,
                               final File dest) {
        return move(src, dest, null);
    }

    /**
     * 移动目录或文件
     *
     * @param src      源
     * @param dest     目的
     * @param listener 替换侦听器
     * @return {@code true}: success<br>{@code false}: fail
     */
    public static boolean move(final File src,
                               final File dest,
                               final OnReplaceListener listener) {
        if (src == null) {
            return false;
        }
        if (src.isDirectory()) {
            return moveDir(src, dest, listener);
        }
        return moveFile(src, dest, listener);
    }

    /**
     * 移动目录
     *
     * @param srcDir   源目录.
     * @param destDir  目标目录
     * @param listener 替换侦听器
     * @return {@code true}: success<br>{@code false}: fail
     */
    public static boolean moveDir(final File srcDir,
                                  final File destDir,
                                  final OnReplaceListener listener) {
        return copyOrMoveDir(srcDir, destDir, listener, true);
    }

    /**
     * 移动文件
     *
     * @param srcFile  源文件
     * @param destFile 目标文件
     * @param listener 替换侦听器
     * @return {@code true}: success<br>{@code false}: fail
     */
    public static boolean moveFile(final File srcFile,
                                   final File destFile,
                                   final OnReplaceListener listener) {
        return copyOrMoveFile(srcFile, destFile, listener, true);
    }

    private static boolean copyOrMoveDir(final File srcDir,
                                         final File destDir,
                                         final OnReplaceListener listener,
                                         final boolean isMove) {
        if (srcDir == null || destDir == null) {
            return false;
        }
        // destDir's path locate in srcDir's path then return false
        String srcPath = srcDir.getPath() + File.separator;
        String destPath = destDir.getPath() + File.separator;
        if (destPath.contains(srcPath)) {
            return false;
        }
        if (!srcDir.exists() || !srcDir.isDirectory()) {
            return false;
        }
        if (!createOrExistsDir(destDir)) {
            return false;
        }
        File[] files = srcDir.listFiles();
        if (files != null && files.length > 0) {
            for (File file : files) {
                File oneDestFile = new File(destPath + file.getName());
                if (file.isFile()) {
                    if (!copyOrMoveFile(file, oneDestFile, listener, isMove)) {
                        return false;
                    }
                } else if (file.isDirectory()) {
                    if (!copyOrMoveDir(file, oneDestFile, listener, isMove)) {
                        return false;
                    }
                }
            }
        }
        return !isMove || deleteDir(srcDir);
    }

    private static boolean copyOrMoveFile(final File srcFile,
                                          final File destFile,
                                          final OnReplaceListener listener,
                                          final boolean isMove) {
        if (srcFile == null || destFile == null) {
            return false;
        }
        // srcFile equals destFile then return false
        if (srcFile.equals(destFile)) {
            return false;
        }
        // srcFile doesn't exist or isn't a file then return false
        if (!srcFile.exists() || !srcFile.isFile()) {
            return false;
        }
        if (destFile.exists()) {
            if (listener == null || listener.onReplace(srcFile, destFile)) {// require delete the old file
                if (!destFile.delete()) {// unsuccessfully delete then return false
                    return false;
                }
            } else {
                return true;
            }
        }
        if (!createOrExistsDir(destFile.getParentFile())) {
            return false;
        }
        try {
            return DawnBridge.writeFileFromIS(destFile.getAbsolutePath(), new FileInputStream(srcFile))
                    && !(isMove && !deleteFile(srcFile));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }


    /**
     * 删除目录。
     *
     * @param filePath 文件的路径。
     * @return {@code true}: success<br>{@code false}: fail
     */
    public static boolean delete(final String filePath) {
        return delete(getFileByPath(filePath));
    }

    /**
     * 删除目录.
     *
     * @param file file.
     * @return {@code true}: success<br>{@code false}: fail
     */
    public static boolean delete(final File file) {
        if (file == null) {
            return false;
        }
        if (file.isDirectory()) {
            return deleteDir(file);
        }
        return deleteFile(file);
    }

    /**
     * 删除目录
     *
     * @param dir 目录
     * @return {@code true}: success<br>{@code false}: fail
     */
    private static boolean deleteDir(final File dir) {
        if (dir == null) {
            return false;
        }
        // dir doesn't exist then return true
        if (!dir.exists()) {
            return true;
        }
        // dir isn't a directory then return false
        if (!dir.isDirectory()) {
            return false;
        }
        File[] files = dir.listFiles();
        if (files != null && files.length > 0) {
            for (File file : files) {
                if (file.isFile()) {
                    if (!file.delete()) {
                        return false;
                    }
                } else if (file.isDirectory()) {
                    if (!deleteDir(file)) {
                        return false;
                    }
                }
            }
        }
        return dir.delete();
    }

    /**
     * 删除文件
     *
     * @param file file.
     * @return {@code true}: success<br>{@code false}: fail
     */
    private static boolean deleteFile(final File file) {
        return file != null && (!file.exists() || file.isFile() && file.delete());
    }

    /**
     * 删除all-in目录。
     *
     * @param dirPath 目录的路径。
     * @return {@code true}: success<br>{@code false}: fail
     */
    public static boolean deleteAllInDir(final String dirPath) {
        return deleteAllInDir(getFileByPath(dirPath));
    }

    /**
     * 删除all-in目录。
     *
     * @param dir 目录
     * @return {@code true}: success<br>{@code false}: fail
     */
    public static boolean deleteAllInDir(final File dir) {
        return deleteFilesInDirWithFilter(dir, new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return true;
            }
        });
    }

    /**
     * 删除目录中的所有文件。
     *
     * @param dirPath 目录的路径。
     * @return {@code true}: success<br>{@code false}: fail
     */
    public static boolean deleteFilesInDir(final String dirPath) {
        return deleteFilesInDir(getFileByPath(dirPath));
    }

    /**
     * 删除目录中的所有文件
     *
     * @param dir 目录的路径。
     * @return {@code true}: success<br>{@code false}: fail
     */
    public static boolean deleteFilesInDir(final File dir) {
        return deleteFilesInDirWithFilter(dir, new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.isFile();
            }
        });
    }

    /**
     * 删除目录中满足筛选器的所有文件。
     *
     * @param dirPath 目录的路径。
     * @param filter  过滤器
     * @return {@code true}: success<br>{@code false}: fail
     */
    public static boolean deleteFilesInDirWithFilter(final String dirPath,
                                                     final FileFilter filter) {
        return deleteFilesInDirWithFilter(getFileByPath(dirPath), filter);
    }

    /**
     * 删除目录中满足筛选器的所有文件。
     *
     * @param dir    目录。
     * @param filter 过滤器
     * @return {@code true}: success<br>{@code false}: fail
     */
    public static boolean deleteFilesInDirWithFilter(final File dir, final FileFilter filter) {
        if (dir == null || filter == null) {
            return false;
        }
        // dir doesn't exist then return true
        if (!dir.exists()) {
            return true;
        }
        // dir isn't a directory then return false
        if (!dir.isDirectory()) {
            return false;
        }
        File[] files = dir.listFiles();
        if (files != null && files.length != 0) {
            for (File file : files) {
                if (filter.accept(file)) {
                    if (file.isFile()) {
                        if (!file.delete()) {
                            return false;
                        }
                    } else if (file.isDirectory()) {
                        if (!deleteDir(file)) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }


    /**
     * 返回目录中的文件。
     * <p>不遍历子目录</p>
     *
     * @param dirPath 目录的路径。
     * @return 目录中的文件
     */
    public static List<File> listFilesInDir(final String dirPath) {
        return listFilesInDir(dirPath, null);
    }

    /**
     * 返回目录中的文件。
     * <p>不遍历子目录</p>
     *
     * @param dir 目录.
     * @return 目录中的文件
     */
    public static List<File> listFilesInDir(final File dir) {
        return listFilesInDir(dir, null);
    }

    /**
     * 返回目录中的文件。
     * <p>不遍历子目录</p>
     *
     * @param dirPath    目录的路径
     * @param comparator 用于确定列表顺序的比较器。
     * @return 目录中的文件
     */
    public static List<File> listFilesInDir(final String dirPath, Comparator<File> comparator) {
        return listFilesInDir(getFileByPath(dirPath), false, comparator);
    }

    /**
     * 返回目录中的文件。
     * <p>不遍历子目录</p>
     *
     * @param dir        目录
     * @param comparator 用于确定列表顺序的比较器。
     * @return 目录中的文件
     */
    public static List<File> listFilesInDir(final File dir, Comparator<File> comparator) {
        return listFilesInDir(dir, false, comparator);
    }

    /**
     * 返回目录中的文件。
     *
     * @param dirPath     目录的路径
     * @param isRecursive True遍历子目录，否则为false。
     * @return 目录中的文件
     */
    public static List<File> listFilesInDir(final String dirPath, final boolean isRecursive) {
        return listFilesInDir(getFileByPath(dirPath), isRecursive);
    }

    /**
     * 返回目录中的文件。
     *
     * @param dir         目录
     * @param isRecursive True遍历子目录，否则为false。
     * @return 目录中的文件
     */
    public static List<File> listFilesInDir(final File dir, final boolean isRecursive) {
        return listFilesInDir(dir, isRecursive, null);
    }

    /**
     * 返回目录中的文件。
     *
     * @param dirPath     目录的路径
     * @param isRecursive True遍历子目录，否则为false。
     * @param comparator  用于确定列表顺序的比较器。
     * @return 目录中的文件
     */
    public static List<File> listFilesInDir(final String dirPath,
                                            final boolean isRecursive,
                                            final Comparator<File> comparator) {
        return listFilesInDir(getFileByPath(dirPath), isRecursive, comparator);
    }

    /**
     * 返回目录中的文件。
     *
     * @param dir         目录
     * @param isRecursive True遍历子目录，否则为false。
     * @param comparator  用于确定列表顺序的比较器。
     * @return 目录中的文件
     */
    public static List<File> listFilesInDir(final File dir,
                                            final boolean isRecursive,
                                            final Comparator<File> comparator) {
        return listFilesInDirWithFilter(dir, new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return true;
            }
        }, isRecursive, comparator);
    }

    /**
     * 返回目录中满足筛选器的文件<p> 不遍历子目录
     *
     * @param dirPath 目录的路径
     * @param filter  过滤器
     * @return 目录中满足筛选器的文件
     */
    public static List<File> listFilesInDirWithFilter(final String dirPath,
                                                      final FileFilter filter) {
        return listFilesInDirWithFilter(getFileByPath(dirPath), filter);
    }

    /**
     * 返回目录中满足筛选器的文件<p> 不遍历子目录
     *
     * @param dir    目录
     * @param filter 过滤器
     * @return 目录中满足筛选器的文件
     */
    public static List<File> listFilesInDirWithFilter(final File dir,
                                                      final FileFilter filter) {
        return listFilesInDirWithFilter(dir, filter, false, null);
    }

    /**
     * 返回目录中满足筛选器的文件<p> 不遍历子目录
     *
     * @param dirPath    目录的路径
     * @param filter     过滤器
     * @param comparator 用于确定列表顺序的比较器
     * @return 目录中满足筛选器的文件
     */
    public static List<File> listFilesInDirWithFilter(final String dirPath,
                                                      final FileFilter filter,
                                                      final Comparator<File> comparator) {
        return listFilesInDirWithFilter(getFileByPath(dirPath), filter, comparator);
    }

    /**
     * 返回目录中满足筛选器的文件<p> 不遍历子目录
     *
     * @param dir        目录
     * @param filter     过滤器
     * @param comparator 用于确定列表顺序的比较器
     * @return 目录中满足筛选器的文件
     */
    public static List<File> listFilesInDirWithFilter(final File dir,
                                                      final FileFilter filter,
                                                      final Comparator<File> comparator) {
        return listFilesInDirWithFilter(dir, filter, false, comparator);
    }

    /**
     * 返回目录中满足筛选器的文件。
     *
     * @param dirPath     目录的路径
     * @param filter      过滤器
     * @param isRecursive True遍历子目录，否则为false。
     * @return 目录中满足筛选器的文件
     */
    public static List<File> listFilesInDirWithFilter(final String dirPath,
                                                      final FileFilter filter,
                                                      final boolean isRecursive) {
        return listFilesInDirWithFilter(getFileByPath(dirPath), filter, isRecursive);
    }

    /**
     * 返回目录中满足筛选器的文件。
     *
     * @param dir         目录
     * @param filter      过滤器
     * @param isRecursive True遍历子目录，否则为false。
     * @return 目录中满足筛选器的文件
     */
    public static List<File> listFilesInDirWithFilter(final File dir,
                                                      final FileFilter filter,
                                                      final boolean isRecursive) {
        return listFilesInDirWithFilter(dir, filter, isRecursive, null);
    }


    /**
     * 返回目录中满足筛选器的文件。
     *
     * @param dirPath     目录的路径
     * @param filter      过滤器
     * @param isRecursive True遍历子目录，否则为false。
     * @param comparator  用于确定列表顺序的比较器
     * @return 目录中满足筛选器的文件
     */
    public static List<File> listFilesInDirWithFilter(final String dirPath,
                                                      final FileFilter filter,
                                                      final boolean isRecursive,
                                                      final Comparator<File> comparator) {
        return listFilesInDirWithFilter(getFileByPath(dirPath), filter, isRecursive, comparator);
    }

    /**
     * 返回目录中满足筛选器的文件。
     *
     * @param dir         目录
     * @param filter      过滤器
     * @param isRecursive True遍历子目录，否则为false。
     * @param comparator  用于确定列表顺序的比较器
     * @return 目录中满足筛选器的文件
     */
    public static List<File> listFilesInDirWithFilter(final File dir,
                                                      final FileFilter filter,
                                                      final boolean isRecursive,
                                                      final Comparator<File> comparator) {
        List<File> files = listFilesInDirWithFilterInner(dir, filter, isRecursive);
        if (comparator != null) {
            Collections.sort(files, comparator);
        }
        return files;
    }

    private static List<File> listFilesInDirWithFilterInner(final File dir,
                                                            final FileFilter filter,
                                                            final boolean isRecursive) {
        List<File> list = new ArrayList<>();
        if (!isDir(dir)) {
            return list;
        }
        File[] files = dir.listFiles();
        if (files != null && files.length > 0) {
            for (File file : files) {
                if (filter.accept(file)) {
                    list.add(file);
                }
                if (isRecursive && file.isDirectory()) {
                    list.addAll(listFilesInDirWithFilterInner(file, filter, true));
                }
            }
        }
        return list;
    }

    /**
     * 返回上次修改文件的时间。
     *
     * @param filePath 文件路径。
     * @return 返回上次修改文件的时间。
     */

    public static long getFileLastModified(final String filePath) {
        return getFileLastModified(getFileByPath(filePath));
    }

    /**
     * 返回上次修改文件的时间。
     *
     * @param file file.
     * @return 返回上次修改文件的时间。
     */
    public static long getFileLastModified(final File file) {
        if (file == null) {
            return -1;
        }
        return file.lastModified();
    }

    /**
     * 简单返回文件的字符集。
     *
     * @param filePath 文件路径。
     * @return 简单返回文件的字符集。
     */
    public static String getFileCharsetSimple(final String filePath) {
        return getFileCharsetSimple(getFileByPath(filePath));
    }

    /**
     * 简单返回文件的字符集。
     *
     * @param file file.
     * @return 简单返回文件的字符集。
     */
    public static String getFileCharsetSimple(final File file) {
        if (file == null) {
            return "";
        }
        if (isUtf8(file)) {
            return "UTF-8";
        }
        int p = 0;
        InputStream is = null;
        try {
            is = new BufferedInputStream(new FileInputStream(file));
            p = (is.read() << 8) + is.read();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        switch (p) {
            case 0xfffe:
                return "Unicode";
            case 0xfeff:
                return "UTF-16BE";
            default:
                return "GBK";
        }
    }

    /**
     * 返回文件的字符集是否为utf8。
     *
     * @param filePath 文件路径。
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isUtf8(final String filePath) {
        return isUtf8(getFileByPath(filePath));
    }

    /**
     * 返回文件的字符集是否为utf8。
     *
     * @param file file.
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isUtf8(final File file) {
        if (file == null) {
            return false;
        }
        InputStream is = null;
        try {
            byte[] bytes = new byte[24];
            is = new BufferedInputStream(new FileInputStream(file));
            int read = is.read(bytes);
            if (read != -1) {
                byte[] readArr = new byte[read];
                System.arraycopy(bytes, 0, readArr, 0, read);
                return isUtf8(readArr) == 100;
            } else {
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * UTF-8编码方式
     * ----------------------------------------------
     * 0xxxxxxx
     * 110xxxxx 10xxxxxx
     * 1110xxxx 10xxxxxx 10xxxxxx
     * 11110xxx 10xxxxxx 10xxxxxx 10xxxxxx
     */
    private static int isUtf8(byte[] raw) {
        int i, len;
        int utf8 = 0, ascii = 0;
        if (raw.length > 3) {
            if ((raw[0] == (byte) 0xEF) && (raw[1] == (byte) 0xBB) && (raw[2] == (byte) 0xBF)) {
                return 100;
            }
        }
        len = raw.length;
        int child = 0;
        for (i = 0; i < len; ) {
            // UTF-8 byte shouldn't be FF and FE
            if ((raw[i] & (byte) 0xFF) == (byte) 0xFF || (raw[i] & (byte) 0xFE) == (byte) 0xFE) {
                return 0;
            }
            if (child == 0) {
                // ASCII format is 0x0*******
                if ((raw[i] & (byte) 0x7F) == raw[i] && raw[i] != 0) {
                    ascii++;
                } else if ((raw[i] & (byte) 0xC0) == (byte) 0xC0) {
                    // 0x11****** maybe is UTF-8
                    for (int bit = 0; bit < 8; bit++) {
                        if ((((byte) (0x80 >> bit)) & raw[i]) == ((byte) (0x80 >> bit))) {
                            child = bit;
                        } else {
                            break;
                        }
                    }
                    utf8++;
                }
                i++;
            } else {
                child = (raw.length - i > child) ? child : (raw.length - i);
                boolean currentNotUtf8 = false;
                for (int children = 0; children < child; children++) {
                    // format must is 0x10******
                    if ((raw[i + children] & ((byte) 0x80)) != ((byte) 0x80)) {
                        if ((raw[i + children] & (byte) 0x7F) == raw[i + children] && raw[i] != 0) {
                            // ASCII format is 0x0*******
                            ascii++;
                        }
                        currentNotUtf8 = true;
                    }
                }
                if (currentNotUtf8) {
                    utf8--;
                    i++;
                } else {
                    utf8 += child;
                    i += child;
                }
                child = 0;
            }
        }
        // UTF-8 contains ASCII
        if (ascii == len) {
            return 100;
        }
        return (int) (100 * ((float) (utf8 + ascii) / (float) len));
    }

    /**
     * 返回文件的行数。
     *
     * @param filePath 文件的路径
     * @return 返回文件的行数。
     */
    public static int getFileLines(final String filePath) {
        return getFileLines(getFileByPath(filePath));
    }

    /**
     * 返回文件的行数。
     *
     * @param file file.
     * @return 返回文件的行数。
     */
    public static int getFileLines(final File file) {
        int count = 1;
        InputStream is = null;
        try {
            is = new BufferedInputStream(new FileInputStream(file));
            byte[] buffer = new byte[1024];
            int readChars;
            if (LINE_SEP.endsWith("\n")) {
                while ((readChars = is.read(buffer, 0, 1024)) != -1) {
                    for (int i = 0; i < readChars; ++i) {
                        if (buffer[i] == '\n') {
                            ++count;
                        }
                    }
                }
            } else {
                while ((readChars = is.read(buffer, 0, 1024)) != -1) {
                    for (int i = 0; i < readChars; ++i) {
                        if (buffer[i] == '\r') {
                            ++count;
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return count;
    }

    /**
     * 返回大小。
     *
     * @param filePath 文件的路径。
     * @return 返回大小。
     */
    public static String getSize(final String filePath) {
        return getSize(getFileByPath(filePath));
    }

    /**
     * 返回大小。
     *
     * @param file 目录
     * @return 返回大小。
     */
    public static String getSize(final File file) {
        if (file == null) {
            return "";
        }
        if (file.isDirectory()) {
            return getDirSize(file);
        }
        return getFileSize(file);
    }

    /**
     * 返回目录的大小。
     *
     * @param dir 目录
     * @return 返回目录的大小。
     */
    private static String getDirSize(final File dir) {
        long len = getDirLength(dir);
        return len == -1 ? "" : DawnBridge.byte2FitMemorySize(len);
    }

    /**
     * 返回文件大小。
     *
     * @param file file.
     * @return 返回文件大小。
     */
    private static String getFileSize(final File file) {
        long len = getFileLength(file);
        return len == -1 ? "" : DawnBridge.byte2FitMemorySize(len);
    }

    /**
     * 返回长度。
     *
     * @param filePath 文件的路径。
     * @return 返回长度。
     */
    public static long getLength(final String filePath) {
        return getLength(getFileByPath(filePath));
    }

    /**
     * 返回长度。
     *
     * @param file file.
     * @return 返回长度。
     */
    public static long getLength(final File file) {
        if (file == null) {
            return 0;
        }
        if (file.isDirectory()) {
            return getDirLength(file);
        }
        return getFileLength(file);
    }

    /**
     * 返回目录的长度。
     *
     * @param dir 目录
     * @return 返回目录的长度。
     */
    private static long getDirLength(final File dir) {
        if (!isDir(dir)) {
            return 0;
        }
        long len = 0;
        File[] files = dir.listFiles();
        if (files != null && files.length > 0) {
            for (File file : files) {
                if (file.isDirectory()) {
                    len += getDirLength(file);
                } else {
                    len += file.length();
                }
            }
        }
        return len;
    }

    /**
     * 返回文件的长度。
     *
     * @param filePath 文件的路径。
     * @return 返回文件的长度。
     */
    public static long getFileLength(final String filePath) {
        boolean isURL = filePath.matches("[a-zA-z]+://[^\\s]*");
        if (isURL) {
            try {
                HttpsURLConnection conn = (HttpsURLConnection) new URL(filePath).openConnection();
                conn.setRequestProperty("Accept-Encoding", "identity");
                conn.connect();
                if (conn.getResponseCode() == 200) {
                    return conn.getContentLength();
                }
                return -1;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return getFileLength(getFileByPath(filePath));
    }

    /**
     * 返回文件的长度。
     *
     * @param file file.
     * @return 返回文件的长度。
     */
    private static long getFileLength(final File file) {
        if (!isFile(file)) {
            return -1;
        }
        return file.length();
    }

    /**
     * 返回文件的 MD5
     *
     * @param filePath 文件路径
     * @return 返回文件的 MD5
     */
    public static String getFileMD5ToString(final String filePath) {
        File file = DawnBridge.isSpace(filePath) ? null : new File(filePath);
        return getFileMD5ToString(file);
    }

    /**
     * 返回文件的 MD5
     *
     * @param file 文件
     * @return  返回文件的 MD5
     */
    public static String getFileMD5ToString(final File file) {
        return DawnBridge.bytes2HexString(getFileMD5(file));
    }

    /**
     *返回文件的 MD5
     *
     * @param filePath 文件路径
     * @return 返回文件的 MD5
     */
    public static byte[] getFileMD5(final String filePath) {
        return getFileMD5(getFileByPath(filePath));
    }

    /**
     * 返回文件的 MD5
     *
     * @param file 文件
     * @return 返回文件的 MD5
     */
    public static byte[] getFileMD5(final File file) {
        if (file == null) {
            return null;
        }
        DigestInputStream dis = null;
        try {
            FileInputStream fis = new FileInputStream(file);
            MessageDigest md = MessageDigest.getInstance("MD5");
            dis = new DigestInputStream(fis, md);
            byte[] buffer = new byte[1024 * 256];
            while (true) {
                if (!(dis.read(buffer) > 0)) {
                    break;
                }
            }
            md = dis.getMessageDigest();
            return md.digest();
        } catch (NoSuchAlgorithmException | IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (dis != null) {
                    dis.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 返回文件的目录路径。
     *
     * @param file  file.
     * @return 返回文件的目录路径。
     */
    public static String getDirName(final File file) {
        if (file == null) {
            return "";
        }
        return getDirName(file.getAbsolutePath());
    }

    /**
     * 返回文件的目录路径。
     *
     * @param filePath 文件路径
     * @return 返回文件的目录路径。
     */
    public static String getDirName(final String filePath) {
        if (DawnBridge.isSpace(filePath)) {
            return "";
        }
        int lastSep = filePath.lastIndexOf(File.separator);
        return lastSep == -1 ? "" : filePath.substring(0, lastSep + 1);
    }

    /**
     * 返回文件名。
     *
     * @param file  file.
     * @return 返回文件名。
     */
    public static String getFileName(final File file) {
        if (file == null) {
            return "";
        }
        return getFileName(file.getAbsolutePath());
    }

    /**
     * 返回文件名。
     *
     * @param filePath 文件路径
     * @return 返回文件名。
     */
    public static String getFileName(final String filePath) {
        if (DawnBridge.isSpace(filePath)) {
            return "";
        }
        int lastSep = filePath.lastIndexOf(File.separator);
        return lastSep == -1 ? filePath : filePath.substring(lastSep + 1);
    }

    /**
     * 返回不带扩展名的文件名。
     *
     * @param file  file.
     * @return 返回不带扩展名的文件名。
     */
    public static String getFileNameNoExtension(final File file) {
        if (file == null) {
            return "";
        }
        return getFileNameNoExtension(file.getPath());
    }

    /**
     * 返回不带扩展名的文件名。
     *
     * @param filePath 文件路径
     * @return 返回不带扩展名的文件名。
     */
    public static String getFileNameNoExtension(final String filePath) {
        if (DawnBridge.isSpace(filePath)) {
            return "";
        }
        int lastPoi = filePath.lastIndexOf('.');
        int lastSep = filePath.lastIndexOf(File.separator);
        if (lastSep == -1) {
            return (lastPoi == -1 ? filePath : filePath.substring(0, lastPoi));
        }
        if (lastPoi == -1 || lastSep > lastPoi) {
            return filePath.substring(lastSep + 1);
        }
        return filePath.substring(lastSep + 1, lastPoi);
    }

    /**
     * 返回文件的扩展名
     *
     * @param file  file.
     * @return 返回文件的扩展名
     */
    public static String getFileExtension(final File file) {
        if (file == null) {
            return "";
        }
        return getFileExtension(file.getPath());
    }

    /**
     * 返回文件的扩展名
     *
     * @param filePath 文件路径
     * @return 返回文件的扩展名
     */
    public static String getFileExtension(final String filePath) {
        if (DawnBridge.isSpace(filePath)) {
            return "";
        }
        int lastPoi = filePath.lastIndexOf('.');
        int lastSep = filePath.lastIndexOf(File.separator);
        if (lastPoi == -1 || lastSep >= lastPoi) {
            return "";
        }
        return filePath.substring(lastPoi + 1);
    }

    /**
     * 通知系统扫描文件。
     *
     * @param filePath 文件路径
     */
    public static void notifySystemToScan(final String filePath) {
        notifySystemToScan(getFileByPath(filePath));
    }

    /**
     * 通知系统扫描文件。
     *
     * @param file  file.
     */
    public static void notifySystemToScan(final File file) {
        if (file == null || !file.exists()) {
            return;
        }
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        intent.setData(Uri.parse("file://" + file.getAbsolutePath()));
        DawnBridge.getApp().sendBroadcast(intent);

     }

    /**
     * 返回文件系统的总大小。
     *
     * @param anyPathInFs 文件系统中的任何路径。
     * @return 返回文件系统的总大小
     */
    public static long getFsTotalSize(String anyPathInFs) {
        if (TextUtils.isEmpty(anyPathInFs)) {
            return 0;
        }
        StatFs statFs = new StatFs(anyPathInFs);
        long blockSize;
        long totalSize;
        blockSize = statFs.getBlockSizeLong();
        totalSize = statFs.getBlockCountLong();
        return blockSize * totalSize;
    }

    /**
     * 返回文件系统的可用大小。
     *
     * @param anyPathInFs 文件系统中的任何路径
     * @return 返回文件系统的可用大小。
     */
    public static long getFsAvailableSize(final String anyPathInFs) {
        if (TextUtils.isEmpty(anyPathInFs)) {
            return 0;
        }
        StatFs statFs = new StatFs(anyPathInFs);
        long blockSize;
        long availableSize;
        blockSize = statFs.getBlockSizeLong();
        availableSize = statFs.getAvailableBlocksLong();
        return blockSize * availableSize;
    }
}
