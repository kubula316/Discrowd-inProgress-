package com.discrowd.auth.service;

import com.discrowd.auth.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;

@FeignClient(name = "UPLOAD-SERVICE", configuration = FeignConfig.class)
public interface UploadServiceClient {

    @PostMapping(value = "/upload/profile-image", consumes = "multipart/form-data")
    @ResponseStatus(HttpStatus.CREATED)
    String updateProfileImage(@RequestPart("file") MultipartFile file);
}
