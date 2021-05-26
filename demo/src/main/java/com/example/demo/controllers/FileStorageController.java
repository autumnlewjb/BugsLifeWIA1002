package com.example.demo.controllers;

import com.example.demo.models.File;
import com.example.demo.models.FileResponse;
import com.example.demo.services.FileStorageService;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class FileStorageController {

    private FileStorageService fileStorageService;

    @Autowired
    public FileStorageController(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    @GetMapping("/download/{file_id}")
    public ResponseEntity <Resource> downloadFile(@PathVariable Integer file_id) {
        File file = fileStorageService.getFile(file_id);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(file.getFileType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "filename=\"" + file.getFileName() + "\"")
                .body(new ByteArrayResource(file.getData()));
    }

    @PostMapping("/upload")
    public FileResponse uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            File fileFromDatabase = fileStorageService.storeFile(file);
            String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/api/download/")
                    .path(fileFromDatabase.getId().toString())
                    .toUriString();
            return new FileResponse(fileFromDatabase.getFileName(),fileDownloadUri,file.getContentType(), file.getSize());
        }catch (Exception e){
            return null;
        }
    }

    @PostMapping("/uploadMultiple")
    public List<FileResponse> uploadMultipleFiles(@RequestParam("files") MultipartFile[] files) {
        return Arrays.stream(files)
                .map(this::uploadFile)
                .collect(Collectors.toList());
    }
}
