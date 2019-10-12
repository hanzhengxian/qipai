package com.linln.util;

import com.linln.domain.GameRoom;
import com.linln.domain.Guser;

import java.util.ArrayList;

public class SortUserUtils {
    public static GameRoom sort(GameRoom gameRoom, Guser guser){
        ArrayList<Guser> guserList = gameRoom.getGuserList();
        ArrayList<Guser> list = new ArrayList<>();
        list.add(guser);
        for (Guser guser1 : guserList) {
            list.add(guser1);
        }
        gameRoom.setGuserList(list);
        return gameRoom;
    }
}
