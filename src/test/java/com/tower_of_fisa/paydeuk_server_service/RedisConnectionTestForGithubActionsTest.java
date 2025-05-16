package com.tower_of_fisa.paydeuk_server_service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

@SpringBootTest
public class RedisConnectionTestForGithubActionsTest {
    @Autowired
    private StringRedisTemplate redisTemplate;

    @Test
    void testRedisConnection() {
        // ping 명령 보내기
        String result = redisTemplate.getConnectionFactory()
                .getConnection()
                .ping();

        assertThat(result).isEqualTo("PONG");
    }
}
