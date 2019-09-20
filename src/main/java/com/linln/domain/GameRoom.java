package com.linln.domain;

import com.linln.util.UUIDUtils;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import javax.persistence.*;
import java.util.Date;
import java.util.Map;
import java.util.Date;
import java.util.Map;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import org.hibernate.annotations.GenericGenerator;


/**
 * 游戏房间对象
 *
 * @author
 *
 */

@Data
public class GameRoom {

    private String id = UUIDUtils.create() + "";
    private String name ;
    private String code ;
    /**
     * 房间ID，房卡游戏的 房间ID是 6位数字，其他为 UUID
     */
    private String roomid;
    /**
     * 是否比赛房间
     */
    private boolean matchmodel;
    /**
     * 赛事ID
     */
    private String matchid;
    /**
     * 比赛场次
     */
    private int matchscreen;
    /**
     * 比赛类型
     */
    private String matchtype;
    /**
     * 最后赢的人 ， 可多人 ， 逗号隔开
     */
    private String lastwinner;


    private Date createtime ;
    private String parentid ;
    private String typeid ;
    private String creater;
    private String username ;
    /**
     * 当前状态
     */
    private String status;

    private Date updatetime;
    private String orgi;
    private String area;
    /**
     * 游戏类型 ： 麻将：地主：德州
     */
    private String game;
    /**
     * 最大游戏人数
     */
    private int players;
    /**
     * 发牌数量
     */
    private int cardsnum;
    /**
     * 游戏房间当前人数
     */
    private int curpalyers;
    /**
     * 房主 ，开设房间的人 或第一个进入的人
     */
    private String master;
    /**
     * 房间类型：[房卡、大厅]
     */
    private String roomtype;
    /**
     * 是否房卡模式
     */
    private boolean cardroom;
    /**
     * 玩法
     */
    private String playway;
    /**
     * 局数
     */
    private int numofgames;
    /**
     * 已完局数
     */
    private int currentnum;
    /**
     * 房间的创建人
     */
    private Guser masterUser;
    /**
     * 房间玩法
     */
    //private GamePlayway gamePlayway;





    /**
     * 房卡模式下的自定义参数
     */
//    private Map<String,String> extparams;
//
//    @Id
//    @Column(length = 32)
//    @GeneratedValue(generator = "system-uuid")
//    @GenericGenerator(name = "system-uuid", strategy = "assigned")

}
