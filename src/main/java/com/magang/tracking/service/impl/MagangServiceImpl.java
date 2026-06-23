package com.magang.tracking.service.impl;

import com.magang.tracking.dto.request.PengajuanMagangRequest;
import com.magang.tracking.dto.request.PenempatanRequest;
import com.magang.tracking.dto.request.VerifikasiRequest;
import com.magang.tracking.dto.response.ApiResponse;
import com.magang.tracking.dto.response.MagangStatusResponse;
import com.magang.tracking.entity.*;
import com.magang.tracking.repository.*;
import com.magang.tracking.service.MagangService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MagangServiceImpl implements MagangService {

    private final UserRepository userRepository;
    private final MahasiswaRepository mahasiswaRepository;
    private final PengajuanMagangRepository pengajuanMagangRepository;
    private final LogbookRepository logbookRepository;
    private final PenilaianRepository penilaianRepository;

    // --- FITUR UTAMA: STATUS MAGANG ---
    @Override
    public ApiResponse<MagangStatusResponse> getStatusMagang() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User tidak ditemukan"));

        Mahasiswa mahasiswa = mahasiswaRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Profil mahasiswa belum dilengkapi!"));

        Optional<PengajuanMagang> pengajuan = pengajuanMagangRepository
                .findFirstByMahasiswaIdOrderByTanggalPengajuanDesc(mahasiswa.getId());

        long countLogbook = logbookRepository.countByMahasiswaIdAndStatus(mahasiswa.getId(), "DISETUJUI");
        Optional<Penilaian> penilaian = penilaianRepository.findByMahasiswaId(mahasiswa.getId());

        MagangStatusResponse response = MagangStatusResponse.builder()
                .mahasiswaId(mahasiswa.getId())
                .namaMahasiswa(mahasiswa.getNama())
                .nim(mahasiswa.getNim())
                .statusPengajuan(pengajuan.map(PengajuanMagang::getStatus).orElse("BELUM_MENGAJUKAN"))
                .perusahaan(pengajuan.map(PengajuanMagang::getPerusahaan).orElse("-"))
                .namaDosenPembimbing("Data Dosen") 
                .statusMagang(pengajuan.isPresent() && "DITERIMA".equalsIgnoreCase(pengajuan.get().getStatus()) ? "AKTIF" : "BELUM_MULAI")
                .jumlahLogbookDisetujui((int) countLogbook)
                .nilaiAkhir(penilaian.map(Penilaian::getNilaiAkhir).orElse(0.0))
                .build();

        return new ApiResponse<>(true, "Status Magang berhasil diambil", response);
    }

    // --- KERANGKA METHOD LAIN (IMPLEMENTASI MENYUSUL) ---

   // 1. Ubah getStatusMagangUserSaatIni menjadi ApiResponse<?>
    @Override
    public ApiResponse<?> getStatusMagangUserSaatIni() {
        return getStatusMagang();
    }

    // 2. Ubah semua method lainnya menjadi ApiResponse<?>
    @Override
    public ApiResponse<?> ajukanMagang(PengajuanMagangRequest request, MultipartFile file1, MultipartFile file2) {
        return new ApiResponse<>(false, "Fitur belum diimplementasikan", null);
    }

    @Override
    public ApiResponse<?> revisiPengajuan(Long id, PengajuanMagangRequest request, MultipartFile file1, MultipartFile file2) {
        return new ApiResponse<>(false, "Fitur belum diimplementasikan", null);
    }

    @Override
    public ApiResponse<?> getAllPengajuan(int page, int size) {
        return new ApiResponse<>(false, "Fitur belum diimplementasikan", null);
    }

    @Override
    public ApiResponse<?> verifikasiPengajuan(Long id, VerifikasiRequest request) {
        return new ApiResponse<>(false, "Fitur belum diimplementasikan", null);
    }

    @Override
    public ApiResponse<?> setPenempatanMagang(PenempatanRequest request) {
        return new ApiResponse<>(false, "Fitur belum diimplementasikan", null);
    }
}