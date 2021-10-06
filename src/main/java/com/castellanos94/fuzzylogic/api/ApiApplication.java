package com.castellanos94.fuzzylogic.api;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication

public class ApiApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(ApiApplication.class,args);
    }
    @Override
    public void run(String... args) throws Exception {
        // Aqui puede levantar tareas incompleteas
    }
}
