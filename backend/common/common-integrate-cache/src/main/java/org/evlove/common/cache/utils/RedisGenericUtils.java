package org.evlove.common.cache.utils;

import io.lettuce.core.RedisFuture;
import io.lettuce.core.api.async.RedisKeyAsyncCommands;
import io.lettuce.core.api.sync.RedisKeyCommands;
import org.evlove.common.cache.RedisClientConfig;
import org.evlove.common.cache.constant.RedisValueType;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import jakarta.annotation.Resource;
import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * Redis generic operation tool class.
 * The same operation almost supports both synchronous and asynchronous.
 *
 * @author massaton.github.io
 */
@Component
public class RedisGenericUtils extends AbstractRedisUtils {

    @Resource(name = RedisClientConfig.BEAN_NAME_REDIS_SYNC_COMMANDS)
    private RedisKeyCommands<String, String> redisKeyCommands;

    @Resource(name = RedisClientConfig.BEAN_NAME_REDIS_ASYNC_COMMANDS)
    private RedisKeyAsyncCommands<String, String> redisKeyAsyncCommands;

    public RedisKeyCommands<String, String> sync() {
        return this.redisKeyCommands;
    }

    public RedisKeyAsyncCommands<String, String> async() {
        return this.redisKeyAsyncCommands;
    }

    /**
     * Determine is key exist
     * @param key A key
     * @return Does the key exist
     */
    public Boolean exists(String key) {
        long existsCount = redisKeyCommands.exists(key);
        return existsCount == 1;
    }
    /**
     * Determine is key exist (Async)
     * @param key A key
     * @param action Subsequent programs to be executed
     */
    public void existsAsync(String key, BiConsumer<Boolean, Throwable> action) {
        RedisFuture<Long> future = redisKeyAsyncCommands.exists(key);
        CompletionStage<Boolean> stage = future.thenApply(new Function<Long, Boolean>() {
            @Override
            public Boolean apply(Long existsCount) {
                return existsCount == 1;
            }
        });

        if (action != null) {
            stage.whenComplete(action);
        }
    }

    /**
     * Find the keys in Redis based on the given pattern
     * @param pattern Example: x* or *x*y
     * @return Array of keys
     */
    public List<String> searchKeys(String pattern) {
        return redisKeyCommands.keys(pattern);
    }
    /**
     * Find the keys in Redis based on the give pattern (Async)
     * @param pattern Example: x* or *x*y
     * @param action Subsequent programs to be executed
     */
    public void searchKeysAsync(String pattern, BiConsumer<List<String>, Throwable> action) {
        RedisFuture<List<String>> future = redisKeyAsyncCommands.keys(pattern);
        if (action != null) {
            future.whenComplete(action);
        }
    }

    /**
     * Find the storage value type of the specified key
     * @param key The key to search for
     * @return Custom value type enumeration, if key does not exist, returns RedisValueType.NONE enumeration value
     */
    public RedisValueType valueType(String key) {
        String type = redisKeyCommands.type(key);
        if (StringUtils.hasText(type)) {
            return RedisValueType.valueOf(type.toUpperCase());
        }
        return RedisValueType.NONE;
    }

    /**
     * Change the name of the key
     * @param oldKey Old key name
     * @param newKey New key name
     * @return true: if oldKey was renamed to newKey.
     *         false: if newKey already exists.
     */
    public Boolean renameKey(String oldKey, String newKey) {
        return redisKeyCommands.renamenx(oldKey, newKey);
    }
    /**
     * Change the name of the key (Async)
     * @param oldKey Old key name
     * @param newKey New key name
     * @return future object
     */
    public RedisFuture<Boolean> renameKeyAsync(String oldKey, String newKey) {
        return redisKeyAsyncCommands.renamenx(oldKey, newKey);
    }

    /**
     * Delete the specified key, supporting simultaneous deletion of multiple keys
     * @param keys The keys to delete for
     * @return Is it deleted successfully
     */
    public Boolean delete(String... keys) {
        Long deletedCount = redisKeyCommands.del(keys);
        return deletedCount == keys.length;
    }
    /**
     * Asynchronously delete the specified key
     * @param action Subsequent programs to be executed
     * @param keys The keys to delete for
     */
    public void deleteAsync(BiConsumer<Boolean, Throwable> action, String... keys) {
        RedisFuture<Long> future = redisKeyAsyncCommands.del(keys);
        CompletionStage<Boolean> stage = future.thenApply(new Function<Long, Boolean>() {
            @Override
            public Boolean apply(Long deletedCount) {
                return deletedCount == keys.length;
            }
        });

        if (action != null) {
            stage.whenComplete(action);
        }
    }
    /**
     * Asynchronously delete the specified key without subsequent programs to be executed.
     * @param keys The keys to delete for
     */
    public void deleteAsync(String... keys) {
        this.deleteAsync(null, keys);
    }

    // region Related to expire time

    /**
     * Get the remaining time to live(TTL) of the specified key
     * @param key The key to search for
     * @return Long integer-reply TTL in seconds, or a negative value in order to signal an error.
     *         The command returns -1 if the key exists but has no associated expiration time.
     *         The command returns -2 if the key does not exist.
     */
    public Long getExpireTime(String key) {
        return redisKeyCommands.ttl(key);
    }
    /**
     * Asynchronously get the remaining time to live(TTL) of the specified key
     * @param key The key to search for
     * @return Future object, inside Long integer reply TTL in seconds, or a negative value in order to signal an error.
     *         The command returns -1 if the key exists but has no associated expiration time.
     *         The command returns -2 if the key does not exist.
     */
    public RedisFuture<Long> getExpireTimeAsync(String key) {
        return redisKeyAsyncCommands.ttl(key);
    }

    /**
     * Set the time to live(TTL) of the specified key
     * @param key The key to set for
     * @param seconds The seconds of expire time
     * @return true if the timeout was set.
     *         false if key does not exist or the timeout could not be set.
     */
    public Boolean setExpireTime(String key, long seconds) {
        return redisKeyCommands.expire(key, seconds);
    }
    /**
     * Asynchronously set the time to live(TTL) of the specified key
     * @param key The key to set for
     * @param seconds The seconds of expire time
     * @return Future object, inside Boolean is true if the timeout was set.
     *         false if key does not exist or the timeout could not be set.
     */
    public RedisFuture<Boolean> setExpireTimeAsync(String key, long seconds) {
        return redisKeyAsyncCommands.expire(key, seconds);
    }

    /**
     * Remove the time to live (TTL) of the specified key.
     * It is equivalent to setting the key for lang-term storage, that is ,the Expire Time is -1.
     * @param key The key to remove for
     * @return true if the timeout was removed.
     *         false if key does not exist or does not have an associated timeout.
     */
    public Boolean removeExpireTime(String key) {
        return redisKeyCommands.persist(key);
    }
    /**
     * Asynchronously remove the time to live (TTL) of the specified key
     * @param key The key to remove for
     * @return Future object, inside Boolean is true if the timeout was removed.
     *         false if key does not exist or does not have an associated timeout.
     */
    public RedisFuture<Boolean> removeExpireTimeAsync(String key) {
        return redisKeyAsyncCommands.persist(key);
    }

    // endregion
}
