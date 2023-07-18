# Explanation
Spring provides RedisTemplate, but it does not support fine-grained and native caching operations (such as: TTL, 
two-level cache, distributed automatic refresh, etc.), and the flexibility and convenience are also average.
Therefore, Alibaba's JetCache second-level cache framework is introduced as a Base, It also encapsulates Lettuce
and provides three cache operation modes with different operation granularity levels to meet various cache usage scenarios.

The operation granularity from coarse to fine is:
1. **Cache annotations:** The annotations provided by JetCache can be used for caching operations through annotations on classes (Spring Bean) and interface methods.
   <br>Reference: https://github.com/alibaba/jetcache/blob/master/docs/CN/MethodCache.md
2. **Cache Interface:** The cache operation object provided by JetCache, injected through CacheManager.
   <br>Reference: https://github.com/alibaba/jetcache/blob/master/docs/CN/CreateCache.md
   <br>_Notice: The @CreateCache has been deprecated in JetCache version 2.7, please use CacheManager.getOrCreateCache(QuickConfig) instead._
3. **RedisXxxUtil:** Combined with common scenarios, Lettuce is further encapsulated, providing basic operations (RedisGenericUtils),
   Common data types (Spring, Hash, Geo), extended types (Number, Object), publish and subscribe mode (RedisPubSubUtils),
   Message Queue (RedisMessageQueueUtils) and other operation classes, common operations provide both synchronous and asynchronous 
   operation methods, which are easy to use.

# Notice
- Lettuce use Netty to establish a single connection to Redis, use NIO multiplexing, and is thread-safe, so there is no need to configure a connection pool.
- When using the asynchronous method in RedisXxxUtils, in the Feature method (BiConsumer), you can no longer refer to RedisXxxUtils for other Redis operations,
  otherwise the Redis connection will time out, the reason may be that the connection is not released during the Feature process.

# Configuration
## Start Class Annotation

```java
import jakarta.annotation.Resource;

// Enable the @CreateCache annotation to create a Cache object - The @CreateCache has been deprecated in JetCache version 2.7, @EnableCreateCacheAnnotation is also deprecated.
@EnableCreateCacheAnnotation
// Enable the method-level cache operation function of @CacheXxx series
@EnableMethodCache(basePackages = FrameworkPackageInfo.BASE)
public class XxxApplication {
    // Introduce the cache operation class encapsulated by the framework where the cache need to be operated
    @Resource
    private RedisXxxUtils redisXxxUtils;

    // The following is the cache operation class injection method provided by JetCache
    @Autowired
    private CacheManager cacheManager;
    private Cache<String, UserDO> userCache;
    @PostConstruct
    public void init() {
        QuickConfig qc = QuickConfig.newBuilder("userCache")
                .expire(Duration.ofSeconds(100))
                .cacheType(CacheType.BOTH) // two level cache
                .syncLocal(true) // invalidate local cache in all jvm process after update
                .build();
        userCache = cacheManager.getOrCreateCache(qc);
    }
    // ----------
}
```

## application.yml Configuration

```yaml
# see com.alicp.jetcache.autoconfigure.JetCacheProperties
jetcache:
  # Statistics interval (unit: minute), 0 means no statistics, default is 0
  statIntervalMinutes: 10
  # Whether areaName (default and other custom names in the remote configuration item) is used as the cache key prefix, the default is ture
  areaInCacheName: false
  # Whether to protect the concurrent loading behavior when the cache access misses 
  # (that is, to prevent avalanche, only protected inside the JVM, to ensure that only one thread loads the same KEY in the same JVM), 
  # default is false
  penetrationProtect: true
  # When @Cached and @CreateCache automatically generate the name, in order not to make the name too long, specify the truncated package name prefix
  hidePackages:
    - com.massless.framework
  # Local cache configuration
  local:
    default:
      # Already supported optional: linkedhashmap, caffeine
      type: caffeine
      # The global configuration of the key converter, currently only: fastjson - @see com.alicp.jetcache.support.FastjsonKeyConvertor
      keyConvertor: fastjson
      # The global configuration of the maximum element of each cache instance, only the cache of the local type needs to be specified
      limit: 100
      # Specifies how long the local cache has not been accessed, and the cache will be invalidated (unit: milliseconds),
      # 0 means to disable the function, this function is available after JetCache 2.2
      expireAfterAccessInMillis: 30000
  # Remote cache configuration
  remote:
    default:
      # Already supported optional: redis, tair
      type: redis.lettuce
      # Format - redis://[password@]host[:port][/databaseNumber][?[timeout=timeout[d|h|m|s|ms|us|ns]]
      # Tip - Do not include special characters such as '@' in the password, otherwise '${**}' will be incompatible when injecting parameters
      uri: redis://123456@127.0.0.1:6379/0?timeout=5s
      # The global configuration of the key converter, currently only: fastjson - @see com.alicp.jetcache.support.FastjsonKeyConvertor
      keyConvertor: fastjson
      # Global configuration for serializers. Already supported optional: java, kryo
      valueEncoder: java
      valueDecoder: java
      # Global configuration specifying timeouts in milliseconds
      expireAfterWriteInMillis: 5000
```

## URI Configuration in Cluster Deployment Mode
```yaml
jetcache:
  remote:
    default:
      mode: cluster
      uri:
        - redis://password@127.0.0.1:7001/0?timeout=5s
        - redis://password@127.0.0.1:7002/0?timeout=5s
        - redis://password@127.0.0.1:7003/0?timeout=5s
```

## URI Configuration in Sentry Deployment Mode
```yaml
jetcache:
  remote:
    default:
      password: xxxxxx
      masterName: xxx
      database: 0
      uri: redis-sentinel://${jetcache.remote.default.password}@localhost:8001,localhost:8002,localhost:8003/?sentinelMasterId=${jetcache.remote.default.masterName}&database=${jetcache.remote.default.database}
```