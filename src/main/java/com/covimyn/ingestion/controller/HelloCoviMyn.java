package com.covimyn.ingestion.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloCoviMyn {

    @GetMapping("/")
    public String sayHello() {
        return "Hello!";
    }

}
