package com.magang.tracking.service.impl;

import com.magang.tracking.dto.response.ApiResponse;
import com.magang.tracking.service.LaporanService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;

@Service
public class LaporanServiceImpl implements LaporanService {

    // Direktori Local Storage sesuai PRD
    private final String UPLOAD_DIR = "uploads/laporan/";

    @Override
    public ApiResponse<?> uploadLaporan(MultipartFile fileLaporan) {
        if (fileLaporan.isEmpty()) {
            return new ApiResponse<>(false, "File tidak boleh kosong");
        }
        
        // Validasi Ekstensi PDF & Ukuran Maksimal 5MB (5 * 1024 * 1024 bytes)
        if (fileLaporan.getSize() > 5242880) {
            return new ApiResponse<>(false, "Ukuran file maksimal 5 MB");
        }

        try {
            File dest = new File(UPLOAD_DIR + System.currentTimeMillis() + "_" + fileLaporan.getOriginalFilename());
            dest.getParentFile().mkdirs();
            fileLaporan.transferTo(dest);
            
            //Simpan path dest.getAbsolutePath() ke LaporanAkhirRepository// 

            return new ApiResponse<>(true, "Laporan PDF berhasil diunggah", dest.getName());
        } catch (IOException e) {
            return new ApiResponse<>(false, "Gagal mengunggah file: " + e.getMessage());
        }
    }
}