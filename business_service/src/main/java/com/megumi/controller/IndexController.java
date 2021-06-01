package com.megumi.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/index")
@ResponseBody
public class IndexController {

    @GetMapping("/background/img")
    public String getBackgroundImg() {
        return "测试";
    }
}
