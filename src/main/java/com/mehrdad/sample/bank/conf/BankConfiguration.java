package com.mehrdad.sample.bank.conf;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Scanner;

/**
 * Created by Mehrdad Ghaderi
 */
@Configuration
public class BankConfiguration {

    @Bean
    public Scanner scanner() {
        return new Scanner(System.in);
    }
}
