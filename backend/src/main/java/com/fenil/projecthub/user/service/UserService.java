package com.fenil.projecthub.user.service;

import com.fenil.projecthub.user.domain.User;
import com.fenil.projecthub.user.dto.UserResponse;
import com.fenil.projecthub.user.exception.UserNotFoundException;
import com.fenil.projecthub.user.mapper.UserMapper;
import com.fenil.projecthub.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserService(
            UserRepository userRepository,
            UserMapper userMapper
    ) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }


    public UserResponse findById(UUID userId) {

        User user = userRepository.findById(userId).orElseThrow(
                () -> new UserNotFoundException(userId)
        );

        return userMapper.toResponse(user);
    }

}
