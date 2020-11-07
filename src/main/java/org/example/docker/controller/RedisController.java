package org.example.docker.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.StreamEntry;
import redis.clients.jedis.StreamEntryID;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @Description: TODO
 * @author: wangwc
 * @date: 2020/11/7 14:27
 */
@RestController
public class RedisController {

    private static int i = 0;
    @Autowired
    private Jedis jedis;

    @GetMapping("/createComsumer")
    public void createCrgoup(@RequestParam("stream") String stream, @RequestParam("group") String group
            , @RequestParam("makeStream") Boolean makeStream) {
        //String key, String groupname, StreamEntryID id, boolean makeStream
        /**
         * key为stream name, group为消费组，id为上次读取的位置，如果空则重新读取，makeStream是否创建流，已有的话就不用创建
         */
        String result = jedis.xgroupCreate(stream, group, null, makeStream);
        System.out.println(result);
    }

    @GetMapping("/add")
    public void addMessage(@RequestParam("stream") String stream) {
        for (int j = 0; j < 1000; j++) {
            //这里可以添加更多的属性
            Map map = new HashMap();
            map.put("date", System.currentTimeMillis() + "");
            StreamEntryID date = jedis.xadd(stream, new StreamEntryID(map.get("date") + "-" + (++i)), map);
            System.out.println("Add StreamEntryID:" + date);
        }

    }


    @GetMapping("/read")
    public void readGroup(@RequestParam("group") String group,
                          @RequestParam("consumer") String consumer,
                          @RequestParam("count") int count,
                          @RequestParam("stream") String stream) {

        Map<String, StreamEntryID> t = new HashMap();
        t.put(stream, StreamEntryID.UNRECEIVED_ENTRY); //null 则为 > 重头读起，也可以为$接受新消息，还可以是上一次未读完的消息id
//        t.put(stream, StreamEntryID.LAST_ENTRY);//从最后开始接受
        Map.Entry e = null;
        for (Map.Entry c : t.entrySet()) {
            e = c;
        }

        //noAck为false的话需要手动ack，true则自动ack. commsumer新建的方式为xreadgroup
        //count 一次消费的数量
        List<Map.Entry<String, StreamEntryID>> list = jedis.xreadGroup(group, consumer, count, 0, false, e);
        if (list == null) {
            System.out.println("无数据....");
            return;
        }
        for (Map.Entry m : list) {
            System.out.println(m.getKey() + "---" + m.getValue().getClass());
            if (m.getValue() instanceof ArrayList) {
                List<StreamEntry> l = (List) m.getValue();
                Map<String, String> result = l.get(0).getFields();
                for (Map.Entry entry : result.entrySet()) {
                    System.out.println(entry.getKey() + "---" + entry.getValue());
                }
                try {
                    TimeUnit.SECONDS.sleep(2);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
                int i = 1 / 0;
                long xack = jedis.xack(stream, group, l.get(0).getID());
                System.out.println("消息消费成功" + xack);
            }
        }
    }

/**
 * String groupname, String consumer, int count, long block, final boolean noAck, Map.Entry<String, StreamEntryID>... streams
 */

}
