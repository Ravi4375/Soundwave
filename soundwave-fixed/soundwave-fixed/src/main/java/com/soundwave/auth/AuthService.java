package com.soundwave.auth;

import com.soundwave.model.User;
import com.soundwave.repository.UserRepository;
import com.soundwave.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authManager;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public AuthDtos.AuthResponse register(AuthDtos.RegisterRequest req) {
        if (userRepository.existsByUsername(req.username()))
            throw new IllegalArgumentException("Username already taken");
        if (userRepository.existsByEmail(req.email()))
            throw new IllegalArgumentException("Email already in use");

        User user = User.builder()
                .username(req.username())
                .email(req.email())
                .password(passwordEncoder.encode(req.password()))
                .displayName(req.displayName() != null ? req.displayName() : req.username())
                .build();
        userRepository.save(user);

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
        String token = jwtService.generateToken(userDetails);
        return new AuthDtos.AuthResponse(token, user.getUsername(), user.getDisplayName(), user.getEmail(), user.getId());
    }

    public AuthDtos.AuthResponse login(AuthDtos.LoginRequest req) {
        authManager.authenticate(new UsernamePasswordAuthenticationToken(req.username(), req.password()));
        UserDetails userDetails = userDetailsService.loadUserByUsername(req.username());
        User user = userRepository.findByUsername(req.username()).orElseThrow();
        String token = jwtService.generateToken(userDetails);
        return new AuthDtos.AuthResponse(token, user.getUsername(), user.getDisplayName(), user.getEmail(), user.getId());
    }
}
