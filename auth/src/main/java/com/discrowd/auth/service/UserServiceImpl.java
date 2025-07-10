package com.discrowd.auth.service;

import com.discrowd.auth.model.User;
import com.discrowd.auth.model.dto.UserData;
import com.discrowd.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;

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
        return new UserData(user.getUsername(), user.getProfileImageUrl());
    }
}
