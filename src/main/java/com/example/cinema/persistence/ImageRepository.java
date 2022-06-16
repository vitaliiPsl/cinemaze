package com.example.cinema.persistence;

import org.springframework.web.multipart.MultipartFile;

public interface ImageRepository {
    // saves poster and returns generated image name/id
    String savePosterImage(MultipartFile file);
    String savePreviewImage(MultipartFile file);

    byte[] loadPosterImage(String identifier);
    byte[] loadPreviewImage(String identifier);
}
