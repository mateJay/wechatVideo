package com.imooc.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @Author: mate_J
 * @Date: 2019/1/25 12:17
 * @Version 1.0
 */
@Controller
public class Hello {

    @ResponseBody
    @GetMapping("/hello")
    public String hello(){
        return "hello";
    }

    @GetMapping("/center")
    public String center(){
        return "center";
    }
}
