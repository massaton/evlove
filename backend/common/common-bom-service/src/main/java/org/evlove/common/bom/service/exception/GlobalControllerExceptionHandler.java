package org.evlove.common.bom.service.exception;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.UnexpectedTypeException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.evlove.common.core.exception.CommonCustomMessageException;
import org.evlove.common.core.exception.CommonReturnCodeException;
import org.evlove.common.core.rcode.CommonReturnCode;
import org.evlove.common.core.rcode.IReturnCode;
import org.evlove.common.core.robj.ReturnVO;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.core.task.TaskRejectedException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.concurrent.TimeoutException;

/**
 * Unified processing of all exceptions thrown from the Controller (including custom exceptions)
 *
 * @author massaton.github.io
 */
@Slf4j
@ControllerAdvice
public class GlobalControllerExceptionHandler {
    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public ReturnVO<Object> handlerException(HttpServletRequest request, Exception e) {
        // Construct exception return information
        IReturnCode returnCode;
        String returnMessage;
        if (e instanceof CommonCustomMessageException) {
            // The "custom message" general runtime exception thrown by the developer actively returns a fixed error code and custom exception information.
            returnCode = CommonReturnCode.COMMON_RUNTIME_EXCEPTION;
            returnMessage = e.getMessage();
        } else if (e instanceof CommonReturnCodeException) {
            // The "custom return code" general runtime exception thrown by the developer actively returns a custom error code and custom exception information.
            returnCode = ((CommonReturnCodeException) e).getReturnCode();
            returnMessage = returnCode.getMessage();
        } else {
            returnCode = lookupSuitReturnCode(e);
            returnMessage = returnCode.getMessage();
        }

        // Print exception log (print stack information)
        String requestUrl = request.getRequestURL().toString();
        ExceptionMessage exceptionMessage = ExceptionHandlerUtils.messageBuilder(request, requestUrl, returnCode, ExceptionUtils.getMessage(e));

        exceptionMessage.setLevel("ERROR");
        exceptionMessage.setStack(ExceptionUtils.getStackTrace(e));
        // Use System.out.println to directly output exception information, which is convenient for Elasticsearch to build data structures after collection
        System.out.println(exceptionMessage.toJson());

        return ReturnVO.fail(
                returnCode.getCode(),
                returnMessage
        );
    }

    /**
     * According to the exception thrown, find the appropriate return code
     */
    private CommonReturnCode lookupSuitReturnCode(Exception e) {
        CommonReturnCode returnCode;
        if (e instanceof MissingServletRequestParameterException) {
            returnCode = CommonReturnCode.REQUEST_PARAM_ERROR;
        } else if (e instanceof MethodArgumentNotValidException) {
            returnCode = CommonReturnCode.REQUEST_PARAM_ERROR;
        } else if (e instanceof UnexpectedTypeException || e instanceof MethodArgumentTypeMismatchException) {
            // The parameter validation annotation does not match the parameter type
            returnCode = CommonReturnCode.REQUEST_PARAM_ERROR;
        } else if (e instanceof MissingRequestHeaderException) {
            // The request is missing a required Header
            returnCode = CommonReturnCode.REQUEST_HEADER_PARAMS_ABSENT;
        } else if (e instanceof ConversionFailedException) {
            returnCode = CommonReturnCode.REQUEST_PARAM_FORMAT_ERROR;
        } else if (e instanceof HttpMessageNotReadableException) {
            if (e.getCause() == null) {
                returnCode = CommonReturnCode.REQUEST_PARAM_ERROR;
            } else {
                Throwable cause = e.getCause();
                if (cause instanceof InvalidFormatException) {
                    returnCode = CommonReturnCode.REQUEST_PARAM_FORMAT_ERROR;
                } else if (cause instanceof MismatchedInputException) {
                    returnCode = CommonReturnCode.REQUEST_PARAM_JSON_FORMAT_ERROR;
                } else if (cause instanceof JsonMappingException) {
                    returnCode = CommonReturnCode.REQUEST_PARAM_JSON_FORMAT_ERROR;
                } else if (cause instanceof JsonParseException) {
                    returnCode = CommonReturnCode.REQUEST_PARAM_JSON_FORMAT_ERROR;
                } else {
                    returnCode = CommonReturnCode.PROGRAM_ERROR;
                }
            }
        } else if (e instanceof HttpRequestMethodNotSupportedException) {
            returnCode = CommonReturnCode.UNSUPPORTED_METHOD_TYPE;
        } else if (e instanceof HttpMediaTypeNotSupportedException) {
            returnCode = CommonReturnCode.UNSUPPORTED_MEDIA_TYPE;
        } else if (e instanceof BindException) {
            // Exception during parameter validation through javax.validation related annotations
            returnCode = CommonReturnCode.REQUEST_PARAM_ERROR;
        } else if (e.getCause() != null && e.getCause() instanceof TimeoutException) {
            // The circuit breaker takes effect, and the request timeout fuse
            returnCode = CommonReturnCode.REQUEST_TIMEOUT;
        } else if (e instanceof TaskRejectedException) {
            // The rejection exception thrown by Spring to the ThreadPoolExecutor thread pool is encapsulated as TaskRejectedException
            returnCode = CommonReturnCode.THREAD_REJECT;
        } else {
            returnCode = CommonReturnCode.PROGRAM_ERROR;
        }
        return returnCode;
    }
}
