package com.linln.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.linln.config.GetHttpSessionConfigurator;
import com.linln.domain.Guser;
import com.linln.service.jpa.GuserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.GsonJsonParser;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.util.HashMap;
import java.util.Map;


@ServerEndpoint(value = "/websocket",configurator= GetHttpSessionConfigurator.class)
@Component
public class WebSocketService  {

    private String userid;

    private Session session;
    private HttpSession httpSession;





    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session,EndpointConfig config) {

        System.out.println(this);
        String userid = session.getQueryString();
        System.out.println(userid);
        System.out.println("连接成功");
        this.httpSession = (HttpSession) config.getUserProperties().get(HttpSession.class.getName());
        if(httpSession != null){
            ApplicationContext app = WebApplicationContextUtils.getRequiredWebApplicationContext(httpSession.getServletContext());
            GuserRepository guserRepository = app.getBean(GuserRepository.class);
            Guser guser = guserRepository.findGuserById(userid);
            session.getAsyncRemote().sendText(JSON.toJSONString(guser));

        }
    }
    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        System.out.println("有一连接关闭");
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param
     */
    @OnMessage
    public void onMessage(String userid , Session session) {
        System.out.println("来自创建房间的消息:" + userid);

    }

    /**
     * 发生错误时调用
     */
    @OnError
    public void onError(Session session, Throwable error) {
        System.out.println("发生错误");
        error.printStackTrace();
    }


}
