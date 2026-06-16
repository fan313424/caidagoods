
# 开发规范指南

为保证代码质量、可维护性、安全性与可扩展性，请在开发过程中严格遵循以下规范。

## 一、项目环境与技术栈

- **项目名称**：caida
- **作者**：lf270
- **工作目录**：`D:\itfan\caida`
- **操作系统**：Windows 11

### 1.1 开发工具与依赖
- **构建工具**：Maven
- **JDK 版本**：Java 17.0.19
- **主框架**：Spring Boot 4.0.6
- **核心依赖**：
  - `spring-boot-starter-webmvc` (Web MVC)
  - `spring-boot-starter-data-jpa` (数据持久化)
  - `mysql-connector-j` (MySQL 驱动)
  - `lombok` (代码简化工具，项目隐式依赖)

### 1.2 配置文件
- **配置文件路径**：`src/main/resources/application.yml`
- **数据库配置**：MySQL (端口 3306, 数据库名 `caida_db`)
- **JPA 配置**：Hibernate DDL 模式为 `update`，显示 SQL，使用 MySQLDialect。

## 二、目录结构规范

项目推荐的基础目录结构如下（包含已有的包结构）：

```text
caida
├── .github                         # GitHub 相关配置
│   └── java-upgrade
│       └── hooks
│           └── scripts
├── caida-qianduan                  # 前端资源目录 (images)
├── src
│   ├── main
│   │   ├── java
│   │   │   └── com
│   │   │       └── caida
│   │   │           ├── common       # 公共工具或常量类
│   │   │           ├── config       # Spring 配置类
│   │   │           ├── controller   # 控制器层
│   │   │           ├── entity       # 实体类 (DO/PO)
│   │   │           ├── interceptor  # 拦截器 (如跨域、鉴权)
│   │   │           ├── repository   # 数据访问层
│   │   │           └── service      # 业务逻辑层
│   │   └── resources
│   │       ├── db                   # SQL 脚本
│   │       ├── static               # 静态资源
│   │       └── templates            # 模板文件
│   └── test
│       └── java
│           └── com
│               └── caida            # 测试代码
```

## 三、分层架构规范

| 层级        | 职责说明                         | 开发约束与注意事项                                               |
|-------------|----------------------------------|------------------------------------------------------------------|
| **Controller** | 处理 HTTP 请求与响应，定义 API 接口 | 不得直接访问数据库，必须通过 Service 层调用；返回 VO 或 DTO      |
| **Service**    | 实现业务逻辑、事务管理与数据校验   | 必须通过 Repository 层访问数据库；返回 DTO/VO 而非 Entity      |
| **Repository** | 数据库访问与持久化操作             | 继承 `JpaRepository`；使用 `@EntityGraph` 避免 N+1 查询问题     |
| **Entity**     | 映射数据库表结构                   | 不得直接返回给前端（需转换为 DTO）；包名统一为 `entity`         |

### 接口与实现分离
- 所有业务逻辑接口（如 `UserService`）需定义在 `service` 包下。
- 具体实现类需放在接口所在包下的 `impl` 子包中。
- 基础 Mapper 接口（继承 `JpaRepository`）直接放在 `repository` 包下。

## 四、安全与性能规范

### 输入校验
- 使用 `@Valid` 与 JSR-303 校验注解（如 `@NotBlank`, `@Size` 等）。
- **注意**：Spring Boot 3.x 及以上版本使用 `jakarta.validation.constraints.*`。
- 禁止手动拼接 SQL 字符串，防止 SQL 注入攻击。

### 事务管理
- `@Transactional` 注解仅用于 **Service 层**方法。
- 避免在循环中频繁提交事务，影响性能。

## 五、代码风格规范

### 1. 命名规范
| 类型       | 命名方式             | 示例                  |
|------------|----------------------|-----------------------|
| 类名       | UpperCamelCase       | `UserServiceImpl`     |
| 方法/变量  | lowerCamelCase       | `saveUser()`          |
| 常量       | UPPER_SNAKE_CASE     | `MAX_LOGIN_ATTEMPTS`  |
| Repository | Repository 接口以 `Repository` 结尾 | `UserRepository`      |
| Service    | Service 接口以 `Service` 结尾 | `UserService`         |

### 2. 注释规范
- 所有类、方法、字段需添加 **Javadoc** 注释。
- 注释语言：**中文**（遵循用户第一语言习惯）。

### 3. 类型命名规范（阿里巴巴风格）
| 后缀 | 用途说明                     | 示例         |
|------|------------------------------|--------------|
| DTO  | 数据传输对象                 | `UserDTO`    |
| DO   | 数据库实体对象               | `UserDO`     |
| BO   | 业务逻辑封装对象             | `UserBO`     |
| VO   | 视图展示对象                 | `UserVO`     |
| Query| 查询参数封装对象             | `UserQuery`  |

### 4. 实体类与工具
- 使用 Lombok 注解简化代码：
  - `@Data` (替代 getter/setter/toString/equals/hashCode)
  - `@NoArgsConstructor` (无参构造)
  - `@AllArgsConstructor` (全参构造)
- 实体类属性命名遵循 Java 驼峰命名，避免与数据库关键字冲突。

## 六、扩展性与日志规范

### 日志记录
- 使用 `@Slf4j` 注解代替 `System.out.println`。
- 记录关键业务操作、异常信息及调试信息。

### 接口优先原则
- 所有业务逻辑通过接口定义（如 `UserService`），具体实现放在 `impl` 包中（如 `UserServiceImpl`）。
- 前端接口（Controller）需定义清晰的 API 文档（推荐 Swagger/OpenAPI 注解）。

## 七、编码原则总结

| 原则       | 说明                                       |
|------------|--------------------------------------------|
| **SOLID**  | 高内聚、低耦合，增强可维护性与可扩展性     |
| **DRY**    | 避免重复代码，提高复用性                   |
| **KISS**   | 保持代码简洁易懂                           |
| **YAGNI**  | 不实现当前不需要的功能                     |
| **OWASP**  | 防范常见安全漏洞，如 SQL 注入、XSS 等      |
