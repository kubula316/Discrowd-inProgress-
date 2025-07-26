package com.discrowd.auth.service;

import com.discrowd.auth.model.User;
import com.discrowd.auth.model.dto.UserData;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {
    public void changePassword(String oldPassword, String newPassword);
    public void changeEmail(String newEmail);
    public void changeUsername(String newUsername);
    public void deleteAccount();
    public UserData getUserData(Long id);

    UserData updateUserProfile(MultipartFile file, Long userId);
}
