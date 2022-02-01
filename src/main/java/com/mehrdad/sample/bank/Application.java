package com.mehrdad.sample.bank;

import com.mehrdad.sample.bank.view.UserInterface;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        ApplicationContext applicationContext = SpringApplication.run(Application.class, args);
        UserInterface userInterface = applicationContext.getBean(UserInterface.class);
        userInterface.start(args);
    }

}
