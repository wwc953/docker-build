package org.example.docker.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description: TODO
 * @author: wangwc
 * @date: 2020/10/27 10:32
 */
@RestController
public class HelloController {

    @GetMapping("/v1/{params}")
    public String hello(@PathVariable String params){
        return  params;
    }
}
