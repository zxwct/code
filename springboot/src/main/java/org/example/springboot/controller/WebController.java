package org.example.springboot.controller;

import jakarta.annotation.Resource;
import org.example.springboot.common.Result;
import org.example.springboot.entity.Account;
import org.example.springboot.entity.User;
import org.example.springboot.exception.CustomerException;
import org.example.springboot.service.AdminService;
import org.example.springboot.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WebController {

    @Resource
    AdminService adminService;
    @Resource
    UserService userService;

    @GetMapping("/hello")
    public Result hello() {
        return Result.success("hello");
    }

    @PostMapping("/login")
    public Result login(@RequestBody Account account) {
        Account account1 = null;
        if("ADMIN".equals(account.getRole())) {
            account1 = adminService.login(account);
        }else if("USER".equals(account.getRole())) {
            account1 = userService.login(account);
        }else {
            throw new CustomerException("非法请求");
        }

        return Result.success(account1);
    }


    @PostMapping("/register")
    public Result register(@RequestBody User user) {
        userService.register(user);
        return Result.success();
    }

    @PostMapping("/updatePassword")
    public Result updatePassword(@RequestBody Account account) {
        if("ADMIN".equals(account.getRole())) {
            adminService.updatePassword(account);
        }
        else if("USER".equals(account.getRole())) {
             userService.updatePassword(account);
        }else {
            throw new CustomerException("非法请求");
        }

        return Result.success();

    }


}
