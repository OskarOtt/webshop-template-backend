package com.oskarott.webshoptemplatebackend.resource;

import com.oskarott.webshoptemplatebackend.dto.ForgotPasswordRequest;
import com.oskarott.webshoptemplatebackend.dto.LoginRequest;
import com.oskarott.webshoptemplatebackend.dto.RefreshTokenRequest;
import com.oskarott.webshoptemplatebackend.dto.RegisterRequest;
import com.oskarott.webshoptemplatebackend.dto.ResetPasswordRequest;
import com.oskarott.webshoptemplatebackend.dto.TokenResponse;
import com.oskarott.webshoptemplatebackend.dto.UserDto;
import com.oskarott.webshoptemplatebackend.service.AuthService;
import com.oskarott.webshoptemplatebackend.service.PasswordResetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Auth", description = "Authentication endpoints")
@RestController
@RequestMapping("/auth")
public class AuthResource {

    private final AuthService authService;
    private final PasswordResetService passwordResetService;

    public AuthResource(AuthService authService, PasswordResetService passwordResetService) {
        this.authService = authService;
        this.passwordResetService = passwordResetService;
    }

    @Operation(summary = "Get current authenticated user", responses = {
            @ApiResponse(responseCode = "200", description = "User details",
                    content = @Content(schema = @Schema(implementation = UserDto.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/me")
    public ResponseEntity<UserDto> me(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(authService.me(userDetails.getUsername()));
    }

    @Operation(summary = "Register a new user", responses = {
            @ApiResponse(responseCode = "201", description = "User registered successfully",
                    content = @Content(schema = @Schema(implementation = TokenResponse.class))),
            @ApiResponse(responseCode = "409", description = "Email already taken", content = @Content),
            @ApiResponse(responseCode = "400", description = "Validation failed", content = @Content)
    })
    @PostMapping("/register")
    public ResponseEntity<TokenResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
    }

    @Operation(summary = "Log in with existing credentials", responses = {
            @ApiResponse(responseCode = "200", description = "Login successful",
                    content = @Content(schema = @Schema(implementation = TokenResponse.class))),
            @ApiResponse(responseCode = "401", description = "Invalid credentials", content = @Content),
            @ApiResponse(responseCode = "400", description = "Validation failed", content = @Content)
    })
    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @Operation(summary = "Refresh access token using a valid refresh token", responses = {
            @ApiResponse(responseCode = "200", description = "New token pair issued",
                    content = @Content(schema = @Schema(implementation = TokenResponse.class))),
            @ApiResponse(responseCode = "401", description = "Refresh token invalid, expired, or revoked", content = @Content)
    })
    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(authService.refresh(request.refreshToken()));
    }

    @Operation(summary = "Log out by revoking the refresh token", responses = {
            @ApiResponse(responseCode = "204", description = "Logged out successfully", content = @Content)
    })
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@Valid @RequestBody RefreshTokenRequest request) {
        authService.logout(request.refreshToken());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Request a password reset email", responses = {
            @ApiResponse(responseCode = "200", description = "If the email exists, a reset link has been sent", content = @Content)
    })
    @PostMapping("/forgot-password")
    public ResponseEntity<Void> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        passwordResetService.initiateReset(request.email());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Reset password using a token from the reset email", responses = {
            @ApiResponse(responseCode = "200", description = "Password reset successfully", content = @Content),
            @ApiResponse(responseCode = "401", description = "Token invalid, expired, or already used", content = @Content),
            @ApiResponse(responseCode = "400", description = "Validation failed", content = @Content)
    })
    @PostMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        passwordResetService.confirmReset(request.token(), request.newPassword());
        return ResponseEntity.ok().build();
    }
}
