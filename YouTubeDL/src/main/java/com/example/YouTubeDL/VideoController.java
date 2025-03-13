package com.example.YouTubeDL;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
public class VideoController {
    
    @GetMapping("/")
    public String getMethodName() {
        return "Hello World";
    }
    
}
