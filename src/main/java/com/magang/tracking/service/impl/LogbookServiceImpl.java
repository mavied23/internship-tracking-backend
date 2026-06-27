package com.magang.tracking.service.impl;

import com.magang.tracking.dto.request.LogbookRequest;
import com.magang.tracking.dto.request.VerifikasiRequest;
import com.magang.tracking.dto.response.ApiResponse;
import com.magang.tracking.service.LogbookService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.magang.tracking.entity.Logbook;
import com.magang.tracking.entity.Mahasiswa;
import com.magang.tracking.entity.User;
import com.magang.tracking.exception.ResourceNotFoundException;
import com.magang.tracking.repository.LogbookRepository;
import com.magang.tracking.repository.MahasiswaRepository;
import com.magang.tracking.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LogbookServiceImpl implements LogbookService {

    private final LogbookRepository logbookRepository;
    private final UserRepository userRepository;
    private final MahasiswaRepository mahasiswaRepository;

    private static final String UPLOAD_DIR = "uploads/logbook/";

    @Override
    public ApiResponse<?> inputLogbook(LogbookRequest request, MultipartFile dokumentasi) {
        //1. Ambil username dari token JWT
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username).orElseThrow(() -> new ResourceNotFoundException("User Tidak Ditemukan"));
        Mahasiswa mahasiswa = mahasiswaRepository.findByUserId(user.getId()).orElseThrow(() -> new ResourceNotFoundException("Profil Mahasiswa Belum Dilengkapi"));

        //2. Simpan file dokumentasi kalau ada
        String pathDokumentasi = null;
        if (dokumentasi != null && !dokumentasi.isEmpty()){
            pathDokumentasi = simpanFile(dokumentasi);
        } 

        //3. Buat objek Logbook dan simpan ke DB
        Logbook logbook = new Logbook();
        logbook.setMahasiswa(mahasiswa);
        logbook.setTanggal(request.getTanggal());
        logbook.setJamKerja(request.getJamKerja());
        logbook.setAktivitas(request.getAktivitas());
        logbook.setDokumentasi(pathDokumentasi);
        logbook.setStatus("MENUNGGU_VERIFIKASI");

        logbookRepository.save(logbook);
        
        return new ApiResponse<>(true, "Logbook harian berhasil disimpan, menunggu verifikasi dosen", logbook);
    }

    @Override
    public ApiResponse<?> verifikasiLogbook(Long logbookId, VerifikasiRequest request) {
        // 1. Cari logbook berdasarkan ID, error kalau tidak ada
        Logbook logbook = logbookRepository.findById(logbookId).orElseThrow(() -> new ResourceNotFoundException("Logbook dengan ID " + logbookId + " tidak ditemukan"));

        // 2. Validasi Status yang boleh dipakai
        String statusBaru = request.getStatus().toUpperCase();
        if (!statusBaru.equals("DISETUJUI") && !statusBaru.equals("REVISI")){
            return new ApiResponse<>(false, "Status tidak valid. Gunakan DISETUJUI atau REVISI", null);
        }

        // 3. Update status dan catatan dosen, lalu simpan
        logbook.setStatus(statusBaru);
        logbook.setCatatanDosen(request.getCatatan());
        logbookRepository.save(logbook);
        
        return new ApiResponse<>(true, "Logbook berhasil diverifikasi dengan status: " + statusBaru, logbook);
    }

    @Override
    public ApiResponse<?> getLogbookMahasiswa() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User tidak ditemukan"));
        Mahasiswa mahasiswa = mahasiswaRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Profil mahasiswa belum dilengkapi"));

        List<Logbook> list = logbookRepository.findByMahasiswaId(mahasiswa.getId());
        return new ApiResponse<>(true, "Daftar logbook berhasil diambil.", list);
    }

    @Override
    public ApiResponse<?> getLogbookByMahasiswaId(Long mahasiswaId) {
        List<Logbook> list = logbookRepository.findByMahasiswaId(mahasiswaId);
        return new ApiResponse<>(true, "Daftar logbook mahasiswa berhasil diambil.", list);
    }

    // Helper: simpan file ke local storage
    private String simpanFile(MultipartFile file) {
        try {
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename().replace(" ", "_");
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            return fileName;
        } catch (IOException e) {
            throw new RuntimeException("Gagal menyimpan file dokumentasi: " + e.getMessage());
        }
    }
}