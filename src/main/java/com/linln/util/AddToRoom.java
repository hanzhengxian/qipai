package com.linln.util;

import com.linln.domain.GameRoom;
import com.linln.domain.Guser;
import lombok.Data;
import org.springframework.data.redis.core.RedisTemplate;
import java.util.ArrayList;

@Data
public class AddToRoom {

    private GameRoom gameRoom;
    private String userid;
    private RedisTemplate redisTemplate;
    private String roomid;

    public static GameRoom addToRoom(GameRoom gameRoom,String userid,RedisTemplate redisTemplate,String roomid){
        Guser user = (Guser)redisTemplate.boundHashOps("userid").get(userid);
        ArrayList<Guser> guserList = gameRoom.getGuserList();
        ArrayList<Guser> list = new ArrayList<>();
        list.add(user);
        for (Guser guser : guserList) {
            list.add(guser);
        }
        gameRoom.setGuserList(list);
        //保存房间到缓存，及时给前端响应
        redisTemplate.boundHashOps("gameRoom").put(roomid,gameRoom);
        return gameRoom;
    }
}
