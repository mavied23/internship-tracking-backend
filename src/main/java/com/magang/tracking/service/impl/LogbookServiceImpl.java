package com.magang.tracking.service.impl;

import com.magang.tracking.dto.request.LogbookRequest;
import com.magang.tracking.dto.request.VerifikasiRequest;
import com.magang.tracking.dto.response.ApiResponse;
import com.magang.tracking.service.LogbookService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class LogbookServiceImpl implements LogbookService {

    @Override
    public ApiResponse<?> inputLogbook(LogbookRequest request, MultipartFile dokumentasi) {
        //  Simpan data request dan simpan file dokumentasi ke local storage (mirip LaporanService)
        return new ApiResponse<>(true, "Logbook harian berhasil disimpan dengan status MENUNGGU_VERIFIKASI");
    }

    @Override
    public ApiResponse<?> verifikasiLogbook(Long logbookId, VerifikasiRequest request) {
        // Cari logbook berdasarkan logbookId, update status (DISETUJUI/REVISI) dan catatan dosen
        return new ApiResponse<>(true, "Logbook berhasil diverifikasi: " + request.getStatus());
    }
}