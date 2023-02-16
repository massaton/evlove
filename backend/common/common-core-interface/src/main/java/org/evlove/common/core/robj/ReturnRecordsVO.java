package org.evlove.common.core.robj;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.evlove.common.core.rcode.CommonReturnCode;
import org.evlove.common.core.rcode.IReturnCode;

/**
 * Return data wrapper class for carrying list data type (VO).
 *
 * Used to quickly build interface return parameters.
 *
 * @author massaton.github.io
 */
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class ReturnRecordsVO<T> extends AbstractReturnVO<T> {

    @Schema(description = "Business data (Array)")
    @Getter
    @Setter(AccessLevel.PROTECTED)
    private Iterable<T> records;

    //---------------------------shortcut: success--------------------
    public static <T> ReturnRecordsVO<T> success() {
        return success(CommonReturnCode.SUCCESS.getCode(), CommonReturnCode.SUCCESS.getMessage(), null);
    }
    public static <T> ReturnRecordsVO<T> success(IReturnCode returnCode) {
        return success(returnCode.getCode(), returnCode.getMessage(), null);
    }
    public static <T> ReturnRecordsVO<T> success(Iterable<T> records) {
        return success(CommonReturnCode.SUCCESS.getCode(), CommonReturnCode.SUCCESS.getMessage(), records);
    }
    public static <T> ReturnRecordsVO<T> success(IReturnCode returnCode, Iterable<T> records) {
        return success(returnCode.getCode(), returnCode.getMessage(), records);
    }
    public static <T> ReturnRecordsVO<T> success(Integer code, String msg, Iterable<T> records) {
        return ReturnRecordsVO.<T>builder()
                .code(code)
                .msg(msg)
                .records(records)
                .build();
    }

    //---------------------------shortcut: fail   --------------------
    public static <T> ReturnRecordsVO<T> fail() {
        return fail(CommonReturnCode.UNKNOWN_ERROR.getCode(), CommonReturnCode.UNKNOWN_ERROR.getMessage());
    }
    public static <T> ReturnRecordsVO<T> fail(IReturnCode returnCode) {
        return fail(returnCode.getCode(), returnCode.getMessage());
    }
    public static <T> ReturnRecordsVO<T> fail(Throwable e) {
        return fail(CommonReturnCode.PROGRAM_ERROR.getCode(), e.getLocalizedMessage());
    }
    public static <T> ReturnRecordsVO<T> fail(Integer code, String msg) {
        return ReturnRecordsVO.<T>builder()
                .code(code)
                .msg(msg)
                .build();
    }

    /**
     * Create ReturnRecordsVO builder
     */
    private static <T> ReturnRecordsVO.Builder<T> builder() {
        return new ReturnRecordsVO.Builder<>();
    }
    /**
     * ReturnRecordsVO builder
     */
    private static class Builder<T> {
        private int code;
        private String msg;
        private Iterable<T> records;

        public ReturnRecordsVO.Builder<T> code(int code) {
            this.code = code;
            return this;
        }

        public ReturnRecordsVO.Builder<T> msg(String msg) {
            this.msg = msg;
            return this;
        }

        public ReturnRecordsVO.Builder<T> records(Iterable<T> records) {
            this.records = records;
            return this;
        }

        public ReturnRecordsVO<T> build() {
            ReturnRecordsVO<T> obj = new ReturnRecordsVO<>();
            obj.setCode(this.code);
            obj.setMsg(this.msg);
            obj.setRecords(this.records);
            return obj;
        }
    }
}
