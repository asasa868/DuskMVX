package com.lzq.dawn.util.regex

/**
 * @Name :RegexConstants
 * @Time :2022/8/29 14:38
 * @Author :  Lzq
 * @Desc : 正则表达式常量
 */
object RegexConstants {
    /**
     * 简单手机的正则表达式
     */
    const val REGEX_MOBILE_SIMPLE = "^[1]\\d{10}$"

    /**
     * 精确移动的正则表达式
     *
     * 中国移动: 134(0-8), 135, 136, 137, 138, 139, 147, 150, 151, 152, 157, 158, 159, 165, 172, 178, 182, 183, 184, 187, 188, 195, 197, 198
     *
     * 中国联通: 130, 131, 132, 145, 155, 156, 166, 167, 175, 176, 185, 186, 196
     *
     * 中国电信: 133, 149, 153, 162, 173, 177, 180, 181, 189, 190, 191, 199
     *
     * 中国广播: 192
     *
     * 全球: 1349
     *
     * 虚拟运营商: 170, 171
     */
    const val REGEX_MOBILE_EXACT =
        "^((13[0-9])|(14[579])|(15[0-35-9])|(16[2567])|(17[0-35-8])|(18[0-9])|(19[0-35-9]))\\d{8}$"

    /**
     * 电话号码的正则表达式。
     */
    const val REGEX_TEL = "^0\\d{2,3}[- ]?\\d{7,8}$"

    /**
     * 长度为 15 的身份证号码的正则表达式。
     */
    const val REGEX_ID_CARD15 = "^[1-9]\\d{7}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}$"

    /**
     * 长度为 18 的身份证号码的正则表达式。
     */
    const val REGEX_ID_CARD18 =
        "^[1-9]\\d{5}[1-9]\\d{3}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}([0-9Xx])$"

    /**
     * 电子邮件的正则表达式
     */
    const val REGEX_EMAIL = "^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$"

    /**
     * 网址的正则表达式。
     */
    const val REGEX_URL = "[a-zA-z]+://[^\\s]*"

    /**
     * 汉字正则表达式。
     */
    const val REGEX_ZH = "^[\\u4e00-\\u9fa5]+$"

    /**
     * 用户名的正则表达式。
     *
     * 范围 "a-z", "A-Z", "0-9", "_", "Chinese character"
     *
     * 不能有 "_"
     *
     * 长度在 6 到 20 之间
     */
    const val REGEX_USERNAME = "^[\\w\\u4e00-\\u9fa5]{6,20}(?<!_)$"

    /**
     * 模式为“yyyy-MM-dd”的日期正则表达式。
     */
    const val REGEX_DATE =
        "^(?:(?!0000)[0-9]{4}-(?:(?:0[1-9]|1[0-2])-(?:0[1-9]|1[0-9]|2[0-8])|(?:0[13-9]|1[0-2])-(?:29|30)|(?:0[13578]|1[02])-31)|(?:[0-9]{2}(?:0[48]|[2468][048]|[13579][26])|(?:0[48]|[2468][048]|[13579][26])00)-02-29)$"

    /**
     * ip地址的正则表达式.
     */
    const val REGEX_IP = "((2[0-4]\\d|25[0-5]|[01]?\\d\\d?)\\.){3}(2[0-4]\\d|25[0-5]|[01]?\\d\\d?)"

    // The following come from http://tool.oschina.net/regex

    /**
     * 双字节字符的正则表达式。
     */
    const val REGEX_DOUBLE_BYTE_CHAR = "[^\\x00-\\xff]"

    /**
     * 空行的正则表达式。
     */
    const val REGEX_BLANK_LINE = "\\n\\s*\\r"

    /**
     * QQ号码的正则表达式。
     */
    const val REGEX_QQ_NUM = "[1-9][0-9]{4,}"

    /**
     * 中国邮政编码的正则表达式。
     */
    const val REGEX_CHINA_POSTAL_CODE = "[1-9]\\d{5}(?!\\d)"

    /**
     * 整数的正则表达式。
     */
    const val REGEX_INTEGER = "^(-?[1-9]\\d*)|0$"

    /**
     * 正整数的正则表达式。
     */
    const val REGEX_POSITIVE_INTEGER = "^[1-9]\\d*$"

    /**
     * Regex of negative integer.
     */
    const val REGEX_NEGATIVE_INTEGER = "^-[1-9]\\d*$"

    /**
     * 非负整数的正则表达式。
     */
    const val REGEX_NOT_NEGATIVE_INTEGER = "^[1-9]\\d*|0$"

    /**
     * 非正整数的正则表达式。
     */
    const val REGEX_NOT_POSITIVE_INTEGER = "^-[1-9]\\d*|0$"

    /**
     * 正浮点数的正则表达式。
     */
    const val REGEX_FLOAT = "^-?([1-9]\\d*\\.\\d*|0\\.\\d*[1-9]\\d*|0?\\.0+|0)$"

    /**
     * 正浮点数的正则表达式。
     */
    const val REGEX_POSITIVE_FLOAT = "^[1-9]\\d*\\.\\d*|0\\.\\d*[1-9]\\d*$"

    /**
     * 负浮点数的正则表达式。
     */
    const val REGEX_NEGATIVE_FLOAT = "^-[1-9]\\d*\\.\\d*|-0\\.\\d*[1-9]\\d*$"

    /**
     * 正浮点数的正则表达式。
     */
    const val REGEX_NOT_NEGATIVE_FLOAT = "^[1-9]\\d*\\.\\d*|0\\.\\d*[1-9]\\d*|0?\\.0+|0$"

    /**
     * 负浮点数的正则表达式。
     */
    const val REGEX_NOT_POSITIVE_FLOAT = "^(-([1-9]\\d*\\.\\d*|0\\.\\d*[1-9]\\d*))|0?\\.0+|0$"
    // If u want more please visit http://toutiao.com/i6231678548520731137
}
