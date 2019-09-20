package com.linln.service.jpa;

import com.linln.domain.Guser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Component;

public interface GuserRepository extends JpaRepository<Guser,String> , JpaSpecificationExecutor<Guser> {

    //通过id值获取玩家信息
    Guser findGuserById(String id);

}
