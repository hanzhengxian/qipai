package com.linln;

import com.linln.domain.Guser;
import com.linln.service.jpa.GuserRepository;
import org.apache.catalina.Session;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.EnableAsync;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;
import java.util.Enumeration;


@ComponentScan(value = {"com"})
@EnableCaching
@SpringBootApplication
@EnableAsync//开启异步执行
@EnableJpaAuditing
public class Application {


    public static void main(String[] args) {
        ConfigurableApplicationContext applicationContext = SpringApplication.run(Application.class, args);
        GuserRepository guserRepository = applicationContext.getBean(GuserRepository.class);

        RedisTemplate redisTemplate = (RedisTemplate)applicationContext.getBean("redisTemplate");
        //redisTemplate.opsForValue().set("guser",new Guser());
        
    }
}
