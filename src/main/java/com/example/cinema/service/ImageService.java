package com.example.cinema.service;

import com.example.cinema.persistence.ImageRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@AllArgsConstructor
@Service
public class ImageService {
    private final ImageRepository imageRepository;

    public byte[] getPosterImage(String identifier) {
        log.debug("get poster by identifier: {}", identifier);

        return imageRepository.loadPosterImage(identifier);
    }

    public byte[] getPreviewImage(String identifier) {
        log.debug("get preview by identifier: {}", identifier);

        return imageRepository.loadPreviewImage(identifier);
    }

    public String savePosterImage(MultipartFile posterImage) {
        log.debug("save poster image: {}", posterImage);

        if (posterImage == null) {
            log.error("Poster image is null");
            throw new IllegalStateException("Poster images cannot be equal null");
        }

        return imageRepository.savePosterImage(posterImage);
    }

    public String savePreviewImage(MultipartFile previewImage) {
        log.debug("save preview image: {}", previewImage);

        if (previewImage == null) {
            log.error("Preview image is null");
            throw new IllegalStateException("Preview images cannot be equal null");
        }

        return imageRepository.savePreviewImage(previewImage);
    }
}
