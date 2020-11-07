package com.example.appserialization.protostufftest;

import com.alibaba.fastjson.JSON;
import org.example.docker.Application;
import org.example.docker.po.Person;
import org.example.docker.redisutils.RedisClusterUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import redis.clients.jedis.JedisCluster;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@SpringBootTest(classes = Application.class)
class AppUserApplicationTests {

    @Autowired
    private JedisCluster jedisCluster;
    @Autowired
    RedisClusterUtil redisUtil;

    String map = "local array = {}\n" +
            "local arrayLength = 1\n" +
            "if (redis.call(\"llen\", KEYS[1]) > tonumber(ARGV[1])) then\n" +
            "    arrayLength = tonumber(ARGV[1])\n" +
            "else\n" +
            "    arrayLength = redis.call(\"llen\", KEYS[1])\n" +
            "end\n" +
            "for i = 1, arrayLength do\n" +
            "    array[i] = redis.call(\"rpop\", KEYS[1])\n" +
            "end\n" +
            "array[table.getn(array) + 1] = redis.call(\"llen\", KEYS[1])\n" +
            "return array";

    @Test
    void rpopMap() {
        String key = "aa";
        int size = 5;
//        Map<String, Object> mm = (Map<String, Object>) jedisCluster.eval(map, Collections.singletonList(key), Collections.singletonList(String.valueOf(size)));
//        Long size1 = (Long) mm.get("size");
//        List<String> list = (List<String>) mm.get("list");
//        System.out.println(JSON.toJSONString(mm));

        List<String> eval = (List<String>)jedisCluster.eval(map, Collections.singletonList(key), Collections.singletonList(String.valueOf(size)));
        System.out.println(JSON.toJSONString(eval));
    }

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
