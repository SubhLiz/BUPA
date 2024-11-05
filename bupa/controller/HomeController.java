package com.incture.bupa.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;

import springfox.documentation.annotations.ApiIgnore;

@Controller
@CrossOrigin("*")
public class HomeController {
	@ApiIgnore
    @GetMapping("/")
    public String welcome() {
        return "home";
    }
}
