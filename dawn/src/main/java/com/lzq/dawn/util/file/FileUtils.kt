package com.lzq.dawn.util.file

import android.content.Intent
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.StatFs
import android.provider.MediaStore
import android.text.TextUtils
import com.lzq.dawn.DawnBridge
import java.io.BufferedInputStream
import java.io.File
import java.io.FileFilter
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream
import java.net.URL
import java.security.DigestInputStream
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.Collections
import javax.net.ssl.HttpsURLConnection

/**
 * @Name :FileUtils
 * @Time :2022/7/22 14:05
 * @Author :  Lzq
 * @Desc : 文件操作
 */
object FileUtils {
    private val LINE_SEP = System.getProperty("line.separator")

    /**
     * 按路径返回文件。
     *
     * @param filePath 文件的路径。
     * @return file
     */
    @JvmStatic
    fun getFileByPath(filePath: String?): File? {
        return if (DawnBridge.isSpace(filePath)) null else filePath?.let { File(it) }
    }

    /**
     * 返回文件是否存在。
     *
     * @param file file.
     * @return `true`: yes<br></br>`false`: no
     */
    @JvmStatic
    fun isFileExists(file: File?): Boolean {
        if (file == null) {
            return false
        }
        return if (file.exists()) {
            true
        } else isFileExists(file.absolutePath)
    }

    /**
     * 返回文件是否存在。
     *
     * @param filePath 文件的路径
     * @return `true`: yes<br></br>`false`: no
     */
    fun isFileExists(filePath: String?): Boolean {
        val file = getFileByPath(filePath) ?: return false
        return if (file.exists()) {
            true
        } else isFileExistsApi29(filePath)
    }

    private fun isFileExistsApi29(filePath: String?): Boolean {
        if (Build.VERSION.SDK_INT >= 29) {
            try {
                val uri = Uri.parse(filePath)
                val cr = DawnBridge.app.contentResolver
                val afd = cr.openAssetFileDescriptor(uri, "r") ?: return false
                try {
                    afd.close()
                } catch (ignore: IOException) {
                }
            } catch (e: FileNotFoundException) {
                return false
            }
            return true
        }
        return false
    }

    /**
     * 重命名文件。
     *
     * @param filePath 文件的路径
     * @param newName  文件的新名称
     * @return `true`: success<br></br>`false`: fail
     */
    fun rename(filePath: String?, newName: String): Boolean {
        return rename(getFileByPath(filePath), newName)
    }

    /**
     * 重命名文件。
     *
     * @param file    file.
     * @param newName 文件的新名称
     * @return `true`: success<br></br>`false`: fail
     */
    fun rename(file: File?, newName: String): Boolean {
        // file is null then return false
        if (file == null) {
            return false
        }
        // file doesn't exist then return false
        if (!file.exists()) {
            return false
        }
        // the new name is space then return false
        if (DawnBridge.isSpace(newName)) {
            return false
        }
        // the new name equals old name then return true
        if (newName == file.name) {
            return true
        }
        val newFile = File((file.parent?.plus(File.separator) ?: "") + newName)
        // the new name of file exists then return false
        return (!newFile.exists() && file.renameTo(newFile))
    }

    /**
     * 返回是否为目录。
     *
     * @param dirPath 目录的路径
     * @return `true`: yes<br></br>`false`: no
     */
    fun isDir(dirPath: String?): Boolean {
        return isDir(getFileByPath(dirPath))
    }

    /**
     * 返回是否为目录
     *
     * @param file file.
     * @return `true`: yes<br></br>`false`: no
     */
    fun isDir(file: File?): Boolean {
        return file != null && file.exists() && file.isDirectory
    }

    /**
     * 返回是否为文件。
     *
     * @param filePath 返回是否为文件。
     * @return `true`: yes<br></br>`false`: no
     */
    fun isFile(filePath: String?): Boolean {
        return isFile(getFileByPath(filePath))
    }

    /**
     * 返回是否为文件
     *
     * @param file file.
     * @return `true`: yes<br></br>`false`: no
     */
    fun isFile(file: File?): Boolean {
        return file != null && file.exists() && file.isFile
    }

    /**
     * 如果目录不存在，请创建一个目录，否则什么也不做。
     *
     * @param dirPath 目录的路径。
     * @return `true`: exists or creates successfully<br></br>`false`: otherwise
     */
    fun createOrExistsDir(dirPath: String?): Boolean {
        return createOrExistsDir(getFileByPath(dirPath))
    }

    /**
     * 如果目录不存在，请创建一个目录，否则什么也不做。
     *
     * @param file file.
     * @return `true`: exists or creates successfully<br></br>`false`: otherwise
     */
    @JvmStatic
    fun createOrExistsDir(file: File?): Boolean {
        return file != null && if (file.exists()) file.isDirectory else file.mkdirs()
    }

    /**
     * 如果文件不存在，请创建一个文件，否则什么也不做。
     *
     * @param filePath 文件的路径。
     * @return `true`: exists or creates successfully<br></br>`false`: otherwise
     */
    fun createOrExistsFile(filePath: String?): Boolean {
        return createOrExistsFile(getFileByPath(filePath))
    }

    /**
     * 如果文件不存在，请创建一个文件，否则什么也不做。
     *
     * @param file file.
     * @return `true`: exists or creates successfully<br></br>`false`: otherwise
     */
    @JvmStatic
    fun createOrExistsFile(file: File?): Boolean {
        if (file == null) {
            return false
        }
        if (file.exists()) {
            return file.isFile
        }
        return if (!createOrExistsDir(file.parentFile)) {
            false
        } else try {
            file.createNewFile()
        } catch (e: IOException) {
            e.printStackTrace()
            false
        }
    }

    /**
     * 如果文件不存在，请创建一个文件，否则请在创建之前删除旧文件。
     *
     * @param filePath 文件的路径
     * @return `true`: success<br></br>`false`: fail
     */
    fun createFileByDeleteOldFile(filePath: String?): Boolean {
        return createFileByDeleteOldFile(getFileByPath(filePath))
    }

    /**
     * 如果文件不存在，请创建一个文件，否则请在创建之前删除旧文件。
     *
     * @param file file.
     * @return `true`: success<br></br>`false`: fail
     */
    @JvmStatic
    fun createFileByDeleteOldFile(file: File?): Boolean {
        if (file == null) {
            return false
        }
        // file exists and unsuccessfully delete then return false
        if (file.exists() && !file.delete()) {
            return false
        }
        return if (!createOrExistsDir(file.parentFile)) {
            false
        } else try {
            file.createNewFile()
        } catch (e: IOException) {
            e.printStackTrace()
            false
        }
    }

    /**
     * 复制目录或文件。
     *
     * @param srcPath  源路径。
     * @param destPath 目的地路径。
     * @return `true`: success<br></br>`false`: fail
     */
    fun copy(
        srcPath: String?, destPath: String?
    ): Boolean {
        return copy(getFileByPath(srcPath), getFileByPath(destPath), null)
    }

    /**
     * 复制目录或文件
     *
     * @param srcPath  源路径。
     * @param destPath 目的地路径。
     * @param listener 替换侦听器。
     * @return `true`: success<br></br>`false`: fail
     */
    fun copy(
        srcPath: String?, destPath: String?, listener: OnReplaceListener?
    ): Boolean {
        return copy(getFileByPath(srcPath), getFileByPath(destPath), listener)
    }

    /**
     * 复制目录或文件
     *
     * @param src      源。
     * @param dest     目的地。
     * @param listener 替换侦听器
     * @return `true`: success<br></br>`false`: fail
     */
    @JvmOverloads
    fun copy(
        src: File?, dest: File?, listener: OnReplaceListener? = null
    ): Boolean {
        if (src == null) {
            return false
        }
        return if (src.isDirectory) {
            copyDir(src, dest, listener)
        } else copyFile(src, dest, listener)
    }

    /**
     * 复制目录
     *
     * @param srcDir   源目录
     * @param destDir  c目录
     * @param listener 替换侦听器.
     * @return `true`: success<br></br>`false`: fail
     */
    private fun copyDir(
        srcDir: File, destDir: File?, listener: OnReplaceListener?
    ): Boolean {
        return copyOrMoveDir(srcDir, destDir, listener, false)
    }

    /**
     * 复制file.
     *
     * @param srcFile  源 file.
     * @param destFile 源 file.
     * @param listener 替换侦听器
     * @return `true`: success<br></br>`false`: fail
     */
    private fun copyFile(
        srcFile: File, destFile: File?, listener: OnReplaceListener?
    ): Boolean {
        return copyOrMoveFile(srcFile, destFile, listener, false)
    }

    /**
     * 移动目录或文件。
     *
     * @param srcPath  源的路径。
     * @param destPath 目的地的路径。
     * @return `true`: success<br></br>`false`: fail
     */
    fun move(
        srcPath: String?, destPath: String?
    ): Boolean {
        return move(getFileByPath(srcPath), getFileByPath(destPath), null)
    }

    /**
     * 移动目录或文件。
     *
     * @param srcPath  源的路径。
     * @param destPath 目的地的路径。
     * @param listener 替换侦听器
     * @return `true`: success<br></br>`false`: fail
     */
    fun move(
        srcPath: String?, destPath: String?, listener: OnReplaceListener?
    ): Boolean {
        return move(getFileByPath(srcPath), getFileByPath(destPath), listener)
    }

    /**
     * 移动目录或文件
     *
     * @param src      源
     * @param dest     目的
     * @param listener 替换侦听器
     * @return `true`: success<br></br>`false`: fail
     */
    @JvmOverloads
    fun move(
        src: File?, dest: File?, listener: OnReplaceListener? = null
    ): Boolean {
        if (src == null) {
            return false
        }
        return if (src.isDirectory) {
            moveDir(src, dest, listener)
        } else moveFile(src, dest, listener)
    }

    /**
     * 移动目录
     *
     * @param srcDir   源目录.
     * @param destDir  目标目录
     * @param listener 替换侦听器
     * @return `true`: success<br></br>`false`: fail
     */
    fun moveDir(
        srcDir: File?, destDir: File?, listener: OnReplaceListener?
    ): Boolean {
        return copyOrMoveDir(srcDir, destDir, listener, true)
    }

    /**
     * 移动文件
     *
     * @param srcFile  源文件
     * @param destFile 目标文件
     * @param listener 替换侦听器
     * @return `true`: success<br></br>`false`: fail
     */
    fun moveFile(
        srcFile: File?, destFile: File?, listener: OnReplaceListener?
    ): Boolean {
        return copyOrMoveFile(srcFile, destFile, listener, true)
    }

    private fun copyOrMoveDir(
        srcDir: File?, destDir: File?, listener: OnReplaceListener?, isMove: Boolean
    ): Boolean {
        if (srcDir == null || destDir == null) {
            return false
        }
        // destDir's path locate in srcDir's path then return false
        val srcPath = srcDir.path + File.separator
        val destPath = destDir.path + File.separator
        if (destPath.contains(srcPath)) {
            return false
        }
        if (!srcDir.exists() || !srcDir.isDirectory) {
            return false
        }
        if (!createOrExistsDir(destDir)) {
            return false
        }
        val files = srcDir.listFiles()
        if (files != null && files.size > 0) {
            for (file in files) {
                val oneDestFile = File(destPath + file.name)
                if (file.isFile) {
                    if (!copyOrMoveFile(file, oneDestFile, listener, isMove)) {
                        return false
                    }
                } else if (file.isDirectory) {
                    if (!copyOrMoveDir(file, oneDestFile, listener, isMove)) {
                        return false
                    }
                }
            }
        }
        return !isMove || deleteDir(srcDir)
    }

    private fun copyOrMoveFile(
        srcFile: File?, destFile: File?, listener: OnReplaceListener?, isMove: Boolean
    ): Boolean {
        if (srcFile == null || destFile == null) {
            return false
        }
        // srcFile equals destFile then return false
        if (srcFile == destFile) {
            return false
        }
        // srcFile doesn't exist or isn't a file then return false
        if (!srcFile.exists() || !srcFile.isFile) {
            return false
        }
        if (destFile.exists()) {
            if (listener == null || listener.onReplace(srcFile, destFile)) { // require delete the old file
                if (!destFile.delete()) { // unsuccessfully delete then return false
                    return false
                }
            } else {
                return true
            }
        }
        return if (!createOrExistsDir(destFile.parentFile)) {
            false
        } else try {
            (DawnBridge.writeFileFromIS(
                destFile.absolutePath, FileInputStream(srcFile)
            ) && !(isMove && !deleteFile(srcFile)))
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
            false
        }
    }

    /**
     * 删除目录。
     *
     * @param filePath 文件的路径。
     * @return `true`: success<br></br>`false`: fail
     */
    fun delete(filePath: String?): Boolean {
        return delete(getFileByPath(filePath))
    }

    /**
     * 删除目录.
     *
     * @param file file.
     * @return `true`: success<br></br>`false`: fail
     */
    fun delete(file: File?): Boolean {
        if (file == null) {
            return false
        }
        return if (file.isDirectory) {
            deleteDir(file)
        } else deleteFile(file)
    }

    /**
     * 删除目录
     *
     * @param dir 目录
     * @return `true`: success<br></br>`false`: fail
     */
    private fun deleteDir(dir: File?): Boolean {
        if (dir == null) {
            return false
        }
        // dir doesn't exist then return true
        if (!dir.exists()) {
            return true
        }
        // dir isn't a directory then return false
        if (!dir.isDirectory) {
            return false
        }
        val files = dir.listFiles()
        if (files != null && files.size > 0) {
            for (file in files) {
                if (file.isFile) {
                    if (!file.delete()) {
                        return false
                    }
                } else if (file.isDirectory) {
                    if (!deleteDir(file)) {
                        return false
                    }
                }
            }
        }
        return dir.delete()
    }

    /**
     * 删除文件
     *
     * @param file file.
     * @return `true`: success<br></br>`false`: fail
     */
    private fun deleteFile(file: File?): Boolean {
        return file != null && (!file.exists() || file.isFile && file.delete())
    }

    /**
     * 删除all-in目录。
     *
     * @param dirPath 目录的路径。
     * @return `true`: success<br></br>`false`: fail
     */
    fun deleteAllInDir(dirPath: String?): Boolean {
        return deleteAllInDir(getFileByPath(dirPath))
    }

    /**
     * 删除all-in目录。
     *
     * @param dir 目录
     * @return `true`: success<br></br>`false`: fail
     */
    @JvmStatic
    fun deleteAllInDir(dir: File?): Boolean {
        return deleteFilesInDirWithFilter(dir) { true }
    }

    /**
     * 删除目录中的所有文件。
     *
     * @param dirPath 目录的路径。
     * @return `true`: success<br></br>`false`: fail
     */
    fun deleteFilesInDir(dirPath: String?): Boolean {
        return deleteFilesInDir(getFileByPath(dirPath))
    }

    /**
     * 删除目录中的所有文件
     *
     * @param dir 目录的路径。
     * @return `true`: success<br></br>`false`: fail
     */
    fun deleteFilesInDir(dir: File?): Boolean {
        return deleteFilesInDirWithFilter(dir) { pathname -> pathname.isFile }
    }

    /**
     * 删除目录中满足筛选器的所有文件。
     *
     * @param dirPath 目录的路径。
     * @param filter  过滤器
     * @return `true`: success<br></br>`false`: fail
     */
    fun deleteFilesInDirWithFilter(
        dirPath: String?, filter: FileFilter?
    ): Boolean {
        return deleteFilesInDirWithFilter(getFileByPath(dirPath), filter)
    }

    /**
     * 删除目录中满足筛选器的所有文件。
     *
     * @param dir    目录。
     * @param filter 过滤器
     * @return `true`: success<br></br>`false`: fail
     */
    fun deleteFilesInDirWithFilter(dir: File?, filter: FileFilter?): Boolean {
        if (dir == null || filter == null) {
            return false
        }
        // dir doesn't exist then return true
        if (!dir.exists()) {
            return true
        }
        // dir isn't a directory then return false
        if (!dir.isDirectory) {
            return false
        }
        val files = dir.listFiles()
        if (files != null && files.size != 0) {
            for (file in files) {
                if (filter.accept(file)) {
                    if (file.isFile) {
                        if (!file.delete()) {
                            return false
                        }
                    } else if (file.isDirectory) {
                        if (!deleteDir(file)) {
                            return false
                        }
                    }
                }
            }
        }
        return true
    }

    /**
     * 返回目录中的文件。
     *
     * 不遍历子目录
     *
     * @param dirPath    目录的路径
     * @param comparator 用于确定列表顺序的比较器。
     * @return 目录中的文件
     */
    @JvmOverloads
    fun listFilesInDir(dirPath: String?, comparator: Comparator<File?>? = null): List<File?> {
        return listFilesInDir(getFileByPath(dirPath), false, comparator)
    }

    /**
     * 返回目录中的文件。
     *
     * 不遍历子目录
     *
     * @param dir        目录
     * @param comparator 用于确定列表顺序的比较器。
     * @return 目录中的文件
     */
    @JvmOverloads
    fun listFilesInDir(dir: File?, comparator: Comparator<File?>? = null): List<File?> {
        return listFilesInDir(dir, false, comparator)
    }

    /**
     * 返回目录中的文件。
     *
     * @param dirPath     目录的路径
     * @param isRecursive True遍历子目录，否则为false。
     * @return 目录中的文件
     */
    fun listFilesInDir(dirPath: String?, isRecursive: Boolean): List<File?> {
        return listFilesInDir(getFileByPath(dirPath), isRecursive)
    }

    /**
     * 返回目录中的文件。
     *
     * @param dirPath     目录的路径
     * @param isRecursive True遍历子目录，否则为false。
     * @param comparator  用于确定列表顺序的比较器。
     * @return 目录中的文件
     */
    fun listFilesInDir(
        dirPath: String?, isRecursive: Boolean, comparator: Comparator<File?>?
    ): List<File?> {
        return listFilesInDir(getFileByPath(dirPath), isRecursive, comparator)
    }

    /**
     * 返回目录中的文件。
     *
     * @param dir         目录
     * @param isRecursive True遍历子目录，否则为false。
     * @param comparator  用于确定列表顺序的比较器。
     * @return 目录中的文件
     */
    @JvmOverloads
    fun listFilesInDir(
        dir: File?, isRecursive: Boolean, comparator: Comparator<File?>? = null
    ): List<File?> {
        return listFilesInDirWithFilter(dir, { true }, isRecursive, comparator)
    }

    /**
     * 返回目录中满足筛选器的文件
     *
     * 不遍历子目录
     *
     * @param dirPath 目录的路径
     * @param filter  过滤器
     * @return 目录中满足筛选器的文件
     */
    fun listFilesInDirWithFilter(
        dirPath: String?, filter: FileFilter
    ): List<File?> {
        return listFilesInDirWithFilter(getFileByPath(dirPath), filter)
    }

    /**
     * 返回目录中满足筛选器的文件
     *
     * 不遍历子目录
     *
     * @param dirPath    目录的路径
     * @param filter     过滤器
     * @param comparator 用于确定列表顺序的比较器
     * @return 目录中满足筛选器的文件
     */
    fun listFilesInDirWithFilter(
        dirPath: String?, filter: FileFilter, comparator: Comparator<File?>?
    ): List<File?> {
        return listFilesInDirWithFilter(getFileByPath(dirPath), filter, comparator)
    }

    /**
     * 返回目录中满足筛选器的文件
     *
     * 不遍历子目录
     *
     * @param dir        目录
     * @param filter     过滤器
     * @param comparator 用于确定列表顺序的比较器
     * @return 目录中满足筛选器的文件
     */
    fun listFilesInDirWithFilter(
        dir: File?, filter: FileFilter, comparator: Comparator<File?>?
    ): List<File?> {
        return listFilesInDirWithFilter(dir, filter, false, comparator)
    }

    /**
     * 返回目录中满足筛选器的文件。
     *
     * @param dirPath     目录的路径
     * @param filter      过滤器
     * @param isRecursive True遍历子目录，否则为false。
     * @return 目录中满足筛选器的文件
     */
    fun listFilesInDirWithFilter(
        dirPath: String?, filter: FileFilter, isRecursive: Boolean
    ): List<File?> {
        return listFilesInDirWithFilter(getFileByPath(dirPath), filter, isRecursive)
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
    fun listFilesInDirWithFilter(
        dirPath: String?, filter: FileFilter, isRecursive: Boolean, comparator: Comparator<File?>?
    ): List<File?> {
        return listFilesInDirWithFilter(getFileByPath(dirPath), filter, isRecursive, comparator)
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
    @JvmOverloads
    fun listFilesInDirWithFilter(
        dir: File?, filter: FileFilter, isRecursive: Boolean = false, comparator: Comparator<File?>? = null
    ): List<File?> {
        val files = listFilesInDirWithFilterInner(dir, filter, isRecursive)
        if (comparator != null) {
            Collections.sort(files, comparator)
        }
        return files
    }

    private fun listFilesInDirWithFilterInner(
        dir: File?, filter: FileFilter, isRecursive: Boolean
    ): List<File?> {
        val list: MutableList<File?> = ArrayList()
        if (!isDir(dir)) {
            return list
        }
        val files = dir!!.listFiles()
        if (files != null && files.size > 0) {
            for (file in files) {
                if (filter.accept(file)) {
                    list.add(file)
                }
                if (isRecursive && file.isDirectory) {
                    list.addAll(listFilesInDirWithFilterInner(file, filter, true))
                }
            }
        }
        return list
    }

    /**
     * 返回上次修改文件的时间。
     *
     * @param filePath 文件路径。
     * @return 返回上次修改文件的时间。
     */
    fun getFileLastModified(filePath: String?): Long {
        return getFileLastModified(getFileByPath(filePath))
    }

    /**
     * 返回上次修改文件的时间。
     *
     * @param file file.
     * @return 返回上次修改文件的时间。
     */
    fun getFileLastModified(file: File?): Long {
        return file?.lastModified() ?: -1
    }

    /**
     * 简单返回文件的字符集。
     *
     * @param filePath 文件路径。
     * @return 简单返回文件的字符集。
     */
    fun getFileCharsetSimple(filePath: String?): String {
        return getFileCharsetSimple(getFileByPath(filePath))
    }

    /**
     * 简单返回文件的字符集。
     *
     * @param file file.
     * @return 简单返回文件的字符集。
     */
    fun getFileCharsetSimple(file: File?): String {
        if (file == null) {
            return ""
        }
        if (isUtf8(file)) {
            return "UTF-8"
        }
        var p = 0
        var `is`: InputStream? = null
        try {
            `is` = BufferedInputStream(FileInputStream(file))
            p = (`is`.read() shl 8) + `is`.read()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                `is`?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return when (p) {
            0xfffe -> "Unicode"
            0xfeff -> "UTF-16BE"
            else -> "GBK"
        }
    }

    /**
     * 返回文件的字符集是否为utf8。
     *
     * @param filePath 文件路径。
     * @return `true`: yes<br></br>`false`: no
     */
    fun isUtf8(filePath: String?): Boolean {
        return isUtf8(getFileByPath(filePath))
    }

    /**
     * 返回文件的字符集是否为utf8。
     *
     * @param file file.
     * @return `true`: yes<br></br>`false`: no
     */
    fun isUtf8(file: File?): Boolean {
        if (file == null) {
            return false
        }
        var `is`: InputStream? = null
        try {
            val bytes = ByteArray(24)
            `is` = BufferedInputStream(FileInputStream(file))
            val read = `is`.read(bytes)
            return if (read != -1) {
                val readArr = ByteArray(read)
                System.arraycopy(bytes, 0, readArr, 0, read)
                isUtf8(readArr) == 100
            } else {
                false
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                `is`?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return false
    }

    /**
     * UTF-8编码方式
     * ----------------------------------------------
     * 0xxxxxxx
     * 110xxxxx 10xxxxxx
     * 1110xxxx 10xxxxxx 10xxxxxx
     * 11110xxx 10xxxxxx 10xxxxxx 10xxxxxx
     */
    private fun isUtf8(raw: ByteArray): Int {
        var i: Int
        val len: Int
        var utf8 = 0
        var ascii = 0
        if (raw.size > 3) {
            if (raw[0] == 0xEF.toByte() && raw[1] == 0xBB.toByte() && raw[2] == 0xBF.toByte()) {
                return 100
            }
        }
        len = raw.size
        var child = 0
        i = 0
        while (i < len) {

            // UTF-8 byte shouldn't be FF and FE
            if (raw[i].toInt() and 0xFF.toByte().toInt() == 0xFF.toByte()
                    .toInt() || raw[i].toInt() and 0xFE.toByte().toInt() == 0xFE.toByte().toInt()
            ) {
                return 0
            }
            if (child == 0) {
                // ASCII format is 0x0*******
                if (raw[i].toInt() and 0x7F.toByte().toInt() == raw[i].toInt() && raw[i].toInt() != 0) {
                    ascii++
                } else if (raw[i].toInt() and 0xC0.toByte().toInt() == 0xC0.toByte().toInt()) {
                    // 0x11****** maybe is UTF-8
                    for (bit in 0..7) {
                        child =
                            if ((0x80 shr bit).toByte().toInt() and raw[i].toInt() == (0x80 shr bit).toByte()
                                    .toInt()
                            ) {
                                bit
                            } else {
                                break
                            }
                    }
                    utf8++
                }
                i++
            } else {
                child = if (raw.size - i > child) child else raw.size - i
                var currentNotUtf8 = false
                for (children in 0 until child) {
                    // format must is 0x10******
                    if (raw[i + children].toInt() and 0x80.toByte().toInt() != 0x80.toByte().toInt()) {
                        if (raw[i + children].toInt() and 0x7F.toByte()
                                .toInt() == raw[i + children].toInt() && raw[i].toInt() != 0
                        ) {
                            // ASCII format is 0x0*******
                            ascii++
                        }
                        currentNotUtf8 = true
                    }
                }
                if (currentNotUtf8) {
                    utf8--
                    i++
                } else {
                    utf8 += child
                    i += child
                }
                child = 0
            }
        }
        // UTF-8 contains ASCII
        return if (ascii == len) {
            100
        } else (100 * ((utf8 + ascii).toFloat() / len.toFloat())).toInt()
    }

    /**
     * 返回文件的行数。
     *
     * @param filePath 文件的路径
     * @return 返回文件的行数。
     */
    fun getFileLines(filePath: String?): Int {
        return getFileLines(getFileByPath(filePath))
    }

    /**
     * 返回文件的行数。
     *
     * @param file file.
     * @return 返回文件的行数。
     */
    fun getFileLines(file: File?): Int {
        var count = 1
        var `is`: InputStream? = null
        try {
            `is` = BufferedInputStream(FileInputStream(file))
            val buffer = ByteArray(1024)
            var readChars: Int
            if (LINE_SEP!!.endsWith("\n")) {
                while (`is`.read(buffer, 0, 1024).also { readChars = it } != -1) {
                    for (i in 0 until readChars) {
                        if (buffer[i] == '\n'.code.toByte()) {
                            ++count
                        }
                    }
                }
            } else {
                while (`is`.read(buffer, 0, 1024).also { readChars = it } != -1) {
                    for (i in 0 until readChars) {
                        if (buffer[i] == '\r'.code.toByte()) {
                            ++count
                        }
                    }
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                `is`?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return count
    }

    /**
     * 返回大小。
     *
     * @param filePath 文件的路径。
     * @return 返回大小。
     */
    fun getSize(filePath: String?): String {
        return getSize(getFileByPath(filePath))
    }

    /**
     * 返回大小。
     *
     * @param file 目录
     * @return 返回大小。
     */
    fun getSize(file: File?): String {
        if (file == null) {
            return ""
        }
        return if (file.isDirectory) {
            getDirSize(file)
        } else getFileSize(file)
    }

    /**
     * 返回目录的大小。
     *
     * @param dir 目录
     * @return 返回目录的大小。
     */
    private fun getDirSize(dir: File): String {
        val len = getDirLength(dir)
        return if (len == -1L) "" else DawnBridge.byte2FitMemorySize(len)
    }

    /**
     * 返回文件大小。
     *
     * @param file file.
     * @return 返回文件大小。
     */
    private fun getFileSize(file: File): String {
        val len = getFileLength(file)
        return if (len == -1L) "" else DawnBridge.byte2FitMemorySize(len)
    }

    /**
     * 返回长度。
     *
     * @param filePath 文件的路径。
     * @return 返回长度。
     */
    fun getLength(filePath: String?): Long {
        return getLength(getFileByPath(filePath))
    }

    /**
     * 返回长度。
     *
     * @param file file.
     * @return 返回长度。
     */
    fun getLength(file: File?): Long {
        if (file == null) {
            return 0
        }
        return if (file.isDirectory) {
            getDirLength(file)
        } else getFileLength(file)
    }

    /**
     * 返回目录的长度。
     *
     * @param dir 目录
     * @return 返回目录的长度。
     */
    private fun getDirLength(dir: File): Long {
        if (!isDir(dir)) {
            return 0
        }
        var len: Long = 0
        val files = dir.listFiles()
        if (files != null && files.size > 0) {
            for (file in files) {
                len += if (file.isDirectory) {
                    getDirLength(file)
                } else {
                    file.length()
                }
            }
        }
        return len
    }

    /**
     * 返回文件的长度。
     *
     * @param filePath 文件的路径。
     * @return 返回文件的长度。
     */
    fun getFileLength(filePath: String): Long {
        val isURL = filePath.matches("[a-zA-z]+://[^\\s]*".toRegex())
        if (isURL) {
            try {
                val conn = URL(filePath).openConnection() as HttpsURLConnection
                conn.setRequestProperty("Accept-Encoding", "identity")
                conn.connect()
                return if (conn.responseCode == 200) {
                    conn.contentLength.toLong()
                } else -1
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return getFileLength(getFileByPath(filePath))
    }

    /**
     * 返回文件的长度。
     *
     * @param file file.
     * @return 返回文件的长度。
     */
    private fun getFileLength(file: File?): Long {
        return if (!isFile(file)) {
            -1
        } else file!!.length()
    }

    /**
     * 返回文件的 MD5
     *
     * @param filePath 文件路径
     * @return 返回文件的 MD5
     */
    fun getFileMD5ToString(filePath: String?): String {
        val file = if (DawnBridge.isSpace(filePath)) null else filePath?.let { File(it) }
        return getFileMD5ToString(file)
    }

    /**
     * 返回文件的 MD5
     *
     * @param file 文件
     * @return  返回文件的 MD5
     */
    fun getFileMD5ToString(file: File?): String {
        return DawnBridge.bytes2HexString(getFileMD5(file))
    }

    /**
     * 返回文件的 MD5
     *
     * @param filePath 文件路径
     * @return 返回文件的 MD5
     */
    fun getFileMD5(filePath: String?): ByteArray? {
        return getFileMD5(getFileByPath(filePath))
    }

    /**
     * 返回文件的 MD5
     *
     * @param file 文件
     * @return 返回文件的 MD5
     */
    fun getFileMD5(file: File?): ByteArray? {
        if (file == null) {
            return null
        }
        var dis: DigestInputStream? = null
        try {
            val fis = FileInputStream(file)
            var md = MessageDigest.getInstance("MD5")
            dis = DigestInputStream(fis, md)
            val buffer = ByteArray(1024 * 256)
            while (true) {
                if (dis.read(buffer) <= 0) {
                    break
                }
            }
            md = dis.messageDigest
            return md.digest()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                dis?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return null
    }

    /**
     * 返回文件的目录路径。
     *
     * @param file  file.
     * @return 返回文件的目录路径。
     */
    fun getDirName(file: File?): String {
        return if (file == null) {
            ""
        } else getDirName(file.absolutePath)
    }

    /**
     * 返回文件的目录路径。
     *
     * @param filePath 文件路径
     * @return 返回文件的目录路径。
     */
    fun getDirName(filePath: String): String {
        if (DawnBridge.isSpace(filePath)) {
            return ""
        }
        val lastSep = filePath.lastIndexOf(File.separator)
        return if (lastSep == -1) "" else filePath.substring(0, lastSep + 1)
    }

    /**
     * 返回文件名。
     *
     * @param file  file.
     * @return 返回文件名。
     */
    fun getFileName(file: File?): String {
        return if (file == null) {
            ""
        } else getFileName(file.absolutePath)
    }

    /**
     * 返回文件名。
     *
     * @param filePath 文件路径
     * @return 返回文件名。
     */
    fun getFileName(filePath: String): String {
        if (DawnBridge.isSpace(filePath)) {
            return ""
        }
        val lastSep = filePath.lastIndexOf(File.separator)
        return if (lastSep == -1) filePath else filePath.substring(lastSep + 1)
    }

    /**
     * 返回不带扩展名的文件名。
     *
     * @param file  file.
     * @return 返回不带扩展名的文件名。
     */
    fun getFileNameNoExtension(file: File?): String {
        return if (file == null) {
            ""
        } else getFileNameNoExtension(file.path)
    }

    /**
     * 返回不带扩展名的文件名。
     *
     * @param filePath 文件路径
     * @return 返回不带扩展名的文件名。
     */
    fun getFileNameNoExtension(filePath: String): String {
        if (DawnBridge.isSpace(filePath)) {
            return ""
        }
        val lastPoi = filePath.lastIndexOf('.')
        val lastSep = filePath.lastIndexOf(File.separator)
        if (lastSep == -1) {
            return if (lastPoi == -1) filePath else filePath.substring(0, lastPoi)
        }
        return if (lastPoi == -1 || lastSep > lastPoi) {
            filePath.substring(lastSep + 1)
        } else filePath.substring(lastSep + 1, lastPoi)
    }

    /**
     * 返回文件的扩展名
     *
     * @param file  file.
     * @return 返回文件的扩展名
     */
    fun getFileExtension(file: File?): String {
        return if (file == null) {
            ""
        } else getFileExtension(file.path)
    }

    /**
     * 返回文件的扩展名
     *
     * @param filePath 文件路径
     * @return 返回文件的扩展名
     */
    fun getFileExtension(filePath: String): String {
        if (DawnBridge.isSpace(filePath)) {
            return ""
        }
        val lastPoi = filePath.lastIndexOf('.')
        val lastSep = filePath.lastIndexOf(File.separator)
        return if (lastPoi == -1 || lastSep >= lastPoi) {
            ""
        } else filePath.substring(lastPoi + 1)
    }

    /**
     * 通知系统扫描文件。
     *
     * @param filePath 文件路径
     */
    fun notifySystemToScan(filePath: String?) {
        notifySystemToScan(getFileByPath(filePath))
    }

    /**
     * 通知系统扫描文件。
     *
     * @param file  file.
     */
    @JvmStatic
    fun notifySystemToScan(file: File?) {
        if (file == null || !file.exists()) {
            return
        }
        MediaScannerConnection.scanFile(DawnBridge.app, arrayOf(file.absolutePath), null, null)
    }

    /**
     * 通知系统扫描文件。
     *
     * @param file  file.
     */
    @JvmStatic
    fun notifySystemToScan(file: File?, callback: MediaScannerConnection.OnScanCompletedListener) {
        if (file == null || !file.exists()) {
            return
        }
        MediaScannerConnection.scanFile(DawnBridge.app, arrayOf(file.absolutePath), null, callback)
    }

    /**
     * 返回文件系统的总大小。
     *
     * @param anyPathInFs 文件系统中的任何路径。
     * @return 返回文件系统的总大小
     */
    @JvmStatic
    fun getFsTotalSize(anyPathInFs: String?): Long {
        if (TextUtils.isEmpty(anyPathInFs)) {
            return 0
        }
        val statFs = StatFs(anyPathInFs)
        val blockSize: Long = statFs.blockSizeLong
        val totalSize: Long = statFs.blockCountLong
        return blockSize * totalSize
    }

    /**
     * 返回文件系统的可用大小。
     *
     * @param anyPathInFs 文件系统中的任何路径
     * @return 返回文件系统的可用大小。
     */
    @JvmStatic
    fun getFsAvailableSize(anyPathInFs: String?): Long {
        if (TextUtils.isEmpty(anyPathInFs)) {
            return 0
        }
        val statFs = StatFs(anyPathInFs)
        val blockSize: Long = statFs.blockSizeLong
        val availableSize: Long = statFs.availableBlocksLong
        return blockSize * availableSize
    }
}