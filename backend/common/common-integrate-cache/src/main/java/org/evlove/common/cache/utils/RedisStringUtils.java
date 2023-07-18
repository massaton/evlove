package org.evlove.common.cache.utils;

import io.lettuce.core.KeyValue;
import io.lettuce.core.RedisFuture;
import io.lettuce.core.api.async.RedisStringAsyncCommands;
import io.lettuce.core.api.sync.RedisStringCommands;
import org.evlove.common.cache.RedisClientConfig;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import jakarta.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletionStage;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * The operation tool class of Redis string value type.
 * Redis related commands: https://redis.io/commands#string
 *
 * The same operation almost supports both synchronous and asynchronous.
 * But, the method used to obtain (get*) does not provide an asynchronous method.
 * When this type of method is actually used, it will inject dependent
 * codes (such as service or persistence layer classes) before and after obtaining data in Redis,
 * so use Spring itself The provided @Async can more flexibly meet asynchronous processing requirements.
 *
 * @author massaton.github.io
 */
@Component
public class RedisStringUtils extends AbstractRedisUtils {

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

    // region Obtain Method - Don't provide asynchronous methods
    /**
     * Get the value of a key.
     *
     * @param key the key.
     * @return reply the value of key, or null when key does not exist.
     */
    public String get(String key) {
        return redisStringCommands.get(key);
    }
    /**
     * Get the values of all the given keys.
     *
     * @param keys the keys.
     * @return reply list of values at the specified keys.
     */
    public Map<String, String> get(String... keys) {
        Map<String, String> result = new HashMap<>(keys.length);

        List<KeyValue<String, String>> keyValues = redisStringCommands.mget(keys);
        for (KeyValue<String, String> keyValue : keyValues) {
            result.put(
                    keyValue.getKey(),
                    keyValue.getValue()
            );
        }

        return result;
    }
    /**
     * Get the value of key and delete the key.
     * Notice: The GETDEL command is supported starting from Redis version 6.2
     *
     * @param key the key.
     * @return reply the value of key, or null when key does not exist.
     */
    public String getAndDelete(String key) {
        return redisStringCommands.getdel(key);
    }
    // endregion


    // region Set Method
    /**
     * Set the string value of a key.
     * @param key the key.
     * @param value the value.
     * @return reply Ture if SET was executed correctly.
     */
    public Boolean set(String key, String value) {
        String result = redisStringCommands.set(key, value);
        return itsOk(result);
    }
    /**
     * Asynchronous set the string value of a key.
     * @param key the key.
     * @param value the value.
     * @param action Subsequent programs to be executed.
     */
    public void setAsync(String key, String value, BiConsumer<Boolean, Throwable> action) {
        RedisFuture<String> future = redisStringAsyncCommands.set(key, value);
        CompletionStage<Boolean> stage = future.thenApply(new Function<String, Boolean>() {
            @Override
            public Boolean apply(String result) {
                return itsOk(result);
            }
        });
        if (action != null) {
            stage.whenComplete(action);
        }
    }
    /**
     * Asynchronous set the string value of a key without subsequent programs to be executed.
     * @param key the key.
     * @param value the value.
     */
    public void setAsync(String key, String value) {
        this.setAsync(key, value, null);
    }

    /**
     * Set the value and expiration of a key.
     * @param key the key.
     * @param value the value.
     * @param seconds the seconds of expire time.
     * @return redis command execution result.
     */
    public Boolean set(String key, String value, long seconds) {
        String result = redisStringCommands.setex(key, seconds, value);
        return itsOk(result);
    }

    /**
     * Asynchronous set the value and expiration of a key.
     * @param key the key.
     * @param value the value.
     * @param seconds the seconds of expire time.
     * @param action subsequent programs to be executed.
     */
    public void setAsync(String key, String value, long seconds, BiConsumer<Boolean, Throwable> action) {
        RedisFuture<String> future = redisStringAsyncCommands.setex(key, seconds, value);
        CompletionStage<Boolean> stage = future.thenApply(new Function<String, Boolean>() {
            @Override
            public Boolean apply(String result) {
                return itsOk(result);
            }
        });
        if (action != null) {
            stage.whenComplete(action);
        }
    }
    /**
     * Asynchronous set the value and expiration of a key without subsequent programs to be executed.
     * @param key the key.
     * @param value the value.
     * @param seconds the seconds of expire time.
     */
    public void setAsync(String key, String value, long seconds) {
        this.setAsync(key, value, seconds, null);
    }
    /**
     * Set multiple keys to multiple values, only if none of the keys exist.
     * @param keyValues the map of the keys and values.
     * @return reply specifically: Ture if the all the keys were set. False if no key was set (at least one key already existed).
     */
    public Boolean setBatch(Map<String, String> keyValues) {
        if (ObjectUtils.isEmpty(keyValues)) {
            return true;
        }
        return redisStringCommands.msetnx(keyValues);
    }
    /**
     * Asynchronous set multiple keys to multiple values, only if none of the keys exist.
     *
     * @param keyValues the map of the keys and values.
     * @param action subsequent programs to do executed.
     */
    public void setBatchAsync(Map<String, String> keyValues, BiConsumer<Boolean, Throwable> action) {
        if (ObjectUtils.isEmpty(keyValues)) {
            return;
        }
        RedisFuture<Boolean> future = redisStringAsyncCommands.msetnx(keyValues);
        CompletionStage<Boolean> completionStage = future.thenApply(new Function<Boolean, Boolean>() {
            @Override
            public Boolean apply(Boolean result) {
                return result;
            }
        });
        if (action != null) {
            completionStage.whenComplete(action);
        }
    }
    /**
     * Asynchronous set multiple keys to multiple values, only if none of the keys exist without subsequent programs to do executed.
     *
     * @param keyValues the map of the keys and values.
     */
    public void setBatchAsync(Map<String, String> keyValues) {
        this.setBatchAsync(keyValues, null);
    }

    /**
     * Append a value to a key.
     * If the key already exists and is a string, the APPEND command appends value to the end of the original value of the key.
     * If the key does not exist, the APPEND command simply sets the given key to value, just like executing SET command.
     *
     * @param key the key.
     * @param appendValue value to be added.
     * @return reply the length of the string after the append operation.
     */
    public Long append(String key, String appendValue) {
        return redisStringCommands.append(key, appendValue);
    }
    /**
     * Asynchronous append a value to a key.
     */
    public RedisFuture<Long> appendAsync(String key, String appendValue) {
        return redisStringAsyncCommands.append(key, appendValue);
    }
    // endregion

}
