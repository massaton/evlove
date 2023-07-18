package org.evlove.common.cache.constant;

/**
 * Redis value types
 *
 * @author massaton.github.io
 */
public enum RedisValueType {
    /**
     * List
     */
    LIST,
    /**
     * Set
     */
    SET,
    /**
     * Sorted Set
     */
    ZSET,
    /**
     * Hash table
     */
    HASH,
    /**
     * String
     */
    STRING,
    /**
     * Key does not exist
     */
    NONE
}
