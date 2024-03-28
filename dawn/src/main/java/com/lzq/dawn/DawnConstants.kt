package com.lzq.dawn

import androidx.annotation.IntDef
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import java.nio.charset.Charset

/**
 * @Name :DawnConstants
 * @Time :2022/7/20 14:34
 * @Author :  Lzq
 * @Desc :
 */
class DawnConstants {
    object MemoryConstants {
        const val BYTE = 1
        const val KB   = 1024
        const val MB   = 1048576
        const val GB   = 1073741824

        @IntDef(BYTE, KB, MB, GB)
        @Retention(AnnotationRetention.SOURCE)
        annotation class Unit
    }

    object TimeConstants {
        const val MSEC  = 1
        const val SEC   = 1000
        const val MIN   = 60000
        const val HOUR  = 3600000
        const val DAY   = 86400000

        @IntDef(MSEC, SEC, MIN, HOUR, DAY)
        @Retention(AnnotationRetention.SOURCE)
        annotation class Unit
    }

    object NetWorkConstants {
        const val CONNECT_TIMEOUT  = 60 * 1000L
        const val READ_TIMEOUT     = 60 * 1000L
        const val WRITE_TIMEOUT    = 60 * 1000L
        const val CALL_TIMEOUT     = 30 * 1000L

        val MEDIA_TYPE: MediaType  = "application/json; charset=UTF-8".toMediaType()
        val UTF_8 : Charset        = Charset.forName("UTF-8")

        /**
         * 异常code
         */
        const val RESPONSE_ANALYZE_CODE          = 1000
        const val RESPONSE_BODY_NULL_CODE        = 1100
        const val RESPONSE_EXCEPTION_NULL_CODE   = 1200
        const val RESPONSE_CODE_NOT_SUCCESS_CODE = 1300
        const val GSON_NULL_CODE                 = 10000
        const val JSON_IO_CODE                   = 10001
        const val JSON_PARSE_CODE                = 10002


        /**
         * http状态码
         * 100
         */
        const val HTTP_CONTINUE_CODE             = 100
        const val HTTP_SWITCHING_PROTOCOLS_CODE  = 101
        const val HTTP_PROCESSING_CODE           = 102
        const val HTTP_EARLY_HINTS_CODE          = 103

        /**
         * 200
         */
        const val HTTP_OK_CODE                             = 200
        const val HTTP_CREATED_CODE                        = 201
        const val HTTP_ACCEPTED_CODE                       = 202
        const val HTTP_NON_AUTHORITATIVE_INFORMATION_CODE  = 203
        const val HTTP_NO_CONTENT_CODE                     = 204
        const val HTTP_RESET_CONTENT_CODE                  = 205
        const val HTTP_PARTIAL_CONTENT_CODE                = 206
        const val HTTP_MULTI_STATUS_CODE                   = 207
        const val HTTP_ALREADY_REPORTED_CODE               = 208
        const val HTTP_IM_USED_CODE                        = 226

        /**
         * 300
         */
        const val HTTP_MULTIPLE_CHOICES_CODE     = 300
        const val HTTP_MOVED_PERMANENTLY_CODE    = 301
        const val HTTP_FOUND_CODE                = 302
        const val HTTP_SEE_OTHER_CODE            = 303
        const val HTTP_NOT_MODIFIED_CODE         = 304
        const val HTTP_USE_PROXY_CODE            = 305
        const val HTTP_SWITCH_PROXY_UNUSED_CODE  = 306
        const val HTTP_TEMPORARY_REDIRECT_CODE   = 307
        const val HTTP_PERMANENT_REDIRECT_CODE   = 308

        /**
         * 400
         */
        const val HTTP_BAD_REQUEST_CODE                      = 400
        const val HTTP_UNAUTHORIZED_CODE                     = 401
        const val HTTP_PAYMENT_REQUIRED_CODE                 = 402
        const val HTTP_FORBIDDEN_CODE                        = 403
        const val HTTP_NOT_FOUND_CODE                        = 404
        const val HTTP_METHOD_NOT_ALLOWED_CODE               = 405
        const val HTTP_NOT_ACCEPTABLE_CODE                   = 406
        const val HTTP_PROXY_AUTHENTICATION_REQUIRED_CODE    = 407
        const val HTTP_REQUEST_TIMEOUT_CODE                  = 408
        const val HTTP_CONFLICT_CODE                         = 409
        const val HTTP_GONE_CODE                             = 410
        const val HTTP_LENGTH_REQUIRED_CODE                  = 411
        const val HTTP_PRECONDITION_FAILED_CODE              = 412
        const val HTTP_PAYLOAD_TOO_LARGE_CODE                = 413
        const val HTTP_URI_TOO_LONG_CODE                     = 414
        const val HTTP_UNSUPPORTED_MEDIA_TYPE_CODE           = 415
        const val HTTP_RANGE_NOT_SATISFIABLE_CODE            = 416
        const val HTTP_EXPECTATION_FAILED_CODE               = 417
        const val HTTP_TEAPOT_CODE                           = 418
        const val HTTP_MISDIRECTED_REQUEST_CODE              = 421
        const val HTTP_UN_PROCESSABLE_ENTITY_CODE            = 422
        const val HTTP_LOCKED_CODE                           = 423
        const val HTTP_FAILED_DEPENDENCY_CODE                = 424
        const val HTTP_TOO_EARLY_CODE                        = 425
        const val HTTP_UPGRADE_REQUIRED_CODE                 = 426
        const val HTTP_PRECONDITION_REQUIRED_CODE            = 428
        const val HTTP_TOO_MANY_REQUESTS_CODE                = 429
        const val HTTP_REQUEST_HEADER_FIELDS_TOO_LARGE_CODE  = 431
        const val HTTP_UNAVAILABLE_FOR_LEGAL_REASONS_CODE    = 451

        /**
         * 500
         */
        const val HTTP_INTERNAL_SERVER_ERROR_CODE           = 500
        const val HTTP_NOT_IMPLEMENTED_CODE                 = 501
        const val HTTP_BAD_GATEWAY_CODE                     = 502
        const val HTTP_SERVICE_UNAVAILABLE_CODE             = 503
        const val HTTP_GATEWAY_TIMEOUT_CODE                 = 504
        const val HTTP_HTTP_VERSION_NOT_SUPPORTED_CODE      = 505
        const val HTTP_VARIANT_ALSO_NEGOTIATES_CODE         = 506
        const val HTTP_INSUFFICIENT_STORAGE_CODE            = 507
        const val HTTP_LOOP_DETECTED_CODE                   = 508
        const val HTTP_NOT_EXTENDED_CODE                    = 510
        const val HTTP_NETWORK_AUTHENTICATION_REQUIRED_CODE = 511


    }
}