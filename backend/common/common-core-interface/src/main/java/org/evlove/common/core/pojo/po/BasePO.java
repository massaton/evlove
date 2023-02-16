package org.evlove.common.core.pojo.po;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.evlove.common.core.pojo.BasePojo;

import java.time.LocalDateTime;

/**
 * Base class for PO(Persistent Object)
 *
 * Contain 4 basic and necessary attributes - id, delFlag, createTime, updateTime
 *
 * @author massaton.github.io
 */
@Data
public class BasePO extends BasePojo {
    /**
     * Primary key
     */
    private Long id;
    /**
     * Delete status
     * 0: Not deleted
     * 1: Deleted
     */
    private Integer delFlag;
    /**
     * Data creation time
     */
    private LocalDateTime createTime;
    /**
     * Data update time
     */
    private LocalDateTime updateTime;
}
