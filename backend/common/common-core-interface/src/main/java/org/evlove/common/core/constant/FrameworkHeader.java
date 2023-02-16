package org.evlove.common.core.constant;

/**
 * Define the parameter names of custom HTTP request and response headers required by the framework.
 *
 * Note: The custom HTTP header parameter belongs to the rule definition of the framework,
 *       and it will remain unchanged as much as possible after being determined. Therefore,
 *       it is deleted from the gateway configuration file and stored as a constant in the
 *       core dependency of the framework, which is also convenient other modules to use.
 *
 * @author massaton.github.io
 */
public class FrameworkHeader {

    /**
     * Define request header parameters
     */
    public class Request {
        // Compulsory request header parameters
        public final static String APP_ID = "appId";
        public final static String CLIENT_TYPE = "clientType";
        public final static String CLIENT_VERSION = "clientVersion";
        public final static String SIGN_TYPE = "signType";
        public final static String SIGN = "sign";
        public final static String TIMESTAMP = "timestamp";

        // Optional request header parameters
        public final static String USER_ID = "userId";
        public final static String RSA_KEY_VERSION = "rsaKeyVersion";
        public final static String APPLY_NO_REPEAT_TICKET = "applyNoRepeatTicket";
        public final static String NO_REPEAT_TICKET = "noRepeatTicket";
        public final static String EXTRA = "extra";
    }

    /**
     * Define response header parameters
     */
    public class Response {
        public final static String SIGN_TYPE = "signType";
        public final static String SIGN = "sign";
        public final static String TIMESTAMP = "timestamp";
        public final static String NO_REPEAT_TICKET = "noRepeatTicket";
    }
}
