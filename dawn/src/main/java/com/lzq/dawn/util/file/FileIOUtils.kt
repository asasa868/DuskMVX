package com.lzq.dawn.util.file

import android.util.Log
import com.lzq.dawn.DawnBridge
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.FileWriter
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStream
import java.io.RandomAccessFile
import java.io.UnsupportedEncodingException
import java.nio.ByteBuffer
import java.nio.channels.FileChannel
import java.nio.charset.Charset

/**
 * @Name :FileIOUtils
 * @Time :2022/7/21 16:24
 * @Author :  Lzq
 * @Desc : 文件 io
 */
object FileIOUtils {
    private var sBufferSize = 524288

    /**
     * 设置缓冲区的大小。
     *
     * 默认大小等于 8192 字节.
     *
     * @param bufferSize 缓冲区的大小。
     */
    fun setBufferSize(bufferSize: Int) {
        sBufferSize = bufferSize
    }

    /**
     * 从输入流写入文件。
     *
     * @param filePath 文件的路径
     * @param is       输入流。
     * @return `true`: success<br></br>`false`: fail
     */
    @JvmStatic
    fun writeFileFromIS(filePath: String?, `is`: InputStream?): Boolean {
        return writeFileFromIS(DawnBridge.getFileByPath(filePath), `is`, false, null)
    }

    /**
     * 从输入流写入文件。
     *
     * @param filePath 文件的路径
     * @param is       输入流。
     * @param append   True to append, false otherwise.
     * @return `true`: success<br></br>`false`: fail
     */
    fun writeFileFromIS(
        filePath: String?, `is`: InputStream?, append: Boolean
    ): Boolean {
        return writeFileFromIS(DawnBridge.getFileByPath(filePath), `is`, append, null)
    }

    /**
     * 从输入流写入文件。
     *
     * @param filePath 文件的路径
     * @param is       输入流。
     * @param listener 进度更新监听器。
     * @return `true`: success<br></br>`false`: fail
     */
    fun writeFileFromIS(
        filePath: String?, `is`: InputStream?, listener: OnProgressUpdateListener?
    ): Boolean {
        return writeFileFromIS(DawnBridge.getFileByPath(filePath), `is`, false, listener)
    }

    /**
     * 从输入流写入文件。
     *
     * @param filePath 文件的路径
     * @param is       输入流。
     * @param append   True to append, false otherwise.
     * @param listener 进度更新监听器。
     * @return `true`: success<br></br>`false`: fail
     */
    fun writeFileFromIS(
        filePath: String?, `is`: InputStream?, append: Boolean, listener: OnProgressUpdateListener?
    ): Boolean {
        return writeFileFromIS(DawnBridge.getFileByPath(filePath), `is`, append, listener)
    }

    /**
     * 从输入流写入文件。
     *
     * @param file     文件
     * @param is       输入流。
     * @param listener 进度更新监听器。
     * @return `true`: success<br></br>`false`: fail
     */
    fun writeFileFromIS(
        file: File, `is`: InputStream?, listener: OnProgressUpdateListener?
    ): Boolean {
        return writeFileFromIS(file, `is`, false, listener)
    }

    /**
     * 按流从字节写入文件。
     *
     * @param filePath 文件路径
     * @param bytes    字节
     * @return `true`: success<br></br>`false`: fail
     */
    fun writeFileFromBytesByStream(filePath: String?, bytes: ByteArray?): Boolean {
        return writeFileFromBytesByStream(DawnBridge.getFileByPath(filePath), bytes, false, null)
    }

    /**
     * 按流从字节写入文件。
     *
     * @param filePath 文件路径
     * @param bytes    字节
     * @param append   True to append, false otherwise.
     * @return `true`: success<br></br>`false`: fail
     */
    fun writeFileFromBytesByStream(
        filePath: String?, bytes: ByteArray?, append: Boolean
    ): Boolean {
        return writeFileFromBytesByStream(DawnBridge.getFileByPath(filePath), bytes, append, null)
    }

    /**
     * 按流从字节写入文件
     *
     * @param filePath 文件路径
     * @param bytes    字节
     * @param listener 进度更新监听器。
     * @return `true`: success<br></br>`false`: fail
     */
    fun writeFileFromBytesByStream(
        filePath: String?, bytes: ByteArray?, listener: OnProgressUpdateListener?
    ): Boolean {
        return writeFileFromBytesByStream(DawnBridge.getFileByPath(filePath), bytes, false, listener)
    }

    /**
     * 按流从字节写入文件
     *
     * @param filePath 文件路径
     * @param bytes    字节
     * @param append   True to append, false otherwise.
     * @param listener 进度更新监听器。
     * @return `true`: success<br></br>`false`: fail
     */
    fun writeFileFromBytesByStream(
        filePath: String?, bytes: ByteArray?, append: Boolean, listener: OnProgressUpdateListener?
    ): Boolean {
        return writeFileFromBytesByStream(DawnBridge.getFileByPath(filePath), bytes, append, listener)
    }

    /**
     * 按流从字节写入文件
     *
     * @param file     文件
     * @param bytes    字节
     * @param listener 进度更新监听器。
     * @return `true`: success<br></br>`false`: fail
     */
    fun writeFileFromBytesByStream(
        file: File, bytes: ByteArray?, listener: OnProgressUpdateListener?
    ): Boolean {
        return writeFileFromBytesByStream(file, bytes, false, listener)
    }
    /**
     * 按流从字节写入文件
     *
     * @param file     文件
     * @param bytes    字节
     * @param append   True to append, false otherwise.
     * @param listener 进度更新监听器。
     * @return `true`: success<br></br>`false`: fail
     */
    /**
     * 按流从字节写入文件。
     *
     * @param file  文件
     * @param bytes 字节
     * @return `true`: success<br></br>`false`: fail
     */
    /**
     * 按流从字节写入文件。
     *
     * @param file   文件
     * @param bytes  字节
     * @param append True to append, false otherwise.
     * @return `true`: success<br></br>`false`: fail
     */
    @JvmOverloads
    fun writeFileFromBytesByStream(
        file: File?, bytes: ByteArray?, append: Boolean = false, listener: OnProgressUpdateListener? = null
    ): Boolean {
        return if (bytes == null) {
            false
        } else writeFileFromIS(
            file, ByteArrayInputStream(bytes), append, listener
        )
    }
    /**
     * 从输入流写入文件。
     *
     * @param file     文件
     * @param is       输入流。
     * @param append   True to append, false otherwise.
     * @param listener 进度更新监听器。
     * @return `true`: success<br></br>`false`: fail
     */
    /**
     * 从输入流写入文件。
     *
     * @param file 文件
     * @param is   输入流。
     * @return `true`: success<br></br>`false`: fail
     */
    /**
     * 从输入流写入文件。
     *
     * @param file   文件
     * @param is     输入流。
     * @param append True to append, false otherwise.
     * @return `true`: success<br></br>`false`: fail
     */
    @JvmOverloads
    fun writeFileFromIS(
        file: File?, `is`: InputStream?, append: Boolean = false, listener: OnProgressUpdateListener? = null
    ): Boolean {
        if (`is` == null || !DawnBridge.createOrExistsFile(file)) {
            Log.e("FileIOUtils", "create file <$file> failed.")
            return false
        }
        var os: OutputStream? = null
        return try {
            os = BufferedOutputStream(FileOutputStream(file, append), sBufferSize)
            if (listener == null) {
                val data = ByteArray(sBufferSize)
                var len: Int
                while (`is`.read(data).also { len = it } != -1) {
                    os.write(data, 0, len)
                }
            } else {
                val totalSize = `is`.available().toDouble()
                var curSize = 0
                listener.onProgressUpdate(0.0)
                val data = ByteArray(sBufferSize)
                var len: Int
                while (`is`.read(data).also { len = it } != -1) {
                    os.write(data, 0, len)
                    curSize += len
                    listener.onProgressUpdate(curSize / totalSize)
                }
            }
            true
        } catch (e: IOException) {
            e.printStackTrace()
            false
        } finally {
            try {
                `is`.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            try {
                os?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    /**
     * 按通道从字节写入文件。
     *
     * @param filePath 文件的路径。
     * @param bytes    bytes.
     * @param isForce  True 强制写入文件，否则为 false。
     * @return `true`: success<br></br>`false`: fail
     */
    fun writeFileFromBytesByChannel(
        filePath: String?, bytes: ByteArray?, isForce: Boolean
    ): Boolean {
        return writeFileFromBytesByChannel(DawnBridge.getFileByPath(filePath), bytes, false, isForce)
    }

    /**
     * 按通道从字节写入文件。
     *
     * @param filePath 文件的路径。
     * @param bytes    bytes.
     * @param append   True to append, false otherwise.
     * @param isForce  True 强制写入文件，否则为 false。
     * @return `true`: success<br></br>`false`: fail
     */
    fun writeFileFromBytesByChannel(
        filePath: String?, bytes: ByteArray?, append: Boolean, isForce: Boolean
    ): Boolean {
        return writeFileFromBytesByChannel(DawnBridge.getFileByPath(filePath), bytes, append, isForce)
    }

    /**
     * 按通道从字节写入文件。
     *
     * @param file    文件。
     * @param bytes   bytes.
     * @param isForce True 强制写入文件，否则为 false。
     * @return `true`: success<br></br>`false`: fail
     */
    @JvmStatic
    fun writeFileFromBytesByChannel(
        file: File, bytes: ByteArray?, isForce: Boolean
    ): Boolean {
        return writeFileFromBytesByChannel(file, bytes, false, isForce)
    }

    /**
     * 按通道从字节写入文件。
     *
     * @param file    文件。
     * @param bytes   bytes.
     * @param append  True to append, false otherwise.
     * @param isForce True 强制写入文件，否则为 false。
     * @return `true`: success<br></br>`false`: fail
     */
    fun writeFileFromBytesByChannel(
        file: File?, bytes: ByteArray?, append: Boolean, isForce: Boolean
    ): Boolean {
        if (bytes == null) {
            Log.e("FileIOUtils", "bytes is null.")
            return false
        }
        if (!DawnBridge.createOrExistsFile(file)) {
            Log.e("FileIOUtils", "create file <$file> failed.")
            return false
        }
        var fc: FileChannel? = null
        return try {
            fc = FileOutputStream(file, append).channel
            if (fc == null) {
                Log.e("FileIOUtils", "fc is null.")
                return false
            }
            fc.position(fc.size())
            fc.write(ByteBuffer.wrap(bytes))
            if (isForce) {
                fc.force(true)
            }
            true
        } catch (e: IOException) {
            e.printStackTrace()
            false
        } finally {
            try {
                fc?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    /**
     * 通过映射从字节写入文件。
     *
     * @param filePath 文件的路径。
     * @param bytes    bytes.
     * @param isForce  True 强制写入文件，否则为 false。
     * @return `true`: success<br></br>`false`: fail
     */
    fun writeFileFromBytesByMap(
        filePath: String?, bytes: ByteArray?, isForce: Boolean
    ): Boolean {
        return writeFileFromBytesByMap(filePath, bytes, false, isForce)
    }

    /**
     * 通过映射从字节写入文件。
     *
     * @param filePath 文件的路径。
     * @param bytes    bytes.
     * @param append   True to append, false otherwise.
     * @param isForce  True 强制写入文件，否则为 false。
     * @return `true`: success<br></br>`false`: fail
     */
    fun writeFileFromBytesByMap(
        filePath: String?, bytes: ByteArray?, append: Boolean, isForce: Boolean
    ): Boolean {
        return writeFileFromBytesByMap(DawnBridge.getFileByPath(filePath), bytes, append, isForce)
    }

    /**
     * 通过映射从字节写入文件。
     *
     * @param file    文件
     * @param bytes   bytes.
     * @param isForce True 强制写入文件，否则为 false。
     * @return `true`: success<br></br>`false`: fail
     */
    fun writeFileFromBytesByMap(
        file: File, bytes: ByteArray?, isForce: Boolean
    ): Boolean {
        return writeFileFromBytesByMap(file, bytes, false, isForce)
    }

    /**
     * 通过映射从字节写入文件。
     *
     * @param file    文件
     * @param bytes   bytes.
     * @param append  True to append, false otherwise.
     * @param isForce True 强制写入文件，否则为 false。
     * @return `true`: success<br></br>`false`: fail
     */
    fun writeFileFromBytesByMap(
        file: File?, bytes: ByteArray?, append: Boolean, isForce: Boolean
    ): Boolean {
        if (bytes == null || !DawnBridge.createOrExistsFile(file)) {
            Log.e("FileIOUtils", "create file <$file> failed.")
            return false
        }
        var fc: FileChannel? = null
        return try {
            fc = FileOutputStream(file, append).channel
            if (fc == null) {
                Log.e("FileIOUtils", "fc is null.")
                return false
            }
            val mbb = fc.map(FileChannel.MapMode.READ_WRITE, fc.size(), bytes.size.toLong())
            mbb.put(bytes)
            if (isForce) {
                mbb.force()
            }
            true
        } catch (e: IOException) {
            e.printStackTrace()
            false
        } finally {
            try {
                fc?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    /**
     * 从字符串写入文件。
     *
     * @param filePath 文件的路径。
     * @param content  内容字符串。
     * @return `true`: success<br></br>`false`: fail
     */
    fun writeFileFromString(filePath: String?, content: String?): Boolean {
        return writeFileFromString(DawnBridge.getFileByPath(filePath), content, false)
    }

    /**
     * 从字符串写入文件。
     *
     * @param filePath 文件的路径。
     * @param content  内容字符串。
     * @param append   True to append, false otherwise.
     * @return `true`: success<br></br>`false`: fail
     */
    @JvmStatic
    fun writeFileFromString(
        filePath: String?, content: String?, append: Boolean
    ): Boolean {
        return writeFileFromString(DawnBridge.getFileByPath(filePath), content, append)
    }
    /**
     * 从字符串写入文件。
     *
     * @param file    文件。
     * @param content 内容字符串。
     * @param append  True to append, false otherwise.
     * @return `true`: success<br></br>`false`: fail
     */
    /**
     * 从字符串写入文件。
     *
     * @param file    文件。
     * @param content 内容字符串。
     * @return `true`: success<br></br>`false`: fail
     */
    @JvmOverloads
    fun writeFileFromString(
        file: File?, content: String?, append: Boolean = false
    ): Boolean {
        if (file == null || content == null) {
            return false
        }
        if (!DawnBridge.createOrExistsFile(file)) {
            Log.e("FileIOUtils", "create file <$file> failed.")
            return false
        }
        var bw: BufferedWriter? = null
        return try {
            bw = BufferedWriter(FileWriter(file, append))
            bw.write(content)
            true
        } catch (e: IOException) {
            e.printStackTrace()
            false
        } finally {
            try {
                bw?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    /**
     * 返回文件中的行。
     *
     * @param filePath 文件路径
     * @return 文件中的行。
     */
    fun readFile2List(filePath: String?): List<String>? {
        return readFile2List(DawnBridge.getFileByPath(filePath), null)
    }

    /**
     * 返回文件中的行。
     *
     * @param filePath 文件路径
     * @param charsetName 字符集的名称。
     * @return 文件中的行。
     */
    fun readFile2List(filePath: String?, charsetName: String?): List<String>? {
        return readFile2List(DawnBridge.getFileByPath(filePath), charsetName)
    }

    /**
     * 返回文件中的行。
     *
     * @param file 文件
     * @param charsetName 字符集的名称
     * @return 文件中的行
     */
    fun readFile2List(file: File?, charsetName: String?): List<String>? {
        return readFile2List(file, 0, 0x7FFFFFFF, charsetName)
    }

    /**
     * 返回文件中的行。
     *
     * @param filePath 文件路径
     * @param st      该行的开始索引。
     * @param end     该行的结束索引。
     * @return  返回文件中的行。
     */
    fun readFile2List(filePath: String?, st: Int, end: Int): List<String>? {
        return readFile2List(DawnBridge.getFileByPath(filePath), st, end, null)
    }

    /**
     * 返回文件中的行。
     *
     * @param filePath 文件路径
     * @param st      该行的开始索引。
     * @param end     该行的结束索引。
     * @param charsetName 字符集的名称
     * @return  返回文件中的行。
     */
    fun readFile2List(
        filePath: String?, st: Int, end: Int, charsetName: String?
    ): List<String>? {
        return readFile2List(DawnBridge.getFileByPath(filePath), st, end, charsetName)
    }
    /**
     * 返回文件中的行。
     *
     * @param file 文件
     * @param st      该行的开始索引。
     * @param end     该行的结束索引。
     * @param charsetName 字符集的名称
     * @return  返回文件中的行。
     */
    /**
     * 返回文件中的行。
     *
     * @param file 文件
     * @return 文件中的行。
     */
    /**
     * 返回文件中的行。
     *
     * @param file 文件
     * @param st      该行的开始索引。
     * @param end     该行的结束索引。
     * @return  返回文件中的行。
     */
    @JvmOverloads
    fun readFile2List(
        file: File?, st: Int = 0, end: Int = 0x7FFFFFFF, charsetName: String? = null
    ): List<String>? {
        if (!DawnBridge.isFileExists(file)) {
            return null
        }
        if (st > end) {
            return null
        }
        var reader: BufferedReader? = null
        return try {
            var line: String
            var curLine = 1
            val list: MutableList<String> = ArrayList()
            reader = if (DawnBridge.isSpace(charsetName)) {
                BufferedReader(InputStreamReader(FileInputStream(file)))
            } else {
                BufferedReader(
                    InputStreamReader(FileInputStream(file), charsetName)
                )
            }
            while (reader.readLine().also { line = it } != null) {
                if (curLine > end) {
                    break
                }
                if (st <= curLine && curLine <= end) {
                    list.add(line)
                }
                ++curLine
            }
            list
        } catch (e: IOException) {
            e.printStackTrace()
            null
        } finally {
            try {
                reader?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    /**
     * 在文件中返回字符串。
     *
     * @param filePath 文件路径
     * @return 字符串。
     */
    fun readFile2String(filePath: String?): String? {
        return readFile2String(DawnBridge.getFileByPath(filePath), null)
    }

    /**
     * 在文件中返回字符串。
     *
     * @param filePath    文件路径
     * @param charsetName 字符集的名称
     * @return 返回字符串。
     */
    fun readFile2String(filePath: String?, charsetName: String?): String? {
        return readFile2String(DawnBridge.getFileByPath(filePath), charsetName)
    }
    /**
     * 在文件中返回字符串。
     *
     * @param file         file.
     * @param charsetName 字符集的名称
     * @return 返回字符串。
     */
    /**
     * 在文件中返回字符串。
     *
     * @param file  file.
     * @return 返回字符串。
     */
    @JvmOverloads
    fun readFile2String(file: File?, charsetName: String? = null): String? {
        val bytes = readFile2BytesByStream(file) ?: return null
        return if (DawnBridge.isSpace(charsetName)) {
            String(bytes)
        } else {
            try {
                String(bytes,Charset.forName(charsetName))
            } catch (e: UnsupportedEncodingException) {
                e.printStackTrace()
                ""
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
    fun readFile2BytesByStream(filePath: String?): ByteArray? {
        return readFile2BytesByStream(DawnBridge.getFileByPath(filePath), null)
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
    fun readFile2BytesByStream(
        filePath: String?, listener: OnProgressUpdateListener?
    ): ByteArray? {
        return readFile2BytesByStream(DawnBridge.getFileByPath(filePath), listener)
    }
    /**
     * 按流返回文件中的字节。
     *
     * @param file      file.
     * @param listener 进度更新侦听器。
     * @return 文件路径
     */
    /**
     * 按流返回文件中的字节。
     *
     * @param file  file.
     * @return 文件中的字节。
     */
    @JvmOverloads
    fun readFile2BytesByStream(
        file: File?, listener: OnProgressUpdateListener? = null
    ): ByteArray? {
        return if (!DawnBridge.isFileExists(file)) {
            null
        } else try {
            var os: ByteArrayOutputStream? = null
            val `is`: InputStream = BufferedInputStream(FileInputStream(file), sBufferSize)
            try {
                os = ByteArrayOutputStream()
                val b = ByteArray(sBufferSize)
                var len: Int
                if (listener == null) {
                    while (`is`.read(b, 0, sBufferSize).also { len = it } != -1) {
                        os.write(b, 0, len)
                    }
                } else {
                    val totalSize = `is`.available().toDouble()
                    var curSize = 0
                    listener.onProgressUpdate(0.0)
                    while (`is`.read(b, 0, sBufferSize).also { len = it } != -1) {
                        os.write(b, 0, len)
                        curSize += len
                        listener.onProgressUpdate(curSize / totalSize)
                    }
                }
                os.toByteArray()
            } catch (e: IOException) {
                e.printStackTrace()
                null
            } finally {
                try {
                    `is`.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                try {
                    os?.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
            null
        }
    }

    /**
     * 按通道返回文件中的字节
     *
     * @param filePath 文件路径
     * @return 文件中的字节
     */
    fun readFile2BytesByChannel(filePath: String?): ByteArray? {
        return readFile2BytesByChannel(DawnBridge.getFileByPath(filePath))
    }

    /**
     * 按通道返回文件中的字节
     *
     * @param file  file.
     * @return 文件中的字节
     */
    @JvmStatic
    fun readFile2BytesByChannel(file: File?): ByteArray? {
        if (!DawnBridge.isFileExists(file)) {
            return null
        }
        var fc: FileChannel? = null
        return try {
            fc = RandomAccessFile(file, "r").channel
            if (fc == null) {
                Log.e("FileIOUtils", "fc is null.")
                return ByteArray(0)
            }
            val byteBuffer = ByteBuffer.allocate(fc.size().toInt())
            while (true) {
                if (fc.read(byteBuffer) <= 0) {
                    break
                }
            }
            byteBuffer.array()
        } catch (e: IOException) {
            e.printStackTrace()
            null
        } finally {
            try {
                fc?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    /**
     * 按映射返回文件中的字节
     *
     * @param filePath 文件路径
     * @return  文件中的字节
     */
    fun readFile2BytesByMap(filePath: String?): ByteArray? {
        return readFile2BytesByMap(DawnBridge.getFileByPath(filePath))
    }

    /**
     * 按映射返回文件中的字节
     *
     * @param file  file.
     * @return 文件中的字节
     */
    fun readFile2BytesByMap(file: File?): ByteArray? {
        if (!DawnBridge.isFileExists(file)) {
            return null
        }
        var fc: FileChannel? = null
        return try {
            fc = RandomAccessFile(file, "r").channel
            if (fc == null) {
                Log.e("FileIOUtils", "fc is null.")
                return ByteArray(0)
            }
            val size = fc.size().toInt()
            val mbb = fc.map(FileChannel.MapMode.READ_ONLY, 0, size.toLong()).load()
            val result = ByteArray(size)
            mbb[result, 0, size]
            result
        } catch (e: IOException) {
            e.printStackTrace()
            null
        } finally {
            try {
                fc?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}