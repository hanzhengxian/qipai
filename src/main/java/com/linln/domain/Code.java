package com.linln.domain;

import lombok.Data;


//websocket 指令
@Data
public class Code {
    //当前页面
    private  int p;

    //当前页面指令
    private  int c;

    //创建房间发送的房间id值
    private String roomid;

    //刷新的时候通过玩家id取后台请求玩家信息。
    private String guserId;

}
