package com.sourav.fluxlb.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BackendController {

    @GetMapping("/backend")
    public String backend() {
        return "Hello from Backend Server";
    }


}