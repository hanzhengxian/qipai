package com.linln.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.linln.config.GetHttpSessionConfigurator;
import com.linln.domain.Code;
import com.linln.domain.Codes;
import com.linln.domain.GameRoom;
import com.linln.domain.Guser;
import com.linln.service.jpa.GameRoomRepository;
import com.linln.service.jpa.GuserRepository;
import com.linln.util.AddToRoom;
import com.linln.util.SortUserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.GsonJsonParser;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.support.WebApplicationContextUtils;
import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.util.*;
import java.util.concurrent.CopyOnWriteArraySet;


@ServerEndpoint(value = "/websocket",configurator= GetHttpSessionConfigurator.class)
@Component
public class WebSocketService  {

    private String userid;
    private GameRoomRepository gameRoomRepository;
    private RedisTemplate redisTemplate;
    private Session session;
    private HttpSession httpSession;
    //concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。
    private static CopyOnWriteArraySet<WebSocketService> webSocketSet = new CopyOnWriteArraySet<WebSocketService>();

    //存放用户的id与session关系,以便给玩家响应数据
    private static HashMap<String, Session> mapSession = new HashMap();


    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session,EndpointConfig config){
        Codes codes = new Codes();
        this.session = session;
        webSocketSet.add(this);
        userid = session.getQueryString();
        //存放玩家id和session，以便房间变动发送房间信息
        mapSession.put(userid,session);
        System.out.println(userid);
        System.out.println("连接成功");
        this.httpSession = (HttpSession) config.getUserProperties().get(HttpSession.class.getName());
        if(httpSession != null){
            ApplicationContext app = WebApplicationContextUtils.getRequiredWebApplicationContext(httpSession.getServletContext());
            GuserRepository guserRepository = app.getBean(GuserRepository.class);
            redisTemplate = app.getBean("redisTemplate",RedisTemplate.class);
            gameRoomRepository = app.getBean(GameRoomRepository.class);
            Guser guser = guserRepository.findGuserById(userid);
            //redisTemplate.opsForValue().set(userid,guser);
            redisTemplate.boundHashOps("userid").put(userid,guser);
            codes.setInpage(2);
            codes.setComm(4);
            codes.setObject(guser);
            session.getAsyncRemote().sendText(JSON.toJSONString(codes));
        }
    }
    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose(Session session) {
        webSocketSet.remove(this);
        System.out.println("有一连接关闭");
        //删除缓存中的玩家信息
        redisTemplate.boundHashOps("userid").delete(userid);
        //删除map集合中的玩家id和session
        mapSession.remove(userid);
        //删除房间里的玩家信息，房间人数减一
        //重新给房间内的玩家响应信息

    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param
     */
    @OnMessage
    public void onMessage(String jsonString , Session session) {

        //获取前端传来的命令，p指令0时候，c=1创建房间，c=2时候查看房间
        JSONObject jsonObject = JSON.parseObject(jsonString);
        Code code = (Code)JSON.toJavaObject(jsonObject, Code.class);
        Codes codes = new Codes();
        switch (code.getP()){
            //处理大厅的一些响应和指令
            case 0:
                switch (code.getC()){
                    //创建房间
                    case 1:
                        //用户创建房间
                        System.out.println("来自创建房间的消息:" + userid);
                        GameRoom gameRoom = new GameRoom();
                        gameRoom.setCreaterId(userid);
                        gameRoom.setPlayers(1);
                        gameRoom.setComm(5);
                        Guser user = (Guser)redisTemplate.boundHashOps("userid").get(this.userid);
                        ArrayList<Guser> guserList = gameRoom.getGuserList();
                        guserList.add(user);
                        gameRoom.setGuserList(guserList);
                        //用户id对应房间id进行存储。
                        redisTemplate.boundHashOps("user_roomid").put(userid,gameRoom.getRoomid());
                        //保存房间到缓存，及时给前端响应
                        redisTemplate.boundHashOps("gameRoom").put(gameRoom.getRoomid(),gameRoom);
                        //添加到命令里
                        codes.setInpage(0);
                        codes.setComm(3);
                        codes.setObject(gameRoom);
                        session.getAsyncRemote().sendText(JSON.toJSONString(codes));
                        break;
                    //查看房间
                    case 2:
                        //键值对存储房间id，房间对象
                        Map map = redisTemplate.boundHashOps("gameRoom").entries();
                        //获取键的集合
                        Set set = map.keySet();
                        //获取房间对象，查看房间人数
                        for (Object o : set) {
                            GameRoom room = (GameRoom)map.get(o);
                            int plays = room.getPlayers();
                            if (plays < 4){
                                //有房间,响应
                                codes.setInpage(0);
                                codes.setComm(1);
                            }else {
                                //没有房间，响应
                                codes.setInpage(0);
                                codes.setComm(2);
                            }
                        }
                        //把查询结果通过命令给玩家响应过去
                        session.getAsyncRemote().sendText(JSON.toJSONString(codes));
                        break;
                    //加入房间
                    case 3:
                        String roomid = code.getRoomid();
                        Map map1 = redisTemplate.boundHashOps("gameRoom").entries();
                        Object ogameRoom1 = redisTemplate.boundHashOps("gameRoom").get(roomid);
                        GameRoom room=null;
                        if(null!=ogameRoom1){
                            room=(GameRoom) ogameRoom1;
                        }
                        if( null!=room && (room.getPlayers() < 4)){
                            //让玩家加入房间，并把房间数据传递过去
                            int num = room.getPlayers();
                            room.setPlayers(num + 1);
                            String userid = session.getQueryString();
                            //往该房间里放玩家，并把房间信息储存redis中，并返回该房间
                            Guser guser = (Guser)redisTemplate.boundHashOps("userid").get(userid);
                            ArrayList<Guser> userList = room.getGuserList();
                            userList.add(guser);
                            room.setGuserList(userList);
                            //保存房间到缓存，及时给前端响应
                            redisTemplate.boundHashOps("gameRoom").put(roomid,room);
                            codes.setInpage(0);
                            codes.setComm(5);
                            codes.setObject(room);
                            //房间更新完成，实时更新房间数据给玩家
                            for (Guser guser1 : userList) {
                                String id = guser1.getId();
                                Session sessions = mapSession.get(id);
                                sessions.getAsyncRemote().sendText(JSON.toJSONString(codes));
                            }

                        }else {
                            //房间号不存在
                            codes.setInpage(0);
                            codes.setComm(2);
                        }


                        //GameRoom room = new GameRoom();
                        //获取键的集合
                        Set set1 = map1.keySet();
//                        for (Object object : set1) {
//                            String s = object.toString();
//                            GameRoom room = (GameRoom)map1.get(object);
//                            //房间号存在map1.get(object).getPlayers() < 4
//                            System.out.println(roomid == s);
//                            System.out.println(room.getPlayers() < 4);
//                            if( (roomid.equals(s)) && (room.getPlayers() < 4)){
//                                //让玩家加入房间，并把房间数据传递过去
//                                int num = room.getPlayers();
//                                room.setPlayers(num + 1);
//                                String userid = session.getQueryString();
//                                //往该房间里放玩家，并把房间信息储存redis中，并返回该房间
//                                Guser guser = (Guser)redisTemplate.boundHashOps("userid").get(userid);
//                                ArrayList<Guser> userList = room.getGuserList();
//                                userList.add(guser);
//                                room.setGuserList(userList);
//                                //保存房间到缓存，及时给前端响应
//                                redisTemplate.boundHashOps("gameRoom").put(roomid,room);
//                                codes.setInpage(0);
//                                codes.setComm(5);
//                                codes.setObject(room);
//                                //房间更新完成，实时更新房间数据给玩家
//                                for (Guser guser1 : userList) {
//                                    String id = guser1.getId();
//                                    Session sessions = mapSession.get(id);
//                                    sessions.getAsyncRemote().sendText(JSON.toJSONString(codes));
//                                }
//
//                            }else {
//                            //房间号不存在
//                                codes.setInpage(0);
//                                codes.setComm(2);
//                                session.getAsyncRemote().sendText(JSON.toJSONString(codes));
//                            }
//                        }
                        break;
                     //刷新，获取玩家信息
                    case 4:
                        String guserId = code.getGuserId();
                        Guser guser = (Guser)redisTemplate.boundHashOps("userid").get(guserId);
                        codes.setInpage(2);
                        codes.setComm(6);
                        codes.setObject(guser);
                        session.getAsyncRemote().sendText(JSON.toJSONString(codes));
                        break;
                    //刷新，获取房间信息,通过玩家id获取房间信息。玩家对应的房间信息也要保存。玩家id对应玩家对象。玩家id对应房间对象。
                    case 5:
                        String id = code.getGuserId();
                        String rid = (String) redisTemplate.boundHashOps("user_roomid").get(id);
                        GameRoom gameRoom1 = (GameRoom) redisTemplate.boundHashOps("gameRoom").get(rid);
                        codes.setInpage(0);
                        codes.setComm(3);
                        codes.setObject(gameRoom1);
                        session.getAsyncRemote().sendText(JSON.toJSONString(codes));
                        System.out.println(codes);
                        break;
                }
                break;
            //处理房间的一些响应和指令
            case 1:
                break;
        }
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
