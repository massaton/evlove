package org.evlove.common.cache.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author massaton.github.io
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GeoDistanceInfo {
    /**
     * Longitude of coordinate.
     */
    private Double longitude;

    /**
     * Latitude of coordinate.
     */
    private Double latitude;

    /**
     * Location name or other information.
     */
    private String member;

    /**
     * The linear distance between the current member and the center point.
     */
    private Double distance;
}
