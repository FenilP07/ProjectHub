package com.fenil.projecthub.auth.service;

import com.fenil.projecthub.auth.dto.RegisterRequest;
import com.fenil.projecthub.auth.dto.RegisterResponse;
import com.fenil.projecthub.auth.exception.EmailAlreadyExistsException;
import com.fenil.projecthub.user.domain.User;
import com.fenil.projecthub.user.domain.UserRole;
import com.fenil.projecthub.user.domain.UserStatus;
import com.fenil.projecthub.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class RegistrationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public RegistrationService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder

    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public RegisterResponse register(RegisterRequest request) {


        String normalizedEmail = normalizeEmail(request.email());

        if (userRepository.existsByEmailIgnoreCase(normalizedEmail)) {
            throw new EmailAlreadyExistsException();
        }

        String passwordHash = passwordEncoder.encode(request.password());

        User user = new User(
                normalizedEmail,
                passwordHash,
                normalizeName(request.firstName()),
                normalizeName(request.lastName()),
                UserRole.USER,
                UserStatus.ACTIVE
        );

        try {
            User savedUser = userRepository.saveAndFlush(user);

            return new RegisterResponse(
                    savedUser.getId(),
                    savedUser.getEmail(),
                    savedUser.getFirstName(),
                    savedUser.getLastName(),
                    savedUser.getCreatedAt()
            );
        } catch (DataIntegrityViolationException exception) {
            throw new EmailAlreadyExistsException();
        }
    }

    private String normalizeEmail(String email) {
        return email.trim();
    }

    private String normalizeName(String name) {
        return name.trim();
    }
}
