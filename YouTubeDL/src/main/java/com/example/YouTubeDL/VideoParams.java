package com.example.YouTubeDL;

import java.util.Objects;

public record VideoParams(DownloadType type, String url, Integer res) {
}
