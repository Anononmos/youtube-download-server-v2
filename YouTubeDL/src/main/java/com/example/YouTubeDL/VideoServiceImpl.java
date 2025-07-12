package com.example.YouTubeDL;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.example.YouTubeDL.Shell.TestParams;
import com.example.YouTubeDL.exceptions.DownloaderExceptions.VideoDownloadException;
import com.example.YouTubeDL.exceptions.DownloaderExceptions.VideoInfoException;
import com.example.YouTubeDL.threads.DownloadThread;
import com.example.YouTubeDL.threads.TestDownloadThread;
import com.example.YouTubeDL.threads.VideoInfoThread;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class VideoServiceImpl implements VideoService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Value("${application.download.directory}")
    private String directory;

    @Value("${application.test.input}")
    private String inputDirectory;

    @Value("${application.test.output}")
    private String outputDirectory;

    // private static final int LINE_LIMIT = 10;

    @Override
    public int createVideo(Video video) {

        String cmd = String.format("INSERT INTO video VALUES (%s)", video.getParamsTemplate());

        return jdbcTemplate.update( cmd, video.toObjectArray() );
    }

    @Override
    public int createVideoFromFile(VideoParams params, String filename) throws IOException, VideoInfoException {

        // Check if file exists
        // Get video info

        File file = new File( String.format("%s/%s", directory, filename) ); 

        if ( !file.exists() || file.isDirectory() ) {
            throw new FileNotFoundException( String.format("Error: File with name [%s] does not exist in directory [%s]", filename, directory) );
        }

        ExecutorService service = Executors.newSingleThreadExecutor();
        VideoInfoThread infoThread = new VideoInfoThread(params);

        Future<Video> futureVideo = service.submit(infoThread);

        Video video = null;

        try {
            video = futureVideo.get();
        }
        catch (ExecutionException e) {
            e.printStackTrace();

            Throwable cause = e.getCause();

            if (cause instanceof VideoInfoException) {
                throw (VideoInfoException) cause;
            }

            throw new IOException(e);
        }
        catch (InterruptedException e) {
            e.printStackTrace();

            throw new IOException(e);
        }
        finally {
            service.shutdown();
        }

        return createVideo(video);
    }

    @Override
    public Video download(VideoParams params, SseEmitter emitter) throws VideoDownloadException, VideoInfoException {

        VideoInfoThread infoThread = new VideoInfoThread(params);
        DownloadThread downloadThread = new DownloadThread(params, directory, (line, _) -> { 

            String cleanedLine = line.replaceAll("\\p{Cc}\\[K", "");
            emitProgressData(emitter, cleanedLine); 
        });

        ExecutorService service = Executors.newFixedThreadPool(2);

        Future<Video> futureInfo = service.submit(infoThread);
        Future<?> futureVideo = service.submit(downloadThread);

        Video video = null;

        try {
            futureVideo.get();
            video = futureInfo.get();

        } 
        catch (ExecutionException e) {
            e.printStackTrace();

            Throwable cause = e.getCause();

            if (cause instanceof VideoDownloadException) {
                throw (VideoDownloadException) cause;
            }

            if (cause instanceof VideoInfoException) {
                throw (VideoInfoException) cause;
            }
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        finally {
            service.shutdown();
        }
        
        return video;
    }

    @Override
    public void testDownload(DownloadType type, Integer res, SseEmitter emitter) {

        TestParams testParams = new TestParams(inputDirectory, outputDirectory, "2M");
        TestDownloadThread downloadThread = new TestDownloadThread(testParams, type, res, (line, _) -> {

            String cleanedLine = line.replaceAll("\\p{Cc}\\[K", "");
            emitProgressData(emitter, cleanedLine); 
        });

        ExecutorService service = Executors.newSingleThreadExecutor();

        Future<Boolean> futureTestVal = service.submit(downloadThread);

        try {
            assert futureTestVal.get();
        }
        catch (ExecutionException e) {
            e.printStackTrace();

            assert false;
        }
        catch (InterruptedException e) {
            e.printStackTrace();

            assert false;
        }
        finally {
            service.shutdown();
        }
    }


    @Override
    public void emitProgressData(SseEmitter emitter, String jsonString) {
        System.out.println(jsonString);

        // TODO: Handle EOF, two sets of progress data

        if ( jsonString.isBlank() ) {
            try {
                emitter.send(
                    SseEmitter.event().name("download_complete").data(""), 
                    MediaType.APPLICATION_JSON
                );
            }
            catch (IOException e) {
                e.printStackTrace();
            }

            return;
        }

        ObjectMapper mapper = new ObjectMapper();

        try {
            ProgressJson json = mapper.readValue(jsonString, ProgressJson.class);
            String data = mapper.writeValueAsString(json);

            emitter.send( 
                SseEmitter.event().name("download_progress").data(data), 
                MediaType.APPLICATION_JSON
            );
        } 
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int deleteVideo(String videoID) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteVideo'");
    }

    @Override
    public int deleteVideoAndFile(String videoID) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteVideoAndFile'");
    }
}
