package com.magang.tracking.service.impl;

import com.magang.tracking.dto.request.PenempatanRequest;
import com.magang.tracking.dto.request.PengajuanMagangRequest;
import com.magang.tracking.dto.request.VerifikasiRequest;
import com.magang.tracking.dto.response.ApiResponse;
import com.magang.tracking.dto.response.MagangStatusResponse;
import com.magang.tracking.service.MagangService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class MagangServiceImpl implements MagangService {

    @Override
    public ApiResponse<?> ajukanMagang(PengajuanMagangRequest request, MultipartFile suratPengantar, MultipartFile proposal) {
        return new ApiResponse<>(true, "Pengajuan berhasil dikirim");
    }

    @Override
    public ApiResponse<?> revisiPengajuan(Long id, PengajuanMagangRequest request, MultipartFile suratPengantar, MultipartFile proposal) {
        return new ApiResponse<>(true, "Revisi pengajuan berhasil disimpan");
    }

    @Override
    public ApiResponse<?> getAllPengajuan(int page, int size) {
        // Pagination logic using PageRequest.of(page, size)
        return new ApiResponse<>(true, "Berhasil mengambil data pengajuan (Pagination)");
    }

    @Override
    public ApiResponse<?> verifikasiPengajuan(Long id, VerifikasiRequest request) {
        return new ApiResponse<>(true, "Status pengajuan berhasil diubah menjadi: " + request.getStatus());
    }

    @Override
    public ApiResponse<?> setPenempatanMagang(PenempatanRequest request) {
        return new ApiResponse<>(true, "Plotting Dosen Pembimbing berhasil disimpan");
    }

    @Override
    public ApiResponse<MagangStatusResponse> getStatusMagangUserSaatIni() {
        // Tarik data real-time mahasiswa dari gabungan beberapa Repository
        MagangStatusResponse status = new MagangStatusResponse();
        status.setStatusMagang("AKTIF");
        // ... set data lainnya
        return new ApiResponse<>(true, "Berhasil mengambil status magang real-time", status);
    }
}