package com.kdob.songservice.exception;

public class NoSuchSongMetadataException extends RuntimeException {
    public NoSuchSongMetadataException(String message) {
        super(message);
    }
}
