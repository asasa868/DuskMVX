package com.lzq.dawn.util.log

import android.util.Log
import androidx.annotation.IntDef
import androidx.annotation.IntRange
import androidx.collection.SimpleArrayMap
import com.lzq.dawn.DawnBridge
import com.lzq.dawn.util.log.LogClass.IFileWriter
import com.lzq.dawn.util.log.LogClass.IFormatter
import com.lzq.dawn.util.log.LogClass.OnConsoleOutputListener
import com.lzq.dawn.util.log.LogClass.OnFileOutputListener
import com.lzq.dawn.util.log.LogFormatter.object2String
import java.io.File
import java.io.IOException
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Collections
import java.util.Date
import java.util.Formatter
import java.util.Locale
import java.util.concurrent.Executors
import java.util.regex.Pattern

/**
 * @Name :LogUtils
 * @Time :2022/8/2 11:46
 * @Author :  Lzq
 * @Desc : log
 */
object LogUtils {
    const val V = Log.VERBOSE
    const val D = Log.DEBUG
    const val I = Log.INFO
    const val W = Log.WARN
    const val E = Log.ERROR
    const val A = Log.ASSERT
    private val T = charArrayOf('V', 'D', 'I', 'W', 'E', 'A')
    const val FILE = 0x10
    const val JSON = 0x20
    const val XML = 0x30
    private val FILE_SEP = System.getProperty("file.separator")
    val LINE_SEP = System.getProperty("line.separator")
    private const val TOP_CORNER = "┌"
    private const val MIDDLE_CORNER = "├"
    private const val LEFT_BORDER = "│ "
    private const val BOTTOM_CORNER = "└"
    private const val SIDE_DIVIDER = "────────────────────────────────────────────────────────"
    private const val MIDDLE_DIVIDER = "┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄"
    private const val TOP_BORDER = TOP_CORNER + SIDE_DIVIDER + SIDE_DIVIDER
    private const val MIDDLE_BORDER = MIDDLE_CORNER + MIDDLE_DIVIDER + MIDDLE_DIVIDER
    private const val BOTTOM_BORDER = BOTTOM_CORNER + SIDE_DIVIDER + SIDE_DIVIDER

    // fit for Chinese character
    private const val MAX_LEN = 1100
    private const val NOTHING = "log nothing"
    private const val NULL = "null"
    private const val ARGS = "args"
    private const val PLACEHOLDER = " "
    private val config = Config()
    private var simpleDateFormat: SimpleDateFormat? = null
    private val EXECUTOR = Executors.newSingleThreadExecutor()
    private val I_FORMATTER_MAP = SimpleArrayMap<Class<*>?, IFormatter<*>>()
    fun v(vararg contents: Any?) {
        log(V, config.globalTag, *contents)
    }

    fun vTag(tag: String?, vararg contents: Any?) {
        log(V, tag, *contents)
    }

    fun d(vararg contents: Any?) {
        log(D, config.globalTag, *contents)
    }

    fun dTag(tag: String?, vararg contents: Any?) {
        log(D, tag, *contents)
    }

    fun i(vararg contents: Any?) {
        log(I, config.globalTag, *contents)
    }

    fun iTag(tag: String?, vararg contents: Any?) {
        log(I, tag, *contents)
    }

    fun w(vararg contents: Any?) {
        log(W, config.globalTag, *contents)
    }

    fun wTag(tag: String?, vararg contents: Any?) {
        log(W, tag, *contents)
    }

    fun e(vararg contents: Any?) {
        log(E, config.globalTag, *contents)
    }

    fun eTag(tag: String?, vararg contents: Any?) {
        log(E, tag, *contents)
    }

    fun a(vararg contents: Any?) {
        log(A, config.globalTag, *contents)
    }

    fun aTag(tag: String?, vararg contents: Any?) {
        log(A, tag, *contents)
    }

    fun file(content: Any?) {
        log(FILE or D, config.globalTag, content)
    }

    fun file(@TYPE type: Int, content: Any?) {
        log(FILE or type, config.globalTag, content)
    }

    fun file(tag: String?, content: Any?) {
        log(FILE or D, tag, content)
    }

    fun file(@TYPE type: Int, tag: String?, content: Any?) {
        log(FILE or type, tag, content)
    }

    fun json(content: Any?) {
        log(JSON or D, config.globalTag, content)
    }

    fun json(@TYPE type: Int, content: Any?) {
        log(JSON or type, config.globalTag, content)
    }

    fun json(tag: String?, content: Any?) {
        log(JSON or D, tag, content)
    }

    fun json(@TYPE type: Int, tag: String?, content: Any?) {
        log(JSON or type, tag, content)
    }

    fun xml(content: String?) {
        log(XML or D, config.globalTag, content)
    }

    fun xml(@TYPE type: Int, content: String?) {
        log(XML or type, config.globalTag, content)
    }

    fun xml(tag: String?, content: String?) {
        log(XML or D, tag, content)
    }

    fun xml(@TYPE type: Int, tag: String?, content: String?) {
        log(XML or type, tag, content)
    }

    fun log(type: Int, tag: String?, vararg contents: Any?) {
        if (!config.isLogSwitch) {
            return
        }
        val type_low = type and 0x0f
        val type_high = type and 0xf0
        if (config.isLog2ConsoleSwitch || config.isLog2FileSwitch || type_high == FILE) {
            if (type_low < config.mConsoleFilter && type_low < config.mFileFilter) {
                return
            }
            val tagHead = processTagAndHead(tag)
            val body = processBody(type_high, *contents)
            if (config.isLog2ConsoleSwitch && type_high != FILE && type_low >= config.mConsoleFilter) {
                print2Console(type_low, tagHead.tag, tagHead.consoleHead, body)
            }
            if ((config.isLog2FileSwitch || type_high == FILE) && type_low >= config.mFileFilter) {
                EXECUTOR.execute { print2File(type_low, tagHead.tag, tagHead.fileHead + body) }
            }
        }
    }

    val currentLogFilePath: String
        get() = getCurrentLogFilePath(Date())
    val logFiles: List<File>
        get() {
            val dir = config.dir
            val logDir = File(dir)
            if (!logDir.exists()) {
                return ArrayList()
            }
            val files = logDir.listFiles { _, name -> isMatchLogFileName(name) }
            val list: MutableList<File> = mutableListOf()
            if (files != null) {
                Collections.addAll(list, *files)
            }
            return list
        }

    private fun processTagAndHead(tag: String?): TagHead {
        var tag = tag
        if (!config.mTagIsSpace && !config.isLogHeadSwitch) {
            tag = config.globalTag
        } else {
            val stackTrace = Throwable().stackTrace
            val stackIndex = 3 + config.stackOffset
            if (stackIndex >= stackTrace.size) {
                val targetElement = stackTrace[3]
                val fileName = getFileName(targetElement)
                if (config.mTagIsSpace && DawnBridge.isSpace(tag)) {
                    val index = fileName.indexOf('.') // Use proguard may not find '.'.
                    tag = if (index == -1) fileName else fileName.substring(0, index)
                }
                return TagHead(tag!!, null, ": ")
            }
            var targetElement = stackTrace[stackIndex]
            val fileName = getFileName(targetElement)
            if (config.mTagIsSpace && DawnBridge.isSpace(tag)) {
                val index = fileName.indexOf('.') // Use proguard may not find '.'.
                tag = if (index == -1) fileName else fileName.substring(0, index)
            }
            if (config.isLogHeadSwitch) {
                val tName = Thread.currentThread().name
                val head = Formatter().format(
                        "%s, %s.%s(%s:%d)",
                        tName,
                        targetElement.className,
                        targetElement.methodName,
                        fileName,
                        targetElement.lineNumber
                    ).toString()
                val fileHead = " [$head]: "
                return if (config.stackDeep <= 1) {
                    TagHead(tag!!, arrayOf(head), fileHead)
                } else {
                    val consoleHead = arrayOfNulls<String>(
                        config.stackDeep.coerceAtMost(stackTrace.size - stackIndex)
                    )
                    consoleHead[0] = head
                    val spaceLen = tName.length + 2
                    val space = Formatter().format("%" + spaceLen + "s", "").toString()
                    var i = 1
                    val len = consoleHead.size
                    while (i < len) {
                        targetElement = stackTrace[i + stackIndex]
                        consoleHead[i] = Formatter().format(
                                "%s%s.%s(%s:%d)",
                                space,
                                targetElement.className,
                                targetElement.methodName,
                                getFileName(targetElement),
                                targetElement.lineNumber
                            ).toString()
                        ++i
                    }
                    TagHead(tag!!, consoleHead, fileHead)
                }
            }
        }
        return TagHead(tag!!, null, ": ")
    }

    private fun getFileName(targetElement: StackTraceElement): String {
        val fileName = targetElement.fileName
        if (fileName != null) {
            return fileName
        }
        // If name of file is null, should add
        // "-keepattributes SourceFile,LineNumberTable" in proguard file.
        var className = targetElement.className
        val classNameInfo = className.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        if (classNameInfo.isNotEmpty()) {
            className = classNameInfo[classNameInfo.size - 1]
        }
        val index = className.indexOf('$')
        if (index != -1) {
            className = className.substring(0, index)
        }
        return "$className.java"
    }

    private fun processBody(type: Int, vararg contents: Any?): String {
        val body: String? = if (contents.size == 1) {
            formatObject(type, contents[0])
        } else {
            val sb = StringBuilder()
            var i = 0
            val len = contents.size
            while (i < len) {
                val content = contents[i]
                sb.append(ARGS).append("[").append(i).append("]").append(" = ")
                    .append(formatObject(content)).append(LINE_SEP)
                ++i
            }
            sb.toString()
        }
        return if (body!!.isEmpty()) NOTHING else body
    }

    private fun formatObject(type: Int, `object`: Any?): String? {
        if (`object` == null) {
            return NULL
        }
        if (type == JSON) {
            return object2String(`object`, JSON)
        }
        return if (type == XML) {
            object2String(`object`, XML)
        } else formatObject(`object`)
    }

    fun formatObject(`object`: Any?): String? {
        if (`object` == null) {
            return NULL
        }
        if (!I_FORMATTER_MAP.isEmpty()) {
            val iFormatter = I_FORMATTER_MAP[getClassFromObject(`object`)]
            if (iFormatter != null) {
                return iFormatter.format(`object` as Nothing)
            }
        }
        return object2String(`object`)
    }

    private fun print2Console(
        type: Int, tag: String, head: Array<String>?, msg: String
    ) {
        if (config.isSingleTagSwitch) {
            printSingleTagMsg(type, tag, processSingleTagMsg(type, tag, head, msg))
        } else {
            printBorder(type, tag, true)
            printHead(type, tag, head)
            printMsg(type, tag, msg)
            printBorder(type, tag, false)
        }
    }

    private fun printBorder(type: Int, tag: String, isTop: Boolean) {
        if (config.isLogBorderSwitch) {
            print2Console(type, tag, if (isTop) TOP_BORDER else BOTTOM_BORDER)
        }
    }

    private fun printHead(type: Int, tag: String, head: Array<String>?) {
        if (head != null) {
            for (aHead in head) {
                print2Console(type, tag, if (config.isLogBorderSwitch) LEFT_BORDER + aHead else aHead)
            }
            if (config.isLogBorderSwitch) {
                print2Console(type, tag, MIDDLE_BORDER)
            }
        }
    }

    private fun printMsg(type: Int, tag: String, msg: String) {
        val len = msg.length
        val countOfSub = len / MAX_LEN
        if (countOfSub > 0) {
            var index = 0
            for (i in 0 until countOfSub) {
                printSubMsg(type, tag, msg.substring(index, index + MAX_LEN))
                index += MAX_LEN
            }
            if (index != len) {
                printSubMsg(type, tag, msg.substring(index, len))
            }
        } else {
            printSubMsg(type, tag, msg)
        }
    }

    private fun printSubMsg(type: Int, tag: String, msg: String) {
        if (!config.isLogBorderSwitch) {
            print2Console(type, tag, msg)
            return
        }
        val lines = msg.split(LINE_SEP.toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        for (line in lines) {
            print2Console(type, tag, LEFT_BORDER + line)
        }
    }

    private fun processSingleTagMsg(
        type: Int, tag: String, head: Array<String>?, msg: String
    ): String {
        val sb = StringBuilder()
        if (config.isLogBorderSwitch) {
            sb.append(PLACEHOLDER).append(LINE_SEP)
            sb.append(TOP_BORDER).append(LINE_SEP)
            if (head != null) {
                for (aHead in head) {
                    sb.append(LEFT_BORDER).append(aHead).append(LINE_SEP)
                }
                sb.append(MIDDLE_BORDER).append(LINE_SEP)
            }
            for (line in msg.split(LINE_SEP.toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()) {
                sb.append(LEFT_BORDER).append(line).append(LINE_SEP)
            }
            sb.append(BOTTOM_BORDER)
        } else {
            if (head != null) {
                sb.append(PLACEHOLDER).append(LINE_SEP)
                for (aHead in head) {
                    sb.append(aHead).append(LINE_SEP)
                }
            }
            sb.append(msg)
        }
        return sb.toString()
    }

    private fun printSingleTagMsg(type: Int, tag: String, msg: String) {
        val len = msg.length
        val countOfSub =
            if (config.isLogBorderSwitch) (len - BOTTOM_BORDER.length) / MAX_LEN else len / MAX_LEN
        if (countOfSub > 0) {
            if (config.isLogBorderSwitch) {
                print2Console(type, tag, msg.substring(0, MAX_LEN) + LINE_SEP + BOTTOM_BORDER)
                var index = MAX_LEN
                for (i in 1 until countOfSub) {
                    print2Console(
                        type,
                        tag,
                        PLACEHOLDER + LINE_SEP + TOP_BORDER + LINE_SEP + LEFT_BORDER + msg.substring(
                            index,
                            index + MAX_LEN
                        ) + LINE_SEP + BOTTOM_BORDER
                    )
                    index += MAX_LEN
                }
                if (index != len - BOTTOM_BORDER.length) {
                    print2Console(
                        type,
                        tag,
                        PLACEHOLDER + LINE_SEP + TOP_BORDER + LINE_SEP + LEFT_BORDER + msg.substring(
                            index,
                            len
                        )
                    )
                }
            } else {
                print2Console(type, tag, msg.substring(0, MAX_LEN))
                var index = MAX_LEN
                for (i in 1 until countOfSub) {
                    print2Console(
                        type, tag, PLACEHOLDER + LINE_SEP + msg.substring(index, index + MAX_LEN)
                    )
                    index += MAX_LEN
                }
                if (index != len) {
                    print2Console(type, tag, PLACEHOLDER + LINE_SEP + msg.substring(index, len))
                }
            }
        } else {
            print2Console(type, tag, msg)
        }
    }

    private fun print2Console(type: Int, tag: String, msg: String) {
        Log.println(type, tag, msg)
        if (config.mOnConsoleOutputListener != null) {
            config.mOnConsoleOutputListener!!.onConsoleOutput(type, tag, msg)
        }
    }

    private fun print2File(type: Int, tag: String, msg: String) {
        val d = Date()
        val format = sdf!!.format(d)
        val date = format.substring(0, 10)
        val currentLogFilePath = getCurrentLogFilePath(d)
        if (!createOrExistsFile(currentLogFilePath, date)) {
            Log.e("LogUtils", "create $currentLogFilePath failed!")
            return
        }
        val time = format.substring(11)
        val content = time + T[type - V] + "/" + tag + msg + LINE_SEP
        input2File(currentLogFilePath, content)
    }

    private fun getCurrentLogFilePath(d: Date): String {
        val format = sdf!!.format(d)
        val date = format.substring(0, 10)
        return (config.dir + config.filePrefix + "_" + date + "_" + config.processName + config.fileExtension)
    }

    private val sdf: SimpleDateFormat?
        get() {
            if (simpleDateFormat == null) {
                simpleDateFormat = SimpleDateFormat("yyyy_MM_dd HH:mm:ss.SSS ", Locale.getDefault())
            }
            return simpleDateFormat
        }

    private fun createOrExistsFile(filePath: String, date: String): Boolean {
        val file = File(filePath)
        if (file.exists()) {
            return file.isFile
        }
        return if (!DawnBridge.createOrExistsDir(file.parentFile)) {
            false
        } else try {
            deleteDueLogs(filePath, date)
            val isCreate = file.createNewFile()
            if (isCreate) {
                printDeviceInfo(filePath, date)
            }
            isCreate
        } catch (e: IOException) {
            e.printStackTrace()
            false
        }
    }

    private fun deleteDueLogs(filePath: String, date: String) {
        if (config.saveDays <= 0) {
            return
        }
        val file = File(filePath)
        val parentFile = file.parentFile
        val files = parentFile?.listFiles { _, name -> isMatchLogFileName(name) }
        if (files.isNullOrEmpty()) {
            return
        }
        val sdf = SimpleDateFormat("yyyy_MM_dd", Locale.getDefault())
        try {
            val dueMillis = sdf.parse(date).time - config.saveDays * 86400000L
            for (aFile in files) {
                val name = aFile.name
                val l = name.length
                val logDay = findDate(name)
                if (sdf.parse(logDay).time <= dueMillis) {
                    EXECUTOR.execute {
                        val delete = aFile.delete()
                        if (!delete) {
                            Log.e("LogUtils", "delete $aFile failed!")
                        }
                    }
                }
            }
        } catch (e: ParseException) {
            e.printStackTrace()
        }
    }

    private fun isMatchLogFileName(name: String): Boolean {
        return name.matches(("^" + config.filePrefix + "_[0-9]{4}_[0-9]{2}_[0-9]{2}_.*$").toRegex())
    }

    private fun findDate(str: String): String {
        val pattern = Pattern.compile("[0-9]{4}_[0-9]{2}_[0-9]{2}")
        val matcher = pattern.matcher(str)
        return if (matcher.find()) {
            matcher.group()
        } else ""
    }

    private fun printDeviceInfo(filePath: String, date: String) {
        config.mFileHead.addFirst("Date of Log", date)
        input2File(filePath, config.mFileHead.toString())
    }

    private fun input2File(filePath: String, input: String) {
        if (config.mFileWriter == null) {
            DawnBridge.writeFileFromString(filePath, input, true)
        } else {
            config.mFileWriter!!.write(filePath, input)
        }
        if (config.mOnFileOutputListener != null) {
            config.mOnFileOutputListener!!.onFileOutput(filePath, input)
        }
    }

    private fun <T> getTypeClassFromParadigm(formatter: IFormatter<T>): Class<*>? {
        val genericInterfaces = formatter.javaClass.genericInterfaces
        var type: Type
        type = if (genericInterfaces.size == 1) {
            genericInterfaces[0]
        } else {
            formatter.javaClass.genericSuperclass
        }
        type = (type as ParameterizedType).actualTypeArguments[0]
        while (type is ParameterizedType) {
            type = type.rawType
        }
        var className = type.toString()
        if (className.startsWith("class ")) {
            className = className.substring(6)
        } else if (className.startsWith("interface ")) {
            className = className.substring(10)
        }
        try {
            return Class.forName(className)
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        }
        return null
    }

    private fun getClassFromObject(obj: Any): Class<*> {
        val objClass: Class<*> = obj.javaClass
        if (objClass.isAnonymousClass || objClass.isSynthetic) {
            val genericInterfaces = objClass.genericInterfaces
            var className: String
            if (genericInterfaces.size == 1) { // interface
                var type = genericInterfaces[0]
                while (type is ParameterizedType) {
                    type = type.rawType
                }
                className = type.toString()
            } else { // abstract class or lambda
                var type = objClass.genericSuperclass
                while (type is ParameterizedType) {
                    type = type.rawType
                }
                className = type.toString()
            }
            if (className.startsWith("class ")) {
                className = className.substring(6)
            } else if (className.startsWith("interface ")) {
                className = className.substring(10)
            }
            try {
                return Class.forName(className)
            } catch (e: ClassNotFoundException) {
                e.printStackTrace()
            }
        }
        return objClass
    }

    @IntDef(V, D, I, W, E, A)
    @Retention(AnnotationRetention.SOURCE)
    annotation class TYPE
     class Config {
        // 日志的默认存储目录
        private var defaultDir: String? = null

        // 日志的存放目录。
        private var mDir: String? = null

        // 日志的文件前缀。
        var filePrefix = "DawnBridge"
            private set

        // 日志的文件扩展名。
        var fileExtension = ".txt"
            private set

        // 日志的切换。
        var isLogSwitch = true
            private set

        // logcat 的日志开关。
        var isLog2ConsoleSwitch = true
            private set

        // 日志的全局标签。
        private var mGlobalTag = ""

        // 全局标签是空格。
        var mTagIsSpace = true

        // 日志的头部开关。
        var isLogHeadSwitch = true
            private set

        // 文件的日志切换。
        var isLog2FileSwitch = false
            private set

        // 日志的边界开关。
        var isLogBorderSwitch = true
            private set

        // 日志的单个标签。
        var isSingleTagSwitch = true
            private set

        // 控制台的日志过滤器。
        var mConsoleFilter = V

        // 文件的日志过滤器。
        var mFileFilter = V

        // 堆栈的深度日志。
        var stackDeep = 1
            private set

        // 堆栈的日志偏移量。
        var stackOffset = 0
            private set

        // 日志的保存天数。
        var saveDays = -1
            private set
        private val mProcessName = DawnBridge.currentProcessName
        var mFileWriter: IFileWriter? = null
        var mOnConsoleOutputListener: OnConsoleOutputListener? = null
        var mOnFileOutputListener: OnFileOutputListener? = null
        val mFileHead = DawnBridge.FileHead("Log")

        init {
            defaultDir = if (DawnBridge.isSDCardEnableByEnvironment && DawnBridge.app.getExternalFilesDir(null) != null
            ) {
                DawnBridge.app.getExternalFilesDir(null).toString() + FILE_SEP + "log" + FILE_SEP
            } else {
                DawnBridge.app.filesDir.toString() + FILE_SEP + "log" + FILE_SEP
            }
        }

        fun setLogSwitch(logSwitch: Boolean): Config {
            isLogSwitch = logSwitch
            return this
        }

        fun setConsoleSwitch(consoleSwitch: Boolean): Config {
            isLog2ConsoleSwitch = consoleSwitch
            return this
        }

        fun setGlobalTag(tag: String): Config {
            if (DawnBridge.isSpace(tag)) {
                mGlobalTag = ""
                mTagIsSpace = true
            } else {
                mGlobalTag = tag
                mTagIsSpace = false
            }
            return this
        }

        fun setLogHeadSwitch(logHeadSwitch: Boolean): Config {
            isLogHeadSwitch = logHeadSwitch
            return this
        }

        fun setLog2FileSwitch(log2FileSwitch: Boolean): Config {
            isLog2FileSwitch = log2FileSwitch
            return this
        }

        fun setDir(dir: String): Config {
            mDir = if (DawnBridge.isSpace(dir)) {
                null
            } else {
                if (dir.endsWith(FILE_SEP)) dir else dir + FILE_SEP
            }
            return this
        }

        fun setDir(dir: File?): Config {
            mDir = if (dir == null) null else dir.absolutePath + FILE_SEP
            return this
        }

        fun setFilePrefix(filePrefix: String): Config {
            if (DawnBridge.isSpace(filePrefix)) {
                this.filePrefix = "util"
            } else {
                this.filePrefix = filePrefix
            }
            return this
        }

        fun setFileExtension(fileExtension: String): Config {
            if (DawnBridge.isSpace(fileExtension)) {
                this.fileExtension = ".txt"
            } else {
                if (fileExtension.startsWith(".")) {
                    this.fileExtension = fileExtension
                } else {
                    this.fileExtension = ".$fileExtension"
                }
            }
            return this
        }

        fun setBorderSwitch(borderSwitch: Boolean): Config {
            isLogBorderSwitch = borderSwitch
            return this
        }

        fun setSingleTagSwitch(singleTagSwitch: Boolean): Config {
            isSingleTagSwitch = singleTagSwitch
            return this
        }

        fun setConsoleFilter(@TYPE consoleFilter: Int): Config {
            mConsoleFilter = consoleFilter
            return this
        }

        fun setFileFilter(@TYPE fileFilter: Int): Config {
            mFileFilter = fileFilter
            return this
        }

        fun setStackDeep(@IntRange(from = 1) stackDeep: Int): Config {
            this.stackDeep = stackDeep
            return this
        }

        fun setStackOffset(@IntRange(from = 0) stackOffset: Int): Config {
            this.stackOffset = stackOffset
            return this
        }

        fun setSaveDays(@IntRange(from = 1) saveDays: Int): Config {
            this.saveDays = saveDays
            return this
        }

        fun <T> addFormatter(iFormatter: IFormatter<T>?): Config {
            if (iFormatter != null) {
                I_FORMATTER_MAP.put(getTypeClassFromParadigm(iFormatter), iFormatter)
            }
            return this
        }

        fun setFileWriter(fileWriter: IFileWriter?): Config {
            mFileWriter = fileWriter
            return this
        }

        fun setOnConsoleOutputListener(listener: OnConsoleOutputListener?): Config {
            mOnConsoleOutputListener = listener
            return this
        }

        fun setOnFileOutputListener(listener: OnFileOutputListener?): Config {
            mOnFileOutputListener = listener
            return this
        }

        fun addFileExtraHead(fileExtraHead: Map<String, String>?): Config {
            mFileHead.append(fileExtraHead)
            return this
        }

        fun addFileExtraHead(key: String, value: String): Config {
            mFileHead.append(key, value)
            return this
        }

        val processName: String
            get() = mProcessName.replace(":", "_")
        val dir: String?
            get() = if (mDir == null) defaultDir else mDir
        val globalTag: String
            get() = if (DawnBridge.isSpace(mGlobalTag)) {
                ""
            } else mGlobalTag
        private val consoleFilter: Char
            get() = T[mConsoleFilter - V]
        private val fileFilter: Char
            get() = T[mFileFilter - V]

        fun haveSetOnConsoleOutputListener(): Boolean {
            return mOnConsoleOutputListener != null
        }

        fun haveSetOnFileOutputListener(): Boolean {
            return mOnFileOutputListener != null
        }

        override fun toString(): String {
            return ("process: " + processName + LINE_SEP + "logSwitch: " + isLogSwitch + LINE_SEP + "consoleSwitch: " + isLog2ConsoleSwitch + LINE_SEP + "tag: " + (if (globalTag == "") "null" else globalTag) + LINE_SEP + "headSwitch: " + isLogHeadSwitch + LINE_SEP + "fileSwitch: " + isLog2FileSwitch + LINE_SEP + "dir: " + dir + LINE_SEP + "filePrefix: " + filePrefix + LINE_SEP + "borderSwitch: " + isLogBorderSwitch + LINE_SEP + "singleTagSwitch: " + isSingleTagSwitch + LINE_SEP + "consoleFilter: " + consoleFilter + LINE_SEP + "fileFilter: " + fileFilter + LINE_SEP + "stackDeep: " + stackDeep + LINE_SEP + "stackOffset: " + stackOffset + LINE_SEP + "saveDays: " + saveDays + LINE_SEP + "formatter: " + I_FORMATTER_MAP + LINE_SEP + "fileWriter: " + mFileWriter + LINE_SEP + "onConsoleOutputListener: " + mOnConsoleOutputListener + LINE_SEP + "onFileOutputListener: " + mOnFileOutputListener + LINE_SEP + "fileExtraHeader: " + mFileHead.appended)
        }
    }
}