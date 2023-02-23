package org.evlove.common.bom.service.exception;

import lombok.Builder;
import lombok.Data;
import org.evlove.common.core.pojo.BasePojo;

/**
 * @author massaton.github.io
 */
@Data
@Builder
class ExceptionMessage  extends BasePojo {
    private String errorName;
    private String errorCode;
    private String errorCodeDesc;
    private String errorMessage;

    private String requestMethod;
    private String requestUrl;
    private String requestParam;

    private String appId;
    private String clientType;
    private String clientVersion;
    private String userId;

    private String clientIp;

    private String serverAddress;

    private String level;
    private String stack;
    private String happen;
}
