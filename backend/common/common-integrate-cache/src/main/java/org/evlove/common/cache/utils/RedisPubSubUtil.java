package org.evlove.common.cache.utils;

import io.lettuce.core.AbstractRedisClient;
import io.lettuce.core.RedisClient;
import io.lettuce.core.cluster.RedisClusterClient;
import io.lettuce.core.cluster.pubsub.StatefulRedisClusterPubSubConnection;
import io.lettuce.core.pubsub.RedisPubSubListener;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import io.lettuce.core.pubsub.api.sync.RedisPubSubCommands;
import jakarta.annotation.Resource;
import org.evlove.common.cache.RedisClientConfig;
import org.springframework.stereotype.Component;

/**
 * It is recommended to use Redis Stream to meet the message queue scenario.
 * <p>
 * Notice: Sentinel deployment mode is not supported.
 *
 * @author massaton.github.io
 */
@Component
public class RedisPubSubUtil {

    @Resource(name = RedisClientConfig.BEAN_NAME_REDIS_CLIENT)
    private AbstractRedisClient abstractRedisClient;

    private RedisPubSubCommands<String, String> subCommands;
    private RedisPubSubCommands<String, String> pubCommands;

    public RedisPubSubCommands<String, String> getSubscriber() {
        return subCommands;
    }
    public RedisPubSubCommands<String, String> getPublisher() {
        return pubCommands;
    }

    public void open(RedisPubSubListener<String, String> messageHandlerListener) {
        // Initialize subscribers (consumers)
        if (subCommands == null) {
            this.openSub(messageHandlerListener);
        }

        // Initialize the publisher (producer)
        if (pubCommands == null) {
            this.openPub();
        }
    }

    public void subscribe(String... channels) {
        subCommands.subscribe(channels);
    }

    /**
     * Send a message to the specified channel.
     * @param channel
     * @param message
     * @return Long integer-reply the number of clients that received the message.
     */
    public Long publish(String channel, String message) {
        return pubCommands.publish(channel, message);
    }

    public void unsubscribe(String... channels) {
        subCommands.unsubscribe(channels);
    }

    public void subscribeByPatterns(String... patterns) {
        subCommands.psubscribe(patterns);
    }
    public void unsubscribeByPatterns(String... patterns) {
        subCommands.punsubscribe(patterns);
    }

    private void openSub(RedisPubSubListener<String, String> messageHandlerListener) {
        if (abstractRedisClient instanceof RedisClient redisClient) {
            // Redis stand-alone deployment mode
            StatefulRedisPubSubConnection<String, String> connection = redisClient.connectPubSub();
            connection.addListener(messageHandlerListener);
            subCommands = connection.sync();
        } else if (abstractRedisClient instanceof RedisClusterClient redisClusterClient) {
            // Redis cluster deployment mode (excluding sentinel mode)
            StatefulRedisClusterPubSubConnection<String, String> connection = redisClusterClient.connectPubSub();
            connection.addListener(messageHandlerListener);
            subCommands = connection.sync();
        }
    }

    private void openPub() {
        if (abstractRedisClient instanceof RedisClient redisClient) {
            // Redis stand-alone deployment mode
            StatefulRedisPubSubConnection<String, String> connection = redisClient.connectPubSub();
            pubCommands = connection.sync();
        } else if (abstractRedisClient instanceof RedisClusterClient redisClusterClient) {
            // Redis cluster deployment mode (excluding sentinel mode)
            StatefulRedisClusterPubSubConnection<String, String> connection = redisClusterClient.connectPubSub();
            pubCommands = connection.sync();
        }
    }


}
