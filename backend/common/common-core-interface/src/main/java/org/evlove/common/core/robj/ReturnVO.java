package org.evlove.common.core.robj;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.evlove.common.core.rcode.CommonReturnCode;
import org.evlove.common.core.rcode.IReturnCode;

/**
 * General return result(VO) wrapper class, containing shortcut methods.
 *
 * Used to quickly build interface return parameters.
 *
 * @author massaton.github.io
 */
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class ReturnVO<T> extends AbstractReturnVO<T> {

    @Schema(description = "Business data")
    @Getter
    @Setter(AccessLevel.PROTECTED)
    private T data;

    //---------------------------shortcut: success--------------------
    public static <T> ReturnVO<T> success() {
        return success(CommonReturnCode.SUCCESS.getCode(), CommonReturnCode.SUCCESS.getMessage(), null);
    }
    public static <T> ReturnVO<T> success(IReturnCode returnCode) {
        return success(returnCode.getCode(), returnCode.getMessage(), null);
    }
    public static <T> ReturnVO<T> success(T data) {
        return success(CommonReturnCode.SUCCESS.getCode(), CommonReturnCode.SUCCESS.getMessage(), data);
    }
    public static <T> ReturnVO<T> success(IReturnCode returnCode, T data) {
        return success(returnCode.getCode(), returnCode.getMessage(), data);
    }
    public static <T> ReturnVO<T> success(Integer code, String msg, T data) {
        return ReturnVO.<T>builder()
                .code(code)
                .msg(msg)
                .data(data)
                .build();
    }

    //---------------------------shortcut: fail   --------------------
    public static <T> ReturnVO<T> fail() {
        return fail(CommonReturnCode.UNKNOWN_ERROR.getCode(), CommonReturnCode.UNKNOWN_ERROR.getMessage());
    }
    public static <T> ReturnVO<T> fail(IReturnCode returnCode) {
        return fail(returnCode.getCode(), returnCode.getMessage());
    }
    public static <T> ReturnVO<T> fail(Throwable e) {
        return fail(CommonReturnCode.PROGRAM_ERROR.getCode(), e.getLocalizedMessage());
    }
    public static <T> ReturnVO<T> fail(Integer code, String msg) {
        return ReturnVO.<T>builder()
                .code(code)
                .msg(msg)
                .build();
    }

    /**
     * Create ReturnVO builder
     */
    private static <T> ReturnVO.Builder<T> builder() {
        return new ReturnVO.Builder<>();
    }
    /**
     * ReturnVO builder
     */
    private static class Builder<T> {
        private int code;
        private String msg;
        private T data;

        public ReturnVO.Builder<T> code(int code) {
            this.code = code;
            return this;
        }

        public ReturnVO.Builder<T> msg(String msg) {
            this.msg = msg;
            return this;
        }

        public ReturnVO.Builder<T> data(T data) {
            this.data = data;
            return this;
        }

        public ReturnVO<T> build() {
            ReturnVO<T> obj = new ReturnVO<>();
            obj.setCode(this.code);
            obj.setMsg(this.msg);
            obj.setData(this.data);
            return obj;
        }
    }
}
