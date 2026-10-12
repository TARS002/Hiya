package com.hiya;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * 冒烟测试：验证 Spring 容器能否正常启动。
 *
 * 注意：@SpringBootTest 会加载完整应用上下文（包括连接 MySQL），
 * 所以运行前需要保证本机 MySQL 已启动且 hiya_db 可访问，
 * 否则会因连不上数据库而启动失败（这是预期行为，不是代码 bug）。
 */
@SpringBootTest
class HiyaServerApplicationTests {

    @Test
    void contextLoads() {
        // 空测试体即可：只要能跑起来，就说明容器装配成功
    }

}
