package ru.practicum.shareit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Map;

@SpringBootApplication
public class ShareItApp {
    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(ShareItApp.class);
        app.setDefaultProperties(Map.of(
                "spring.datasource.url", "jdbc:postgresql://localhost:25432/testdb",
                "spring.datasource.username", "pgadmin",
                "spring.datasource.password", "pgadmin",
                "spring.datasource.driver-class-name", "org.postgresql.Driver"
        ));
        app.run(args);

    }
}