package com.discrowd.auth.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;

@FeignClient(name = "SERVER-SERVICE")
public interface ServerServiceClient {

    @PostMapping("/server/update/MembershipProfileImage")
    public void updateMembershipProfileImage(@RequestParam Long userId, @RequestParam String imageUrl);

}