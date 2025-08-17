package com.example.YouTubeDL;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.postgresql.util.PGInterval;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.example.YouTubeDL.downloadOptions.DownloadRate;
import com.example.YouTubeDL.downloadOptions.DownloadType;
import com.example.YouTubeDL.exceptions.InternalServerException;
import com.example.YouTubeDL.exceptions.DownloaderExceptions.AudioDownloadException;
import com.example.YouTubeDL.exceptions.DownloaderExceptions.DownloaderException;
import com.example.YouTubeDL.exceptions.DownloaderExceptions.VideoDownloadException;
import com.example.YouTubeDL.exceptions.DownloaderExceptions.VideoInfoException;
import com.example.YouTubeDL.exceptions.QueryExceptions.VideoNotFoundException;
import com.example.YouTubeDL.shells.Downloader;
import com.example.YouTubeDL.shells.ShellOutput;
import com.example.YouTubeDL.shells.Tester;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class VideoServiceImpl implements VideoService {

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    @Value("${application.download.directory}")
    private String directory;

    @Value("${application.test.input}")
    private String inputDirectory;

    @Value("${application.test.output}")
    private String outputDirectory;

    // private static final int EMIT_EVERY = 100;

    @Override
    public int createVideo(Video video) {
        final String channelInsertCmd = "INSERT INTO channel VALUES (:id, :name, DEFAULT) ON CONFLICT (id) DO UPDATE SET num_videos = EXCLUDED.num_videos + 1";
        int changedRows;

        SqlParameterSource channelInsertParams = new MapSqlParameterSource()
        .addValue("id", video.channelID() )
        .addValue("name", video.channel() );

        // Must update 1 line
        changedRows = jdbcTemplate.update(channelInsertCmd, channelInsertParams);

        final String videoInsertCmd = "INSERT INTO video VALUES (:media::MEDIA, :id, :title, :channel, :duration, :uploaded, :downloaded, :resolution, :path)";

        SqlParameterSource videoInsertParams = new MapSqlParameterSource()
        .addValue("media", video.media().label)
        .addValue("id", video.id())
        .addValue("title", video.title())
        .addValue("channel", video.channelID())
        .addValue("duration", video.duration())
        .addValue("uploaded", video.uploaded())
        .addValue("downloaded", video.downloaded())
        .addValue("resolution", video.resolution())
        .addValue("path", video.filePath());

        changedRows = jdbcTemplate.update(videoInsertCmd, videoInsertParams);

        return changedRows;
    }

    @Override
    public int createVideoFromFile(VideoParams params, String filename) throws FileNotFoundException {

        // Check if file exists
        // Get video info

        File file = new File( "%s/%s".formatted(directory, filename) ); 

        if ( !file.exists() || file.isDirectory() ) {
            throw new FileNotFoundException( String.format("Error: File with name [%s] does not exist in directory [%s]", filename, directory) );
        }

        Downloader downloader = new Downloader(params, directory);
        ShellOutput<Video, VideoInfoException> futureInfo = downloader.getVideoInfo();

        try {
            futureInfo.error.join();
            Video video = futureInfo.result.join();

            return createVideo(video);
        }
        catch (CompletionException e) {
            try {
                throw e.getCause();
            }
            catch (VideoInfoException ex) {
                throw ex;
            }
            catch (InternalServerException ex) {
                throw ex;
            }
            catch (Throwable impossible) {
                impossible.printStackTrace();

                throw new InternalServerException(impossible);
            }
        }
        catch (CancellationException e) {
            e.printStackTrace();

            throw new InternalServerException(e);
        }
    }

    @Override
    public Video getVideo(String url) throws VideoNotFoundException {
        final String id = getIDFromURL(url);
        final String cmd = "SELECT v.*, c.name AS channel_name FROM video v JOIN channel c ON c.id=v.channel WHERE v.id=:id";

        SqlParameterSource params = new MapSqlParameterSource("id", id);

        Video video = jdbcTemplate.query(cmd, params, (result) -> {

            // If no video exists
            if ( !result.isBeforeFirst() ) {
                return null;
            }

            return new Video(result);
        });

        if (video == null) {
            throw new VideoNotFoundException(url);
        }

        return video;
    }

    @Override
    public List<Video> getVideos(final int limit, final int page) {
        final String cmd = "SELECT v.*, c.name AS channel_name FROM video v JOIN channel c ON c.id=v.channel LIMIT :limit OFFSET :offset";

        if (limit <= 0 || limit >= 10) {
            throw new IllegalArgumentException("ERROR: The specified limit is out of bounds.");
        }

        if (page < 0) {
            throw new IllegalArgumentException("ERROR: The specified page is negative.");
        }

        SqlParameterSource params = new MapSqlParameterSource()
        .addValue("limit", limit)
        .addValue("offset", page * limit);

        List<Video> videos = jdbcTemplate.query( cmd, params, (rs, row) -> new Video(rs) );

        return videos;
    }

    @Override 
    public String getFile(final String url) throws VideoNotFoundException {
        final String id = getIDFromURL(url);
        final String cmd = "SELECT file_path FROM video WHERE id=:id";

        SqlParameterSource params = new MapSqlParameterSource("id", id);

        String filePath = jdbcTemplate.query(cmd, params, (result) -> {

            if ( !result.isBeforeFirst() ) {
                return null;
            }

            return result.getString("file_path");
        });

        if (filePath == null) {
            throw new VideoNotFoundException(url);
        }

        return "%s/%s".formatted(directory, filePath);
    }

    @Override
    public Channel getChannel(String channelID) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getChannel'");
    }

    @Override
    public String getIDFromURL(final String url) throws VideoNotFoundException {
        final Map<String, String> urlRegexPairs = Map.ofEntries(
                Map.entry("https://www.youtube.com/watch?", "v=([^&]+)"), 
                Map.entry("https://www.youtube.", "^([^/?]+)")
            );

        String id = null;

        for ( var entry : urlRegexPairs.entrySet() ) {
            String key = entry.getKey();
            String value = entry.getValue();

            if ( url.startsWith(key) ) {
                String params = url.substring( key.length() ); // Strips starting characters

                Pattern pattern = Pattern.compile(value);
                Matcher matcher = pattern.matcher(params);

                id = matcher.find() ? matcher.group(1) : null;
            }
        }

        if (id == null) {
            throw new VideoNotFoundException(url);
        }

        return id;
    }

    @Override
    public int deleteVideo(final String videoID) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteVideo'");
    }

    @Override
    public int deleteVideoAndFile(final String videoID) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteVideoAndFile'");
    }

    @Override
    public void download(VideoParams params, SseEmitter emitter) {
        ExecutorService service = Executors.newSingleThreadExecutor();
        Downloader downloader = new Downloader(params, directory);

        ShellOutput<Void, ? extends DownloaderException> futureDownload;
        futureDownload = switch ( params.type() ) {
            case Video -> {
                yield downloader.downloadVideo((line, i) -> emitProgressData(emitter, line, i));
            }
            case Audio -> {
                yield downloader.downloadAudio((line, i) -> emitProgressData(emitter, line, i));
            }
        };

        // Returns number of rows modified in Video table
        service.submit(() -> {

            try {
                ShellOutput<Video, VideoInfoException> futureInfo = downloader.getVideoInfo();

                futureInfo.error.join();
                Video video = futureInfo.result.join();

                System.out.println("Waiting for download to complete.");

                futureDownload.error.join();
                futureDownload.result.join();

                emitter.complete();

                return createVideo(video);
            }
            catch (CompletionException e) {
                e.printStackTrace();

                try {
                    throw e.getCause();
                }
                catch (VideoInfoException ex) {
                    emitter.completeWithError(ex);
                }
                catch (VideoDownloadException ex) {
                    emitter.completeWithError(ex);
                }
                catch (AudioDownloadException ex) {
                    emitter.completeWithError(ex);
                }
                catch (InternalServerException ex) {
                    emitter.completeWithError(ex);
                }
                catch (Throwable ex) {
                    emitter.completeWithError( 
                        new InternalServerException(ex)
                    );
                }

                return 0;
            }
            catch (CancellationException e) {
                e.printStackTrace();

                String message = "ERROR: %s download process was cancelled.";
                String url = params.url();
                Integer res = params.res();

                switch ( params.type() ) {

                    case Video: {
                        var ex = new VideoDownloadException(url, res);

                        ex.addError( message.formatted("Video") );
                        emitter.completeWithError(ex);

                        break;
                    }

                    case Audio: {
                        var ex = new AudioDownloadException(url);

                        ex.addError( message.formatted("Audio") );
                        emitter.completeWithError(ex);

                        break;
                    }
                }

                return 0;
            }
        });

        service.shutdown();

        return;
    }

    @Override
    public void testDownload(DownloadType type, DownloadRate rate, Integer res, SseEmitter emitter) {
        Tester tester = new Tester(directory, inputDirectory, rate);

        ShellOutput<Void, ? extends DownloaderException> futureDownload;

        futureDownload = switch (type) {
            case Video -> {
                yield tester.downloadVideo(res, (line, i) -> emitProgressData(emitter, line, i) );
            }
            case Audio -> {
                yield tester.downloadAudio( (line, i) -> emitProgressData(emitter, line, i) );
            }
        };

        ExecutorService service = Executors.newSingleThreadExecutor();

        service.submit(() -> {
            try {
                futureDownload.error.join();
                futureDownload.result.join();

                emitter.complete();
            }
            catch (CompletionException e) {
                e.printStackTrace();

                try {
                    throw e.getCause();
                }
                catch (VideoInfoException ex) {
                    emitter.completeWithError(ex);
                }
                catch (VideoDownloadException ex) {
                    emitter.completeWithError(ex);
                }
                catch (AudioDownloadException ex) {
                    emitter.completeWithError(ex);
                }
                catch (InternalServerException ex) {
                    emitter.completeWithError(ex);
                }
                catch (Throwable ex) {
                    emitter.completeWithError( 
                        new InternalServerException(ex)
                    );
                }
            }
            catch (CancellationException e) {
                e.printStackTrace();

                String message = "ERROR: Test %s download process was cancelled.";
                String url = "http://localhost:8000/test-video.webm";

                switch (type) {

                    case Video: {
                        var ex = new VideoDownloadException(url, res);

                        ex.addError( message.formatted("video") );
                        emitter.completeWithError(ex);

                        break;
                    }

                    case Audio: {
                        var ex = new AudioDownloadException(url);

                        ex.addError( message.formatted("audio") );
                        emitter.completeWithError(ex);

                        break;
                    }
                }
            }
        });
    }

    @Override
    public void emitProgressData(SseEmitter emitter, String line, Integer lineIndex) {

        if ( line.isBlank() ) {
            String json = "{ \"line\": %d }".formatted(lineIndex);

            try {
                emitter.send(
                    SseEmitter.event()
                    .name("completed")
                    .data(
                        json,
                        MediaType.APPLICATION_JSON
                    )
                );

            } catch (IOException e) {
                e.printStackTrace();
            }

            return;
        }

        ObjectMapper mapper = new ObjectMapper();
        ProgressJson json;

        try {
            json = mapper.readValue(line, ProgressJson.class);

        } catch (JsonProcessingException e) {
            e.printStackTrace();

            return;
        }

        try {
            if ( json.isFinished() ) {

                emitter.send(
                    SseEmitter.event()
                    .name("finished")
                    .data(mapper.writeValueAsString(json), MediaType.APPLICATION_JSON)
                );

                return;
            }

            emitter.send(
                SseEmitter.event()
                .name("progress")
                .data(mapper.writeValueAsString(json), MediaType.APPLICATION_JSON)
            );

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
