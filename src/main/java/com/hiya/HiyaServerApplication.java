package com.hiya;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Hiya 服务端启动类。
 *
 * 这是整个 SpringBoot 应用的入口。运行 main 方法后，SpringBoot 会做这几件事：
 *   1. 根据 classpath 上已有的依赖自动配置（比如有了 JPA 依赖就自动配置数据源、
 *      实体管理器；有了 webmvc 依赖就自动配置内嵌 Tomcat、JSON 转换器）；
 *   2. 扫描 com.hiya 包及其子包下所有带 @Component / @Service / @Controller /
 *      @Repository 等注解的类，把它们注册成 Spring 容器中的 Bean（组件）；
 *   3. 启动内嵌的 Tomcat 服务器，监听 server.port 配置的端口（默认 8080）。
 *
 * @SpringBootApplication 是一个组合注解，等价于：
 *   - @SpringBootConfiguration  标记这是一个配置类（含 @Configuration）
 *   - @EnableAutoConfiguration  开启自动配置
 *   - @ComponentScan            默认扫描本类所在包及其所有子包
 *   （这三件事合起来就是"约定大于配置"的核心体现）
 */
@SpringBootApplication
public class HiyaServerApplication {

    public static void main(String[] args) {
        // 启动 Spring 容器 + 内嵌 Tomcat，应用正式跑起来
        SpringApplication.run(HiyaServerApplication.class, args);
    }

}
