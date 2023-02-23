package org.evlove.monolith;

import org.evlove.common.core.constant.FrameworkPackage;
//import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Startup Class
 *
 * @author massaton.github.io
 */
//@MapperScan(FrameworkPackage.MAPPER)
@SpringBootApplication(scanBasePackages = FrameworkPackage.BASE)
public class MonolithApplication {

    public static void main(String[] args) {
        SpringApplication.run(MonolithApplication.class, args);
    }
}

