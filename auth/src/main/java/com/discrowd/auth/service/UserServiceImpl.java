package com.discrowd.auth.service;

import com.discrowd.auth.model.User;
import com.discrowd.auth.model.dto.UserData;
import com.discrowd.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;
    private final UploadServiceClient uploadServiceClient;
    private final ServerServiceClient serverServiceClient;

    @Override
    public void changePassword(String oldPassword, String newPassword) {

    }

    @Override
    public void changeEmail(String newEmail) {

    }

    @Override
    public void changeUsername(String newUsername) {

    }

    @Override
    public void deleteAccount() {

    }

    @Override
    public UserData getUserData(Long id) {
        User user =userRepository.findById(id).orElseThrow();
        return new UserData(user.getNickname(), user.getProfileImageUrl());
    }

    @Override
    public UserData updateUserProfile(MultipartFile file, Long userId) {
        User user =userRepository.findById(userId).orElseThrow();
        String newUrl = uploadServiceClient.updateProfileImage(file);
        user.setProfileImageUrl(newUrl);
        serverServiceClient.updateMembershipProfileImage(userId, newUrl);
        userRepository.save(user);
        return new UserData(user.getNickname(), newUrl);
    }
}
