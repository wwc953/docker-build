package com.example.appserialization.protostufftest;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.example.docker.Application;
import org.example.docker.po.Person;
import org.example.docker.redisutils.RedisUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import redis.clients.jedis.BinaryClient;
import redis.clients.jedis.JedisCluster;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@SpringBootTest(classes = Application.class)
class AppUserApplicationTests {

    @Autowired
    private JedisCluster jedisCluster;
    @Autowired
    RedisUtil redisUtil;

    @Test
    void luaget() {
        String key = "aa1";
        int size = 5;
        List<Person> rpop = redisUtil.rpop(key, size, Person.class);
        System.out.println(rpop.size());
    }

    @Test
    void rpop() {
        String key = "aa";
        Long llen = jedisCluster.llen(key);
        System.out.println("list大小：" + llen);

        System.out.println(JSON.toJSONString(null));
    }

    @Test
    void lpush() {
        int size = 300;
        List<Person> args = new ArrayList<Person>();
        for (int i = 0; i < size; i++) {
            Person s = new Person();
            s.setAge(30);
            s.setBeginTime(new Date());
            s.setBigDecimal(new BigDecimal(i * 10 + Math.random()));
            s.setName("我是是" + i);
            s.setId(Long.valueOf("300000" + i));
            args.add(s);
        }

//        String[] strings = new String[args.size()];
//        for (int i = 0; i < size; i++) {
//            strings[i] = JSON.toJSONString(args.get(i));
//        }

        String[] strings = args.parallelStream().map(v -> JSON.toJSONString(v)).toArray(String[]::new);

        Long res = jedisCluster.lpush("aa", strings);
        System.out.println(res);

    }

}
