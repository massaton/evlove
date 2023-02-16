package org.evlove.common.core.rcode;

/**
 * Framework-level common interface return codes.
 *
 * Business services can additionally define their own return code enumeration class.
 *
 * Return code rules:
 * - Code can use up to 5 digits.
 * - 0, 1-9999, 99999 are return code reserved for the framework, and other values can be used for business system custom return codes.
 * - ......
 *
 * The definition of the return code must have a certain meaning, is convenient for observation, memory and monitoring, such as:
 * ReturnCode = business segment number (2 digits) + business segment internal exception type (1 digit) + sequence number (2 digits)
 *
 * @author massaton.github.io
 */
public enum CommonReturnCode implements IReturnCode {
    // Everything be normal
    SUCCESS(0, "SUCCESS"),

    // 4xx - Standard HTTP request exception
    API_NOT_FOUND(404, "请求的接口地址不存在"),
    UNSUPPORTED_METHOD_TYPE(405, "不支持的请求方法类型"),
    UNSUPPORTED_MEDIA_TYPE(415, "不支持的请求媒体类型"),
    OTHER_HTTP_ERROR(499, "未知的HTTP错误"),

    // 50x - Program exception
    PROGRAM_ERROR(500, "服务端程序异常"),
    PROGRAM_PROCESS_FAILURE(501, "程序原因，执行失败"),
    THREAD_REJECT(502, "超过线程池设定，线程被拒收"),
    SERVICE_UNAVAILABLE(503, "远程服务不可用"),
    COMMON_RUNTIME_EXCEPTION(509, "[使用异常中的message替换]"),

    // 1xxx - Custom framework exception: HTTP request exception
    REQUEST_TIMEOUT(1000, "请求超时"),
    REQUEST_PARAM_ERROR(1001, "请求的接口参数有误"),
    REQUEST_PARAM_FORMAT_ERROR(1002, "请求参数的格式有误"),
    REQUEST_PARAM_JSON_FORMAT_ERROR(1003, "请求参数的JSON格式错误"),
    REQUEST_HEADER_PARAMS_ABSENT(1004,"缺少请求头参数"),
    REMOTE_REQUEST_ERROR(1005,"远程调用异常"),
    REQUEST_NO_REPEAT_SUBMIT(1006, "请求不能重复提交"),

    // 2xxx - Custom framework exception: gateway exception
    GATEWAY_ERROR(2000, "网关程序异常"),
    GATEWAY_ABSENT_HEADER(2001, "缺少必要的请求头参数"),
    GATEWAY_URI_FORMAT_ERROR(2002, "请求地址格式错误"),
    GATEWAY_BODY_NOT_ALLOW_ARRAY_ERROR(2003, "请求的Body参数不允许使用数组作为根节点"),
    GATEWAY_GLOBAL_VERIFY_DISALLOW_API(2101, "接口禁止访问（全局级别限制）"),
    GATEWAY_GLOBAL_VERIFY_DISALLOW_IP(2102, "访问IP被禁止访问（全局级别限制）"),
    GATEWAY_APP_VERIFY_DISALLOW_API(2103, "接口禁止访问（应用级别限制）"),
    GATEWAY_APP_VERIFY_DISALLOW_IP(2104, "访问IP被禁止访问（应用级别限制）"),
    GATEWAY_SIGN_RSA_NOT_PERMIT(2201, "请求不支持RSA验签"),
    GATEWAY_SIGN_TYPE_FAIL(2202, "未找到符合条件的签名类型"),
    GATEWAY_CHECK_SIGN_FAIL(2203, "签名验证错误"),
    GATEWAY_NOT_FOUND_RSA_PUBLIC_KEY(2204, "未找到用于RSA验签的公钥"),
    GATEWAY_USER_TOKEN_ABSENT(2205, "用户Token不存在或已过期"),
    GATEWAY_SIGN_OVERDUE(2206, "签名已过期"),
    GATEWAY_SIGN_TIMESTAMP_FORMAT_ERROR(2207, "签名时间戳的格式错误"),
    GATEWAY_NOT_FOUND_PLATFORM_RSA_PRIVATE_KEY(2208, "未找到用于RSA签名的平台私钥"),

    // Any other undefined exception
    UNKNOWN_ERROR(99999, "未知问题");

    private final Integer _code;
    private final String _message;

    CommonReturnCode(Integer code, String message) {
        this._code = code;
        this._message = message;
    }

    @Override
    public Integer getCode() {
        return this._code;
    }

    @Override
    public String getMessage() {
        return this._message;
    }

    @Override
    public String getName() {
        return this.name();
    }

    @Override
    public String toString() {
        return String.format("%d[%s] - %s", _code, name(), _message);
    }
}
