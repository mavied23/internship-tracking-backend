package com.magang.tracking.service;

import com.magang.tracking.dto.request.LoginRequest;
import com.magang.tracking.dto.response.ApiResponse;
import com.magang.tracking.dto.response.JwtResponse;

public interface AuthService {
    ApiResponse<JwtResponse> login(LoginRequest request);
    ApiResponse<?> logout();
}