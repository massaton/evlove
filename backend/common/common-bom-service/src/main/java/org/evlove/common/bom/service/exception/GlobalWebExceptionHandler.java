package org.evlove.common.bom.service.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.evlove.common.core.rcode.CommonReturnCode;
import org.evlove.common.core.robj.ReturnVO;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import java.util.Map;

/**
 * Global web exception handler, returning unified JSON structure information
 *
 * @author massaton.github.io
 */
@Slf4j
@RestController
public class GlobalWebExceptionHandler implements ErrorController {

    private final ErrorAttributes errorAttributes;

    public GlobalWebExceptionHandler(ErrorAttributes errorAttributes) {
        this.errorAttributes = errorAttributes;
    }

    @RequestMapping(value = "/error")
    public ReturnVO<Object> handleError(HttpServletRequest request) {

        WebRequest webRequest = new ServletWebRequest(request);
        Map<String, Object> errorInfo = errorAttributes.getErrorAttributes(webRequest, ErrorAttributeOptions.defaults());

        Integer status = (Integer) errorInfo.get("status");
        String errorMessage = (String) errorInfo.get("error");
        String requestUrl = (String) errorInfo.get("path");

        CommonReturnCode returnCode;
        if (HttpStatus.NOT_FOUND.value() == status) {
            returnCode = CommonReturnCode.API_NOT_FOUND;
        } else {
            returnCode = CommonReturnCode.OTHER_HTTP_ERROR;
        }
        // Print exception log (do not print stack information)
        ExceptionMessage exceptionMessage = ExceptionHandlerUtils.messageBuilder(request, requestUrl, returnCode, errorMessage);
        exceptionMessage.setLevel("ERROR");
        // Use System.out.println to directly output exception information, which is convenient for Elasticsearch to build data structures after collection
        System.out.println(exceptionMessage.toJson());

        if (returnCode == CommonReturnCode.OTHER_HTTP_ERROR) {
            return ReturnVO.fail(status, errorMessage);
        } else {
            return ReturnVO.fail(returnCode);
        }
    }
}
