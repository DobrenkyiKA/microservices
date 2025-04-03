package com.kdob.songservice.exception;

public class AlreadyExistSongMetadataException extends RuntimeException {
    public AlreadyExistSongMetadataException(String message) {
        super(message);
    }
}
