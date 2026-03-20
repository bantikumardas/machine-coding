package com.example.MachineCoding.Service;

import com.example.MachineCoding.DTO.AuthResponse;
import com.example.MachineCoding.DTO.RegisterRequest;
import com.example.MachineCoding.Models.User;
import com.example.MachineCoding.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authManager;
    private final JwtService jwtService;

    public AuthResponse register(RegisterRequest req) {
        if (userRepository.findByEmail(req.getEmail()).isPresent()) {
            throw new IllegalArgumentException("User already exists with email: " + req.getEmail());
        }
        User user = new User();
        user.setEmail(req.getEmail());
        user.setUserName(req.getUserName());
        user.setHashPassword(passwordEncoder.encode(req.getPassword()));
        user.setGender(req.getGender());
        user.setPhoneNumber(req.getPhoneNumber());
        userRepository.save(user);
        String token = jwtService.generateToken(user);
        return new AuthResponse(token);
    }

    public AuthResponse login(String email, String password) {
        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
        );
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        String token = jwtService.generateToken(user);
        return new AuthResponse(token);
    }
}
