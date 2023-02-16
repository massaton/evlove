package org.evlove.common.core.rcode;

/**
 * Abstract for return code enum classes.
 *
 * CommonReturnCode and other business return code enum needs to implement this interface,
 * so that it can be handled uniformly in the framework.
 *
 * @author massaton.github.io
 */
public interface IReturnCode {
    /**
     * Get exception code.
     */
    Integer getCode();

    /**
     * Get exception description, used to deliver error message to user.
     */
    String getMessage();

    /**
     * Get the name of the enum constant definition.
     */
    String getName();
}
