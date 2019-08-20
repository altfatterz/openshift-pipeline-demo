package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class OpenshiftPipelineDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(OpenshiftPipelineDemoApplication.class, args);
    }

    @RestController
    static class GreetingController {

        @GetMapping("/")
        public String greet() {
            return "Hello World!";
        }

    }
}


