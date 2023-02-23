package org.evlove.monolith.controller;

import lombok.extern.slf4j.Slf4j;
import org.evlove.common.core.pojo.ro.BasePagingParam;
import org.evlove.common.core.robj.ReturnVO;
import org.springframework.web.bind.annotation.*;

/**
 * @author massaton.github.io
 */
@Slf4j
@RestController
@RequestMapping("/demo")
public class DemoController {

    /**
     * GET请求可以通过Query、Header、Path接收参数
     *
     * 建议约定所有Query参数均添加@RequestParam注解
     * 注意：如果接口需要供后端远程调用，则不能省略@RequestParam，否则RPC会报找不到参数的错误
     *
     * @param field1 加@RequestParam的参数默认必传，可通过required、value、defaultValue等注解属性，控制参数的特性
     * @param field2 不加@RequestParam的参数非必传，前端请求的参数名与后端方法的变量名必须一致
     */
    @GetMapping("")
    public ReturnVO<String> demoGet(@RequestParam String field1, String field2) {
        log.info("DemoGet");

        log.info(field1);
        log.info(field2);

        return ReturnVO.success("DemoGet");
    }

    /**
     * POST、PUT请求可以通过Body接收请求参数，也可以通过Query、Header、Path接收参数
     */
    @PostMapping("/{id}")
    public ReturnVO<String> demoPost(
            @RequestBody BasePagingParam param,
            @PathVariable Integer id,
            @RequestHeader String xxx,
            @RequestParam String yyy) {
        log.info("DemoPost");

        log.info(param.toJson());
        log.info(id.toString());
        log.info(xxx);
        log.info(yyy);

        return ReturnVO.success("DemoPost");
    }

    /**
     * DELETE请求可以通过Query、Header、Path接收参数
     */
    @DeleteMapping("/{id}")
    public ReturnVO<String> demoDelete(@PathVariable Integer id) {
        log.info("DemoDelete");

        log.info(id.toString());

        return ReturnVO.success("DemoDelete");
    }
}
