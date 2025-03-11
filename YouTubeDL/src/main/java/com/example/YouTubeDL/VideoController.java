package com.example.YouTubeDL;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;



@RestController
public class VideoController {
    
    @GetMapping("/download")
    public String download(String param) {
        return "";
    }

    @GetMapping("videos")
    public String videos(@RequestParam String param) {
        return new String();
    }
    
    @PostMapping("/offload")
    public String postMethodName(@RequestBody String entity) {
        //TODO: process POST request
        
        return entity;
    }
}
