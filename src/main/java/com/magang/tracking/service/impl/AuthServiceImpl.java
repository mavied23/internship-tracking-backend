package com.magang.tracking.service.impl;

import com.magang.tracking.dto.request.LoginRequest;
import com.magang.tracking.dto.response.ApiResponse;
import com.magang.tracking.dto.response.JwtResponse;
// UserRepository dihapus karena tidak digunakan di method login/logout
import com.magang.tracking.security.jwt.JwtUtils;
import com.magang.tracking.security.services.UserDetailsImpl;
import com.magang.tracking.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    @Override
    public ApiResponse<JwtResponse> login(LoginRequest request) {
        // 1. Otentikasi
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

        // 2. Simpan ke Security Context
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 3. Generate JWT
        String jwt = jwtUtils.generateJwtToken(authentication);

        // 4. Ambil detail user
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        
        // Mengambil role dengan pengecekan aman jika user tidak punya role (opsional)
        String role = userDetails.getAuthorities().isEmpty() ? "ROLE_USER" 
                    : userDetails.getAuthorities().iterator().next().getAuthority();

        // 5. Return response
        JwtResponse response = new JwtResponse(jwt, userDetails.getId(), userDetails.getUsername(), role);
        return new ApiResponse<>(true, "Login Berhasil", response);
    }

    @Override
    public ApiResponse<?> logout() {
        SecurityContextHolder.clearContext();
        return new ApiResponse<>(true, "Logout Berhasil");
    }
}