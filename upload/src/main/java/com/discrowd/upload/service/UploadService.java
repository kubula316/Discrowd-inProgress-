package com.discrowd.upload.service;

import org.springframework.web.multipart.MultipartFile;

public interface UploadService {
    String UploadMessageImage(MultipartFile file);

    String uploadProfileImage(MultipartFile file);
}
