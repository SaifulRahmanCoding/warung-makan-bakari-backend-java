package com.enigma.wmb_api.service.impl;

import com.enigma.wmb_api.entity.Image;
import com.enigma.wmb_api.repository.ImageRepository;
import com.enigma.wmb_api.service.ImageService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class ImageServiceImplTest {
    @TempDir
    private Path directoryPath;
    @Mock
    private ImageRepository imageRepository;
    private ImageService imageService;

    @BeforeEach
    void setUp() {
        imageService = new ImageServiceImpl(directoryPath.toString(), imageRepository);
    }

    @Test
    void shouldReturnImageWhenCreate() {
        // given
        byte[] content = "test content".getBytes(StandardCharsets.UTF_8);
        MockMultipartFile mockImage = new MockMultipartFile("image", "test.jpg", "image/jpg", content);
        Path filePath = directoryPath.resolve(mockImage.getName());

        // stubbing
        Image image = Image.builder()
                .name(System.currentTimeMillis() + "_" + mockImage.getOriginalFilename())
                .contentType(mockImage.getContentType())
                .path(filePath.toString())
                .build();
        // stubbing config
        when(imageRepository.saveAndFlush(Mockito.any())).thenReturn(image);

        // when
        Image actualImage = imageService.create(mockImage);

        // then
        assertNotNull(actualImage);
    }

    @Test
    void shouldThrowNotFoundWhenGetById() {
        // given
        String id = null;
        // stubbing config
        when(imageRepository.findById(id)).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));

        // when
        Assertions.assertThrows(ResponseStatusException.class, () -> {
            // When
            imageService.getById(id);
        });
    }
}