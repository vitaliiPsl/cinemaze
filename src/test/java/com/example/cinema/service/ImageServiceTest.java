package com.example.cinema.service;

import com.example.cinema.persistence.ImageRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ImageServiceTest {

    @Mock
    ImageRepository imageRepository;

    @InjectMocks
    ImageService imageService;

    @Test
    void testGetPosterImage() {
        // given
        String identifier = "test";
        byte[] image = {};

        // when
        when(imageRepository.loadPosterImage(identifier)).thenReturn(image);
        byte[] result = imageService.getPosterImage(identifier);

        // then
        verify(imageRepository).loadPosterImage(identifier);
        assertThat(result, equalTo(image));
    }

    @Test
    void testGetPreviewImage() {
        // given
        String identifier = "test";
        byte[] image = {};

        // when
        when(imageRepository.loadPreviewImage(identifier)).thenReturn(image);
        byte[] result = imageService.getPreviewImage(identifier);

        // then
        verify(imageRepository).loadPreviewImage(identifier);
        assertThat(result, equalTo(image));
    }

    @Test
    void testSavePosterImage() {
        // given
        MultipartFile poster = new MockMultipartFile("poster", new byte[]{});
        String posterIdentifier = "test";

        // when
        when(imageRepository.savePosterImage(poster)).thenReturn(posterIdentifier);
        String result = imageService.savePosterImage(poster);

        // then
        verify(imageRepository).savePosterImage(poster);
        assertThat(result, equalTo(posterIdentifier));
    }


    @Test
    void testSavePosterImageThrowsExceptionWhenPosterEqualToNull() {
        // given
        MultipartFile poster = null;

        // then
        assertThrows(IllegalStateException.class, () -> imageService.savePosterImage(poster));
    }

    @Test
    void savePreviewImage() {
        // given
        MultipartFile preview = new MockMultipartFile("preview", new byte[]{});
        String previewIdentifier = "test";

        // when
        when(imageRepository.savePreviewImage(preview)).thenReturn(previewIdentifier);
        String result = imageService.savePreviewImage(preview);

        // then
        verify(imageRepository).savePreviewImage(preview);
        assertThat(result, equalTo(previewIdentifier));
    }

    @Test
    void testSavePreviewImageThrowsExceptionWhenPosterEqualToNull() {
        // given
        MultipartFile preview = null;

        // then
        assertThrows(IllegalStateException.class, () -> imageService.savePreviewImage(preview));
    }
}