package com.oskarott.webshoptemplatebackend.service;

import com.oskarott.webshoptemplatebackend.dto.LoginRequest;
import com.oskarott.webshoptemplatebackend.dto.RegisterRequest;
import com.oskarott.webshoptemplatebackend.dto.TokenResponse;
import com.oskarott.webshoptemplatebackend.dto.UserDto;
import com.oskarott.webshoptemplatebackend.exception.ConflictException;
import com.oskarott.webshoptemplatebackend.exception.NotFoundException;
import com.oskarott.webshoptemplatebackend.model.Role;
import com.oskarott.webshoptemplatebackend.model.UserEntity;
import com.oskarott.webshoptemplatebackend.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService refreshTokenService;
    private final EmailService emailService;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService,
                       AuthenticationManager authenticationManager,
                       RefreshTokenService refreshTokenService,
                       EmailService emailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.refreshTokenService = refreshTokenService;
        this.emailService = emailService;
    }

    public TokenResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new ConflictException("Email already registered");
        }

        UserEntity user = new UserEntity();
        user.setEmail(request.email());
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole(Role.USER);

        userRepository.save(user);

        emailService.sendWelcomeEmail(user);

        String accessToken = jwtService.generateToken(user);
        String refreshToken = refreshTokenService.createRefreshToken(user);
        return TokenResponse.bearer(accessToken, refreshToken, jwtService.getExpirationMs());
    }

    public UserDto me(String email) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found"));
        return new UserDto(user.getFirstName(), user.getLastName(), user.getEmail(), user.getPhone(), user.getRole().name());
    }

    public TokenResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        UserEntity user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new NotFoundException("User not found"));

        String accessToken = jwtService.generateToken(user);
        String refreshToken = refreshTokenService.createRefreshToken(user);
        return TokenResponse.bearer(accessToken, refreshToken, jwtService.getExpirationMs());
    }

    public TokenResponse refresh(String rawRefreshToken) {
        RefreshTokenService.RotateResult result = refreshTokenService.rotate(rawRefreshToken);
        String newAccessToken = jwtService.generateToken(result.user());
        return TokenResponse.bearer(newAccessToken, result.newRawToken(), jwtService.getExpirationMs());
    }

    public void logout(String rawRefreshToken) {
        refreshTokenService.revoke(rawRefreshToken);
    }
}
