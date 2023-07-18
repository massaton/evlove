package org.evlove.common.cache;

import com.alicp.jetcache.autoconfigure.LettuceFactory;
import com.alicp.jetcache.autoconfigure.RedisLettuceAutoConfiguration;
import io.lettuce.core.AbstractRedisClient;
import io.lettuce.core.cluster.api.async.RedisClusterAsyncCommands;
import io.lettuce.core.cluster.api.reactive.RedisClusterReactiveCommands;
import io.lettuce.core.cluster.api.sync.RedisClusterCommands;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

/**
 * Provide various types of Redis client operation endpoints based on Lettuce
 *
 * It has nothing to do with JetCache, and only uses the Redis link managed by JetCache to provide additional cache operation methods.
 *
 * On this basis, further encapsulation is carried out, which is convenient for developers to use.
 * For details, see each Redis***Util class in the util package.
 *
 * @author massaton.github.io
 */
@Configuration
public class RedisClientConfig {
    /**
     * Cache area prefix
     */
    private final String LETTUCE_FACTORY_KEY = "remote.default";

    /**
     * Bean Name of various Redis Clients
     */
    public static final String BEAN_NAME_REDIS_CLIENT = "redisClient";
    public static final String BEAN_NAME_REDIS_SYNC_COMMANDS = "redisSyncCommands";
    public static final String BEAN_NAME_REDIS_ASYNC_COMMANDS = "redisAsyncCommands";
    public static final String BEAN_NAME_REDIS_REACTIVE_COMMANDS = "redisReactiveCommands";

    /**
     * Inject the Redis general command operation class (AbstractRedisClient.class) into the Spring context.
     * You can directly use Lettuce's RedisClient to rebuild Lettuce's Connection, Commands, etc.
     *
     * Example:
     * <pre>
     *      @Resource(name = "redisClient")
     *      private RedisClient redisClient;
     *      RedisClusterCommands<String, String> commands = redisClient.connect().sync();
     *      commands.***();
     * </pre>
     */
    @Bean(name = BEAN_NAME_REDIS_CLIENT)
    @DependsOn(RedisLettuceAutoConfiguration.AUTO_INIT_BEAN_NAME)
    public LettuceFactory defaultClient() {
        return new LettuceFactory(LETTUCE_FACTORY_KEY, AbstractRedisClient.class);
    }

    /**
     * Inject the Redis sync command operation class (RedisClusterCommands.class) into the Spring context.
     *
     * Explain:
     * - Lettuce still uses the asynchronous method when implementing the synchronous interface, which can reduce the
     *   multi-thread blocking caused by synchronization in the case of a single connection.
     * - The performance is about 10 times higher than using redisClient.connect().sync(), because it saves the time to connect to Redis.
     *
     * It can be directly injected into the collection interface (RedisClusterCommands) or its domain-specific interface to perform synchronous operations of Redis commands.
     * The domain-specific interface for synchronization is as follows:
     * BaseRedisCommands<K, V>
     * RedisGeoCommands<K, V>
     * RedisHashCommands<K, V>
     * RedisHLLCommands<K, V>
     * RedisKeyCommands<K, V>
     * RedisListCommands<K, V>
     * RedisScriptingCommands<K, V>
     * RedisServerCommands<K, V>
     * RedisSetCommands<K, V>
     * RedisSortedSetCommands<K, V> —— Capable of scoring, sorting, etc., it can more conveniently complete the caching scenario of content identification corresponding to content recommendation scores in content recommendation business
     * RedisStreamCommands<K, V>
     * RedisStringCommands<K, V>
     */
    @Bean(name = BEAN_NAME_REDIS_SYNC_COMMANDS)
    @DependsOn(RedisLettuceAutoConfiguration.AUTO_INIT_BEAN_NAME)
    public LettuceFactory redisSyncCommands() {
        return new LettuceFactory(LETTUCE_FACTORY_KEY, RedisClusterCommands.class);
    }


    /**
     * Inject the Redis async command operation class (RedisClusterAsyncCommands.class) into the Spring context.
     *
     * Explain:
     * - The performance is about 10 times higher than using redisClient.connect().sync(), because it saves the time to connect to Redis.
     *
     * The collection interface (RedisClusterCommands) and its domain-specific interfaces can be directly injected to perform asynchronous operations of Redis commands.
     * The asynchronous domain-specific interface is as follows:
     * BaseRedisAsyncCommands<K, V>
     * RedisGeoAsyncCommands<K, V>
     * RedisHashAsyncCommands<K, V>
     * RedisHLLAsyncCommands<K, V>
     * RedisKeyAsyncCommands<K, V>
     * RedisListAsyncCommands<K, V>
     * RedisScriptingAsyncCommands<K, V>
     * RedisServerAsyncCommands<K, V>
     * RedisSetAsyncCommands<K, V>
     * RedisSortedSetAsyncCommands<K, V>
     * RedisStreamAsyncCommands<K, V>
     * RedisStringAsyncCommands<K, V>
     */
    @Bean(name = BEAN_NAME_REDIS_ASYNC_COMMANDS)
    @DependsOn(RedisLettuceAutoConfiguration.AUTO_INIT_BEAN_NAME)
    public LettuceFactory redisAsyncCommands() {
        return new LettuceFactory(LETTUCE_FACTORY_KEY, RedisClusterAsyncCommands.class);
    }

    /**
     * Inject Redis reactive command operation class (RedisClusterReactiveCommands.class) into the Spring context.
     * Provides an operation method for the responsive programming style (essentially provides an optimized, event-driven asynchronous call method)
     */
    @Bean(name = BEAN_NAME_REDIS_REACTIVE_COMMANDS)
    @DependsOn(RedisLettuceAutoConfiguration.AUTO_INIT_BEAN_NAME)
    public LettuceFactory redisReactiveCommands() {
        return new LettuceFactory(LETTUCE_FACTORY_KEY, RedisClusterReactiveCommands.class);
    }
}
