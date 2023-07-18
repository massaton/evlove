package org.evlove.common.cache.utils;

import com.alibaba.fastjson2.JSON;
import io.lettuce.core.RedisFuture;
import io.lettuce.core.api.async.RedisStringAsyncCommands;
import io.lettuce.core.api.sync.RedisStringCommands;
import org.evlove.common.cache.RedisClientConfig;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import jakarta.annotation.Resource;
import java.util.concurrent.CompletionStage;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * Provide a convenient tool class for accessing POJO object types in Redis.
 * Essentially, it operates on String type.
 *
 * @author massaton.github.io
 */
@Component
public class RedisObjectUtils extends AbstractRedisUtils {

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


    public <T> Boolean set(String key, T obj) {
        String result = redisStringCommands.set(
                key,
                JSON.toJSONString(obj)
        );
        return itsOk(result);
    }
    public <T> void setAsync(String key, T obj, BiConsumer<Boolean, Throwable> action) {
        RedisFuture<String> future = redisStringAsyncCommands.set(
                key,
                JSON.toJSONString(obj)
        );
        CompletionStage<Boolean> completionStage = future.thenApply(new Function<String, Boolean>() {
            @Override
            public Boolean apply(String result) {
                return itsOk(result);
            }
        });
        if (action != null) {
            completionStage.whenComplete(action);
        }
    }
    public <T> void setAsync(String key, T obj) {
        this.setAsync(key, obj, null);
    }

    public <T> T get(String key, Class<T> clazz) {
        String jsonValue = redisStringCommands.get(key);
        if (StringUtils.hasText(jsonValue)) {
            return JSON.parseObject(jsonValue, clazz);
        }
        return null;
    }
    public <T> T getAndDelete(String key, Class<T> clazz) {
        String jsonValue = redisStringCommands.getdel(key);
        if (StringUtils.hasText(jsonValue)) {
            return JSON.parseObject(jsonValue, clazz);
        }
        return null;
    }
}
