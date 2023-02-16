package org.evlove.common.core.pojo.ro;

import cn.hutool.core.util.PageUtil;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.javatuples.Pair;

/**
 * Base class for RO(Request Object), contains the necessary attributes for paging data requests.
 * @author massaton.github.io
 */
@Data
public class BasePagingParam extends BaseParam {
    @Schema(description = "Page size (How many records to display per page)", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "pageSize can not be empty")
    private Integer pageSize;

    @Schema(description = "Page number", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "pageNum can not be empty")
    private Integer pageNum;

    /**
     * According to pageSize and pageNum, it is convert to the start position and end position of the page in the record.
     *
     * For example:
     * 	   pageSize=0 and pageNum=10 trans to [0, 10]
     * 	   pageSize=1 and pageNum=10 trans to [10, 20]
     *
     * @return Value0 is start point, Value1 is end point
     */
    public Pair<Integer, Integer> transToPagePosition() {
        int[] startAndEnd = PageUtil.transToStartEnd(pageNum, pageSize);
        return Pair.with(startAndEnd[0], startAndEnd[1]);
    }
}
