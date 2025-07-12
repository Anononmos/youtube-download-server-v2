package com.example.YouTubeDL.threads;

import java.util.concurrent.Callable;

import com.example.YouTubeDL.DownloadType;
import com.example.YouTubeDL.Shell;
import com.example.YouTubeDL.Shell.LineHandler;
import com.example.YouTubeDL.Shell.TestParams;

public class TestDownloadThread implements Callable<Boolean> {

    private final TestParams params;
    private final DownloadType type;
    private final Integer res;
    private final LineHandler handler;

    public TestDownloadThread(TestParams params, DownloadType type, Integer res, LineHandler handler) {
        this.params = params;
        this.type = type;
        this.res = res;
        this.handler = handler;
    }

    public Boolean call() {

        Boolean output = false;

        switch ( type ) {
            case Audio:
                output = Shell.testDownloadAudio(params, handler);

                break;
            
            case Video:
                output = Shell.testDownloadVideo(params, res, handler);

                break;
        }

        return output;
    } 
}