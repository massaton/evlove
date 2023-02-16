package org.evlove.common.core.robj;

import cn.hutool.core.util.PageUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.evlove.common.core.rcode.CommonReturnCode;
import org.evlove.common.core.rcode.IReturnCode;

import java.util.List;
import java.util.Map;

/**
 * Return data wrapper class for carrying list data type (VO) and paging information.
 *
 * Used to quickly build interface return parameters.
 *
 * @author massaton.github.io
 */
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class ReturnPagingVO<T> extends AbstractReturnVO<T> {

    /**
     * The response data can be any object type extended from java.util.List. for example: ArrayList, Vector, etc.
     */
    @Schema(description = "Business data (Array)")
    @Getter
    @Setter(AccessLevel.PROTECTED)
    private List<T> records;

    @Schema(description = "Page size (How many records to display per page)")
    @Getter
    @Setter(AccessLevel.PROTECTED)
    private Integer pageSize;

    @Schema(description = "Current page number")
    @Getter
    @Setter(AccessLevel.PROTECTED)
    private Integer pageNum;

    @Schema(description = "Total number of records")
    @Getter
    @Setter(AccessLevel.PROTECTED)
    private Integer total;

    @Schema(description = "Total pages")
    @Getter
    @Setter(AccessLevel.PROTECTED)
    private Integer pages;

    @Schema(description = "Extended field, which can store additional information according to business needs")
    @Getter
    @Setter(AccessLevel.PROTECTED)
    private Map<String, Object> extra;

    //---------------------   shortcut tool method  ------------------
    /**
     * It is used to calculate the front and rear page numbers when displaying the pager on the page.
     * For example:
     *      < Prev 3 4 [5] 6 7 8 Next >
     *
     * @param displayCount Pages displayed per screen
     * @return The current page is page 5,
     *         there are 20 pages in total,
     *         only 6 page numbers are displayed,
     *         and the displayed paging list should be: [3, 4, 5, 6, 7, 8]
     */
    public int[] rainbow(Integer displayCount) {
        return PageUtil.rainbow(pageNum, pages, displayCount);
    }

    //---------------------------shortcut: success--------------------
    public static <T> ReturnPagingVO<T> success() {
        return success(CommonReturnCode.SUCCESS.getCode(), CommonReturnCode.SUCCESS.getMessage(), null, null, null, null, null, null);
    }
    public static <T> ReturnPagingVO<T> success(IReturnCode returnCode) {
        return success(returnCode.getCode(), returnCode.getMessage(), null, null, null, null, null, null);
    }
    //List - Fill in the required attributes of ReturnPagingVO one by one.
    public static <T> ReturnPagingVO<T> success(List<T> records, Integer pageNum, Integer pageSize, Integer total, Integer pages) {
        return success(CommonReturnCode.SUCCESS.getCode(), CommonReturnCode.SUCCESS.getMessage(), records, pageNum, pageSize, total, pages, null);
    }
    public static <T> ReturnPagingVO<T> success(List<T> records, Integer pageNum, Integer pageSize, Integer total, Integer pages, Map<String, Object> extra) {
        return success(CommonReturnCode.SUCCESS.getCode(), CommonReturnCode.SUCCESS.getMessage(), records, pageNum, pageSize, total, pages, extra);
    }
    public static <T> ReturnPagingVO<T> success(IReturnCode returnCode, List<T> records, Integer pageNum, Integer pageSize, Integer total, Integer pages) {
        return success(returnCode.getCode(), returnCode.getMessage(), records, pageNum, pageSize, total, pages, null);
    }
    public static <T> ReturnPagingVO<T> success(IReturnCode returnCode, List<T> records, Integer pageNum, Integer pageSize, Integer total, Integer pages, Map<String, Object> extra) {
        return success(returnCode.getCode(), returnCode.getMessage(), records, pageNum, pageSize, total, pages, extra);
    }
    public static <T> ReturnPagingVO<T> success(Integer code, String msg, List<T> records, Integer pageNum, Integer pageSize, Integer total, Integer pages) {
        return success(code, msg, records, pageNum, pageSize, total, pages, null);
    }
    public static <T> ReturnPagingVO<T> success(Integer code, String msg, List<T> records, Integer pageNum, Integer pageSize, Integer total, Integer pages, Map<String, Object> extra) {
        return ReturnPagingVO.<T>builder()
                .code(code)
                .msg(msg)
                .records(records)
                .pageNum(pageNum).pageSize(pageSize).total(total).pages(pages)
                .extra(extra)
                .build();
    }
    //IPage - Fill in the required properties of ReturnPagingVO through the IPage of MyBatis-plus
    public static <T> ReturnPagingVO<T> success(IPage<T> myBatisObj) {
        return success(CommonReturnCode.SUCCESS.getCode(), CommonReturnCode.SUCCESS.getMessage(), myBatisObj, null);
    }
    public static <T> ReturnPagingVO<T> success(IPage<T> myBatisObj, Map<String, Object> extra) {
        return success(CommonReturnCode.SUCCESS.getCode(), CommonReturnCode.SUCCESS.getMessage(), myBatisObj, extra);
    }
    public static <T> ReturnPagingVO<T> success(IReturnCode returnCode, IPage<T> myBatisObj) {
        return success(returnCode.getCode(), returnCode.getMessage(), myBatisObj, null);
    }
    public static <T> ReturnPagingVO<T> success(IReturnCode returnCode, IPage<T> myBatisObj, Map<String, Object> extra) {
        return success(returnCode.getCode(), returnCode.getMessage(), myBatisObj, extra);
    }
    public static <T> ReturnPagingVO<T> success(Integer code, String msg, IPage<T> myBatisObj) {
        return success(code, msg, myBatisObj, null);
    }
    public static <T> ReturnPagingVO<T> success(Integer code, String msg, IPage<T> myBatisObj, Map<String, Object> extra) {
        return ReturnPagingVO.<T>builder()
                .code(code)
                .msg(msg)
                .records(myBatisObj.getRecords())
                .pageNum(Math.toIntExact(myBatisObj.getCurrent()))
                .pageSize(Math.toIntExact(myBatisObj.getSize()))
                .total(Math.toIntExact(myBatisObj.getTotal()))
                .pages(Math.toIntExact(myBatisObj.getPages()))
                .extra(extra)
                .build();
    }

    //---------------------------shortcut: fail   --------------------
    public static <T> ReturnPagingVO<T> fail() {
        return fail(CommonReturnCode.UNKNOWN_ERROR.getCode(), CommonReturnCode.UNKNOWN_ERROR.getMessage());
    }
    public static <T> ReturnPagingVO<T> fail(IReturnCode returnCode) {
        return fail(returnCode.getCode(), returnCode.getMessage());
    }
    public static <T> ReturnPagingVO<T> fail(Throwable e) {
        return fail(CommonReturnCode.PROGRAM_ERROR.getCode(), e.getLocalizedMessage());
    }
    public static <T> ReturnPagingVO<T> fail(Integer code, String msg) {
        return ReturnPagingVO.<T>builder()
                .code(code)
                .msg(msg)
                .build();
    }

    /**
     * Create ReturnPagingVO builder
     */
    private static <T> ReturnPagingVO.Builder<T> builder() {
        return new ReturnPagingVO.Builder<>();
    }
    /**
     * ReturnPagingVO builder
     */
    private static class Builder<T> {
        private int code;
        private String msg;
        private List<T> records;
        private Integer pageNum;
        private Integer pageSize;
        private Integer total;
        private Integer pages;
        private Map<String, Object> extra;

        public ReturnPagingVO.Builder<T> code(int code) {
            this.code = code;
            return this;
        }
        public ReturnPagingVO.Builder<T> msg(String msg) {
            this.msg = msg;
            return this;
        }
        public ReturnPagingVO.Builder<T> records(List<T> records) {
            this.records = records;
            return this;
        }
        public ReturnPagingVO.Builder<T> pageNum(Integer pageNum) {
            this.pageNum = pageNum;
            return this;
        }
        public ReturnPagingVO.Builder<T> pageSize(Integer pageSize) {
            this.pageSize = pageSize;
            return this;
        }
        public ReturnPagingVO.Builder<T> total(Integer total) {
            this.total = total;
            return this;
        }
        public ReturnPagingVO.Builder<T> pages(Integer pages) {
            this.pages = pages;
            return this;
        }
        public ReturnPagingVO.Builder<T> extra(Map<String, Object> extra) {
            this.extra = extra;
            return this;
        }

        public ReturnPagingVO<T> build() {
            ReturnPagingVO<T> obj = new ReturnPagingVO<>();

            obj.setCode(this.code);
            obj.setMsg(this.msg);
            obj.setRecords(this.records);
            obj.setPageNum(this.pageNum);
            obj.setPageSize(this.pageSize);
            obj.setTotal(this.total);
            obj.setPages(this.pages);
            obj.setExtra(this.extra);

            return obj;
        }
    }
}
