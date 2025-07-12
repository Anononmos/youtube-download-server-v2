package com.example.YouTubeDL;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.example.YouTubeDL.exceptions.DownloaderExceptions.VideoDownloadException;
import com.example.YouTubeDL.exceptions.DownloaderExceptions.VideoInfoException;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@CrossOrigin
@RestController
public class VideoController {

    @Autowired
    VideoService videoService;

    @Value("${application.download.resolutions}")
    private String resolutions;
    
    @PostMapping(value = "/offload", consumes = "application/json", produces = "application/json")
    public ResponseEntity<SseEmitter> offload(@Valid @RequestBody DownloadRequest body) {

        SseEmitter sseEmitter = new SseEmitter();
        VideoParams params = body.toVideoParams();
        
        Video video;

        // TODO: Implement callbacks for completion, timeout, and error

        // sseEmitter.onCompletion( () -> {} );
        // sseEmitter.onTimeout( () -> {} );
        // sseEmitter.onError( (ex) -> {} );

        try {
            video = videoService.download(params, sseEmitter);
            // videoService.testDownload(params.type(), params.res(), sseEmitter);

            if (video != null) {
                videoService.createVideo(video);
            }

            sseEmitter.complete();
        }
        catch (VideoDownloadException e) {
            e.printStackTrace();
            e.printErrors();
            e.printWarnings();

            sseEmitter.completeWithError(e);

            return new ResponseEntity<SseEmitter>(HttpStatus.BAD_REQUEST);
        }

        catch (VideoInfoException e) {
            e.printStackTrace();
            e.printErrors();
            e.printWarnings();

            sseEmitter.completeWithError(e);

            return new ResponseEntity<SseEmitter>(HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<SseEmitter>(sseEmitter, HttpStatus.OK);
    }
    

    @GetMapping(value = "/")
    public String index() {
        return String.format("[%s]", resolutions);
    }
    
    
    @GetMapping(value = "/download")
    public String getDownload( @RequestParam(value = "url", required = true) String url ) {
        return new String("Hello World");
    }
    
}
