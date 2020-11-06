package org.example.docker.redisutils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@Component
public class RedisUtils {
    @Autowired
    JedisPool jedisPool;

    public void createStream(){
        Jedis jedis = jedisPool.getResource();
    }

}
