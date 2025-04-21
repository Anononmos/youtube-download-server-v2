package com.example.YouTubeDL.YouTubeURLValidation;

import jakarta.validation.Payload;

public final class DownloadValidationErrorType {
    public static interface MissingURL extends Payload {};
    public static interface InvalidURL extends Payload {};
    public static interface InvalidResolution extends Payload {};
}
