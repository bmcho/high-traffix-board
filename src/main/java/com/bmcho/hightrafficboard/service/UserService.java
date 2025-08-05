package com.bmcho.hightrafficboard.service;

import com.bmcho.hightrafficboard.dto.CreateUserDto;
import com.bmcho.hightrafficboard.entity.UserEntity;
import com.bmcho.hightrafficboard.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public UserEntity createUser(CreateUserDto userDto) {
        UserEntity user = new UserEntity(userDto.getUsername(), userDto.getPassword(), userDto.getEmail());
        return userRepository.save(user);
    }

    @Transactional
    public UserEntity updateUser(Long id, String username, String email) {
        UserEntity user = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found"));
        return user;
    }

    public Optional<UserEntity> getUser(Long id) {
        return userRepository.findById(id);
    }

    @Transactional
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

}
