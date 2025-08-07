package com.bmcho.hightrafficboard.service;

import com.bmcho.hightrafficboard.controller.user.dto.CreateUserRequest;
import com.bmcho.hightrafficboard.domain.UserDomain;
import com.bmcho.hightrafficboard.entity.UserEntity;
import com.bmcho.hightrafficboard.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public UserEntity createUser(CreateUserRequest userDto) {
        UserEntity user = new UserEntity(
            userDto.getUsername(),
            userDto.getPassword(),
            userDto.getEmail()
        );
        return userRepository.save(user);
    }

    @Transactional
    public UserEntity updateUser(long id, String username, String email) {
        return userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public UserDomain getUserById(long id) {
        return UserDomain.fromEntity(
            userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("임시 처방")));
    }

    public UserDomain getUserByEmail(String email) {
        return UserDomain.fromEntity(
            userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("임시 처방")));
    }

    @Transactional
    public void deleteUser(long id) {
        userRepository.deleteById(id);
    }

}
