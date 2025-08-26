package com.example.YouTubeDL.shells;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;
import com.example.YouTubeDL.downloadOptions.DownloadType;
import com.example.YouTubeDL.exceptions.InternalServerException;
import com.example.YouTubeDL.exceptions.FileExceptions.InvalidFileException;


public class FilePropertiesExtractor {

    private final Path path;

    @FunctionalInterface
    public interface LineHandler {
        void handle(String line, Integer index);
    }

    @FunctionalInterface
    public interface CompletionHandler<V> {
        V handle(String output);
    }

    public FilePropertiesExtractor(final String directory, final String filename) {
        this.path = Paths.get(directory, filename);
    }

    private void handleShellError(InputStream stderr) throws InvalidFileException {
        
        try (
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(stderr)
            )
        ) {
            String line; 

            while ( (line = reader.readLine()) != null ) {
                System.err.println(line);
            }
            
        } catch (IOException e) {
            e.printStackTrace();

            final String filename = path.getFileName().toString();

            throw new InternalServerException( "ERROR: Failed to read from stderr when accessing the resolution of file [%s]".formatted(filename) );
        }
    }

    private <V> V handleShellOutput(InputStream stdout, CompletionHandler<V> onComplete) {
        String lines = "";

        System.out.println( "Getting resolution of file [%s].".formatted( path.toString() ) );

        try (
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(stdout)
            )
        ) {
            String line;

            while ( (line = reader.readLine()) != null ) {
                System.out.println(line);

                lines += line + '\n';
            }

        } catch (IOException e) {
            e.printStackTrace();

            final String filename = path.getFileName().toString();

            throw new InternalServerException( "ERROR: Failed to read from stdout when accessing the resolution of file [%s].".formatted(filename) );
        }

        V value = onComplete.handle(lines);

        return value;
    }


    public Integer getResolution() throws FileNotFoundException, InvalidFileException {
        final String filename = path.getFileName().toString();

        File file = path.toFile();

        if ( !file.exists() || file.isDirectory() ) {
            throw new FileNotFoundException( "ERROR: File [%s] does not exist on the server.".formatted(filename) );
        }

        final String filePath = path.toString();

        String cmd[] = "ffprobe -v error -select_streams v:0 -show_entries stream=height -of default=nw=1:nk=1"
        .split(" ");

        List<String> commands = Arrays.asList(cmd);
        commands.add( "\"%s\"".formatted(filePath) );

        ProcessBuilder builder = new ProcessBuilder();
        builder.command(commands);

        try {
            Process process = builder.start();

            InputStream stdout = process.getInputStream();
            InputStream stderr = process.getErrorStream();

            int exitCode = process.waitFor();

            if (exitCode != 0) {
                handleShellError(stderr);

                throw new InvalidFileException( "ERROR: Failed to access the resolution of file [%s]".formatted(filename) );
            }

            Integer resolution = handleShellOutput(stdout, (output) -> { return Integer.parseInt(output); });

            return resolution;

        }
        catch (IOException e) {
            e.printStackTrace();

            throw new InternalServerException( "ERROR: Failed to start resolution retrieval process for file [%s]".formatted(filename) );
        }
        catch (InterruptedException e) {
            e.printStackTrace();

            throw new InternalServerException("ERROR: Resolution retrieval process for file [%s] was interrupted.");
        }
    }

    public LocalDateTime getCreationDate() throws InvalidFileException {

        try {
            FileTime fileTime = (FileTime) Files.getAttribute(path, "creationTime");

            return LocalDateTime.ofInstant(fileTime.toInstant(), ZoneId.systemDefault());
        }
        catch (IOException e) {
            e.printStackTrace();

            final String filename = path.getFileName().toString();

            throw new InvalidFileException("ERROR: Failed to access creation date of file [%s].".formatted(filename) );
        }
    }

    public String getMimeType() throws InternalServerException {
        try {
            String mimeType = Files.probeContentType(path);

            return mimeType;
        }
        catch (IOException e) {
            final String filename = path.getFileName().toString();

            throw new InternalServerException("ERROR: Unable to access Content-Type of file [%s].".formatted(filename));
        }
    }

    public DownloadType getDownloadType() throws InvalidFileException {
        final String mimeType = getMimeType();
        final String filename = path.getFileName().toString();
        
        if ( mimeType.startsWith("video") ) {
            return DownloadType.Video;
        }

        if ( mimeType.startsWith("audio") ) {
            return DownloadType.Audio;
        }

        throw new InvalidFileException("ERROR: Unsupported file type for file [%s]".formatted(filename));
    }
}
