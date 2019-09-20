package com.linln.domain;

import com.linln.util.UUIDUtils;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "qp_guser")
public class Guser {

    @Id
    @Column(name = "id")
    private String id = UUIDUtils.create() + "";

    @Column(name = "name")
    private String name;

    @Column(name = "coin")
    private int coin;

    @Column(name = "diamond")
    private String diamond;

    @Column(name = "province")
    private String province;

    @Column(name = "city")
    private String city;

    @Override
    public String toString() {
        return "Guser{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", coin=" + coin +
                ", diamond='" + diamond + '\'' +
                ", province='" + province + '\'' +
                ", city='" + city + '\'' +
                '}';
    }
}
