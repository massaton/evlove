package org.evlove.common.cache.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Used to send messages through Redis's Pub, Sub or Stream.
 *
 * @author massaton.github.io
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RedisMessage {
    /**
     * Message ID
     */
    private String id;

    /**
     * Message content
     */
    private Map<String, String> content;
}
