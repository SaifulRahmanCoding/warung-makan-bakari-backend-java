package com.enigma.wmb_api.service.impl;

import com.enigma.wmb_api.constant.ResponseMessage;
import com.enigma.wmb_api.entity.Image;
import com.enigma.wmb_api.repository.ImageRepository;
import com.enigma.wmb_api.service.ImageService;
import jakarta.annotation.PostConstruct;
import jakarta.validation.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
public class ImageServiceImpl implements ImageService {
    private final Path directoryPath;
    private final ImageRepository imageRepository;

    @Autowired
    public ImageServiceImpl(@Value("${wmb.multipart.path.location}") String directoryPath, ImageRepository imageRepository) {
        this.directoryPath = Paths.get(directoryPath);
        this.imageRepository = imageRepository;
    }

    @PostConstruct
    public void initDirectory() {
        if (!Files.exists(directoryPath)) {
            try {
                Files.createDirectory(directoryPath);
            } catch (IOException e) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
            }
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Image create(MultipartFile file) {
        try {
            if (!List.of("image/jpeg", "image/png", "image/jpg", "image/svg+xml").contains(file.getContentType()))
                throw new ConstraintViolationException(ResponseMessage.INVALID_IMAGE_TYPE, null);
            String uniqueFilename = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path filePath = directoryPath.resolve(uniqueFilename);
            // method getInputStream perlu di try catch
            Files.copy(file.getInputStream(), filePath);
            Image image = Image.builder()
                    .name(uniqueFilename)
                    .contentType(file.getContentType())
                    .size(file.getSize())
                    .path(filePath.toString())
                    .build();
            imageRepository.saveAndFlush(image);
            return image;
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    @Override
    public Resource getById(String id) {
        try {
            Image image = imageRepository.findById(id)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, ResponseMessage.ERROR_NOT_FOUND));
            Path filePath = Paths.get(image.getPath());
            if (!Files.exists(filePath))
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, ResponseMessage.ERROR_NOT_FOUND);
            return new UrlResource(filePath.toUri());
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(String id) {
        try {
            Image image = imageRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, ResponseMessage.ERROR_NOT_FOUND));
            Path filePath = Paths.get(image.getPath());
            if (!Files.exists(filePath))
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, ResponseMessage.ERROR_NOT_FOUND);
            Files.delete(filePath);
            imageRepository.delete(image);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}
