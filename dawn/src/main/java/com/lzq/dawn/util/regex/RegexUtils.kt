package com.lzq.dawn.util.regex

import androidx.collection.SimpleArrayMap
import java.util.regex.Pattern

/**
 * @Name :RegexUtils
 * @Time :2022/8/29 14:32
 * @Author :  Lzq
 * @Desc :  正则表达式
 */
object RegexUtils {
    private val CITY_MAP = SimpleArrayMap<String, String?>()

    /**
     * 返回输入是否与简单手机的正则表达式匹配。
     *
     * @param input input.
     * @return `true`: yes<br></br>`false`: no
     */
    fun isMobileSimple(input: CharSequence?): Boolean {
        return isMatch(RegexConstants.REGEX_MOBILE_SIMPLE, input)
    }

    /**
     * 返回给定电子邮件地址的域部分
     *
     * @param email 电子邮件地址。
     * 例如，从给定的电子邮件“johnsmith@protonmail.com”返回“protonmail.com”。
     * @return 给定电子邮件地址的域部分。
     */
    fun extractEmailProvider(email: String): String {
        return email.substring(email.lastIndexOf("@") + 1)
    }

    /**
     * 返回给定电子邮件地址的用户名部分。
     *
     * @param email 电子邮件地址。
     * 例如。从给定的电子邮件“johnsmith@protonmail.com”返回“johnsmith”。
     * @return 给定电子邮件地址的用户名部分。
     */
    fun extractEmailUsername(email: String): String {
        return email.substring(0, email.lastIndexOf("@"))
    }

    /**
     * 返回给定的电子邮件地址是否在指定的电子邮件提供商上。
     *
     * @param email         电子邮件地址。
     * 例如。 “johnsmith@protonmail.com”和“gmail.com”将返回 false。
     * @param emailProvider 要作证的电子邮件提供商。
     * @return `true`: yes<br></br>`false`: no
     */
    fun isFromEmailProvider(
        email: String,
        emailProvider: String?,
    ): Boolean {
        return extractEmailProvider(email).equals(emailProvider, ignoreCase = true)
    }

    /**
     * 返回输入是否与确切移动设备的正则表达式匹配。
     *
     * @param input input.
     * @return `true`: yes<br></br>`false`: no
     */
    fun isMobileExact(input: CharSequence?): Boolean {
        return isMobileExact(input, null)
    }

    /**
     * 返回输入是否与确切移动设备的正则表达式匹配。
     *
     * @param input       input.
     * @param newSegments 新的手机号码段。
     * @return `true`: yes<br></br>`false`: no
     */
    fun isMobileExact(
        input: CharSequence?,
        newSegments: List<String?>?,
    ): Boolean {
        val match = isMatch(RegexConstants.REGEX_MOBILE_EXACT, input)
        if (match) {
            return true
        }
        if (newSegments == null) {
            return false
        }
        if (input == null || input.length != 11) {
            return false
        }
        val content = input.toString()
        for (c in content.toCharArray()) {
            if (!Character.isDigit(c)) {
                return false
            }
        }
        for (newSegment in newSegments) {
            if (content.startsWith(newSegment!!)) {
                return true
            }
        }
        return false
    }

    /**
     * 返回输入是否匹配电话号码的正则表达式
     *
     * @param input input.
     * @return `true`: yes<br></br>`false`: no
     */
    fun isTel(input: CharSequence?): Boolean {
        return isMatch(RegexConstants.REGEX_TEL, input)
    }

    /**
     * 返回输入是否与长度为 15 的身份证号的正则表达式匹配。
     *
     * @param input input.
     * @return `true`: yes<br></br>`false`: no
     */
    fun isIDCard15(input: CharSequence?): Boolean {
        return isMatch(RegexConstants.REGEX_ID_CARD15, input)
    }

    /**
     * 返回输入是否与长度为 18 的身份证号的正则表达式匹配。
     *
     * @param input input.
     * @return `true`: yes<br></br>`false`: no
     */
    fun isIDCard18(input: CharSequence?): Boolean {
        return isMatch(RegexConstants.REGEX_ID_CARD18, input)
    }

    /**
     * 返回输入是否与长度为 18 的确切身份证号的正则表达式匹配。
     *
     * @param input input.
     * @return `true`: yes<br></br>`false`: no
     */
    fun isIDCard18Exact(input: CharSequence): Boolean {
        if (isIDCard18(input)) {
            val factor = intArrayOf(7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2)
            val suffix = charArrayOf('1', '0', 'X', '9', '8', '7', '6', '5', '4', '3', '2')
            if (CITY_MAP.isEmpty()) {
                CITY_MAP.put("11", "北京")
                CITY_MAP.put("12", "天津")
                CITY_MAP.put("13", "河北")
                CITY_MAP.put("14", "山西")
                CITY_MAP.put("15", "内蒙古")
                CITY_MAP.put("21", "辽宁")
                CITY_MAP.put("22", "吉林")
                CITY_MAP.put("23", "黑龙江")
                CITY_MAP.put("31", "上海")
                CITY_MAP.put("32", "江苏")
                CITY_MAP.put("33", "浙江")
                CITY_MAP.put("34", "安徽")
                CITY_MAP.put("35", "福建")
                CITY_MAP.put("36", "江西")
                CITY_MAP.put("37", "山东")
                CITY_MAP.put("41", "河南")
                CITY_MAP.put("42", "湖北")
                CITY_MAP.put("43", "湖南")
                CITY_MAP.put("44", "广东")
                CITY_MAP.put("45", "广西")
                CITY_MAP.put("46", "海南")
                CITY_MAP.put("50", "重庆")
                CITY_MAP.put("51", "四川")
                CITY_MAP.put("52", "贵州")
                CITY_MAP.put("53", "云南")
                CITY_MAP.put("54", "西藏")
                CITY_MAP.put("61", "陕西")
                CITY_MAP.put("62", "甘肃")
                CITY_MAP.put("63", "青海")
                CITY_MAP.put("64", "宁夏")
                CITY_MAP.put("65", "新疆")
                CITY_MAP.put("71", "台湾老")
                CITY_MAP.put("81", "香港")
                CITY_MAP.put("82", "澳门")
                CITY_MAP.put("83", "台湾新")
                CITY_MAP.put("91", "国外")
            }
            if (CITY_MAP[input.subSequence(0, 2).toString()] != null) {
                var weightSum = 0
                for (i in 0..16) {
                    weightSum += (input[i].code - '0'.code) * factor[i]
                }
                val idCardMod = weightSum % 11
                val idCardLast = input[17]
                return idCardLast == suffix[idCardMod]
            }
        }
        return false
    }

    /**
     * 返回输入是否与电子邮件的正则表达式匹配。
     *
     * @param input input.
     * @return `true`: yes<br></br>`false`: no
     */
    fun isEmail(input: CharSequence?): Boolean {
        return isMatch(RegexConstants.REGEX_EMAIL, input)
    }

    /**
     * 返回输入是否匹配 url 的正则表达式。
     *
     * @param input input.
     * @return `true`: yes<br></br>`false`: no
     */
    fun isURL(input: CharSequence?): Boolean {
        return isMatch(RegexConstants.REGEX_URL, input)
    }

    /**
     * 返回输入是否匹配汉字的正则表达式
     *
     * @param input input.
     * @return `true`: yes<br></br>`false`: no
     */
    fun isZh(input: CharSequence?): Boolean {
        return isMatch(RegexConstants.REGEX_ZH, input)
    }

    /**
     * 返回输入是否与用户名的正则表达式匹配。
     *
     * 适用范围 "a-z", "A-Z", "0-9", "_", "Chinese character"
     *
     * 不能有 "_"
     *
     * 长度在 6 到 20 之间.
     *
     * @param input input.
     * @return `true`: yes<br></br>`false`: no
     */
    fun isUsername(input: CharSequence?): Boolean {
        return isMatch(RegexConstants.REGEX_USERNAME, input)
    }

    /**
     * 返回输入是否与模式为“yyyy-MM-dd”的日期的正则表达式匹配。
     *
     * @param input input.
     * @return `true`: yes<br></br>`false`: no
     */
    fun isDate(input: CharSequence?): Boolean {
        return isMatch(RegexConstants.REGEX_DATE, input)
    }

    /**
     * 返回输入是否匹配 ip 地址的正则表达式。
     *
     * @param input The input.
     * @return `true`: yes<br></br>`false`: no
     */
    fun isIP(input: CharSequence?): Boolean {
        return isMatch(RegexConstants.REGEX_IP, input)
    }

    /**
     * 返回输入是否与正则表达式匹配。
     *
     * @param regex 正则表达式
     * @param input input.
     * @return `true`: yes<br></br>`false`: no
     */
    fun isMatch(
        regex: String?,
        input: CharSequence?,
    ): Boolean {
        return !input.isNullOrEmpty() && Pattern.matches(regex, input)
    }

    /**
     * 返回与正则表达式匹配的输入列表。
     *
     * @param regex 正则表达式.
     * @param input input.
     * @return 输入列表与正则表达式匹配
     */
    fun getMatches(
        regex: String,
        input: CharSequence?,
    ): List<String> {
        if (input == null) {
            return emptyList()
        }
        val matches: MutableList<String> = ArrayList()
        val pattern = Pattern.compile(regex)
        val matcher = pattern.matcher(input)
        while (matcher.find()) {
            matches.add(matcher.group())
        }
        return matches
    }

    /**
     * 围绕正则表达式的匹配拆分输入。
     *
     * @param input input.
     * @param regex 正则表达式
     * @return 通过围绕正则表达式的匹配拆分输入计算的字符串数组
     */
    fun getSplits(
        input: String?,
        regex: String,
    ): Array<String?> {
        return input?.split(regex.toRegex())?.dropLastWhile { it.isEmpty() }?.toTypedArray() ?: arrayOfNulls(
            0,
        )
    }

    /**
     * 用给定的替换字符串替换与正则表达式匹配的输入序列的第一个子序列。
     *
     * @param input       input.
     * @param regex       正则表达式
     * @param replacement 替换字符串。
     * @return 通过用替换字符串替换第一个匹配子序列构造的字符串，根据需要替换捕获的子序列
     */
    fun getReplaceFirst(
        input: String?,
        regex: String,
        replacement: String,
    ): String {
        return if (input == null) {
            ""
        } else {
            Pattern.compile(regex).matcher(input).replaceFirst(replacement)
        }
    }

    /**
     * 用给定的替换字符串替换与模式匹配的输入序列的每个子序列。
     *
     * @param input       input.
     * @param regex       正则表达式
     * @param replacement 替换字符串
     * @return 通过用替换字符串替换每个匹配的子序列构造的字符串，根据需要替换捕获的子序列
     */
    fun getReplaceAll(
        input: String?,
        regex: String,
        replacement: String,
    ): String {
        return if (input == null) {
            ""
        } else {
            Pattern.compile(regex).matcher(input).replaceAll(replacement)
        }
    }
}
