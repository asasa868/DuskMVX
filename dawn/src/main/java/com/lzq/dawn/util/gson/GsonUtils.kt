package com.lzq.dawn.util.gson

import android.text.TextUtils
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.io.Reader
import java.lang.reflect.Type
import java.util.concurrent.ConcurrentHashMap

/**
 * @Name :GsonUtils
 * @Time :2022/8/1 15:23
 * @Author :  Lzq
 * @Desc : gson
 */
object GsonUtils {
    private const val KEY_DEFAULT = "defaultGson"
    private const val KEY_DELEGATE = "delegateGson"
    private const val KEY_LOG_UTILS = "logUtilsGson"
    private val GSONS: MutableMap<String, Gson?> = ConcurrentHashMap()

    /**
     * 设置 [Gson] 的委托。
     *
     * @param delegate [Gson] 的代表。
     */
    fun setGsonDelegate(delegate: Gson?) {
        if (delegate == null) {
            return
        }
        setGson(KEY_DELEGATE, delegate)
    }

    /**
     * 用键设置 [Gson]。
     *
     * @param key  key.
     * @param gson [Gson].
     */
    fun setGson(key: String, gson: Gson?) {
        if (TextUtils.isEmpty(key) || gson == null) {
            return
        }
        GSONS[key] = gson
    }

    /**
     * 使用键返回 [Gson]。
     *
     * @param key key.
     * @return 带键的 [Gson]
     */
    fun getGson(key: String): Gson? {
        return GSONS[key]
    }

    val gson: Gson?
        get() {
            val gsonDelegate = GSONS[KEY_DELEGATE]
            if (gsonDelegate != null) {
                return gsonDelegate
            }
            var gsonDefault = GSONS[KEY_DEFAULT]
            if (gsonDefault == null) {
                gsonDefault = createGson()
                GSONS[KEY_DEFAULT] = gsonDefault
            }
            return gsonDefault
        }

    /**
     * 将对象序列化为 json。
     *
     * @param object 要序列化的对象。
     * @return 对象序列化为 json。
     */
    @JvmStatic
    fun toJson(`object`: Any?): String {
        return toJson(gson!!, `object`)
    }

    /**
     * 将对象序列化为 json。
     *
     * @param src       要序列化的对象
     * @param typeOfSrc src 的具体泛化类型。
     * @return 对象序列化为 json。
     */
    fun toJson(src: Any?, typeOfSrc: Type): String {
        return toJson(gson!!, src, typeOfSrc)
    }

    /**
     * 将对象序列化为 json。
     *
     * @param gson   gson.
     * @param object 要序列化的对象。
     * @return 对象序列化为 json。
     */
    fun toJson(gson: Gson, `object`: Any?): String {
        return gson.toJson(`object`)
    }

    /**
     * 将对象序列化为 json。
     *
     * @param gson      gson.
     * @param src       要序列化的对象。
     * @param typeOfSrc src 的具体泛化类型。
     * @return 对象序列化为 json。
     */
    fun toJson(gson: Gson, src: Any?, typeOfSrc: Type): String {
        return gson.toJson(src, typeOfSrc)
    }

    /**
     * 将 [String] 转换为给定类型。
     *
     * @param json 要转换的 json。
     * @param type 类型 json 将被转换为。
     * @return 类型的实例
     */
    fun <T> fromJson(json: String?, type: Class<T>): T {
        return fromJson(gson!!, json, type)
    }

    /**
     * 将 [String] 转换为给定类型。
     *
     * @param json 要转换的 json。
     * @param type 类型 json 将被转换为。
     * @return 类型的实例
     */
    @JvmStatic
    fun <T> fromJson(json: String?, type: Type): T {
        return fromJson(gson!!, json, type)
    }

    /**
     * 将 [Reader] 转换为给定类型。
     *
     * @param reader 要转换的reader
     * @param type   类型 json 将被转换为。
     * @return 类型的实例
     */
    fun <T> fromJson(reader: Reader, type: Class<T>): T {
        return fromJson(gson!!, reader, type)
    }

    /**
     * 将 [Reader] 转换为给定类型。
     *
     * @param reader 要转换的reader
     * @param type   类型 json 将被转换为。
     * @return 类型的实例
     */
    fun <T> fromJson(reader: Reader, type: Type): T {
        return fromJson(gson!!, reader, type)
    }

    /**
     * 将 [String] 转换为给定类型。
     *
     * @param gson gson.
     * @param json 要转换的 json。
     * @param type 类型 json 将被转换为。
     * @return 类型的实例
     */
    fun <T> fromJson(gson: Gson, json: String?, type: Class<T>): T {
        return gson.fromJson(json, type)
    }

    /**
     * 将 [String] 转换为给定类型。
     *
     * @param gson The gson.
     * @param json 要转换的 json。
     * @param type 类型 json 将被转换为。
     * @return 类型的实例
     */
    fun <T> fromJson(gson: Gson, json: String?, type: Type): T {
        return gson.fromJson(json, type)
    }

    /**
     * 将 [Reader] 转换为给定类型。
     *
     * @param gson   gson.
     * @param reader 要转换的reader
     * @param type   类型 json 将被转换为。
     * @return 类型的实例
     */
    fun <T> fromJson(gson: Gson, reader: Reader?, type: Class<T>): T {
        return gson.fromJson(reader, type)
    }

    /**
     * 将 [Reader] 转换为给定类型。
     *
     * @param gson   gson.
     * @param reader 要转换的reader
     * @param type   类型 json 将被转换为。
     * @return 类型的实例
     */
    fun <T> fromJson(gson: Gson, reader: Reader?, type: Type): T {
        return gson.fromJson(reader, type)
    }

    /**
     * 使用 `type` 返回 [List] 的类型。
     *
     * @param type type.
     * @return [List] 的类型与 `type`
     */
    fun getListType(type: Type): Type {
        return TypeToken.getParameterized(MutableList::class.java, type).type
    }

    /**
     * 使用 `type` 返回 [Set] 的类型。
     *
     * @param type type.
     * @return 具有 `类型` 的 [Set] 的类型
     */
    fun getSetType(type: Type): Type {
        return TypeToken.getParameterized(MutableSet::class.java, type).type
    }

    /**
     * 使用 `keyType` 和 `valueType` 返回map类型。
     *
     * @param keyType   key的类型
     * @param valueType value的类型
     * @return `keyType` 和 `valueType` 的映射类型
     */
    fun getMapType(keyType: Type, valueType: Type): Type {
        return TypeToken.getParameterized(MutableMap::class.java, keyType, valueType).type
    }

    /**
     * 返回具有 `type` 的数组类型。
     *
     * @param type type.
     * @return `type` 的map类型
     */
    fun getArrayType(type: Type): Type {
        return TypeToken.getArray(type).type
    }

    /**
     * 使用 `typeArguments` 返回 `rawType` 的类型。
     *
     * @param rawType       原始类型。
     * @param typeArguments 参数的类型。
     * @return `type` 的map类型
     */
    fun getType(rawType: Type, vararg typeArguments: Type): Type {
        return TypeToken.getParameterized(rawType, *typeArguments).type
    }

    @JvmStatic
    val gson4LogUtils: Gson?
        get() {
            var gson4LogUtils = GSONS[KEY_LOG_UTILS]
            if (gson4LogUtils == null) {
                gson4LogUtils = GsonBuilder().setPrettyPrinting().serializeNulls().create()
                GSONS[KEY_LOG_UTILS] = gson4LogUtils
            }
            return gson4LogUtils
        }

    private fun createGson(): Gson {
        return GsonBuilder().serializeNulls().disableHtmlEscaping().create()
    }
}