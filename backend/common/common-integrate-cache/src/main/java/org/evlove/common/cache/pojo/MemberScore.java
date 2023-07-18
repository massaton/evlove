package org.evlove.common.cache.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Encapsulate the members of Redis SortedSet to store member information when adding and querying.
 * @author massaton.github.io
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MemberScore {
    /**
     * A member of ZSet
     */
    private String member;

    /**
     * Scores corresponding to members
     */
    private Double score;
}
