package org.evlove.common.cache.constant;

/**
 * Redis sort types
 *
 * @author massaton.github.io
 */
public enum RedisSortType {
    /**
     * Positive sequence (from small to large, from near to far)
     */
    ASC,
    /**
     * Reverse order (from large to small, from far to near)
     */
    DESC
}