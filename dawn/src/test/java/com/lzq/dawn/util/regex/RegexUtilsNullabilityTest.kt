package com.lzq.dawn.util.regex

import org.junit.Assert.assertFalse
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.regex.PatternSyntaxException

/** 验证可空正则参数的既有匹配与失败边界。 */
class RegexUtilsNullabilityTest {
    @Test fun validPatternPreservesMatchingResults() {
        assertTrue(RegexUtils.isMatch("[a-z]+", "dawn"))
        assertFalse(RegexUtils.isMatch("[a-z]+", "123"))
    }

    @Test fun missingOrEmptyInputDoesNotEvaluateThePattern() {
        assertFalse(RegexUtils.isMatch(null, null))
        assertFalse(RegexUtils.isMatch(null, ""))
        assertFalse(RegexUtils.isMatch("[", ""))
    }

    @Test fun missingPatternWithNonEmptyInputRetainsNullPointerFailure() {
        assertThrows(NullPointerException::class.java) { RegexUtils.isMatch(null, "dawn") }
    }

    @Test fun invalidPatternWithNonEmptyInputRetainsSyntaxFailure() {
        assertThrows(PatternSyntaxException::class.java) { RegexUtils.isMatch("[", "dawn") }
    }
}
