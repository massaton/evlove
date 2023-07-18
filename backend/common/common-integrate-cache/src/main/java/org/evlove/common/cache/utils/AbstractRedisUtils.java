package org.evlove.common.cache.utils;

import org.evlove.common.cache.constant.CacheConstant;

import java.nio.charset.StandardCharsets;

/**
 * @author massaton.github.io
 */
abstract class AbstractRedisUtils {

    protected boolean itsOk(String redisCommandExecutionResult) {
        return CacheConstant.RESULT_OK.equalsIgnoreCase(redisCommandExecutionResult);
    }

    /*protected byte[] toBytes(String str) {
        return str.getBytes(StandardCharsets.UTF_8);
    }

    protected String toString(Object byteObj) {
        if (byteObj == null) {
            return null;
        }
        return new String((byte[]) byteObj, StandardCharsets.UTF_8);
    }*/
}