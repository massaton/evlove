package org.evlove.common.cache.utils;

import io.lettuce.core.*;
import io.lettuce.core.api.async.RedisStreamAsyncCommands;
import io.lettuce.core.api.sync.RedisStreamCommands;
import io.lettuce.core.models.stream.PendingMessages;
import jakarta.annotation.Resource;
import org.evlove.common.cache.RedisClientConfig;
import org.evlove.common.cache.pojo.RedisMessage;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.evlove.common.cache.constant.CacheConstant.RESULT_OK;

/**
 * Based on Redis 5.0+, satisfying the implementation of message queue in simple scenarios.
 * <p>
 * ref:
 * <a href="https://redis.io/commands/xgroup-create/">Redis Stream Operation Instructions</a>
 * <a href="https://www.runoob.com/redis/redis-stream.html">Redis Stream Guide</a>
 * <p>
 * Redis Stream is a newly added data structure in Redis version 5.0.
 * Redis Stream is mainly used for MQ (Message Queues). Redis itself has a Redis publish subscription (pub/sub) to
 * realize the function of message queues, but it has a disadvantage that messages cannot be persisted.
 * Open, Redis down, etc., the message will be discarded.
 * Simply put, publish and subscribe (pub/sub) can distribute messages, but cannot record historical message.
 * Redis Stream provides message persistence and master-backup replication functions, allowing any client to access
 * data at any time, remembering the access location of each client, and ensuring that messages are not lost.
 *
 * @author massaton.github.io
 */
@Component
public class RedisMessageQueueUtil {

    @Resource(name = RedisClientConfig.BEAN_NAME_REDIS_SYNC_COMMANDS)
    private RedisStreamCommands<String, String> redisStreamCommands;

    @Resource(name = RedisClientConfig.BEAN_NAME_REDIS_ASYNC_COMMANDS)
    private RedisStreamAsyncCommands<String, String> redisStreamAsyncCommands;

    public RedisStreamCommands<String, String> sync() {
        return this.redisStreamCommands;
    }
    public RedisStreamAsyncCommands<String, String> async() {
        return this.redisStreamAsyncCommands;
    }

    /**
     * Create a new consumer group uniquely identified by groupName for the stream stored at queueName(key)
     * @param queueName
     * @param groupName
     * @param fromHead Whether to consume from the head of the queue, otherwise consume from the tail of the queue
     * @return
     */
    public Boolean createConsumerGroup(String queueName, String groupName, Boolean fromHead) {
        String ackResult;

        try {
            // offset
            // 0: indicates that the group starts to consume from the first message
            // $: indicates that the group starts to consume from the latest message
            String offset = fromHead ? "0" : "$";
            XReadArgs.StreamOffset<String> streamOffset = XReadArgs.StreamOffset.from(queueName, offset);
            // Make a stream(queue) if it does not exist.
            XGroupCreateArgs args = new XGroupCreateArgs().mkstream(true);

            ackResult = redisStreamCommands.xgroupCreate(streamOffset, groupName, args);
        } catch (Exception e) {
            String busyGroup = "BUSYGROUP";
            if (e instanceof RedisBusyException && e.getMessage().contains(busyGroup)) {
                // When a consumer group with the same name already exists, the command returns a -BUSYGROUP error.
                return true;
            } else {
                throw e;
            }
        }

        return RESULT_OK.equals(ackResult);
    }

    /**
     * Push a message to the specified queue
     * @param queueName
     * @param message
     * @return
     */
    public String pushMessage(String queueName, Map<String, String> message) {
        String messageId = redisStreamCommands.xadd(queueName, message);
        return messageId;
    }

    /**
     * Read messages in consumer group
     * @param queueName The name of the message queue (ie key)
     * @param groupName Consumer group name
     * @param consumerName Consumer name, each consumer will hold its own list of pending messages
     * @param autoAck Whether to automatically complete the consumed reply of the message
     * @param pullCount The number o messages that need to be pulled this time
     * @return
     */
    public List<RedisMessage> pullMessages(String queueName, String groupName, String consumerName, boolean autoAck, Integer pullCount) {
        XReadArgs readArgs = XReadArgs.Builder
                .noack(autoAck)
                .count(pullCount);

        // Read all new arriving elements from the stream identified by name with ids greater than the last one consumed by the consumer group.
        XReadArgs.StreamOffset<String> streamOffset = XReadArgs.StreamOffset.lastConsumed(queueName);

        Consumer<String> consumer = Consumer.from(groupName, consumerName);

        List<StreamMessage<String, String>> messages = redisStreamCommands.xreadgroup(
                consumer,
                readArgs,
                streamOffset
        );

        return transMessage(messages);
    }

    /**
     * Obtain messages that have been read by the consumer but have not yet been acknowledged.
     * @return
     */
    public List<RedisMessage> pullPendingMessages(String queueName, String groupName, Integer pullCount) {
        Range<String> range = Range.unbounded();

        // just get the meta information of the pending message, and does not differentiate between consumers
        PendingMessages pendingMessages = redisStreamCommands.xpending(
                queueName,
                groupName
        );

        List<StreamMessage<String, String>> messages = redisStreamCommands.xrange(
                queueName,
                pendingMessages.getMessageIds(),
                Limit.from(pullCount)
        );

        return transMessage(messages);
    }

    /**
     * Acknowledge one or more messages as processed.
     * @param queueName
     * @param groupName
     * @param messageIds
     * @return reply the lenght of acknowledged messages.
     */
    public Long ackMessage(String queueName, String groupName, String... messageIds) {
        return redisStreamCommands.xack(queueName, groupName, messageIds);
    }

    /**
     * Removes the specified entries from the stream. Returns the number of items deleted,
     * that may be different from the number of IDs passed in case certain IDs do not exist.
     * @param queueName
     * @param messageIds
     * @return reply number of removed entries.
     */
    public Long deleteMessage(String queueName, String... messageIds) {
        return redisStreamCommands.xdel(queueName, messageIds);
    }


    private List<RedisMessage> transMessage(List<StreamMessage<String, String>> messages) {
        List<RedisMessage> results = new ArrayList<>();

        for (StreamMessage<String, String> message: messages) {
            RedisMessage redisMessage = new RedisMessage();
            redisMessage.setId(message.getId());
            redisMessage.setContent(message.getBody());
            results.add(redisMessage);
        }

        return results;
    }
}
