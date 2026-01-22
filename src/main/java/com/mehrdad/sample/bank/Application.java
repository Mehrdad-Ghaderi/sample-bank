package com.mehrdad.sample.bank;

import com.mehrdad.sample.bank.view.UserInterface;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

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
        ApplicationContext applicationContext = SpringApplication.run(Application.class, args);
       /* UserInterface userInterface = applicationContext.getBean(UserInterface.class);
        userInterface.start(args);*/
    }

}
