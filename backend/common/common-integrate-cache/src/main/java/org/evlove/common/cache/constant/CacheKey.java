package org.evlove.common.cache.constant;

/**
 * Overall management of common cache Key related elements.
 * You can customize constants in the service to manage your own cache Key elements
 *
 * @author massaton.github.io
 */
public class CacheKey {
    /**
     * The delimiter between words in the name of the cache key
     */
    public final static String SEPARATOR = "_";

    /**
     * The cache key prefix of the user Token
     */
    public final static String PREFIX_USER_TOKEN = "USER_TOKEN_";

    /**
     * The cache key prefix of the Anti-Duplicate ticket
     */
    public final static String PREFIX_NO_REPEAT_TICKET = "NO_REPEAT_TICKET_";
}
