package org.example.docker.redisutils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.example.docker.po.Person;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.JedisCluster;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description: TODO
 * @author: wangwc
 * @date: 2020/11/1 14:34
 */
@Component
public class RedisUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(RedisUtil.class);

    public static final String rpopScript = "local array = {}\n" +
            "local arrayLength = 1\n" +
            "if (redis.call(\"llen\", KEYS[1]) > tonumber(ARGV[1])) then\n" +
            "    arrayLength = tonumber(ARGV[1])\n" +
            "else\n" +
            "    arrayLength = redis.call(\"llen\", KEYS[1])\n" +
            "end\n" +
            "for i = 1, arrayLength do\n" +
            "    array[i] = redis.call(\"rpop\", KEYS[1])\n" +
            "end\n" +
            "return array";

    @Autowired
    private JedisCluster jedisCluster;

    /**
     * 批量获取rpop
     * @param key
     * @param size
     * @return
     */
    public List<String> rpop(String key, int size) {
        List<String> resultList = null;
        try {
            resultList = (List<String>) jedisCluster.eval(rpopScript, Collections.singletonList(key), Collections.singletonList(String.valueOf(size)));
        } catch (Exception e) {
            e.printStackTrace();
            if (resultList != null) {
                jedisCluster.lpush(key, resultList.toArray(new String[resultList.size()]));
            }
            return null;
        }
        return resultList;
    }

    /**
     * 批量获取rpop
     * @param key
     * @param size
     * @return
     */
    public <T> List<T> rpop(String key, int size, Class<T> clazz) {
        List<String> resultList = rpop(key, size);
        List<T> collect = null;
        try {
            collect = resultList.parallelStream().map(v -> JSONObject.parseObject(v, clazz)).collect(Collectors.toList());
        } catch (Exception e) {
            if (resultList != null) {
                jedisCluster.lpush(key, resultList.toArray(new String[resultList.size()]));
            }
        }
        return collect;
    }

    /**
     * 批量lpush
     * @param key
     * @param collection
     * @return
     */
    public Long lpush(String key, List<?> collection) {
        String[] array = collection.parallelStream().map(v -> JSON.toJSONString(v)).toArray(String[]::new);
        Long res = jedisCluster.lpush(key, array);
        return res;
    }

    /**
     * 设置缓存
     *
     * @param key   缓存key
     * @param value 缓存value
     */
    public void set(String key, String value) {
        jedisCluster.set(key, value);
        LOGGER.debug("RedisUtil:set cache key={},value={}", key, value);
    }

    public void set(byte[] key, byte[] value) {
        jedisCluster.set(key, value);
        LOGGER.debug("RedisUtil:set cache key={},value={}", key, value);
    }

    /**
     * 设置缓存对象
     *
     * @param key 缓存key
     * @param obj 缓存value
     */
    public <T> void setObject(String key, T obj, int expireTime) {
        jedisCluster.setex(key, expireTime, JSON.toJSONString(obj));
    }

    /**
     * 获取指定key的缓存
     *
     * @param key---JSON.parseObject(value, User.class);
     */
    public String getObject(String key) {
        return jedisCluster.get(key);
    }

    /**
     * 判断当前key值 是否存在
     *
     * @param key
     */
    public boolean hasKey(String key) {
        return jedisCluster.exists(key);
    }


    /**
     * 设置缓存，并且自己指定过期时间
     *
     * @param key
     * @param value
     * @param expireTime 过期时间
     */
    public void setWithExpireTime(String key, String value, int expireTime) {
        jedisCluster.setex(key, expireTime, value);
        LOGGER.debug("RedisUtil:setWithExpireTime cache key={},value={},expireTime={}", key, value, expireTime);
    }


    /**
     * 获取指定key的缓存
     *
     * @param key
     */
    public String get(String key) {
        String value = jedisCluster.get(key);
        LOGGER.debug("RedisUtil:get cache key={},value={}", key, value);
        return value;
    }

    public byte[] get(byte[] key) {
        byte[] value = jedisCluster.get(key);
        LOGGER.debug("RedisUtil:get cache key={},value={}", key, value);
        return value;
    }

    /**
     * 删除指定key的缓存
     *
     * @param key
     */
    public void delete(String key) {
        jedisCluster.del(key);
        LOGGER.debug("RedisUtil:delete cache key={}", key);
    }

    public void delete(byte[] key) {
        jedisCluster.del(key);
        LOGGER.debug("RedisUtil:delete cache key={}", key);
    }


}
