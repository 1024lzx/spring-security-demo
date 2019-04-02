package com.lzx.websecuritydemo.web;

import com.lzx.websecuritydemo.service.UserService;
import com.lzx.websecuritydemo.vo.UserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;
    @PostMapping
    public String addUser(@RequestBody UserVO userVO){
        userService.addUser(userVO);
        return "success";
    }

}
