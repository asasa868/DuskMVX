package com.lzq.dawn.util.gson;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.Reader;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Name :GsonUtils
 * @Time :2022/8/1 15:23
 * @Author :  Lzq
 * @Desc : gson
 */
public final class GsonUtils {

    private static final String KEY_DEFAULT = "defaultGson";
    private static final String KEY_DELEGATE = "delegateGson";
    private static final String KEY_LOG_UTILS = "logUtilsGson";

    private static final Map<String, Gson> GSONS = new ConcurrentHashMap<>();

    private GsonUtils() {
    }


    /**
     * 设置 {@link Gson} 的委托。
     *
     * @param delegate {@link Gson} 的代表。
     */
    public static void setGsonDelegate(Gson delegate) {
        if (delegate == null) {
            return;
        }
        setGson(KEY_DELEGATE, delegate);
    }

    /**
     * 用键设置 {@link Gson}。
     *
     * @param key  key.
     * @param gson {@link Gson}.
     */
    public static void setGson(final String key, final Gson gson) {
        if (TextUtils.isEmpty(key) || gson == null) {
            return;
        }
        GSONS.put(key, gson);
    }

    /**
     * 使用键返回 {@link Gson}。
     *
     * @param key key.
     * @return 带键的 {@link Gson}
     */
    public static Gson getGson(final String key) {
        return GSONS.get(key);
    }

    public static Gson getGson() {
        Gson gsonDelegate = GSONS.get(KEY_DELEGATE);
        if (gsonDelegate != null) {
            return gsonDelegate;
        }
        Gson gsonDefault = GSONS.get(KEY_DEFAULT);
        if (gsonDefault == null) {
            gsonDefault = createGson();
            GSONS.put(KEY_DEFAULT, gsonDefault);
        }
        return gsonDefault;
    }

    /**
     * 将对象序列化为 json。
     *
     * @param object 要序列化的对象。
     * @return 对象序列化为 json。
     */
    public static String toJson(final Object object) {
        return toJson(getGson(), object);
    }

    /**
     * 将对象序列化为 json。
     *
     * @param src       要序列化的对象
     * @param typeOfSrc src 的具体泛化类型。
     * @return 对象序列化为 json。
     */
    public static String toJson(final Object src, @NonNull final Type typeOfSrc) {
        return toJson(getGson(), src, typeOfSrc);
    }

    /**
     * 将对象序列化为 json。
     *
     * @param gson   gson.
     * @param object 要序列化的对象。
     * @return 对象序列化为 json。
     */
    public static String toJson(@NonNull final Gson gson, final Object object) {
        return gson.toJson(object);
    }

    /**
     * 将对象序列化为 json。
     *
     * @param gson      gson.
     * @param src       要序列化的对象。
     * @param typeOfSrc src 的具体泛化类型。
     * @return 对象序列化为 json。
     */
    public static String toJson(@NonNull final Gson gson, final Object src, @NonNull final Type typeOfSrc) {
        return gson.toJson(src, typeOfSrc);
    }

    /**
     * 将 {@link String} 转换为给定类型。
     *
     * @param json 要转换的 json。
     * @param type 类型 json 将被转换为。
     * @return 类型的实例
     */
    public static <T> T fromJson(final String json, @NonNull final Class<T> type) {
        return fromJson(getGson(), json, type);
    }

    /**
     * 将 {@link String} 转换为给定类型。
     *
     * @param json 要转换的 json。
     * @param type 类型 json 将被转换为。
     * @return 类型的实例
     */
    public static <T> T fromJson(final String json, @NonNull final Type type) {
        return fromJson(getGson(), json, type);
    }

    /**
     * 将 {@link Reader} 转换为给定类型。
     *
     * @param reader 要转换的reader
     * @param type   类型 json 将被转换为。
     * @return 类型的实例
     */
    public static <T> T fromJson(@NonNull final Reader reader, @NonNull final Class<T> type) {
        return fromJson(getGson(), reader, type);
    }

    /**
     * 将 {@link Reader} 转换为给定类型。
     *
     * @param reader 要转换的reader
     * @param type   类型 json 将被转换为。
     * @return 类型的实例
     */
    public static <T> T fromJson(@NonNull final Reader reader, @NonNull final Type type) {
        return fromJson(getGson(), reader, type);
    }

    /**
     * 将 {@link String} 转换为给定类型。
     *
     * @param gson gson.
     * @param json 要转换的 json。
     * @param type 类型 json 将被转换为。
     * @return 类型的实例
     */
    public static <T> T fromJson(@NonNull final Gson gson, final String json, @NonNull final Class<T> type) {
        return gson.fromJson(json, type);
    }

    /**
     * 将 {@link String} 转换为给定类型。
     *
     * @param gson The gson.
     * @param json 要转换的 json。
     * @param type 类型 json 将被转换为。
     * @return 类型的实例
     */
    public static <T> T fromJson(@NonNull final Gson gson, final String json, @NonNull final Type type) {
        return gson.fromJson(json, type);
    }

    /**
     * 将 {@link Reader} 转换为给定类型。
     *
     * @param gson   gson.
     * @param reader 要转换的reader
     * @param type   类型 json 将被转换为。
     * @return 类型的实例
     */
    public static <T> T fromJson(@NonNull final Gson gson, final Reader reader, @NonNull final Class<T> type) {
        return gson.fromJson(reader, type);
    }

    /**
     * 将 {@link Reader} 转换为给定类型。
     *
     * @param gson   gson.
     * @param reader 要转换的reader
     * @param type   类型 json 将被转换为。
     * @return 类型的实例
     */
    public static <T> T fromJson(@NonNull final Gson gson, final Reader reader, @NonNull final Type type) {
        return gson.fromJson(reader, type);
    }

    /**
     * 使用 {@code type} 返回 {@link List} 的类型。
     *
     * @param type type.
     * @return {@link List} 的类型与 {@code type}
     */
    public static Type getListType(@NonNull final Type type) {
        return TypeToken.getParameterized(List.class, type).getType();
    }

    /**
     * 使用 {@code type} 返回 {@link Set} 的类型。
     *
     * @param type type.
     * @return 具有 {@code 类型} 的 {@link Set} 的类型
     */
    public static Type getSetType(@NonNull final Type type) {
        return TypeToken.getParameterized(Set.class, type).getType();
    }

    /**
     * 使用 {@code keyType} 和 {@code valueType} 返回map类型。
     *
     * @param keyType   key的类型
     * @param valueType value的类型
     * @return {@code keyType} 和 {@code valueType} 的映射类型
     */
    public static Type getMapType(@NonNull final Type keyType, @NonNull final Type valueType) {
        return TypeToken.getParameterized(Map.class, keyType, valueType).getType();
    }

    /**
     * 返回具有 {@code type} 的数组类型。
     *
     * @param type type.
     * @return {@code type} 的map类型
     */
    public static Type getArrayType(@NonNull final Type type) {
        return TypeToken.getArray(type).getType();
    }

    /**
     * 使用 {@code typeArguments} 返回 {@code rawType} 的类型。
     *
     * @param rawType       原始类型。
     * @param typeArguments 参数的类型。
     * @return {@code type} 的map类型
     */
    public static Type getType(@NonNull final Type rawType, @NonNull final Type... typeArguments) {
        return TypeToken.getParameterized(rawType, typeArguments).getType();
    }


    public static Gson getGson4LogUtils() {
        Gson gson4LogUtils = GSONS.get(KEY_LOG_UTILS);
        if (gson4LogUtils == null) {
            gson4LogUtils = new GsonBuilder().setPrettyPrinting().serializeNulls().create();
            GSONS.put(KEY_LOG_UTILS, gson4LogUtils);
        }
        return gson4LogUtils;
    }

    private static Gson createGson() {
        return new GsonBuilder().serializeNulls().disableHtmlEscaping().create();
    }
}
