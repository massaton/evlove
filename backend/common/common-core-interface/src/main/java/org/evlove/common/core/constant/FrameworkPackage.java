package org.evlove.common.core.constant;

/**
 * It is used in annotations related to package scanning scope for backend services, for example:
 * @SpringBootApplication(scanBasePackages = FrameworkPackageInfo.BASE)
 * @MapperScan(FrameworkPackageInfo.MAPPER)
 * @EnableFeignClients(FrameworkPackageInfo.BASE)
 *
 * Otherwise, due to the isolation of directory names between modules,
 * the classes injected into the 'xxx-service' by common and service-api modules will not be scanned.
 *
 * @author massaton.github.io
 */
public class FrameworkPackage {

    /**
     * Public basic package path
     */
    public static final String BASE = "com.evlove";

    /**
     * DAO layer package path
     */
    public static final String MAPPER = "com.evlove.**.mapper";
}