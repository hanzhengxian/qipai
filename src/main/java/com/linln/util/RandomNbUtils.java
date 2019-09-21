package com.linln.util;

public class RandomNbUtils {
    //获取随机数6位数，做房间编号
    public static String getRandomNumber(){
        int v = (int)((Math.random() * 9 + 1) * 100000);
        return String.valueOf(v);
    }
}
