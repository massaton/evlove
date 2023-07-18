package org.evlove.common.cache.utils;

import io.lettuce.core.RedisFuture;
import io.lettuce.core.ScoredValue;
import io.lettuce.core.api.async.RedisSortedSetAsyncCommands;
import io.lettuce.core.api.sync.RedisSortedSetCommands;
import jakarta.annotation.Resource;
import org.evlove.common.cache.RedisClientConfig;
import org.evlove.common.cache.constant.RedisSortType;
import org.evlove.common.cache.pojo.MemberScore;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

/**
 * Redis's ordered collection is also a collection of string type elements like collections, and duplicate members are not allowed.
 * The differance is that each element will be associated with a score of type double. Redis uses scores to sort the
 * members of the set from small to large.
 * The members of the ordered set are unique, but the score can be repeated.
 * The collection is implemented through a hash table, so the complexity of adding, deleting, and searching is O(1).
 * The maximum number of members in a collection is 2ˆ32-1(4294967295), i.e. each collection can store more than 4 billion members.
 *
 * @author massaton.github.io
 */
@Component
public class RedisSortSetUtils extends AbstractRedisUtils {
    @Resource
    private RedisGenericUtils redisGenericUtils;

    @Resource(name = RedisClientConfig.BEAN_NAME_REDIS_SYNC_COMMANDS)
    private RedisSortedSetCommands<String, String> redisSortedSetCommands;

    @Resource(name = RedisClientConfig.BEAN_NAME_REDIS_ASYNC_COMMANDS)
    private RedisSortedSetAsyncCommands<String, String> redisSortedSetAsyncCommands;

    public RedisSortedSetCommands<String, String> sync() {
        return this.redisSortedSetCommands;
    }

    public RedisSortedSetAsyncCommands<String, String> async() {
        return this.redisSortedSetAsyncCommands;
    }

    /**
     * Add one or more members to a sorted set, or update its score if it already exists.
     * @param key The key
     * @param members Member information to add
     * @return
     */
    public Boolean add(String key, List<MemberScore> members) {
        List<ScoredValue<String>> scoredValues = new ArrayList<>();

        for (MemberScore member : members) {
            ScoredValue<String> scoredValue = ScoredValue.just(
                    member.getScore(),
                    member.getMember()
            );
            scoredValues.add(scoredValue);
        }

        Long count = redisSortedSetCommands.zadd(
                key,
                scoredValues.toArray()
        );
        // The number of elements added to the sorted sets, not including elements already existing for which the score was updated.
        // So return directly to ture.
        return true;
    }
    /**
     * Asynchronous add one or more members to a sorted set, or update its score if it already exists.
     * and without action.
     * @param key The key
     * @param members Member information to add
     */
    public void addAsync(String key, List<MemberScore> members) {
        this.addAsync(key, members, null);
    }
    /**
     * Asynchronous add one or more members to a sorted set, or update its score if it already exists.
     * @param key The key
     * @param members Member information to add
     * @param action Program to execute after the add operation is complete.
     */
    public void addAsync(String key, List<MemberScore> members, BiConsumer<Long, Throwable> action) {
        List<ScoredValue<String>> scoredValues = new ArrayList<>();

        for (MemberScore member : members) {
            ScoredValue<String> scoredValue = ScoredValue.just(
                    member.getScore(),
                    member.getMember()
            );
            scoredValues.add(scoredValue);
        }

        RedisFuture<Long> redisFuture = redisSortedSetAsyncCommands.zadd(
                key,
                scoredValues.toArray()
        );

        if (action != null) {
            // The number of elements added to the sorted sets, not including elements already existing for which the score was updated.
            // So directly execute.
            redisFuture.whenComplete(action);
        }
    }

    /**
     * Get all sorted members
     * @param key The key
     * @param sortType Specifies the sort type of the member
     * @return
     */
    public List<MemberScore> get(String key, RedisSortType sortType) {
        return this.getByPage(key, sortType, 0L, -1L);
    }

    /**
     * Paging to get sorted members
     * @param key The key
     * @param sortType Specifies the sort type of the member
     * @param pageNum Number of pages to query
     * @param pageSize How many queries per pages
     * @return
     */
    public List<MemberScore> get(String key, RedisSortType sortType, Long pageNum, Long pageSize) {
        Long start = (pageNum - 1) * pageSize;
        Long stop  = start + pageSize - 1;

        return this.getByPage(key, sortType, start, stop);
    }

    private List<MemberScore> getByPage(String key, RedisSortType sortType, Long start, Long stop) {
        List<MemberScore> result = new ArrayList<>();

        List<ScoredValue<String>> scoredValues;
        if (RedisSortType.ASC.equals(sortType)) {
            scoredValues = redisSortedSetCommands.zrangeWithScores(key, start, stop);
        } else if (RedisSortType.DESC.equals(sortType)) {
            scoredValues = redisSortedSetCommands.zrevrangeWithScores(key, start, stop);
        } else {
            return result;
        }

        for (ScoredValue<String> scoredValue : scoredValues) {
            MemberScore memberScore = new MemberScore(scoredValue.getValue(), scoredValue.getScore());
            result.add(memberScore);
        }

        return result;
    }

    /**
     * Delete all member
     * @param keys The keys
     * @return
     */
    public Boolean deleteAll(String... keys) {
        return redisGenericUtils.delete(keys);
    }

    /**
     * Delete specified members
     * @param key The key
     * @param members The name of the member to delete
     * @return
     */
    public Boolean delete(String key, String... members) {
        Long count = redisSortedSetCommands.zrem(key, members);
        // The number of elements added to the sorted sets, not including elements already existing for which the score was updated.
        // So return directly to ture.
        return true;
    }

}
