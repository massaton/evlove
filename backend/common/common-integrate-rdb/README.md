## Common configuration examples - MySQL 8+

```yaml
spring:
  # MySQL数据库相关配置
  datasource:
    type: com.zaxxer.hikari.HikariDataSource
    driver-class-name: com.mysql.cj.jdbc.Driver
    # jdbc的url，链接中的参数移到连接池中进行配置（spring.datasource.hikari.data-source-properties）
    url: jdbc:mysql://${spring.datasource.host}:${spring.datasource.port}/${spring.datasource.db_name}
    username: root
    password: root
    # 以下自定义配置便于url所需变量从容器注入
    db_name: nacos_config
    port: 3306
    host: 127.0.0.1
    # 数据库连接池默认配置：com.zaxxer.hikari.HikariConfig
    hikari:
      # 池中连接最长生命周期（毫秒，与MySQL的`wait_timeout`参数值配合）
      max-lifetime: 1800000
      # 连接允许在池中闲置的最长时间（毫秒）
      idle-timeout: 600000
      # 池中最大连接数，包括闲置和使用中的连接
      maximum-pool-size: 10
      # 池中维护的最小空闲连接数（设置成与maximum-pool-size相等，降低创建连接带来的性能影响）
      minimum-idle: 10
      # 数据库连接建立时的超时时间（单位：毫秒，默认：30000）
      connection-timeout: 3000
      # 连接测试时执行的SQL语句
      connection-test-query: SELECT 1
      # MySQL链接URL中的参数
      # 注：8.0没有userLegacyDatetimeCode和userTimezone两个参数
      # autoReconnect=true（MySQL8.0已弃用）         - 当数据库连接异常中断时，自动重新连接（MySQL默认未使用的连接8小时后失效[show global variables like 'wait_timeout';] 或 数据库停机维护导致的闪断），官方不建议使用，参考链接：https://dev.mysql.com/doc/connector-j/8.0/en/connector-j-connp-props-high-availability-and-clustering.html#cj-conn-prop_autoReconnect
      # autoReconnectForPools=true（MySQL8.0已弃用） - 将在每次执行SQL之前尝试ping服务器
      # failOverReadOnly=false（MySQL8.0已弃用）     - autoReconnect为true时，自动重连成功后，连接不设置为只读
      data-source-properties:
        # useSSL=false - 不使用SSL认证连接（默认：false）
        useSSL: false
        # serverTimezone=Asia/Shanghai - 指定连接时区与MySQL实例所在宿主机时区一致（show variables like 'character%';）
        serverTimezone: Asia/Shanghai
        # characterEncoding=utf8 - 防止SQL语句中包含中文所导致的乱码问题
        characterEncoding: utf8
        # useUnicode=true - 与characterEncoding配套使用
        useUnicode: true
        # zeroDateTimeBehavior=convertToNull - timestamp类型的字段存入0时转换成NULL值（驱动为8.0.13以上时会自动转换为MySQL8提供的新格式：CONVERT_TO_NULL）
        zeroDateTimeBehavior: convertToNull
        # 以下为性能提升相关配置（https://github.com/brettwooldridge/HikariCP/wiki/MySQL-Configuration）
        # 驱动程序是否应对客户端预处理语句的PreparedStatements的解析过程进行缓存，是否应检查服务器端预处理语句的适用性以及服务器端预处理语句本身？
        cachePrepStmts: true
        # cachePrepStmts为true时，缓存处理多少条预处理语句（默认：25）
        prepStmtCacheSize: 250
        # cachePrepStmts为true时，指定SQL大小，大于指定大小的不进行缓存处理（默认：256）
        prepStmtCacheSqlLimit: 2048
        # 如果服务器支持，是否使用服务器端预处理语句（默认：true）
        useServerPrepStmts: true
        # 驱动程序是否应引用autocommit的内部值，以及由Connection.setAutoCommit()和Connection.setTransactionIsolation()设置的事务隔离，而不是查询数据库？
        useLocalSessionState: true
        # 打开SQL批处理操作的支持，提高性能
        rewriteBatchedStatements: true
        # 是否缓存Statements和PreparedStatements的ResultSetMetaData的执行结果
        cacheResultSetMetadata: true
        # 是否根据每条URL对“HOW VARIABLES”和“SHOW COLLATION”的结果执行缓存处理
        cacheServerConfiguration: true
        # 当服务器的状态与Connection.setAutoCommit(boolean)请求的状态不匹配时，驱动程序是否仅应发出“set autocommit=n”查询？
        elideSetAutoCommits: true
        # 驱动程序是否应维持各种内部定时器，以允许空闲时间计算，以及与服务器的连接失败时允许提供更详细的错误消息？ 将该属性设置为“假”，对于每次查询，至少能减少两次对System.getCurrentTimeMillis()的调用。
        maintainTimeStats: false

```

## Common configuration examples - MyBatis-plus

```yaml
# MyBatis及MyBatis-plus的相关配置
mybatis-plus:
  # 指定Mapper类对应xml文件的位置，默认值：["classpath*:/mapper/**/*.xml"]
  # 注意：Maven 多模块项目的扫描路径需以 classpath*: 开头 （即加载多个 jar 包下的 XML 文件）
  mapper-locations:
    - classpath:/mapper/**/*Mapper.xml
  # 指定 MyBatis 的执行器（默认：simple），MyBatis 的执行器总共有三种：
  # ExecutorType.SIMPLE：该执行器类型不做特殊的事情，为每个语句的执行创建一个新的预处理语句（PreparedStatement）
  # ExecutorType.REUSE： 该执行器类型会复用预处理语句（PreparedStatement）
  # ExecutorType.BATCH： 该执行器类型会批量执行所有的更新语句，insert、update、delete方法返回值一直会是负数-2147482646
  executor-type: reuse
  global-config:
    # 关闭日志中的LOGO打印
    banner: false
    # MyBatis-Plus 全局策略中的 DB 策略配置
    db-config:
      # 全局默认主键类型 [auto：使用数据库自增ID] [assign_id（默认）：使用雪花算法自动生成主键ID] [assign_uuid：排除中划线的UUID] [input：插入前自行设置主键值] [none：未指定主键生成类型，约等于input]
      id-type: auto
      # 表名使用驼峰转下划线命名,只对表名生效
      table-underline: true
      # SQL字段验证策略（insert、update、where），对标2.x版本中的`field-strategy`参数
      insert-strategy: not_null
      update-strategy: not_null
      where-strategy: not_null
  # Configuration中的配置大都为`MyBatis`原生支持的配置
  configuration:
    # 关闭MyBatis"一级缓存"，默认：session（即开启）。
    # 注意：服务部署的实例数量如果超过1个，需要关闭一级缓存。
    # 原因：Service1先查询数据，若之后Service2修改了数据，之后Service1又再次以同样的查询条件查询数据，因走缓存会出现查出的是修改前的数据。
    local-cache-scope: statement
    # 关闭MyBatis"二级缓存"，默认：true。
    cache-enabled: false
    # 是否开启自动驼峰命名规则（camel case）映射，即从经典数据库列名 A_COLUMN（下划线命名） 到经典 Java 属性名 aColumn（驼峰命名） 的类似映射。
    map-underscore-to-camel-case: true
    # 是否开启懒加载（MyBatis相关资源的初始化，默认为false），开启后可提升服务的启动速度
    lazy-loading-enabled: false

```
