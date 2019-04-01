package com.lzx.websecuritydemo.api.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/open-api")
public class ApiController {
    @PostMapping("/user")
    public String addUser(){
        return "forward:/user";
    }
}
