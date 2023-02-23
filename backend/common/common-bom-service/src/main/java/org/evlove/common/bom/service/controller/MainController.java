package org.evlove.common.bom.service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.evlove.common.bom.service.lifecycle.ServiceInfoListener;
import org.evlove.common.core.pojo.ro.BaseParam;
import org.evlove.common.core.pojo.vo.BaseVO;
import org.evlove.common.core.robj.ReturnVO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringBootVersion;
import org.springframework.boot.system.JavaVersion;
import org.springframework.boot.web.context.WebServerApplicationContext;
import org.springframework.core.SpringVersion;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Platform preset root path interface
 *
 * @author massaton.github.io
 */
@Slf4j
@RestController
@RequestMapping("/")
public class MainController {
    @Value("${spring.profiles.active:}")
    private String curEnv;

    @Value("${spring.application.version:}")
    private String projectVersion;

    @Value("${spring.application.name:}")
    private String serviceName;

    private final WebServerApplicationContext applicationContext;

    public MainController(WebServerApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Operation(
            summary="Service root path (framework preset)",
            description="It can be used to test whether the service is running normally or to obtain some main environment information"
    )
    @GetMapping("/")
    public ReturnVO<ServiceInfoVO> main(ServiceInfoParam param) {
        log.info("Request Parameter: " + param);

        String javaVersion = JavaVersion.getJavaVersion().toString();
        String springVersion = SpringVersion.getVersion();
        String springBootVersion = SpringBootVersion.getVersion();

        String webContainerVersion = applicationContext.getWebServer().getClass().getName();
        String serviceAddress = ServiceInfoListener.IP + ":" + ServiceInfoListener.PORT;

        return ReturnVO.success(
                ServiceInfoVO.builder()
                        .msg(String.format("Hello from `%s` service!", this.serviceName))
                        .serverTime(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss SSS").format(LocalDateTime.now()))
                        .profile(this.curEnv)
                        .address(serviceAddress)
                        .javaVersion(javaVersion)
                        .springVersion(springVersion)
                        .springBootVersion(springBootVersion)
                        .projectVersion(this.projectVersion)
                        .webContainer(webContainerVersion)
                        .build()
        );
    }

    @Data
    static class ServiceInfoParam extends BaseParam {
        @Schema(title = "Purpose")
        private String purpose;
    }

    @Data
    @Builder
    static class ServiceInfoVO extends BaseVO {
        @Schema(title = "Custom return information")
        private String msg;

        @Schema(title = "Server's current time")
        private String serverTime;

        @Schema(title = "Service environment")
        private String profile;

        @Schema(title = "Request address (host:port)")
        private String address;

        @Schema(title = "The Java version number used by the service")
        private String javaVersion;

        @Schema(title = "The Spring version number used by the service")
        private String springVersion;

        @Schema(title = "The SpringBoot version number used by the service")
        private String springBootVersion;

        @Schema(title = "The service's own version number")
        private String projectVersion;

        @Schema(title = "The web container used by the service")
        private String webContainer;
    }
}
