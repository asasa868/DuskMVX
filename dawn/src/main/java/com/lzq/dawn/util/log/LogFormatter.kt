package com.lzq.dawn.util.log

import android.content.ClipData
import android.content.Intent
import android.os.Build
import android.os.Bundle
import com.lzq.dawn.DawnBridge
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.StringReader
import java.io.StringWriter
import javax.xml.transform.OutputKeys
import javax.xml.transform.Source
import javax.xml.transform.TransformerFactory
import javax.xml.transform.stream.StreamResult
import javax.xml.transform.stream.StreamSource

/**
 * @Name :LogFormatter
 * @Time :2022/8/2 15:24
 * @Author :  Lzq
 * @Desc :
 */
object LogFormatter {
    @JvmStatic
    @JvmOverloads
    fun object2String(`object`: Any, type: Int = -1): String {
        if (`object`.javaClass.isArray) {
            return array2String(`object`)
        }
        if (`object` is Throwable) {
            return DawnBridge.getFullStackTrace(`object`)
        }
        if (`object` is Bundle) {
            return bundle2String(`object`)
        }
        if (`object` is Intent) {
            return intent2String(`object`)
        }
        if (type == LogUtils.JSON) {
            return object2Json(`object`)
        } else if (type == LogUtils.XML) {
            return formatXml(`object`.toString())
        }
        return `object`.toString()
    }

    private fun bundle2String(bundle: Bundle): String {
        val iterator: Iterator<String> = bundle.keySet().iterator()
        if (!iterator.hasNext()) {
            return "Bundle {}"
        }
        val sb = StringBuilder(128)
        sb.append("Bundle { ")
        while (true) {
            val key = iterator.next()
            val value = bundle[key]
            sb.append(key).append('=')
            if (value is Bundle) {
                sb.append(if (value === bundle) "(this Bundle)" else bundle2String(value))
            } else {
                sb.append(LogUtils.formatObject(value))
            }
            if (!iterator.hasNext()) {
                return sb.append(" }").toString()
            }
            sb.append(',').append(' ')
        }
    }

    private fun intent2String(intent: Intent): String {
        val sb = StringBuilder(128)
        sb.append("Intent { ")
        var first = true
        val mAction = intent.action
        if (mAction != null) {
            sb.append("act=").append(mAction)
            first = false
        }
        val mCategories = intent.categories
        if (mCategories != null) {
            if (!first) {
                sb.append(' ')
            }
            first = false
            sb.append("cat=[")
            var firstCategory = true
            for (c in mCategories) {
                if (!firstCategory) {
                    sb.append(',')
                }
                sb.append(c)
                firstCategory = false
            }
            sb.append("]")
        }
        val mData = intent.data
        if (mData != null) {
            if (!first) {
                sb.append(' ')
            }
            first = false
            sb.append("dat=").append(mData)
        }
        val mType = intent.type
        if (mType != null) {
            if (!first) {
                sb.append(' ')
            }
            first = false
            sb.append("typ=").append(mType)
        }
        val mFlags = intent.flags
        if (mFlags != 0) {
            if (!first) {
                sb.append(' ')
            }
            first = false
            sb.append("flg=0x").append(Integer.toHexString(mFlags))
        }
        val mPackage = intent.getPackage()
        if (mPackage != null) {
            if (!first) {
                sb.append(' ')
            }
            first = false
            sb.append("pkg=").append(mPackage)
        }
        val mComponent = intent.component
        if (mComponent != null) {
            if (!first) {
                sb.append(' ')
            }
            first = false
            sb.append("cmp=").append(mComponent.flattenToShortString())
        }
        val mSourceBounds = intent.sourceBounds
        if (mSourceBounds != null) {
            if (!first) {
                sb.append(' ')
            }
            first = false
            sb.append("bnds=").append(mSourceBounds.toShortString())
        }
        val mClipData = intent.clipData
        if (mClipData != null) {
            if (!first) {
                sb.append(' ')
            }
            first = false
            clipData2String(mClipData, sb)
        }
        val mExtras = intent.extras
        if (mExtras != null) {
            if (!first) {
                sb.append(' ')
            }
            first = false
            sb.append("extras={")
            sb.append(bundle2String(mExtras))
            sb.append('}')
        }
        val mSelector = intent.selector
        if (mSelector != null) {
            if (!first) {
                sb.append(' ')
            }
            sb.append("sel={")
            sb.append(if (mSelector === intent) "(this Intent)" else intent2String(mSelector))
            sb.append("}")
        }
        sb.append(" }")
        return sb.toString()
    }

    private fun clipData2String(clipData: ClipData, sb: StringBuilder) {
        val item = clipData.getItemAt(0)
        if (item == null) {
            sb.append("ClipData.Item {}")
            return
        }
        sb.append("ClipData.Item { ")
        val mHtmlText = item.htmlText
        if (mHtmlText != null) {
            sb.append("H:")
            sb.append(mHtmlText)
            sb.append("}")
            return
        }
        val mText = item.text
        if (mText != null) {
            sb.append("T:")
            sb.append(mText)
            sb.append("}")
            return
        }
        val uri = item.uri
        if (uri != null) {
            sb.append("U:").append(uri)
            sb.append("}")
            return
        }
        val intent = item.intent
        if (intent != null) {
            sb.append("I:")
            sb.append(intent2String(intent))
            sb.append("}")
            return
        }
        sb.append("NULL")
        sb.append("}")
    }

    private fun object2Json(`object`: Any): String {
        return try {
            DawnBridge.gson4LogUtils?.toJson(`object`)?:""
        } catch (t: Throwable) {
            `object`.toString()
        }
    }

    private fun formatJson(json: String): String {
        try {
            var i = 0
            val len = json.length
            while (i < len) {
                val c = json[i]
                if (c == '{') {
                    return JSONObject(json).toString(2)
                } else if (c == '[') {
                    return JSONArray(json).toString(2)
                } else if (!Character.isWhitespace(c)) {
                    return json
                }
                i++
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return json
    }

    private fun formatXml(xmlStr: String): String {
        var xml = xmlStr
        try {
            val xmlInput: Source = StreamSource(StringReader(xml))
            val xmlOutput = StreamResult(StringWriter())
            val transformer = TransformerFactory.newInstance().newTransformer()
            transformer.setOutputProperty(OutputKeys.INDENT, "yes")
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2")
            transformer.transform(xmlInput, xmlOutput)
            xml = xmlOutput.writer.toString().replaceFirst(">".toRegex(), ">" + LogUtils.LINE_SEP)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return xml
    }

    private fun array2String(`object`: Any): String {
        if (`object` is Array<*> && `object`.isArrayOf<Any>()) {
            return `object`.contentDeepToString()
        } else if (`object` is BooleanArray) {
            return `object`.contentToString()
        } else if (`object` is ByteArray) {
            return `object`.contentToString()
        } else if (`object` is CharArray) {
            return `object`.contentToString()
        } else if (`object` is DoubleArray) {
            return `object`.contentToString()
        } else if (`object` is FloatArray) {
            return `object`.contentToString()
        } else if (`object` is IntArray) {
            return `object`.contentToString()
        } else if (`object` is LongArray) {
            return `object`.contentToString()
        } else if (`object` is ShortArray) {
            return `object`.contentToString()
        }
        throw IllegalArgumentException("Array has incompatible type: " + `object`.javaClass)
    }
}