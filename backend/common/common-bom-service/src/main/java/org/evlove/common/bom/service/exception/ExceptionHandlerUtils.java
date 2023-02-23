package org.evlove.common.bom.service.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.Builder;
import lombok.Data;
import org.evlove.common.core.constant.FrameworkHeader;
import org.evlove.common.core.pojo.BasePojo;
import org.evlove.common.core.rcode.IReturnCode;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author massaton.github.io
 */
class ExceptionHandlerUtils {

    /**
     * Get exception log output in a format convenient for log analysis
     *
     * Format: JSON
     */
    protected static ExceptionMessage messageBuilder(HttpServletRequest request, String requestUrl, IReturnCode returnCode, String errorMessage) {
        return ExceptionMessage.builder()
                .happen(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss SSS").format(LocalDateTime.now()))
                .errorName(returnCode.getName())
                .errorCode(returnCode.getCode().toString())
                .errorCodeDesc(returnCode.getMessage())
                .errorMessage(errorMessage)
                .requestMethod(request.getMethod())
                .requestUrl(requestUrl)
                .requestParam(request.getQueryString())
                .appId(request.getHeader(FrameworkHeader.Request.APP_ID))
                .clientType(request.getHeader(FrameworkHeader.Request.CLIENT_TYPE))
                .clientVersion(request.getHeader(FrameworkHeader.Request.CLIENT_VERSION))
                .userId(request.getHeader(FrameworkHeader.Request.USER_ID))
                .clientIp(findClientAddress(request))
                .serverAddress(request.getServerName() + ":" + request.getServerPort())
                .build();
    }

    /**
     * Get the client IP address of the reactive request
     */
    private static String findClientAddress(HttpServletRequest request) {
        // When Nginx seven-layer proxy, use "X-Forwarded-For: user real IP, proxy server 1-IP, proxy server 2-IP,..."
        String ip = request.getHeader("X-Forwarded-For");
        if (StringUtils.hasLength(ip) && !"unknown".equalsIgnoreCase(ip)) {
            int index = ip.indexOf(",");
            if (index != -1) {
                // There will be multiple IP values after multiple reverse proxies, the first one being the real IP.
                return ip.substring(0, index);
            } else {
                return ip;
            }
        } else {
            // For direct access, use "RemoteAddr"
            return request.getRemoteAddr();
        }
    }
}
