package com.bmcho.hightrafficboard.service;

import com.bmcho.hightrafficboard.config.security.BoardUser;
import com.bmcho.hightrafficboard.controller.user.dto.CreateUserRequest;
import com.bmcho.hightrafficboard.domain.UserDomain;
import com.bmcho.hightrafficboard.entity.UserEntity;
import com.bmcho.hightrafficboard.exception.UserException;
import com.bmcho.hightrafficboard.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserEntity getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        BoardUser boardUser = (BoardUser) authentication.getPrincipal();
        return userRepository.findById(boardUser.getId())
            .orElseThrow(UserException.UserDoesNotExistException::new);
    }

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
    public void updateUser(long id, String username) {
        UserEntity userEntity = userRepository.findById(id)
            .orElseThrow(UserException.UserDoesNotExistException::new);
        userEntity.setUsername(username);
    }

    public UserDomain getUserById(long id) {
        return UserDomain.fromEntity(
            userRepository.findById(id)
                .orElseThrow(UserException.UserDoesNotExistException::new));
    }

    public UserDomain getUserByEmail(String email) {
        return UserDomain.fromEntity(
            userRepository.findByEmail(email)
                .orElseThrow(UserException.UserDoesNotExistException::new));
    }

    @Transactional
    public void deleteUser(long id) {
        userRepository.deleteById(id);
    }

}
