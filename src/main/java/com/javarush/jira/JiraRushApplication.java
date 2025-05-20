package com.javarush.jira;

import com.javarush.jira.common.internal.config.AppProperties;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

import java.util.Arrays;

@SpringBootApplication
@EnableConfigurationProperties(AppProperties.class)
@EnableCaching
public class JiraRushApplication {

    public static void main(String[] args) {
        SpringApplication.run(JiraRushApplication.class, args);
    }


    @Bean
    public CommandLineRunner checkProfile(Environment env) {
        return args -> {
            System.out.println("Активный профиль: " +
                    Arrays.toString(env.getActiveProfiles()));
            if (env.getActiveProfiles().length == 0) {
                System.out.println("Нет активных профилей (used default)");
            }
        };
    }
}
