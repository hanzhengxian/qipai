package com.linln.web.controller;

import com.linln.util.UUIDUtils;

import java.util.UUID;

public class Test1 {
    public static void main(String[] args) {
        UUID uuid = UUIDUtils.create();
        System.out.println(uuid);
        System.out.println((int)((Math.random()*9+1)*100000));

    }
}
