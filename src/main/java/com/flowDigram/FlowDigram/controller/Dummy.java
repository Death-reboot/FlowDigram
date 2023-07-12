package com.flowDigram.FlowDigram.controller;

import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/dummy")
public class Dummy {
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
    @GetMapping("/")
    @ResponseBody
    public List<List<String>> getRequirements() {
        return Arrays.asList(
                Arrays.asList("10 Digit valid Mobile Number"),
                Arrays.asList("Mobile Number")
        );
    }
    @PostMapping("/")
    public String post(@RequestBody String input) {
        return "The string you posted is: " + input;
    }
}
