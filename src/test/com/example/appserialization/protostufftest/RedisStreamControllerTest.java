package com.example.appserialization.protostufftest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.docker.Application;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @Description:
 * @author: wangwc
 * @date: 2020/11/7 14:37
 */
@SpringBootTest(classes = Application.class)
@AutoConfigureMockMvc
public class RedisStreamControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    String streamName = "123stream";
    String groupName = "123group";
    String consumerName = "hahahaconsumer";
    String consumerName2 = "xixixiconsumer";

    @Test
    public void createComsumer() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                .get("/createComsumer")
                .param("stream", streamName)
                .param("group", groupName)
                .param("makeStream", "true")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void add() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                .get("/add")
                .param("stream", streamName)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void read() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                .get("/read")
                .param("stream", streamName)
                .param("group", groupName)
                .param("consumer", consumerName2)
                .param("count", "1")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

    }

}
