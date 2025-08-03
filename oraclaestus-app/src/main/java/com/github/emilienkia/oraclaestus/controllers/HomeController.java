package com.github.emilienkia.oraclaestus.controllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/")
public class HomeController {
    @Value("${spring.application.name}")
    String appName;
    @Value("${spring.application.version}")
    String appVersion;

    @GetMapping
    public String home() {
        return String.format("""
               {
                   "application": "%s",
                   "version": "%s"
               }
               """, appName, appVersion);
    }
}
