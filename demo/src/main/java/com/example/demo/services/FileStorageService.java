package com.example.demo.services;

import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.models.File;
import com.example.demo.repository.FileStorageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class FileStorageService {

    private FileStorageRepository fileStorageRepository;

    @Autowired
    public FileStorageService(FileStorageRepository fileStorageRepository) {
        this.fileStorageRepository = fileStorageRepository;
    }

    public File storeFile(MultipartFile file) throws IOException {
        if (file != null) {
            String fileName = StringUtils.cleanPath(file.getOriginalFilename());
            File newFile = new File(fileName, file.getContentType(), file.getBytes());
            return fileStorageRepository.save(newFile);
        }
        return null;
    }

    public File getFile(Integer fileId) {
        return fileStorageRepository.findById(fileId)
                .orElseThrow(() -> new ResourceNotFoundException("File", "id", fileId));
    }
}
