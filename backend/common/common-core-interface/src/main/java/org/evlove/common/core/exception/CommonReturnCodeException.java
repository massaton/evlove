package org.evlove.common.core.exception;

import org.evlove.common.core.rcode.IReturnCode;

/**
 * The return code exception to the FRAMEWORK-LEVEL common runtime exception.
 *
 * The framework will uniformly capture this exception, and return the ReturnCode
 * specified by the user of the exception to the frontend.
 *
 * @author massaton.github.io
 */
public class CommonReturnCodeException extends RuntimeException {

    private IReturnCode _returnCode;

    public CommonReturnCodeException(IReturnCode returnCode) {
        this._returnCode = returnCode;
    }

    public IReturnCode getReturnCode() {
        return this._returnCode;
    }
}
