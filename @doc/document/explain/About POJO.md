[TOC]

# 关于POJO的应用规范

POJO是一个无规则、无业务逻辑的简单Java数据模型对象，分为RO、BO、PO、VO四类模型。

## 基础能力

### POJO基础能力

框架为POJO模型提供了通用的工具方法，方便开发业务逻辑时使用，具体如下：

`注：当使用MyBatis-plus，PO模型需继承于Model，无法继承BasePO时，无法具备POJO模型基础能力`

1. 浅复制 - `shallowClone()`

2. 深复制 - `deepClone()`

3. 对象内容比较 - `equalsContent()`

   使用lombok的@Data后，会重写equals和hashCode，能力与equalsContent一致

4. 将模型转换为Map - `toMap()`

5. 将模型转换为JSON字符串 - `toJson()`

6. 将模型转换为其它模型（相同属性复制） - `toBean()`

7. 通过Map填充模型 - `fill(map)`

8. 通过JSON字符串填充模型  - `fill(json)`

9. 通过其它模型填充模型（相同属性复制） - `fill(bean)`

### 响应VO基础能力

ObjectVO、PagingVO具备POJO基础能力的同时，还具备下述通用能力：

1. 快捷判断调用接口是否正确（方便远程接口调用时，判断返回结果是否正确） - `successful()`
2. 快捷构建成功的响应对象 - `success()系列`
3. 快捷构建失败的响应对象 - `fail()系列`
4. 分页彩虹表（PagingVO特有，用于在页面上显示分页器时的前后页号计算） - `rainbow(displayCount)`
5. 通过MyBatis-plus的分页查询结果（IPage）快捷构建分页响应结果（PagingVO特有） - `success()系列`

## POJO使用说明

### POJO模型通用约定

- 使用lombok的@Data注解（赋予属性get、set方法，重写equals、hashCode、toString方法）

  `@Data`相当于`@Getter` `@Setter` `@RequiredArgsConstructor` `@ToString` `@EqualsAndHashCode`这5个注解的合集

- POJO对象中的属性不能使用基础类型（如：int、long），需使用封装类型（如：Integer、Long）；

- 可使用hutool提供的@Alias注解，以控制不同POJO转换（相同属性进行复制）时的字段对应关系，但要注意依赖影响（A、B模型都可以从C转换而来，但C中的某个字段对应到A和B中的字段名称不同）；

- 妥善pojo模型在工程中的包名管理，避免随着系统发展，模型过多及多版本接口而带来的易读性下降问题。子包可根据领域进行规划管理，变化出现时，及时重构。

  通常pojo.vo、pojo.ro可按Controller所划分的领域进行包名的设定，即同一个Controller中定义的接口所涉及的RO、VO模型均在工程的同一个子包中。建议包名按`pojo分类.版本.领域.[子领域].*`来管理VO、RO模型，以满足接口版本增加时，多版本接口对应的RO、VO模型的管理。例如：`pojo.vo`。

  pojo.po不增加子包名，模型与数据库中表的顺序一致，名称一致。

  pojo.bo根据领域进行包名的设定。

- 关于模型继承

    - 因为驱动各类模型变化的因素不同，为降低模型变动所带来的联动影响，不同类型的模型之间不能相互继承。虽然带来的冗余，但降低了复杂度，提高了灵活性。

      `例如：VO不能继承于PO，PO模型的变化会随数据库的变化而变化，VO模型的变化会随应用展示逻辑的变化而变化，不能因为数据库的变化而影响展示层的数据结构`

    - 同类模型可抽象出公共模型或子模型，供同类的其它模型作为父类继承或作为属性引用。

      `例如：XxxVO可以继承于CommonXxxVO，XxxVO中的属性可以引用XxxChildVO`

    - PO、BO、VO具备演化性（例如：模型可从PO演化到BO或VO，或从BO演化到VO），可以通过框架提供的POJO基础能力（fill、toBean）进行模型的填充演化。

      `在实际实际开发中，模型能够成功从PO演化到VO的情况不多，大多数都是直接使用VO，对VO模型逐步填充（Dao层填充一部分，Service层通过计算再填充一部分）`

### RO（Request Object - 请求对象）

- **作用**

  仅用于包装Controller接口所接收的Query或Body请求参数。

  Service层操作类中的方法参数可以接收RO模型，但仅在参数全部需要使用的情况下，否则需自行定义BO模型或平铺定义参数。

  Dao层操作类中的方法参数应平铺定义，避免传入复杂对象参数，导致SQL复杂。

- **创建时机**

  完成接口设计后创建。

- **约定**

    - Path和Header参数，不在RO对象定义范围内，仍使用对应的@PathVariable或@RequestHeader注解。
    - 超过2个参数的查询需要封装成RO对象。
    - 禁止使用Map来传输参数。
    - RO模型的属性可使用javax.validation`注解，对属性进行框架级校验，例如：`@NotNull`、`@NotBlank`。
    - RO模型的属性需要用`@ApiModelProperty`注解进行描述，以便通过Swagger将描述注入到接口管理平台。属性无需注释，通过注解中的message属性将属性的意义描述清楚，既起到代码注释的作用，又可描述到接口管理平台。

- **命名规范**

  统一以Param为后缀，例如：`XxxParam`。

  因为一个接口仅对应一个根RO模型（子模型作为根RO模型的属性），为方便类的命名，Param的前面可使用接口路径或接口方法作为名称，例如：接口`GET /user/info`，Controller方法名 `getUserInfo()`，VO类名`GetUserInfoParam`。

- **存放位置**

  存放于xxx-service-api工程的pojo.ro包中，方便接口暴露给其它服务使用。

- **继承关系**

  可继承BaseRO，以获得POJO基础能力和RO模型特有的公共基础能力。

- **示例**

  ```java
  @Data
  public class XxxParam extend BaseRO {
    @ApiModelProperty(value = "xxx的描述", required = true)
    @NotNull(message = "xxx不能为空")
    private String xxx;
    
    @ApiModelProperty(value = "yyy的描述")
    private Integer yyy;
    
    @ApiModelProperty(value = "zzz的描述")
    private ZzzParam zzz;
  }
  ```

### VO（View Object - 视图对象）

- **作用**

  主要用于包装接口响应数据，作为ObjectVO或PagingVO的泛型数据字段使用。

- **创建时机**

  完成接口设计后创建。

- **约定**

    - 若接口响应字段与PO模型一致，应创建VO模型并继承PO模型，通过`vo.fill(po)`对VO模型填充值，不能直接使用PO模型作为接口返回参数，因为驱动VO和PO模型变化的原因不同。
    - VO模型可添加`jackson`提供的注解，以控制JSON序列化时的行为。
    - VO模型的属性需要用`@ApiModelProperty`注解进行描述，以便通过Swagger将描述注入到接口管理平台。属性无需注释，通过注解中的message属性将属性的意义描述清楚，既起到代码注释的作用，又可描述到接口管理平台。
    - 接口仅需要响应是否成功，无需响应业务数据时，通过`ObjectVO.success()`即可满足，无需定义空的VO模型。

- **命名规范**

  统一以VO作为后缀，例如`XxxVO`。

  因为一个接口仅对应一个根VO模型（子模型作为根VO模型的属性），为方便类的命名，VO的前面可使用接口路径或接口方法作为名称，例如：接口`GET /user/info`，Controller方法名 `getUserInfo()`，VO类名`GetUserInfoVO`。

- **存放位置**

  存放于xxx-service-api工程的pojo.vo包中，方便接口暴露给其它服务使用。

- **继承关系**

  可继承BaseVO，以获得POJO公共基础能力和VO模型特有的公共基础能力。

- **示例**

  ```java
  @Data
  public class XxxVO extend BaseVO {
    @ApiModelProperty(value = "xxx的描述")
    private String xxx;
    
    @ApiModelProperty(value = "yyy的描述")
    private Integer yyy;
    
    @ApiModelProperty(value = "zzz的描述")
    private ZzzVO zzz;
  }
  ```

### BO（Business Object - 业务对象）

- **作用**

  ==（暂时不明确）==

  主要用于承载服务内部在做业务逻辑处理时所需的数据模型。若使用DDD进行建模，则BO模型作为承载领域建模的模型类使用。

- **创建时机**

  根据业务逻辑实现需要，随时增减。

- **约定**

  暂无

- **命名规范**

  没有限制，可自行根据情况进行包的分类和类名前后缀的定义。

  `例如：可以使用BO、Domain等作为类名后缀`

- **存放位置**

  存放于xxx-service工程的pojo.bo包中，仅在服务内部使用

- **继承关系**

  可继承BaseBO，以获得POJO基础能力和BO模型特有的公共基础能力。

- **示例**

  ```java
  @Data
  public class XxxBO extends BaseBO {
    ……
  }
  ```

### PO（Persistent Object  - 持久化对象）

- **作用**

  用于持久化到数据存储的数据模型，与数据库的表一一对应，承载Dao层单表查询的结果数据向上层传递。

- **创建时机**

  完成数据库结构设计后创建，通常通过代码生成器创建即可。

- **约定**

    - 在符合PO命名规范的情况下，无需使用@TableName标识PO模型类。
    - PO模型中的属性可添加MyBatis或MyBatis-plus注解，以定义模型在Dao层的意义。如：`@TableLogic`、`@TableId`
    - PO模型中的属性除MyBatis提供的注解，无需添加其它注解。
    - 当服务被分为应用领域（layer-app）和管理领域（layer-oms）时，同一个数据库会被两个不同的领域服务使用，通常，管理领域增加的字段，应用领域并不需要关心，反之同理。此时PO模型中的字段无需同步变更，即同一个表对应的应用领域PO模型和管理领域PO模型中的字段可以是不同的。

- **命名规范**

  与数据库表名一致，符合MyBatis配置的数据表下划线分隔转驼峰类名的命名规范。例如：表名为`tb_xxx_zzz`，PO类名为`TbXxxZzz`。

- **存放位置**

  存放于xxx-service工程的pojo.po包中，仅在服务内部使用（1个服务仅对应1个数据库，1个数据库可对应n个服务）。

- **继承关系**

  PO模型类继承于MyBatis-plus提供的Model，以便Mapper接口获得单表的各种操作能力。

  但在特殊场景（如对性能有极致要求的情况时），应使用原生MyBatis的方式操作Dao层（甚至更接近jdbc的方式），以减少MyBatis-plus框架为满足通用性而带来的不必要的计算。

- **示例**

  ```java
  /**
   * PO模型
   */
  @Data
  @TableName("tb_xxx")
  public class TbXxx extends Model<TbXxx> {
    /**
     * 标识
     */
  	@TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    /**
     * 创建时间
     */
    private Date createTime;
    
    /**
     * 最后更新时间
     */
    private Date updateTime;
    
    /**
     * 删除标志（0-未删除，1-已删除）
     */
    @TableLogic
    private Integer delFlag;
  }
  
  /**
   * Dao层操作接口
   */
  public interface TbXxxMapper extends BaseMapper<TbXxx> {
  	
  }
  
  /**
   * Dao层操作描述文件，名称与Dao层操作接口一致：TbXxxMapper.xml
   */
  <?xml version="1.0" encoding="UTF-8"?>
  <!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
  <mapper namespace="com.???.mapper.TbXxxMapper">
  
      <resultMap id="TbXxxMap" type="com.???.pojo.po.TbXxx">
          <id property="id" column="id"/>
          <id property="createTime" column="create_time"/>
          <id property="updateTime" column="update_time"/>
          <id property="delFlag" column="del_flag"/>
      </resultMap>
  </mapper>
  ```

## 关于其它模型

**MQ消息模型**：存放于`xxx-service-api`的`pojo.message`保重，可在服务间共享使用

**Elasticsearch存储模型**：存放于`xxx-service`的`pojo.po.es`包中，仅在服务内部使用

**数据层视图**：存放于`xxx-service`的`pojo.po.view`包中，仅在服务内部使用，用于当表连接查询时，单个PO模型无法承载查询结果数据，且不适合使用VO模型（例如：从Dao层到VO模型的转换还需经过很多业务逻辑的处理）承载的，或无需转换为VO（例如：导出Excel时）的时候。


