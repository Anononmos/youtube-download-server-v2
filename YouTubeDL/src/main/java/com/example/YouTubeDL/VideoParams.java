package com.example.YouTubeDL;

import com.example.YouTubeDL.downloadOptions.DownloadType;

public record VideoParams(DownloadType type, String url, Integer res) {}