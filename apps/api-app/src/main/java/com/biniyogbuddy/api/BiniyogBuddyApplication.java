package com.biniyogbuddy.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "com.biniyogbuddy")
@EnableJpaRepositories(basePackages = {
        "com.biniyogbuddy.users.repository",
        "com.biniyogbuddy.stocks.repository"
})
@EntityScan(basePackages = {
                "com.biniyogbuddy.users.entity",
                "com.biniyogbuddy.stocks.entity"
})
public class BiniyogBuddyApplication {

    public static void main(String[] args) {
        SpringApplication.run(BiniyogBuddyApplication.class, args);
    }
}


