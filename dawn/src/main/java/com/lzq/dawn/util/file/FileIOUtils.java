package com.lzq.dawn.util.file;

import android.util.Log;

import com.lzq.dawn.DawnBridge;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

/**
 * @Name :FileIOUtils
 * @Time :2022/7/21 16:24
 * @Author :  Lzq
 * @Desc : 文件 io
 */
public final class FileIOUtils {

    private static int sBufferSize = 524288;

    private FileIOUtils() {
    }

    /**
     * 设置缓冲区的大小。
     * <p>默认大小等于 8192 字节.</p>
     *
     * @param bufferSize 缓冲区的大小。
     */
    public static void setBufferSize(final int bufferSize) {
        sBufferSize = bufferSize;
    }

    /**
     * 从输入流写入文件。
     *
     * @param filePath 文件的路径
     * @param is       输入流。
     * @return {@code true}: success<br>{@code false}: fail
     */
    public static boolean writeFileFromIS(final String filePath, final InputStream is) {
        return writeFileFromIS(DawnBridge.getFileByPath(filePath), is, false, null);
    }

    /**
     * 从输入流写入文件。
     *
     * @param filePath 文件的路径
     * @param is       输入流。
     * @param append   True to append, false otherwise.
     * @return {@code true}: success<br>{@code false}: fail
     */
    public static boolean writeFileFromIS(final String filePath,
                                          final InputStream is,
                                          final boolean append) {
        return writeFileFromIS(DawnBridge.getFileByPath(filePath), is, append, null);
    }

    /**
     * 从输入流写入文件。
     *
     * @param file 文件
     * @param is   输入流。
     * @return {@code true}: success<br>{@code false}: fail
     */
    public static boolean writeFileFromIS(final File file, final InputStream is) {
        return writeFileFromIS(file, is, false, null);
    }

    /**
     * 从输入流写入文件。
     *
     * @param file   文件
     * @param is     输入流。
     * @param append True to append, false otherwise.
     * @return {@code true}: success<br>{@code false}: fail
     */
    public static boolean writeFileFromIS(final File file,
                                          final InputStream is,
                                          final boolean append) {
        return writeFileFromIS(file, is, append, null);
    }

    /**
     * 从输入流写入文件。
     *
     * @param filePath 文件的路径
     * @param is       输入流。
     * @param listener 进度更新监听器。
     * @return {@code true}: success<br>{@code false}: fail
     */
    public static boolean writeFileFromIS(final String filePath,
                                          final InputStream is,
                                          final OnProgressUpdateListener listener) {
        return writeFileFromIS(DawnBridge.getFileByPath(filePath), is, false, listener);
    }


    /**
     * 从输入流写入文件。
     *
     * @param filePath 文件的路径
     * @param is       输入流。
     * @param append   True to append, false otherwise.
     * @param listener 进度更新监听器。
     * @return {@code true}: success<br>{@code false}: fail
     */
    public static boolean writeFileFromIS(final String filePath,
                                          final InputStream is,
                                          final boolean append,
                                          final OnProgressUpdateListener listener) {
        return writeFileFromIS(DawnBridge.getFileByPath(filePath), is, append, listener);
    }

    /**
     * 从输入流写入文件。
     *
     * @param file     文件
     * @param is       输入流。
     * @param listener 进度更新监听器。
     * @return {@code true}: success<br>{@code false}: fail
     */
    public static boolean writeFileFromIS(final File file,
                                          final InputStream is,
                                          final OnProgressUpdateListener listener) {
        return writeFileFromIS(file, is, false, listener);
    }

    /**
     * 按流从字节写入文件。
     *
     * @param filePath 文件路径
     * @param bytes    字节
     * @return {@code true}: success<br>{@code false}: fail
     */
    public static boolean writeFileFromBytesByStream(final String filePath, final byte[] bytes) {
        return writeFileFromBytesByStream(DawnBridge.getFileByPath(filePath), bytes, false, null);
    }

    /**
     * 按流从字节写入文件。
     *
     * @param filePath 文件路径
     * @param bytes    字节
     * @param append   True to append, false otherwise.
     * @return {@code true}: success<br>{@code false}: fail
     */
    public static boolean writeFileFromBytesByStream(final String filePath,
                                                     final byte[] bytes,
                                                     final boolean append) {
        return writeFileFromBytesByStream(DawnBridge.getFileByPath(filePath), bytes, append, null);
    }

    /**
     * 按流从字节写入文件。
     *
     * @param file  文件
     * @param bytes 字节
     * @return {@code true}: success<br>{@code false}: fail
     */
    public static boolean writeFileFromBytesByStream(final File file, final byte[] bytes) {
        return writeFileFromBytesByStream(file, bytes, false, null);
    }

    /**
     * 按流从字节写入文件。
     *
     * @param file   文件
     * @param bytes  字节
     * @param append True to append, false otherwise.
     * @return {@code true}: success<br>{@code false}: fail
     */
    public static boolean writeFileFromBytesByStream(final File file,
                                                     final byte[] bytes,
                                                     final boolean append) {
        return writeFileFromBytesByStream(file, bytes, append, null);
    }

    /**
     * 按流从字节写入文件
     *
     * @param filePath 文件路径
     * @param bytes    字节
     * @param listener 进度更新监听器。
     * @return {@code true}: success<br>{@code false}: fail
     */
    public static boolean writeFileFromBytesByStream(final String filePath,
                                                     final byte[] bytes,
                                                     final OnProgressUpdateListener listener) {
        return writeFileFromBytesByStream(DawnBridge.getFileByPath(filePath), bytes, false, listener);
    }

    /**
     * 按流从字节写入文件
     *
     * @param filePath 文件路径
     * @param bytes    字节
     * @param append   True to append, false otherwise.
     * @param listener 进度更新监听器。
     * @return {@code true}: success<br>{@code false}: fail
     */
    public static boolean writeFileFromBytesByStream(final String filePath,
                                                     final byte[] bytes,
                                                     final boolean append,
                                                     final OnProgressUpdateListener listener) {
        return writeFileFromBytesByStream(DawnBridge.getFileByPath(filePath), bytes, append, listener);
    }

    /**
     * 按流从字节写入文件
     *
     * @param file     文件
     * @param bytes    字节
     * @param listener 进度更新监听器。
     * @return {@code true}: success<br>{@code false}: fail
     */
    public static boolean writeFileFromBytesByStream(final File file,
                                                     final byte[] bytes,
                                                     final OnProgressUpdateListener listener) {
        return writeFileFromBytesByStream(file, bytes, false, listener);
    }

    /**
     * 按流从字节写入文件
     *
     * @param file     文件
     * @param bytes    字节
     * @param append   True to append, false otherwise.
     * @param listener 进度更新监听器。
     * @return {@code true}: success<br>{@code false}: fail
     */
    public static boolean writeFileFromBytesByStream(final File file,
                                                     final byte[] bytes,
                                                     final boolean append,
                                                     final OnProgressUpdateListener listener) {
        if (bytes == null) {
            return false;
        }
        return writeFileFromIS(file, new ByteArrayInputStream(bytes), append, listener);
    }

    /**
     * 从输入流写入文件。
     *
     * @param file     文件
     * @param is       输入流。
     * @param append   True to append, false otherwise.
     * @param listener 进度更新监听器。
     * @return {@code true}: success<br>{@code false}: fail
     */
    public static boolean writeFileFromIS(final File file,
                                          final InputStream is,
                                          final boolean append,
                                          final OnProgressUpdateListener listener) {
        if (is == null || !DawnBridge.createOrExistsFile(file)) {
            Log.e("FileIOUtils", "create file <" + file + "> failed.");
            return false;
        }
        OutputStream os = null;
        try {
            os = new BufferedOutputStream(new FileOutputStream(file, append), sBufferSize);
            if (listener == null) {
                byte[] data = new byte[sBufferSize];
                for (int len; (len = is.read(data)) != -1; ) {
                    os.write(data, 0, len);
                }
            } else {
                double totalSize = is.available();
                int curSize = 0;
                listener.onProgressUpdate(0);
                byte[] data = new byte[sBufferSize];
                for (int len; (len = is.read(data)) != -1; ) {
                    os.write(data, 0, len);
                    curSize += len;
                    listener.onProgressUpdate(curSize / totalSize);
                }
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (os != null) {
                    os.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 按通道从字节写入文件。
     *
     * @param filePath 文件的路径。
     * @param bytes    bytes.
     * @param isForce  True 强制写入文件，否则为 false。
     * @return {@code true}: success<br>{@code false}: fail
     */
    public static boolean writeFileFromBytesByChannel(final String filePath,
                                                      final byte[] bytes,
                                                      final boolean isForce) {
        return writeFileFromBytesByChannel(DawnBridge.getFileByPath(filePath), bytes, false, isForce);
    }

    /**
     * 按通道从字节写入文件。
     *
     * @param filePath 文件的路径。
     * @param bytes    bytes.
     * @param append   True to append, false otherwise.
     * @param isForce  True 强制写入文件，否则为 false。
     * @return {@code true}: success<br>{@code false}: fail
     */
    public static boolean writeFileFromBytesByChannel(final String filePath,
                                                      final byte[] bytes,
                                                      final boolean append,
                                                      final boolean isForce) {
        return writeFileFromBytesByChannel(DawnBridge.getFileByPath(filePath), bytes, append, isForce);
    }

    /**
     * 按通道从字节写入文件。
     *
     * @param file    文件。
     * @param bytes   bytes.
     * @param isForce True 强制写入文件，否则为 false。
     * @return {@code true}: success<br>{@code false}: fail
     */
    public static boolean writeFileFromBytesByChannel(final File file,
                                                      final byte[] bytes,
                                                      final boolean isForce) {
        return writeFileFromBytesByChannel(file, bytes, false, isForce);
    }

    /**
     * 按通道从字节写入文件。
     *
     * @param file    文件。
     * @param bytes   bytes.
     * @param append  True to append, false otherwise.
     * @param isForce True 强制写入文件，否则为 false。
     * @return {@code true}: success<br>{@code false}: fail
     */
    public static boolean writeFileFromBytesByChannel(final File file,
                                                      final byte[] bytes,
                                                      final boolean append,
                                                      final boolean isForce) {
        if (bytes == null) {
            Log.e("FileIOUtils", "bytes is null.");
            return false;
        }
        if (!DawnBridge.createOrExistsFile(file)) {
            Log.e("FileIOUtils", "create file <" + file + "> failed.");
            return false;
        }
        FileChannel fc = null;
        try {
            fc = new FileOutputStream(file, append).getChannel();
            if (fc == null) {
                Log.e("FileIOUtils", "fc is null.");
                return false;
            }
            fc.position(fc.size());
            fc.write(ByteBuffer.wrap(bytes));
            if (isForce) {
                fc.force(true);
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (fc != null) {
                    fc.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 通过映射从字节写入文件。
     *
     * @param filePath 文件的路径。
     * @param bytes    bytes.
     * @param isForce  True 强制写入文件，否则为 false。
     * @return {@code true}: success<br>{@code false}: fail
     */
    public static boolean writeFileFromBytesByMap(final String filePath,
                                                  final byte[] bytes,
                                                  final boolean isForce) {
        return writeFileFromBytesByMap(filePath, bytes, false, isForce);
    }

    /**
     * 通过映射从字节写入文件。
     *
     * @param filePath 文件的路径。
     * @param bytes    bytes.
     * @param append   True to append, false otherwise.
     * @param isForce  True 强制写入文件，否则为 false。
     * @return {@code true}: success<br>{@code false}: fail
     */
    public static boolean writeFileFromBytesByMap(final String filePath,
                                                  final byte[] bytes,
                                                  final boolean append,
                                                  final boolean isForce) {
        return writeFileFromBytesByMap(DawnBridge.getFileByPath(filePath), bytes, append, isForce);
    }

    /**
     * 通过映射从字节写入文件。
     *
     * @param file    文件
     * @param bytes   bytes.
     * @param isForce True 强制写入文件，否则为 false。
     * @return {@code true}: success<br>{@code false}: fail
     */
    public static boolean writeFileFromBytesByMap(final File file,
                                                  final byte[] bytes,
                                                  final boolean isForce) {
        return writeFileFromBytesByMap(file, bytes, false, isForce);
    }

    /**
     * 通过映射从字节写入文件。
     *
     * @param file    文件
     * @param bytes   bytes.
     * @param append  True to append, false otherwise.
     * @param isForce True 强制写入文件，否则为 false。
     * @return {@code true}: success<br>{@code false}: fail
     */
    public static boolean writeFileFromBytesByMap(final File file,
                                                  final byte[] bytes,
                                                  final boolean append,
                                                  final boolean isForce) {
        if (bytes == null || !DawnBridge.createOrExistsFile(file)) {
            Log.e("FileIOUtils", "create file <" + file + "> failed.");
            return false;
        }
        FileChannel fc = null;
        try {
            fc = new FileOutputStream(file, append).getChannel();
            if (fc == null) {
                Log.e("FileIOUtils", "fc is null.");
                return false;
            }
            MappedByteBuffer mbb = fc.map(FileChannel.MapMode.READ_WRITE, fc.size(), bytes.length);
            mbb.put(bytes);
            if (isForce) {
                mbb.force();
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (fc != null) {
                    fc.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 从字符串写入文件。
     *
     * @param filePath 文件的路径。
     * @param content  内容字符串。
     * @return {@code true}: success<br>{@code false}: fail
     */
    public static boolean writeFileFromString(final String filePath, final String content) {
        return writeFileFromString(DawnBridge.getFileByPath(filePath), content, false);
    }

    /**
     * 从字符串写入文件。
     *
     * @param filePath 文件的路径。
     * @param content  内容字符串。
     * @param append   True to append, false otherwise.
     * @return {@code true}: success<br>{@code false}: fail
     */
    public static boolean writeFileFromString(final String filePath,
                                              final String content,
                                              final boolean append) {
        return writeFileFromString(DawnBridge.getFileByPath(filePath), content, append);
    }

    /**
     * 从字符串写入文件。
     *
     * @param file    文件。
     * @param content 内容字符串。
     * @return {@code true}: success<br>{@code false}: fail
     */
    public static boolean writeFileFromString(final File file, final String content) {
        return writeFileFromString(file, content, false);
    }

    /**
     * 从字符串写入文件。
     *
     * @param file    文件。
     * @param content 内容字符串。
     * @param append  True to append, false otherwise.
     * @return {@code true}: success<br>{@code false}: fail
     */
    public static boolean writeFileFromString(final File file,
                                              final String content,
                                              final boolean append) {
        if (file == null || content == null) {
            return false;
        }
        if (!DawnBridge.createOrExistsFile(file)) {
            Log.e("FileIOUtils", "create file <" + file + "> failed.");
            return false;
        }
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new FileWriter(file, append));
            bw.write(content);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (bw != null) {
                    bw.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 返回文件中的行。
     *
     * @param filePath 文件路径
     * @return 文件中的行。
     */
    public static List<String> readFile2List(final String filePath) {
        return readFile2List(DawnBridge.getFileByPath(filePath), null);
    }

    /**
     * 返回文件中的行。
     *
     * @param filePath 文件路径
     * @param charsetName 字符集的名称。
     * @return 文件中的行。
     */
    public static List<String> readFile2List(final String filePath, final String charsetName) {
        return readFile2List(DawnBridge.getFileByPath(filePath), charsetName);
    }

    /**
     * 返回文件中的行。
     *
     * @param file 文件
     * @return 文件中的行。
     */
    public static List<String> readFile2List(final File file) {
        return readFile2List(file, 0, 0x7FFFFFFF, null);
    }

    /**
     * 返回文件中的行。
     *
     * @param file 文件
     * @param charsetName 字符集的名称
     * @return 文件中的行
     */
    public static List<String> readFile2List(final File file, final String charsetName) {
        return readFile2List(file, 0, 0x7FFFFFFF, charsetName);
    }

    /**
     *  返回文件中的行。
     *
     * @param filePath 文件路径
     * @param st      该行的开始索引。
     * @param end     该行的结束索引。
     * @return  返回文件中的行。
     */
    public static List<String> readFile2List(final String filePath, final int st, final int end) {
        return readFile2List(DawnBridge.getFileByPath(filePath), st, end, null);
    }

    /**
     *  返回文件中的行。
     *
     * @param filePath 文件路径
     * @param st      该行的开始索引。
     * @param end     该行的结束索引。
     * @param charsetName 字符集的名称
     * @return  返回文件中的行。
     */
    public static List<String> readFile2List(final String filePath,
                                             final int st,
                                             final int end,
                                             final String charsetName) {
        return readFile2List(DawnBridge.getFileByPath(filePath), st, end, charsetName);
    }

    /**
     * 返回文件中的行。
     *
     * @param file 文件
     * @param st      该行的开始索引。
     * @param end     该行的结束索引。
     * @return  返回文件中的行。
     */
    public static List<String> readFile2List(final File file, final int st, final int end) {
        return readFile2List(file, st, end, null);
    }

    /**
     *  返回文件中的行。
     *
     * @param file 文件
     * @param st      该行的开始索引。
     * @param end     该行的结束索引。
     * @param charsetName 字符集的名称
     * @return  返回文件中的行。
     */
    public static List<String> readFile2List(final File file,
                                             final int st,
                                             final int end,
                                             final String charsetName) {
        if (!DawnBridge.isFileExists(file)) {
            return null;
        }
        if (st > end) {
            return null;
        }
        BufferedReader reader = null;
        try {
            String line;
            int curLine = 1;
            List<String> list = new ArrayList<>();
            if (DawnBridge.isSpace(charsetName)) {
                reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            } else {
                reader = new BufferedReader(
                        new InputStreamReader(new FileInputStream(file), charsetName)
                );
            }
            while ((line = reader.readLine()) != null) {
                if (curLine > end) {
                    break;
                }
                if (st <= curLine && curLine <= end) {
                    list.add(line);
                }
                ++curLine;
            }
            return list;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 在文件中返回字符串。
     *
     * @param filePath 文件路径
     * @return 字符串。
     */
    public static String readFile2String(final String filePath) {
        return readFile2String(DawnBridge.getFileByPath(filePath), null);
    }

    /**
     * 在文件中返回字符串。
     *
     * @param filePath    文件路径
     * @param charsetName 字符集的名称
     * @return 返回字符串。
     */
    public static String readFile2String(final String filePath, final String charsetName) {
        return readFile2String(DawnBridge.getFileByPath(filePath), charsetName);
    }

    /**
     * 在文件中返回字符串。
     *
     * @param file  file.
     * @return 返回字符串。
     */
    public static String readFile2String(final File file) {
        return readFile2String(file, null);
    }

    /**
     *在文件中返回字符串。
     *
     * @param file         file.
     * @param charsetName 字符集的名称
     * @return 返回字符串。
     */
    public static String readFile2String(final File file, final String charsetName) {
        byte[] bytes = readFile2BytesByStream(file);
        if (bytes == null) {
            return null;
        }
        if (DawnBridge.isSpace(charsetName)) {
            return new String(bytes);
        } else {
            try {
                return new String(bytes, charsetName);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                return "";
            }
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // readFile2BytesByStream without progress
    ///////////////////////////////////////////////////////////////////////////

    /**
     * 按流返回文件中的字节。
     *
     * @param filePath 文件路径
     * @return 文件中的字节。
     */
    public static byte[] readFile2BytesByStream(final String filePath) {
        return readFile2BytesByStream(DawnBridge.getFileByPath(filePath), null);
    }

    /**
     * 按流返回文件中的字节。
     *
     * @param file  file.
     * @return 文件中的字节。
     */
    public static byte[] readFile2BytesByStream(final File file) {
        return readFile2BytesByStream(file, null);
    }

    ///////////////////////////////////////////////////////////////////////////
    // readFile2BytesByStream with progress
    ///////////////////////////////////////////////////////////////////////////

    /**
     * 按流返回文件中的字节。
     *
     * @param filePath 文件路径
     * @param listener 进度更新侦听器。
     * @return 文件中的字节。
     */
    public static byte[] readFile2BytesByStream(final String filePath,
                                                final OnProgressUpdateListener listener) {
        return readFile2BytesByStream(DawnBridge.getFileByPath(filePath), listener);
    }

    /**
     * 按流返回文件中的字节。
     *
     * @param file      file.
     * @param listener 进度更新侦听器。
     * @return 文件路径
     */
    public static byte[] readFile2BytesByStream(final File file,
                                                final OnProgressUpdateListener listener) {
        if (!DawnBridge.isFileExists(file)) {
            return null;
        }
        try {
            ByteArrayOutputStream os = null;
            InputStream is = new BufferedInputStream(new FileInputStream(file), sBufferSize);
            try {
                os = new ByteArrayOutputStream();
                byte[] b = new byte[sBufferSize];
                int len;
                if (listener == null) {
                    while ((len = is.read(b, 0, sBufferSize)) != -1) {
                        os.write(b, 0, len);
                    }
                } else {
                    double totalSize = is.available();
                    int curSize = 0;
                    listener.onProgressUpdate(0);
                    while ((len = is.read(b, 0, sBufferSize)) != -1) {
                        os.write(b, 0, len);
                        curSize += len;
                        listener.onProgressUpdate(curSize / totalSize);
                    }
                }
                return os.toByteArray();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            } finally {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    if (os != null) {
                        os.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 按通道返回文件中的字节
     *
     * @param filePath 文件路径
     * @return 文件中的字节
     */
    public static byte[] readFile2BytesByChannel(final String filePath) {
        return readFile2BytesByChannel(DawnBridge.getFileByPath(filePath));
    }

    /**
     *按通道返回文件中的字节
     *
     * @param file  file.
     * @return 文件中的字节
     */
    public static byte[] readFile2BytesByChannel(final File file) {
        if (!DawnBridge.isFileExists(file)) {
            return null;
        }
        FileChannel fc = null;
        try {
            fc = new RandomAccessFile(file, "r").getChannel();
            if (fc == null) {
                Log.e("FileIOUtils", "fc is null.");
                return new byte[0];
            }
            ByteBuffer byteBuffer = ByteBuffer.allocate((int) fc.size());
            while (true) {
                if (!((fc.read(byteBuffer)) > 0)) {
                    break;
                }
            }
            return byteBuffer.array();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (fc != null) {
                    fc.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 按映射返回文件中的字节
     *
     * @param filePath 文件路径
     * @return  文件中的字节
     */
    public static byte[] readFile2BytesByMap(final String filePath) {
        return readFile2BytesByMap(DawnBridge.getFileByPath(filePath));
    }

    /**
     * 按映射返回文件中的字节
     *
     * @param file  file.
     * @return 文件中的字节
     */
    public static byte[] readFile2BytesByMap(final File file) {
        if (!DawnBridge.isFileExists(file)) {
            return null;
        }
        FileChannel fc = null;
        try {
            fc = new RandomAccessFile(file, "r").getChannel();
            if (fc == null) {
                Log.e("FileIOUtils", "fc is null.");
                return new byte[0];
            }
            int size = (int) fc.size();
            MappedByteBuffer mbb = fc.map(FileChannel.MapMode.READ_ONLY, 0, size).load();
            byte[] result = new byte[size];
            mbb.get(result, 0, size);
            return result;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (fc != null) {
                    fc.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}


