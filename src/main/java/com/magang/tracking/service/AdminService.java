package com.magang.tracking.service;

import com.magang.tracking.dto.response.ApiResponse;

public interface AdminService {
    ApiResponse<?> getAllMahasiswa();
    ApiResponse<?> getDetailMahasiswa(Long mahasiswaId);
    ApiResponse<?> getAllDosen();
    ApiResponse<?> getDashboardStats();
}