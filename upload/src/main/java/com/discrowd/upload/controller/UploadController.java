package com.discrowd.upload.controller;

import com.discrowd.upload.service.UploadService;
import com.discrowd.upload.storage.ImageStorageClient;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/upload")
@RequiredArgsConstructor
public class UploadController {

    private final UploadService uploadService;

    @PostMapping("/message-image")
    @ResponseStatus(HttpStatus.CREATED)
    public String updateImage(@RequestParam MultipartFile file) {
        return uploadService.UploadMessageImage(file);
    }
}
