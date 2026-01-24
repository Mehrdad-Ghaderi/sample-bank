package com.mehrdad.sample.bank;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Created by Mehrdad Ghaderi
 */
@SpringBootApplication
public class Application {

    @Value("${spring.datasource.url}")
    private String jdbcUrl;

    @Value("${spring.datasource.username}")
    private String dbUser;

    @PostConstruct
    public void logDbConfig() {
        System.out.println("----------- DB CONFIG -----------");
        System.out.println("URL: " + jdbcUrl);
        System.out.println("USER: " + dbUser);
        System.out.println("---------------------------------");
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
