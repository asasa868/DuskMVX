package com.lzq.dawn.util.reflect

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertThrows
import org.junit.Test

/** 验证非空反射类型推导仍保留重载选择、空参数与代理行为。 */
class ReflectUtilsNullabilityTest {
    @Test fun constructorSelectsTheMostSpecificAssignableParameter() {
        val result = ReflectUtils.reflect(ConstructorTarget::class.java)
            .newInstance(StringBuffer("value")).get<ConstructorTarget>()

        assertEquals("sequence:value", result?.selected)
    }

    @Test fun constructorRetainsNullableArgumentSupport() {
        val result = ReflectUtils.reflect(NullableTarget::class.java).newInstance(null).get<NullableTarget>()

        assertNotNull(result)
        assertNull(result?.value)
    }

    @Test fun methodSelectsTheMostSpecificAssignableParameter() {
        val result = ReflectUtils.reflect(MethodTarget()).method("choose", StringBuffer("value")).get<String>()

        assertEquals("sequence:value", result)
    }

    @Test fun methodRetainsNullableArgumentSupport() {
        val result = ReflectUtils.reflect(NullableTarget("seed")).method("echo", null).get<String>()

        assertNull(result)
    }

    @Test fun typedProxyDelegatesToTheOriginalObject() {
        val proxy = ReflectUtils.reflect(MethodTarget()).proxy(Echo::class.java)

        assertEquals("echo:value", proxy.echo("value"))
    }

    @Test fun mapProxyRetainsGetterAndSetterBehavior() {
        val values = mutableMapOf<String, Any>("title" to "before")
        val proxy = ReflectUtils.reflect(values).proxy(MapBacked::class.java)

        assertEquals("before", proxy.getTitle())
        proxy.setTitle("after")

        assertEquals("after", values["title"])
    }

    @Test fun objectAndNullResultsRetainTheirOriginalIdentity() {
        val target = MethodTarget()

        assertSame(target, ReflectUtils.reflect(target).get<MethodTarget>())
        assertNull(ReflectUtils.reflect(null).get<Any>())
    }

    @Test fun unknownMethodRetainsTheReflectionFailure() {
        assertThrows(ReflectException::class.java) { ReflectUtils.reflect(MethodTarget()).method("missing") }
    }

    private class ConstructorTarget {
        val selected: String

        constructor(value: Any) { selected = "any:$value" }
        constructor(value: CharSequence) { selected = "sequence:$value" }
    }

    private class NullableTarget(val value: String?) {
        fun echo(value: String?): String? = value
    }

    private class MethodTarget {
        fun choose(value: Any) = "any:$value"
        fun choose(value: CharSequence) = "sequence:$value"
        fun echo(value: String) = "echo:$value"
    }

    private interface Echo {
        fun echo(value: String): String
    }

    private interface MapBacked {
        fun getTitle(): String
        fun setTitle(value: String)
    }
}
