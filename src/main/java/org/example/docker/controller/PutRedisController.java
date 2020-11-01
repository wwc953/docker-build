package org.example.docker.controller;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.example.docker.kryo2.KryoSerializer;
import org.example.docker.po.Person;
import org.example.docker.redisutils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * @Description: TODO
 * @author: wangwc
 * @date: 2020/11/1 14:39
 */
@RestController
@RequestMapping("/put")
public class PutRedisController {

    @Autowired
    RedisUtil redisUtil;

    String chartSetName = "UTF-8";


    @GetMapping("/t/{type}/{size}")
    public String put(@PathVariable String type, @PathVariable int size) {
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
        if (size < 200)
            System.out.println(JSON.toJSONString(args));

        switch (type) {
            case "kryo":
                kryo(args, size);
                break;
            case "pf":
                break;
            case "json":
                json(args);
                break;
            default:
                System.out.println("=====空====");
                break;
        }
        return "success";

    }

    public void kryo(List args, int size) {
        long begin = System.currentTimeMillis();
        byte[] bytes = KryoSerializer.serialize(args);
        int bb = bytes.length / 1024;
        int mm = bb / 1024;
        System.out.println("kryo序列化时间：" + (System.currentTimeMillis() - begin) + "ms,大小：" + bytes.length + "B," + bb + "KB," + mm + "MB");

        long begin3 = System.currentTimeMillis();
        String key = "kryo:" + System.nanoTime();
        System.out.println("key=" + key);
        redisUtil.set(key.getBytes(Charset.forName(chartSetName)), bytes);
        System.out.println("set Redis时间：" + (System.currentTimeMillis() - begin3) + "ms");

        long begin4 = System.currentTimeMillis();
        byte[] redisByts = redisUtil.get(key.getBytes(Charset.forName(chartSetName)));
        System.out.println("get Redis时间：" + (System.currentTimeMillis() - begin4) + "ms");

        long begin2 = System.currentTimeMillis();
        List<Person> deserialize = (List<Person>) KryoSerializer.deserialize(redisByts);
        System.out.println("kryo反序列化时间：" + (System.currentTimeMillis() - begin2) + "ms,反序列化：" + deserialize.size());

        System.out.println("总耗时：" + (System.currentTimeMillis() - begin) + "ms");
        if (size < 200)
            System.out.println(JSON.toJSONString(deserialize));
    }

    public void json(List args) {
        long begin = System.currentTimeMillis();
        byte[] bytes = JSON.toJSONString(args).getBytes();
        int bb = bytes.length / 1024;
        int mm = bb / 1024;
        System.out.println("json序列化时间：" + (System.currentTimeMillis() - begin) + "ms,大小：" + bytes.length + "B," + bb + "KB," + mm + "MB");

        long begin3 = System.currentTimeMillis();
        String key = "json:" + System.nanoTime();
        System.out.println("key=" + key);
        redisUtil.set(key.getBytes(Charset.forName(chartSetName)), bytes);
        System.out.println("set Redis时间：" + (System.currentTimeMillis() - begin3) + "ms");

        long begin4 = System.currentTimeMillis();
        byte[] redisByts = redisUtil.get(key.getBytes(Charset.forName(chartSetName)));
        System.out.println("get Redis时间：" + (System.currentTimeMillis() - begin4) + "ms");

        long begin2 = System.currentTimeMillis();
        List<Person> deserialize = JSONObject.parseArray(new String(redisByts), Person.class);
        System.out.println("json反序列化时间：" + (System.currentTimeMillis() - begin2) + "ms,反序列化：" + deserialize.size());

        System.out.println("总耗时：" + (System.currentTimeMillis() - begin) + "ms");

    }

}
