package org.evlove.common.core.exception;

/**
 * The custom message exception to the FRAMEWORK-LEVEL common runtime exception.
 *
 * The framework will uniformly capture this exception, and returns a fixed ReturnCode
 * and custom error message to the frontend.
 *
 * @author massaton.github.io
 */
public class CommonCustomMessageException extends RuntimeException {

    public CommonCustomMessageException(String message) {
        super(message);
    }
}
