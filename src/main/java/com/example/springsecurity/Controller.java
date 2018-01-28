package com.example.springsecurity;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Controller {

    @GetMapping
    public String get() {
        return String.valueOf(System.currentTimeMillis());
    }

    @GetMapping("css/hello")
    public String cssHello() {
        return "Hello I'm secret data";
    }

    @PostMapping("post")
    public String testPost() {
        return "Hello it is post request";
    }

    @GetMapping("customHeader")
    public String customHeader(@RequestHeader("x-custom-header") String customHeader)
            throws Exception {
        return customHeader;
    }
}
