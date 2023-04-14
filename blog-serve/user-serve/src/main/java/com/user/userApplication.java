package com.user;

import com.user.Utils.TokenUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication

public class userApplication {
    public static void main(String[] args) {
         ApplicationContext application = SpringApplication.run(userApplication.class, args);
            TokenUtil.setApplicationContext(application);
    }

}
