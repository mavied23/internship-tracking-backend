package com.magang.tracking.service;

import com.magang.tracking.dto.response.ApiResponse;
import org.springframework.web.multipart.MultipartFile;

public interface LaporanService {
    ApiResponse<?> uploadLaporan(MultipartFile fileLaporan);
}