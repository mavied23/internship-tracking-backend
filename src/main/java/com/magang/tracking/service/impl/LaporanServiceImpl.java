package com.magang.tracking.service.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.magang.tracking.dto.response.ApiResponse;
import com.magang.tracking.entity.LaporanAkhir;
import com.magang.tracking.entity.Mahasiswa;
import com.magang.tracking.entity.User;
import com.magang.tracking.exception.ResourceNotFoundException;
import com.magang.tracking.repository.LaporanAkhirRepository;
import com.magang.tracking.repository.MahasiswaRepository;
import com.magang.tracking.repository.UserRepository;
import com.magang.tracking.service.LaporanService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LaporanServiceImpl implements LaporanService {

    private final LaporanAkhirRepository laporanAkhirRepository;
    private final UserRepository userRepository;
    private final MahasiswaRepository mahasiswaRepository;

    private static final String UPLOAD_DIR = "uploads/laporan/";
    private static final long MAX_SIZE = 5 * 1024 * 1024; // 5MB

    @Override
    public ApiResponse<?> uploadLaporan(MultipartFile fileLaporan) {
        // 1. Validasi file
        if (fileLaporan == null || fileLaporan.isEmpty()) {
            return new ApiResponse<>(false, "File tidak boleh kosong.", null);
        }
        if (fileLaporan.getSize() > MAX_SIZE) {
            return new ApiResponse<>(false, "Ukuran file maksimal 5 MB.", null);
        }
        String originalName = fileLaporan.getOriginalFilename();
        if (originalName == null || !originalName.toLowerCase().endsWith(".pdf")) {
            return new ApiResponse<>(false, "Format file harus PDF.", null);
        }

        // 2. Ambil mahasiswa yang sedang login
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User tidak ditemukan"));
        Mahasiswa mahasiswa = mahasiswaRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Profil mahasiswa belum dilengkapi"));

        // 3. Simpan file ke local storage
        String namaFile = System.currentTimeMillis() + "_" + originalName.replace(" ", "_");
        try {
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            Path filePath = uploadPath.resolve(namaFile);
            Files.copy(fileLaporan.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Gagal menyimpan file laporan: " + e.getMessage());
        }

        // 4. Simpan ke database (replace kalau sudah pernah upload sebelumnya)
        LaporanAkhir laporan = laporanAkhirRepository.findByMahasiswaId(mahasiswa.getId())
                .orElse(new LaporanAkhir());
        laporan.setMahasiswa(mahasiswa);
        laporan.setFileLaporan(namaFile);
        laporanAkhirRepository.save(laporan);

        return new ApiResponse<>(true, "Laporan akhir berhasil diunggah.", laporan);
    }

    @Override
    public ApiResponse<?> getLaporanMahasiswa() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User tidak ditemukan"));
        Mahasiswa mahasiswa = mahasiswaRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Profil mahasiswa belum dilengkapi"));

        LaporanAkhir laporan = laporanAkhirRepository.findByMahasiswaId(mahasiswa.getId())
                .orElse(null);
        if (laporan == null) {
            return new ApiResponse<>(false, "Laporan akhir belum diunggah.", null);
        }
        return new ApiResponse<>(true, "Laporan akhir ditemukan.", laporan);
    }
}