package com.magang.tracking.service;

import com.magang.tracking.dto.request.*;
import com.magang.tracking.dto.response.*;
import org.springframework.web.multipart.MultipartFile;

public interface MagangService {
    ApiResponse<MagangStatusResponse> getStatusMagang();
    ApiResponse<?> getStatusMagangUserSaatIni(); // Pastikan return type-nya ApiResponse<?>
    ApiResponse<?> ajukanMagang(PengajuanMagangRequest request, MultipartFile file1, MultipartFile file2);
    ApiResponse<?> revisiPengajuan(Long id, PengajuanMagangRequest request, MultipartFile file1, MultipartFile file2);
    ApiResponse<?> getAllPengajuan(int page, int size);
    ApiResponse<?> verifikasiPengajuan(Long id, VerifikasiRequest request);
    ApiResponse<?> setPenempatanMagang(PenempatanRequest request);
}