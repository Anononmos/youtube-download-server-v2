package com.example.YouTubeDL;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.example.YouTubeDL.validation.YouTubeURLValidation.YouTubeURLValidation;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@CrossOrigin
@RestController
public class VideoController {

    @Autowired
    VideoService videoService;

    @Value("#{'${application.download.resolutions}'.split(', ')}")
    private List<Integer> resolutions;
    
    @PostMapping(value = "/offload", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<SseEmitter> offload(@Valid @RequestBody DownloadRequest body) {

        SseEmitter sseEmitter = new SseEmitter(0L);
        VideoParams params = body.toVideoParams(resolutions);

        videoService.download(params, sseEmitter);

        return new ResponseEntity<SseEmitter>(sseEmitter, HttpStatus.OK);
    }
    
    public interface InnerVideoController {
        public void emitData(SseEmitter emitter, Object data);
    }

    @GetMapping(value = "/")
    public String index() {
        return String.format("Available resolutions for download: %s", resolutions);
    }
    
    
    @GetMapping(value = "/download")
    public String download( 
        HttpServletResponse response, 
        @RequestParam(value = "url", required = true) @YouTubeURLValidation() String url 
    ) {

        

        return "Hello World";
    }
    
}
