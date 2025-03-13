package com.example.YouTubeDL;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
public class VideoController {
    
    @PostMapping("/download")
    public String download(@RequestBody String entity) {
        //TODO: process POST request
        
        return entity;
    }
    
    @GetMapping("/download")
    public String getDownload(@RequestParam String param) {
        return new String();
    }
    
}
