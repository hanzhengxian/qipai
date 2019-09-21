package com.linln.service.jpa;

import com.linln.domain.GameRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface GameRoomRepository extends JpaRepository<GameRoom,String>, JpaSpecificationExecutor<GameRoom> {
//
//    //保存数据
//    public void saveGameRoom(GameRoom gameRoom);
}
