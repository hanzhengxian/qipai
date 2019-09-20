package com.linln.web.controller;

import com.linln.domain.Guser;
import com.linln.service.WebSocketService;
import com.linln.service.jpa.GuserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
@RequestMapping("/login")
public class LoginController {
    @Autowired
    GuserRepository guserRepository;
    @Autowired
    RedisTemplate redisTemplate;


    @RequestMapping("/get")
    @ResponseBody
    public String get(){
        redisTemplate.boundSetOps("ddd").add("1232");
        String a = (String)redisTemplate.boundSetOps("ddd").getKey();
        return a;

    }

    @RequestMapping("/getuser")
    @ResponseBody
    public Guser getuser(){
        Guser guser1 = guserRepository.findGuserById("123");
        return guser1;
    }
}
