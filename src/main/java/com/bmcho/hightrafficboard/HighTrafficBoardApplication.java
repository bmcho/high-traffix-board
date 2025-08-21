package com.bmcho.hightrafficboard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class HighTrafficBoardApplication {

    public static void main(String[] args) {
        SpringApplication.run(HighTrafficBoardApplication.class, args);
    }

}
