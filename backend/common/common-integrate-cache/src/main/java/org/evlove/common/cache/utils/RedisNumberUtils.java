package org.evlove.common.cache.utils;

import io.lettuce.core.RedisFuture;
import io.lettuce.core.api.async.RedisStringAsyncCommands;
import io.lettuce.core.api.sync.RedisStringCommands;
import org.evlove.common.cache.RedisClientConfig;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;

/**
 * Tool class for manipulating values of type number (essentially String type storage).
 *
 * @author massaton.github.io
 */
@Component
public class RedisNumberUtils {

    @Resource(name = RedisClientConfig.BEAN_NAME_REDIS_SYNC_COMMANDS)
    private RedisStringCommands<String, String> redisStringCommands;

    @Resource(name = RedisClientConfig.BEAN_NAME_REDIS_ASYNC_COMMANDS)
    private RedisStringAsyncCommands<String, String> redisStringAsyncCommands;

    public RedisStringCommands<String, String> sync() {
        return this.redisStringCommands;
    }

    public RedisStringAsyncCommands<String, String> async() {
        return this.redisStringAsyncCommands;
    }

    /**
     * Increment the integer value of a key by the given amount.
     *
     * If the key does not exist, the value of the key will be initialized to 0 before executing the INCREBY command.
     * If the value contains an incorrect type, or if a value of string type cannot be represented as a number, then an error is returned.
     * The value of this operation is limited to 64 bit signed numerical representations.
     *
     * @param key the key.
     * @param amount the increment.
     * @return reply the value of key after the increment.
     */
    public Long increment(String key, long amount) {
        return redisStringCommands.incrby(key, amount);
    }
    public RedisFuture<Long> incrementAsync(String key, long amount) {
        return redisStringAsyncCommands.incrby(key, amount);
    }
    public Double increment(String key, double amount) {
        return redisStringCommands.incrbyfloat(key, amount);
    }
    public RedisFuture<Double> incrementAsync(String key, double amount) {
        return redisStringAsyncCommands.incrbyfloat(key, amount);
    }

    /**
     * Decrement the integer value of a key by the given number.
     *
     * If the key does not exist, the value of the key will be initialized to 0 before executing the DECRBY command.
     * If the value contains an incorrect type, or if a value of string type cannot be represented as a number, then an error is returned.
     * The value of this operation is limited to 64 bit signed numerical representations.
     *
     * @param key
     * @param amount
     * @return
     */
    public Long decrement(String key, long amount) {
        return redisStringCommands.decrby(key, amount);
    }
    public RedisFuture<Long> decrementAsync(String key, long amount) {
        return redisStringAsyncCommands.decrby(key, amount);
    }
}
