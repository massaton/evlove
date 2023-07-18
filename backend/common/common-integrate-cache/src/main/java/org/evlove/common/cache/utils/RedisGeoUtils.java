package org.evlove.common.cache.utils;

import io.lettuce.core.*;
import io.lettuce.core.api.async.RedisGeoAsyncCommands;
import io.lettuce.core.api.sync.RedisGeoCommands;
import org.evlove.common.cache.RedisClientConfig;
import org.evlove.common.cache.constant.RedisSortType;
import org.evlove.common.cache.pojo.GeoPointInfo;
import org.evlove.common.cache.pojo.GeoDistanceInfo;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

/**
 * Used to manipulate geographic location information stored in Redis (GEO Value Type).
 * GEO related commands are supported after Redis 3.2 version.
 * <p>
 * Starting from Redis 6.2, the GEOSEARCH command replaces the GEORADIUS command.
 * This tool contains GEOSEARCH commands, which require Redis 6.2 or higher.
 * REFER TO:
 * <a href="https://redis.io/commands/georadius/">Redis - georadius command</a>
 * <a href="https://redis.io/commands/geosearch/">Redis - geosearch command</a>
 *
 * @author massaton.github.io
 */
@Component
public class RedisGeoUtils extends AbstractRedisUtils {

    @Resource(name = RedisClientConfig.BEAN_NAME_REDIS_SYNC_COMMANDS)
    private RedisGeoCommands<String, String> redisGeoCommands;

    @Resource(name = RedisClientConfig.BEAN_NAME_REDIS_ASYNC_COMMANDS)
    private RedisGeoAsyncCommands<String, String> redisGeoAsyncCommands;

    public RedisGeoCommands<String, String> sync() {
        return this.redisGeoCommands;
    }

    public RedisGeoAsyncCommands<String, String> async() {
        return this.redisGeoAsyncCommands;
    }

    /**
     * Add one or more coordinate points to the specified key.
     * @param key The key.
     * @param points Coordinate points.
     * @return Is it added successfully?
     */
    public Boolean add(String key, GeoPointInfo... points) {
        Long insertCount = redisGeoCommands.geoadd(
                key,
                transToGeoValue(points)
        );
        return insertCount == points.length;
    }
    /**
     * Asynchronously add one or more coordinate points to the specified key.
     * @param key The key.
     * @param points Coordinate points.
     * @return The CompletableFuture, inside about is it added successfully?
     */
    public CompletableFuture<Boolean> addAsync(String key, GeoPointInfo... points) {

        return redisGeoAsyncCommands.geoadd(
                key,
                transToGeoValue(points)
        ).thenApply(new Function<Long, Boolean>() {
            @Override
            public Boolean apply(Long insertCount) {
                return insertCount == points.length;
            }
        }).toCompletableFuture();
    }

    /**
     * Get the geographical location information of the specified members.
     * If some members do not exist, they will be excluded from the results.
     * @param key Member belongs to the key.
     * @param members The member to be queried.
     * @return Coordinate point information of Map data structure.
     */
    public Map<String, GeoPointInfo> getMembers(String key, String... members) {
        List<GeoCoordinates> coordinates = redisGeoCommands.geopos(key, members);

        Map<String, GeoPointInfo> result = new HashMap<>(members.length);

        for (int i = 0; i < members.length; i++) {
            GeoPointInfo pointInfo = new GeoPointInfo(
                    coordinates.get(i).getX().doubleValue(),
                    coordinates.get(i).getY().doubleValue(),
                    members[i]
            );
            result.put(members[i], pointInfo);
        }

        return result;
    }

    /**
     * Get the straight line distance between two points/members.
     * @param key Member belongs to the key.
     * @param memberOne The name/member of point one.
     * @param memberTwo The name/member of point two.
     * @return The unit is the distance in meters.
     */
    public Double getDistance(String key, String memberOne, String memberTwo) {
        return redisGeoCommands.geodist(
                key,
                memberOne,
                memberTwo,
                // NOTICE: The unit is fixed in METERs
                GeoArgs.Unit.m
        );
    }


    /**
     * Search for coordinate points within the specified circle range.
     * <p>
     * Notice:
     * 1. For precise queries(When ANY is not provided), the GEOSEARCH command is more suitable for queries under small-scale datasets.
     * 2. Because the command does not natively support pagination, the applicable scenarios are also limited.
     *    A better way si to use MySQL 8+ or Elasticsearch to support geographic location queries.
     * <p>
     *
     * @param key The key to which the member to search belongs.
     * @param centerX Longitude coordinates of the center point.
     * @param centerY Latitude coordinates of the center point.
     * @param radius Search radius from the center point, unit is meters.
     * @param sortType Specify the sorting method for search results.
     * @param count The maximum number of search result, when less than 0, returns all.
     * @return Search result.
     */
    public List<GeoDistanceInfo> searchFromCircle(String key, Double centerX, Double centerY, Double radius, RedisSortType sortType, Integer count) {
        GeoSearch.GeoRef<String> ref = GeoSearch.fromCoordinates(centerX, centerY);
        GeoSearch.GeoPredicate predicate = GeoSearch.byRadius(radius, GeoArgs.Unit.m);
        GeoArgs args = this.generateSearchGeoArgs(sortType, count, true);

        List<GeoWithin<String>> result = redisGeoCommands.geosearch(key, ref, predicate, args);
        return transToDistanceInfo(result);
    }

    /**
     * Search for coordinate points within the specified box range.
     *
     * @param key The key to which the member to search belongs.
     * @param centerX Longitude coordinates of the center point.
     * @param centerY Latitude coordinates of the center point.
     * @param width The width of the search area.
     * @param height The height of the search area.
     * @param sortType Specify the sorting method for search results.
     * @param count The maximum number of search result, when less than 0, return all.
     * @return Search result.
     */
    public List<GeoDistanceInfo> searchFromBox(String key, Double centerX, Double centerY, Double width, Double height, RedisSortType sortType, Integer count) {
        GeoSearch.GeoRef<String> ref = GeoSearch.fromCoordinates(centerX, centerY);
        GeoSearch.GeoPredicate predicate = GeoSearch.byBox(width, height, GeoArgs.Unit.m);
        GeoArgs args = this.generateSearchGeoArgs(sortType, count, true);

        List<GeoWithin<String>> result = redisGeoCommands.geosearch(key, ref, predicate, args);
        return transToDistanceInfo(result);
    }


    // region Private Method Zone
    /**
     * Convert the custom coordinate point model to the coordinate point model required by the Lettuce framework.
     *
     * @param points The custom coordinate point model.
     * @return The coordinate point model required by the Lettuce framework.
     */
    private GeoValue<String>[] transToGeoValue(GeoPointInfo... points) {
        GeoValue<String>[] geoValues = new GeoValue[points.length];

        int idx = 0;
        for (GeoPointInfo point : points) {
            GeoValue<String> geoValue = GeoValue.just(
                    point.getLongitude(),
                    point.getLatitude(),
                    point.getMember()
            );
            geoValues[idx] = geoValue;
            idx++;
        }
        return geoValues;
    }

    /**
     * Convert Lettuce Geo query results to a custom storage model.
     *
     * @param points Lettuce search model.
     * @return Custom model.
     */
    private List<GeoDistanceInfo> transToDistanceInfo(List<GeoWithin<String>> points) {
        List<GeoDistanceInfo> result = new ArrayList<>();
        for (GeoWithin<String> point : points) {
            GeoDistanceInfo distanceInfo = GeoDistanceInfo.builder()
                    .member(point.getMember())
                    .distance(point.getDistance())
                    .longitude(point.getCoordinates().getX().doubleValue())
                    .latitude(point.getCoordinates().getY().doubleValue())
                    .build();
            result.add(distanceInfo);
        }
        return result;
    }

    private GeoArgs generateSearchGeoArgs(RedisSortType sortType, Integer count, Boolean isAccurate) {
        GeoArgs geoArgs = new GeoArgs()
                .withDistance()
                .withCoordinates();

        if (count > 0) {
            geoArgs.withCount(count, !isAccurate);
        }

        if (Objects.requireNonNull(sortType) == RedisSortType.ASC) {
            geoArgs.asc();
        } else if (sortType == RedisSortType.DESC) {
            geoArgs.desc();
        }

        return geoArgs;
    }
    // endregion
}
