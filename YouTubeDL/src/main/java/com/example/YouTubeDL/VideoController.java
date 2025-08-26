package com.example.YouTubeDL;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.web.util.UriUtils;

import com.example.YouTubeDL.exceptions.QueryExceptions.VideoNotFoundException;
import com.example.YouTubeDL.validation.YouTubeURLValidation.YouTubeURLValidation;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@CrossOrigin
@RestController
public class VideoController {

    @Autowired
    VideoService videoService;

    @Value("${application.download.directory}")
    private String directory;

    @Value("#{'${application.download.resolutions}'.split(', ')}")
    private List<Integer> resolutions;
    
    @PostMapping(value = "/offload", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<SseEmitter> offload(@Valid @RequestBody DownloadRequest body) {

        SseEmitter sseEmitter = new SseEmitter(0L);
        VideoParams params = body.toVideoParams(resolutions);

        videoService.download(params, sseEmitter);

        return new ResponseEntity<SseEmitter>(sseEmitter, HttpStatus.OK);
    }
    
    @PostMapping("/migrate")
    public ResponseEntity<Video> migrate(@RequestBody UploadRequest body) {
        Video video;

        try {
            video = videoService.createVideoFromFile(body.url, body.filename);
        }
        catch (FileNotFoundException e) {
            return ResponseEntity.notFound().build();
        }  
        
        return new ResponseEntity<>(video, HttpStatus.OK);
    }

    @GetMapping(value = "/")
    public String index() {
        return String.format("Available resolutions for download: %s", resolutions);
    }
    
    
    @GetMapping(value = "/download")
    public ResponseEntity<InputStreamResource> download( @RequestParam(value = "url", required = true) @YouTubeURLValidation String url ) {

        try {
            Path path = videoService.getFile(url);

            String filename = UriUtils.encodeQuery(path.getFileName().toString(), "UTF-8");

            String mimeType = Files.probeContentType(path);
            long fileSize = path.toFile().length();

            InputStreamResource resource = new InputStreamResource( Files.newInputStream(path) );

            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachment; filename=%s".formatted(filename));
            headers.add("Content-Type", mimeType);
            headers.add("Accept-Ranges", "bytes");

            return (
                ResponseEntity.ok()
                .headers(headers)
                .contentLength(fileSize)
                .body(resource)
            );

        }
        catch (VideoNotFoundException e) {
            return new ResponseEntity<>( HttpStatus.NOT_FOUND );
        } 
        catch (IOException e) {
            return new ResponseEntity<>( HttpStatus.NOT_FOUND );
        }
    }
}
