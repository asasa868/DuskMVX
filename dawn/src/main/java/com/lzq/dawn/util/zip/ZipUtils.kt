package com.lzq.dawn.util.zip

import android.util.Log
import com.lzq.dawn.DawnBridge
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.Enumeration
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipOutputStream

/**
 * @Name : ZipUtils
 * @Time : 2022/12/21  16:33
 * @Author :  Lzq
 * @Desc : zip
 */
object ZipUtils {
    private const val BUFFER_LEN = 8192

    /**
     * 压缩文件
     *
     * @param srcFiles    文件的来源。
     * @param zipFilePath 压缩后文件的路径。
     * @return `true`: success<br></br>`false`: fail
     * @throws IOException 如果发生 IO 错误
     */
    @Throws(IOException::class)
    fun zipFiles(
        srcFiles: Collection<String?>?, zipFilePath: String?
    ): Boolean {
        return zipFiles(srcFiles, zipFilePath, null)
    }

    /**
     * 压缩文件
     *
     * @param srcFilePaths 源文件的路径。
     * @param zipFilePath  压缩后文件的路径。
     * @param comment      评论
     * @return `true`: success<br></br>`false`: fail
     * @throws IOException 如果发生 IO 错误
     */
    @Throws(IOException::class)
    fun zipFiles(
        srcFilePaths: Collection<String?>?, zipFilePath: String?, comment: String?
    ): Boolean {
        if (srcFilePaths == null || zipFilePath == null) {
            return false
        }
        var zos: ZipOutputStream? = null
        return try {
            zos = ZipOutputStream(FileOutputStream(zipFilePath))
            for (srcFile in srcFilePaths) {
                if (!zipFile(DawnBridge.getFileByPath(srcFile), "", zos, comment)) {
                    return false
                }
            }
            true
        } finally {
            if (zos != null) {
                zos.finish()
                zos.close()
            }
        }
    }
    /**
     * 压缩文件
     *
     * @param srcFiles 文件的来源。
     * @param zipFile  压缩后文件的路径。
     * @param comment  评论
     * @return `true`: success<br></br>`false`: fail
     * @throws IOException 如果发生 IO 错误
     */
    @JvmOverloads
    @Throws(IOException::class)
    fun zipFiles(
        srcFiles: Collection<File>?, zipFile: File?, comment: String? = null
    ): Boolean {
        if (srcFiles == null || zipFile == null) {
            return false
        }
        var zos: ZipOutputStream? = null
        return try {
            zos = ZipOutputStream(FileOutputStream(zipFile))
            for (srcFile in srcFiles) {
                if (!zipFile(srcFile, "", zos, comment)) {
                    return false
                }
            }
            true
        } finally {
            if (zos != null) {
                zos.finish()
                zos.close()
            }
        }
    }

    /**
     * 压缩文件
     *
     * @param srcFilePath 源文件的路径。
     * @param zipFilePath 压缩后文件的路径
     * @return `true`: success<br></br>`false`: fail
     * @throws IOException 如果发生 IO 错误
     */
    @Throws(IOException::class)
    fun zipFile(
        srcFilePath: String?, zipFilePath: String?
    ): Boolean {
        return zipFile(DawnBridge.getFileByPath(srcFilePath), DawnBridge.getFileByPath(zipFilePath), null)
    }

    /**
     * 压缩文件
     *
     * @param srcFilePath 源文件的路径。
     * @param zipFilePath 压缩后文件的路径
     * @param comment  评论
     * @return `true`: success<br></br>`false`: fail
     * @throws IOException 如果发生 IO 错误
     */
    @Throws(IOException::class)
    fun zipFile(
        srcFilePath: String?, zipFilePath: String?, comment: String?
    ): Boolean {
        return zipFile(DawnBridge.getFileByPath(srcFilePath), DawnBridge.getFileByPath(zipFilePath), comment)
    }

    /**
     * 压缩文件
     *
     * @param srcFile 源文件的路径。
     * @param zipFile 压缩后文件的路径
     * @return `true`: success<br></br>`false`: fail
     * @throws IOException 如果发生 IO 错误
     */
    @JvmOverloads
    @Throws(IOException::class)
    fun zipFile(
        srcFile: File?, zipFile: File?, comment: String? = null
    ): Boolean {
        if (srcFile == null || zipFile == null) {
            return false
        }
        var zos: ZipOutputStream? = null
        return try {
            zos = ZipOutputStream(FileOutputStream(zipFile))
            zipFile(srcFile, "", zos, comment)
        } finally {
            zos?.close()
        }
    }

    @Throws(IOException::class)
    private fun zipFile(
        srcFile: File, rootPath: String, zos: ZipOutputStream, comment: String?
    ): Boolean {
        var rootPath = rootPath
        rootPath = rootPath + (if (DawnBridge.isSpace(rootPath)) "" else File.separator) + srcFile.name
        if (srcFile.isDirectory) {
            val fileList = srcFile.listFiles()
            if (fileList == null || fileList.isEmpty()) {
                val entry = ZipEntry("$rootPath/")
                entry.comment = comment
                zos.putNextEntry(entry)
                zos.closeEntry()
            } else {
                for (file in fileList) {
                    if (!zipFile(file, rootPath, zos, comment)) {
                        return false
                    }
                }
            }
        } else {
            var `is`: InputStream? = null
            try {
                `is` = BufferedInputStream(FileInputStream(srcFile))
                val entry = ZipEntry(rootPath)
                entry.comment = comment
                zos.putNextEntry(entry)
                val buffer = ByteArray(BUFFER_LEN)
                var len: Int
                while (`is`.read(buffer, 0, BUFFER_LEN).also { len = it } != -1) {
                    zos.write(buffer, 0, len)
                }
                zos.closeEntry()
            } finally {
                `is`?.close()
            }
        }
        return true
    }

    /**
     * 解压缩文件。
     *
     * @param zipFilePath ZIP 文件的路径。
     * @param destDirPath 目标目录
     * @return 解压缩的文件
     * @throws IOException 如果解压缩不成功
     */
    @Throws(IOException::class)
    fun unzipFile(
        zipFilePath: String?, destDirPath: String?
    ): List<File>? {
        return unzipFileByKeyword(zipFilePath, destDirPath, null)
    }

    /**
     * 解压缩文件。
     *
     * @param zipFile ZIP 文件。
     * @param destDir 目标目录。
     * @return 解压缩的文件
     * @throws IOException 如果解压缩不成功
     */
    @Throws(IOException::class)
    fun unzipFile(
        zipFile: File?, destDir: File?
    ): List<File>? {
        return unzipFileByKeyword(zipFile, destDir, null)
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
    @Throws(IOException::class)
    fun unzipFileByKeyword(
        zipFilePath: String?, destDirPath: String?, keyword: String?
    ): List<File>? {
        return unzipFileByKeyword(
            DawnBridge.getFileByPath(zipFilePath),
            DawnBridge.getFileByPath(destDirPath),
            keyword
        )
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
    @Throws(IOException::class)
    fun unzipFileByKeyword(
        zipFile: File?, destDir: File?, keyword: String?
    ): List<File>? {
        if (zipFile == null || destDir == null) {
            return null
        }
        val files: MutableList<File> = ArrayList()
        val zip = ZipFile(zipFile)
        val entries: Enumeration<*> = zip.entries()
        zip.use { zip ->
            if (DawnBridge.isSpace(keyword)) {
                while (entries.hasMoreElements()) {
                    val entry = entries.nextElement() as ZipEntry
                    val entryName = entry.name.replace("\\", "/")
                    if (entryName.contains("../")) {
                        Log.e("ZipUtils", "entryName: $entryName is dangerous!")
                        continue
                    }
                    if (!unzipChildFile(destDir, files, zip, entry, entryName)) {
                        return files
                    }
                }
            } else {
                while (entries.hasMoreElements()) {
                    val entry = entries.nextElement() as ZipEntry
                    val entryName = entry.name.replace("\\", "/")
                    if (entryName.contains("../")) {
                        Log.e("ZipUtils", "entryName: $entryName is dangerous!")
                        continue
                    }
                    if (entryName.contains(keyword!!)) {
                        if (!unzipChildFile(destDir, files, zip, entry, entryName)) {
                            return files
                        }
                    }
                }
            }
        }
        return files
    }

    @Throws(IOException::class)
    private fun unzipChildFile(
        destDir: File, files: MutableList<File>, zip: ZipFile, entry: ZipEntry, name: String
    ): Boolean {
        val file = File(destDir, name)
        files.add(file)
        if (entry.isDirectory) {
            return DawnBridge.createOrExistsDir(file)
        } else {
            if (!DawnBridge.createOrExistsFile(file)) {
                return false
            }
            var `in`: InputStream? = null
            var out: OutputStream? = null
            try {
                `in` = BufferedInputStream(zip.getInputStream(entry))
                out = BufferedOutputStream(FileOutputStream(file))
                val buffer = ByteArray(BUFFER_LEN)
                var len: Int
                while (`in`.read(buffer).also { len = it } != -1) {
                    out.write(buffer, 0, len)
                }
            } finally {
                `in`?.close()
                out?.close()
            }
        }
        return true
    }

    /**
     * 返回 ZIP 文件中的文件路径。
     *
     * @param zipFilePath ZIP 文件的路径。
     * @return ZIP 文件中的文件路径
     * @throws IOException 如果发生 IO 错误
     */
    @Throws(IOException::class)
    fun getFilesPath(zipFilePath: String?): List<String>? {
        return getFilesPath(DawnBridge.getFileByPath(zipFilePath))
    }

    /**
     * 返回 ZIP 文件中的文件路径。
     *
     * @param zipFile 压缩文件。
     * @return ZIP 文件中的文件路径
     * @throws IOException 如果发生 IO 错误
     */
    @Throws(IOException::class)
    fun getFilesPath(zipFile: File?): List<String>? {
        if (zipFile == null) {
            return null
        }
        val paths: MutableList<String> = ArrayList()
        val zip = ZipFile(zipFile)
        val entries: Enumeration<*> = zip.entries()
        while (entries.hasMoreElements()) {
            val entryName = (entries.nextElement() as ZipEntry).name.replace("\\", "/")
            if (entryName.contains("../")) {
                Log.e("ZipUtils", "entryName: $entryName is dangerous!")
                paths.add(entryName)
            } else {
                paths.add(entryName)
            }
        }
        zip.close()
        return paths
    }

    /**
     * 以 ZIP 文件返回文件的注释。
     *
     * @param zipFilePath ZIP 文件的路径。
     * @return ZIP 文件中的文件注释
     * @throws IOException 如果发生 IO 错误
     */
    @Throws(IOException::class)
    fun getComments(zipFilePath: String?): List<String>? {
        return getComments(DawnBridge.getFileByPath(zipFilePath))
    }

    /**
     * 返回文件的注释。
     *
     * @param zipFile 压缩文件。
     * @return 文件的注释
     * @throws IOException 如果发生 IO 错误
     */
    @Throws(IOException::class)
    fun getComments(zipFile: File?): List<String>? {
        if (zipFile == null) {
            return null
        }
        val comments: MutableList<String> = ArrayList()
        val zip = ZipFile(zipFile)
        val entries: Enumeration<*> = zip.entries()
        while (entries.hasMoreElements()) {
            val entry = entries.nextElement() as ZipEntry
            comments.add(entry.comment)
        }
        zip.close()
        return comments
    }
}