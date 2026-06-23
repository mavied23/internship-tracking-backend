package com.magang.tracking.service;

import com.magang.tracking.dto.request.PenempatanRequest;
import com.magang.tracking.dto.request.PengajuanMagangRequest;
import com.magang.tracking.dto.request.VerifikasiRequest;
import com.magang.tracking.dto.response.ApiResponse;
import com.magang.tracking.dto.response.MagangStatusResponse;
import org.springframework.web.multipart.MultipartFile;

public interface MagangService {
    ApiResponse<?> ajukanMagang(PengajuanMagangRequest request, MultipartFile suratPengantar, MultipartFile proposal);
    ApiResponse<?> revisiPengajuan(Long id, PengajuanMagangRequest request, MultipartFile suratPengantar, MultipartFile proposal);
    ApiResponse<?> getAllPengajuan(int page, int size);
    ApiResponse<?> verifikasiPengajuan(Long id, VerifikasiRequest request);
    ApiResponse<?> setPenempatanMagang(PenempatanRequest request);
    ApiResponse<MagangStatusResponse> getStatusMagangUserSaatIni();
}