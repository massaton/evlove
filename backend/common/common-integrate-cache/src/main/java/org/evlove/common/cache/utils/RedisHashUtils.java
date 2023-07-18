package org.evlove.common.cache.utils;

import io.lettuce.core.RedisFuture;
import io.lettuce.core.api.async.RedisHashAsyncCommands;
import io.lettuce.core.api.sync.RedisHashCommands;
import jakarta.annotation.Resource;
import org.evlove.common.cache.RedisClientConfig;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.Map;
import java.util.concurrent.CompletionStage;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * Operation tool class for Hash data structures in Redis.
 * The Hash data structures is a mapping table of string type filed and values.
 *
 * @author massaton.github.io
 */
@Component
public class RedisHashUtils extends AbstractRedisUtils {

    @Resource
    private RedisGenericUtils redisGenericUtils;

    @Resource(name = RedisClientConfig.BEAN_NAME_REDIS_SYNC_COMMANDS)
    private RedisHashCommands<String, String> redisHashCommands;

    @Resource(name = RedisClientConfig.BEAN_NAME_REDIS_ASYNC_COMMANDS)
    private RedisHashAsyncCommands<String, String> redisHashAsyncCommands;

    public RedisHashCommands<String, String> sync() {
        return this.redisHashCommands;
    }

    public RedisHashAsyncCommands<String, String> async() {
        return this.redisHashAsyncCommands;
    }

    public Map<String, String> getAll(String key) {
        return redisHashCommands.hgetall(key);
    }

    public String getValue(String key, String field) {
        return redisHashCommands.hget(key, field);
    }

    public Boolean set(String key, Map<String, String> map) {
        if (ObjectUtils.isEmpty(map)) {
            return true;
        }

        Long count = redisHashCommands.hset(key, map);

        // If the field exists, the corresponding value will be modified, but it will not be counted(that is, count==0)
        // so return ture directly.
        return true;
    }

    public void setAsync(String key, Map<String, String> map) {
        this.setAsync(key, map, null);
    }

    public void setAsync(String key, Map<String, String> map, BiConsumer<Boolean, Throwable> action) {
        if (ObjectUtils.isEmpty(map)) {
            return;
        }

        RedisFuture<Long> future = redisHashAsyncCommands.hset(key, map);
        CompletionStage<Boolean> completionStage = future.thenApply(new Function<Long, Boolean>() {
            @Override
            public Boolean apply(Long count) {
                // If the field exists, the corresponding value will be modified, but it will not be counted(that is, count==0)
                // so return ture directly.
                return true;
            }
        });

        if (action != null) {
            completionStage.whenComplete(action);
        }
    }

    public Boolean set(String key, String field, String value) {
        redisHashCommands.hset(key, field, value);

        // If the field exists, the corresponding value will be modified, but it will return false,
        // so return ture directly.
        return true;
    }

    public void setAsync(String key, String field, String value) {
        this.setAsync(key, field, value, null);
    }

    public void setAsync(String key, String field, String value, BiConsumer<Boolean, Throwable> action) {
        RedisFuture<Boolean> future = redisHashAsyncCommands.hset(key, field, value);

        CompletionStage<Boolean> completionStage = future.thenApply(new Function<Boolean, Boolean>() {
            @Override
            public Boolean apply(Boolean result) {
                // If the field exists, the corresponding value will be modified, but it will return false,
                // so return ture directly.
                return true;
            }
        });
        if (action != null) {
            completionStage.whenComplete(action);
        }
    }


    public Boolean deleteAll(String... keys) {
        return redisGenericUtils.delete(keys);
    }

    public Boolean delete(String key, String... fields) {
        // The count just including fields that are specified and existed.
        Long count = redisHashCommands.hdel(key, fields);
        // Consider excluding fields that are specified but do not exist, so return ture directly.
        return true;
    }
}
