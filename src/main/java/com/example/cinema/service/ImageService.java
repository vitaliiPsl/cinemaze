package com.example.cinema.service;

import com.example.cinema.persistence.ImageRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashSet;
import java.util.Set;

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

    public String savePosterImage(MultipartFile posterImage){
        log.debug("save poster image: {}", posterImage.getOriginalFilename());

        return imageRepository.savePosterImage(posterImage);
    }

    public String savePreviewImage(MultipartFile previewImage){
        log.debug("save preview image: {}", previewImage.getOriginalFilename());

        return imageRepository.savePreviewImage(previewImage);
    }

    public Set<String> savePreviewImages(MultipartFile[] previewImages){
        Set<String> previewsIdentifiers = new HashSet<>();
        if (previewImages == null || previewImages.length == 0) {
            return previewsIdentifiers;
        }

        for (MultipartFile previewImage : previewImages) {
            String preview = savePreviewImage(previewImage);
            previewsIdentifiers.add(preview);
        }

        return previewsIdentifiers;
    }
}
