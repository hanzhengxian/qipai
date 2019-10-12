package com.linln.domain;

import lombok.Data;

//websocket 指令,后台发给前台的指令
@Data
public class Codes {
    //当前页面
    private  int inpage;

    //当前页面指令
    private  int comm;

    //对象信息
    private  Object object;
}
