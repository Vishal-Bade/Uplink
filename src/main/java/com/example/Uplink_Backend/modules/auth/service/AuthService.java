package com.example.Uplink_Backend.modules.auth.service;

import com.example.Uplink_Backend.common.exception.BadRequestException;
import com.example.Uplink_Backend.common.exception.ResourceNotFoundException;
import com.example.Uplink_Backend.common.security.JwtTokenProvider;
import com.example.Uplink_Backend.common.security.UserPrincipal;
import com.example.Uplink_Backend.common.service.EmailService;
import com.example.Uplink_Backend.modules.auth.dto.*;
import com.example.Uplink_Backend.modules.auth.entity.Role;
import com.example.Uplink_Backend.modules.auth.entity.User;
import com.example.Uplink_Backend.modules.auth.repository.RoleRepository;
import com.example.Uplink_Backend.modules.auth.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final EmailService emailService;

    public AuthService(UserRepository userRepository,
                       RoleRepository roleRepository,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager,
                       JwtTokenProvider tokenProvider,
                       EmailService emailService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
        this.emailService = emailService;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email is already registered: " + request.getEmail());
        }
        if (request.getPhone() != null && !request.getPhone().isBlank() && userRepository.existsByPhone(request.getPhone())) {
            throw new BadRequestException("Phone number is already in use: " + request.getPhone());
        }

        User user = new User();
        user.setEmail(request.getEmail().toLowerCase().trim());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName().trim());
        user.setPhone(request.getPhone());
        user.setIsActive(true);
        user.setIsVerified(false);

        // Assign role
        String roleName = "ROLE_STUDENT";
        if (request.getRole() != null && !request.getRole().isBlank()) {
            String requested = request.getRole().toUpperCase().trim();
            if (!requested.startsWith("ROLE_")) {
                requested = "ROLE_" + requested;
            }
            if (roleRepository.existsByName(requested)) {
                roleName = requested;
            }
        }

        final String targetRoleName = roleName;
        Role userRole = roleRepository.findByName(targetRoleName)
                .orElseGet(() -> roleRepository.save(new Role(targetRoleName, "Default role for " + targetRoleName)));

        Set<Role> roles = new HashSet<>();
        roles.add(userRole);
        user.setRoles(roles);

        User savedUser = userRepository.save(user);

        // Authenticate automatically after registration
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = tokenProvider.generateToken(authentication);
        String refreshToken = tokenProvider.generateRefreshToken(savedUser.getId());

        // Send welcome & verification email
        emailService.sendVerificationEmail(savedUser.getEmail(), savedUser.getId().toString());

        List<String> roleNames = savedUser.getRoles().stream().map(Role::getName).collect(Collectors.toList());

        return new AuthResponse(jwt, refreshToken, savedUser.getId(), savedUser.getEmail(), savedUser.getFullName(), roleNames);
    }

    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail().toLowerCase().trim(), request.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        String jwt = tokenProvider.generateToken(authentication);
        String refreshToken = tokenProvider.generateRefreshToken(userPrincipal.getId());

        List<String> roleNames = userPrincipal.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return new AuthResponse(jwt, refreshToken, userPrincipal.getId(), userPrincipal.getUsername(), userPrincipal.getFullName(), roleNames);
    }

    public AuthResponse refreshToken(RefreshTokenRequest request) {
        if (!tokenProvider.validateToken(request.getRefreshToken())) {
            throw new BadRequestException("Invalid or expired refresh token");
        }

        UUID userId = tokenProvider.getUserIdFromJWT(request.getRefreshToken());
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        UserPrincipal userPrincipal = UserPrincipal.create(user);
        Authentication auth = new UsernamePasswordAuthenticationToken(userPrincipal, null, userPrincipal.getAuthorities());
        String newJwt = tokenProvider.generateToken(auth);
        String newRefreshToken = tokenProvider.generateRefreshToken(user.getId());

        List<String> roleNames = user.getRoles().stream().map(Role::getName).collect(Collectors.toList());

        return new AuthResponse(newJwt, newRefreshToken, user.getId(), user.getEmail(), user.getFullName(), roleNames);
    }

    @Transactional
    public void requestPasswordReset(String email) {
        User user = userRepository.findByEmail(email.toLowerCase().trim())
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));

        String token = UUID.randomUUID().toString();
        emailService.sendPasswordResetEmail(user.getEmail(), token);
    }

    @Transactional
    public void resetPassword(PasswordResetRequest request) {
        User user = userRepository.findByEmail(request.getEmail().toLowerCase().trim())
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", request.getEmail()));

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setUpdatedAt(Instant.now());
        userRepository.save(user);
    }

    @Transactional
    public void verifyEmail(VerifyEmailRequest request) {
        User user = userRepository.findByEmail(request.getEmail().toLowerCase().trim())
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", request.getEmail()));

        user.setIsVerified(true);
        user.setUpdatedAt(Instant.now());
        userRepository.save(user);
    }

    public User getCurrentUser(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
    }
}
