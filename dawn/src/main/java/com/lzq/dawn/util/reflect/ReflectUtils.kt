package com.lzq.dawn.util.reflect

import java.lang.reflect.AccessibleObject
import java.lang.reflect.Constructor
import java.lang.reflect.Field
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Member
import java.lang.reflect.Method
import java.lang.reflect.Modifier
import java.lang.reflect.Proxy
import java.util.Arrays
import java.util.Collections
import java.util.Locale

/**
 * @Name :ReflectUtils
 * @Time :2022/8/16 15:41
 * @Author :  Lzq
 * @Desc : 反射
 */
class ReflectUtils private constructor(private val type: Class<*>, private val objects: Any? = type) {
    ///////////////////////////////////////////////////////////////////////////
    // newInstance
    ///////////////////////////////////////////////////////////////////////////
    /**
     * 创建并初始化一个新实例
     *
     * @return [ReflectUtils] 实例
     */
    fun newInstance(): ReflectUtils {
        return newInstance(*arrayOfNulls<Any>(0))
    }

    /**
     * 创建并初始化一个新实例
     *
     * @param args args.
     * @return [ReflectUtils] 实例
     */
    fun newInstance(vararg args: Any?): ReflectUtils {
        val types = getArgsType(*args)
        return try {
            val constructor = type().getDeclaredConstructor(*types)
            newInstance(constructor, *args)
        } catch (e: NoSuchMethodException) {
            val list: MutableList<Constructor<*>> = ArrayList()
            for (constructor in type().declaredConstructors) {
                if (match(constructor.parameterTypes, types)) {
                    list.add(constructor)
                }
            }
            if (list.isEmpty()) {
                throw ReflectException(e)
            } else {
                sortConstructors(list)
                newInstance(list[0], *args)
            }
        }
    }

    private fun getArgsType(vararg args: Any?): Array<Class<*>?> {
        val result: Array<Class<*>?> = arrayOfNulls(args.size)
        for (i in args.indices) {
            val value = args[i]
            result[i] = value?.javaClass ?: NULL::class.java
        }
        return result
    }

    private fun sortConstructors(list: List<Constructor<*>>) {
        Collections.sort(list, java.util.Comparator { o1, o2 ->
            val types1 = o1.parameterTypes
            val types2 = o2.parameterTypes
            val len = types1.size
            for (i in 0 until len) {
                if (types1[i] != types2[i]) {
                    return@Comparator if (wrapper(types1[i])!!.isAssignableFrom(wrapper(types2[i]))) {
                        1
                    } else {
                        -1
                    }
                }
            }
            0
        })
    }

    private fun newInstance(constructor: Constructor<*>, vararg args: Any): ReflectUtils {
        return try {
            ReflectUtils(
                constructor.declaringClass, accessible(constructor)!!.newInstance(*args)
            )
        } catch (e: Exception) {
            throw ReflectException(e)
        }
    }
    ///////////////////////////////////////////////////////////////////////////
    // field
    ///////////////////////////////////////////////////////////////////////////
    /**
     * 获得字段。
     *
     * @param name 字段的名称。
     * @return [ReflectUtils] 实例
     */
    fun field(name: String): ReflectUtils {
        return try {
            val field = getField(name)
            ReflectUtils(field.type, field[objects])
        } catch (e: IllegalAccessException) {
            throw ReflectException(e)
        }
    }

    /**
     * 设置字段。
     *
     * @param name  字段的名称。
     * @param value value.
     * @return [ReflectUtils] 实例
     */
    fun field(name: String, value: Any): ReflectUtils {
        return try {
            val field = getField(name)
            field[objects] = unwrap(value)
            this
        } catch (e: Exception) {
            throw ReflectException(e)
        }
    }

    @Throws(IllegalAccessException::class)
    private fun getField(name: String): Field {
        val field = getAccessibleField(name)
        if (field.modifiers and Modifier.FINAL == Modifier.FINAL) {
            try {
                val modifiersField = Field::class.java.getDeclaredField("modifiers")
                modifiersField.isAccessible = true
                modifiersField.setInt(field, field.modifiers and Modifier.FINAL.inv())
            } catch (ignore: NoSuchFieldException) {
                // runs in android will happen
                field.isAccessible = true
            }
        }
        return field
    }

    private fun getAccessibleField(name: String): Field {
        var type: Class<*>? = type()
        return try {
            accessible(type!!.getField(name))!!
        } catch (e: NoSuchFieldException) {
            do {
                try {
                    return accessible(type!!.getDeclaredField(name))!!
                } catch (ignore: NoSuchFieldException) {
                }
                type = type!!.superclass
            } while (type != null)
            throw ReflectException(e)
        }
    }

    private fun unwrap(`object`: Any): Any {
        return if (`object` is ReflectUtils) {
            `object`.get<Any>()!!
        } else `object`
    }
    ///////////////////////////////////////////////////////////////////////////
    // method
    ///////////////////////////////////////////////////////////////////////////
    /**
     * 调用方法
     *
     * @param name 方法的名称。
     * @return [ReflectUtils] 实例
     * @throws ReflectException 如果反射不成功
     */
    @Throws(ReflectException::class)
    fun method(name: String): ReflectUtils {
        return method(name, *arrayOfNulls<Any>(0))
    }

    /**
     * 调用方法
     *
     * @param name 方法的名称。
     * @param args args.
     * @return [ReflectUtils] 实例
     * @throws ReflectException 如果反射不成功
     */
    @Throws(ReflectException::class)
    fun method(name: String, vararg args: Any?): ReflectUtils {
        val types = getArgsType(*args)

        try {
            val method = exactMethod(name, types)
            return methods(method, objects, *args)
        } catch (e: NoSuchMethodException) {
            try {
                val method = similarMethod(name, types)
                return methods(method, objects, *args)
            } catch (e1: NoSuchMethodException) {
                throw ReflectException(e1)
            }
        }

    }

    private fun methods(method: Method, obj: Any?, vararg args: Any?): ReflectUtils {
        return try {
            accessible(method)
            if (method.returnType == Void.TYPE) {
                method.invoke(obj, *args)
                reflect(obj)
            } else {
                reflect(method.invoke(obj, *args))
            }
        } catch (e: Exception) {
            throw ReflectException(e)
        }
    }

    @Throws(NoSuchMethodException::class)
    private fun exactMethod(name: String, types: Array<Class<*>?>): Method {
        var type: Class<*>? = type()
        return try {
            type!!.getMethod(name, *types)
        } catch (e: NoSuchMethodException) {
            do {
                try {
                    return type!!.getDeclaredMethod(name, *types)
                } catch (ignore: NoSuchMethodException) {
                }
                type = type!!.superclass
            } while (type != null)
            throw NoSuchMethodException()
        }
    }

    @Throws(NoSuchMethodException::class)
    private fun similarMethod(name: String, types: Array<Class<*>?>): Method {
        var type: Class<*>? = type()
        val methods: MutableList<Method> = ArrayList()
        for (method in type!!.methods) {
            if (isSimilarSignature(method, name, types)) {
                methods.add(method)
            }
        }
        if (!methods.isEmpty()) {
            sortMethods(methods)
            return methods[0]
        }
        do {
            for (method in type!!.declaredMethods) {
                if (isSimilarSignature(method, name, types)) {
                    methods.add(method)
                }
            }
            if (!methods.isEmpty()) {
                sortMethods(methods)
                return methods[0]
            }
            type = type.superclass
        } while (type != null)
        throw NoSuchMethodException(
            "No similar method " + name + " with params " + Arrays.toString(types) + " could be found on type " + type() + "."
        )
    }

    private fun sortMethods(methods: List<Method>) {
        Collections.sort(methods, java.util.Comparator { o1, o2 ->
            val types1 = o1.parameterTypes
            val types2 = o2.parameterTypes
            val len = types1.size
            for (i in 0 until len) {
                if (types1[i] != types2[i]) {
                    return@Comparator if (wrapper(types1[i])!!.isAssignableFrom(wrapper(types2[i]))) {
                        1
                    } else {
                        -1
                    }
                }
            }
            0
        })
    }

    private fun isSimilarSignature(
        possiblyMatchingMethod: Method, desiredMethodName: String, desiredParamTypes: Array<Class<*>?>
    ): Boolean {
        return possiblyMatchingMethod.name == desiredMethodName && match(
            possiblyMatchingMethod.parameterTypes, desiredParamTypes
        )
    }

    private fun match(declaredTypes: Array<Class<*>>, actualTypes: Array<Class<*>?>): Boolean {
        return if (declaredTypes.size == actualTypes.size) {
            for (i in actualTypes.indices) {
                if (actualTypes[i] == NULL::class.java || wrapper(declaredTypes[i])!!.isAssignableFrom(
                        wrapper(actualTypes[i])
                    )
                ) {
                    continue
                }
                return false
            }
            true
        } else {
            false
        }
    }

    private fun <T : AccessibleObject?> accessible(accessible: T?): T? {
        if (accessible == null) {
            return null
        }
        if (accessible is Member) {
            val member = accessible as Member
            if (Modifier.isPublic(member.modifiers) && Modifier.isPublic(member.declaringClass.modifiers)) {
                return accessible
            }
        }
        if (!accessible.isAccessible) {
            accessible.isAccessible = true
        }
        return accessible
    }
    ///////////////////////////////////////////////////////////////////////////
    // proxy
    ///////////////////////////////////////////////////////////////////////////
    /**
     * 为包装对象创建一个代理，允许使用自定义接口在其上安全地调用方法。
     *
     * @param proxyType 代理实现的接口类型。
     * @return 包装对象的代理
     */
    fun <P> proxy(proxyType: Class<P>): P {
        val isMap = objects is Map<*, *>
        val handler = InvocationHandler { proxy, method, args ->
            val name = method.name
            try {
                return@InvocationHandler reflect(objects).method(name, *args).get<Any>()!!
            } catch (e: ReflectException) {
                if (isMap) {
                    val map = objects as MutableMap<String, Any>?
                    val length = args?.size ?: 0
                    if (length == 0 && name.startsWith("get")) {
                        return@InvocationHandler map!![property(name.substring(3))]!!
                    } else if (length == 0 && name.startsWith("is")) {
                        return@InvocationHandler map!![property(name.substring(2))]!!
                    } else if (length == 1 && name.startsWith("set")) {
                        map!![property(name.substring(3))] = args[0]
                        return@InvocationHandler null
                    }
                }
                throw e
            }
        }
        return Proxy.newProxyInstance(
            proxyType.classLoader, arrayOf<Class<*>>(proxyType), handler
        ) as P
    }

    private fun type(): Class<*> {
        return type
    }

    private fun wrapper(type: Class<*>?): Class<*>? {
        if (type == null) {
            return null
        } else if (type.isPrimitive) {
            if (Boolean::class.javaPrimitiveType == type) {
                return Boolean::class.java
            } else if (Int::class.javaPrimitiveType == type) {
                return Int::class.java
            } else if (Long::class.javaPrimitiveType == type) {
                return Long::class.java
            } else if (Short::class.javaPrimitiveType == type) {
                return Short::class.java
            } else if (Byte::class.javaPrimitiveType == type) {
                return Byte::class.java
            } else if (Double::class.javaPrimitiveType == type) {
                return Double::class.java
            } else if (Float::class.javaPrimitiveType == type) {
                return Float::class.java
            } else if (Char::class.javaPrimitiveType == type) {
                return Char::class.java
            } else if (Void.TYPE == type) {
                return Void::class.java
            }
        }
        return type
    }

    /**
     * 得到结果
     *
     * @param <T> 值类型
     * @return result
    </T> */
    fun <T> get(): T? {
        return objects as T?
    }

    override fun hashCode(): Int {
        return objects.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        return other is ReflectUtils && objects == other.get<Any>()
    }

    override fun toString(): String {
        return objects.toString()
    }

    private class NULL
    companion object {
        ///////////////////////////////////////////////////////////////////////////
        // reflect
        ///////////////////////////////////////////////////////////////////////////
        /**
         * 反射class
         *
         * @param className class
         * @return [ReflectUtils] 实例
         * @throws ReflectException 如果反射不成功
         */
        @Throws(ReflectException::class)
        fun reflect(className: String): ReflectUtils {
            return reflect(forName(className))
        }

        /**
         * 反射class
         *
         * @param className   class
         * @param classLoader 类的加载器。
         * @return [ReflectUtils] 实例
         * @throws ReflectException 如果反射不成功
         */
        @Throws(ReflectException::class)
        fun reflect(className: String, classLoader: ClassLoader): ReflectUtils {
            return reflect(forName(className, classLoader))
        }

        /**
         * 反射class
         *
         * @param clazz class.
         * @return [ReflectUtils] 实例
         * @throws ReflectException 如果反射不成功
         */
        @Throws(ReflectException::class)
        fun reflect(clazz: Class<*>): ReflectUtils {
            return ReflectUtils(clazz)
        }

        /**
         * 反射class
         *
         * @param objects object.
         * @return [ReflectUtils] 实例
         * @throws ReflectException 如果反射不成功
         */
        @Throws(ReflectException::class)
        fun reflect(objects: Any?): ReflectUtils {
            return ReflectUtils(objects?.javaClass ?: Any::class.java, objects)
        }

        private fun forName(className: String): Class<*> {
            return try {
                Class.forName(className)
            } catch (e: ClassNotFoundException) {
                throw ReflectException(e)
            }
        }

        private fun forName(name: String, classLoader: ClassLoader): Class<*> {
            return try {
                Class.forName(name, true, classLoader)
            } catch (e: ClassNotFoundException) {
                throw ReflectException(e)
            }
        }

        /**
         * 获取 getter/setter 的 POJO 属性名称
         */
        private fun property(string: String): String {
            val length = string.length
            return if (length == 0) {
                ""
            } else if (length == 1) {
                string.lowercase(Locale.getDefault())
            } else {
                string.substring(0, 1).lowercase(Locale.getDefault()) + string.substring(1)
            }
        }
    }
}