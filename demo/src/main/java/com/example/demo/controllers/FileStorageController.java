package com.example.demo.controllers;

import com.example.demo.models.Attachment;
import com.example.demo.models.FileResponse;
import com.example.demo.services.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.Arrays;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class FileStorageController {

    private final FileStorageService fileStorageService;

    @Autowired
    public FileStorageController(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    /*@GetMapping("/download/{file_id}")
    public ResponseEntity<Resource> downloadFile(@PathVariable Integer file_id) {
        Attachment attachment = fileStorageService.getFile(file_id);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(attachment.getFileType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "filename=\"" + attachment.getFileName() + "\"")
                .body(new ByteArrayResource(attachment.getData()));
    }*/

    @PostMapping("/upload")
    public ResponseEntity<?> uploadMultipleFiles(@RequestParam("files") MultipartFile[] files,
                                                 @RequestParam("parent") String parent,
                                                 @RequestParam("id") String id) {
        return ResponseEntity.ok(
                Arrays.stream(files)
                        .map(file -> uploadFile(file, parent, id))
                        .collect(Collectors.toList())
        );
    }

    private FileResponse uploadFile(MultipartFile file, String parent, String id) {
        try {
            Attachment attachment = fileStorageService.storeFile(file, parent, id);
            String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/api/download/")
                    .path(attachment.getId().toString())
                    .toUriString();
            return new FileResponse(attachment.getId(), attachment.getFileName(), fileDownloadUri,
                    file.getContentType(), file.getSize());
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @DeleteMapping("/file/del")
    public ResponseEntity<?> deleteFile(@RequestParam("parent") String parent,
                                        @RequestParam("id") String id) {
        fileStorageService.deleteFile(parent, Integer.parseInt(id));
        return ResponseEntity.ok(HttpStatus.OK);
    }
}
