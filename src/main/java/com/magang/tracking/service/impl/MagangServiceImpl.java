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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MagangServiceImpl implements MagangService {

    private final UserRepository userRepository;
    private final MahasiswaRepository mahasiswaRepository;
    private final PengajuanMagangRepository pengajuanMagangRepository;
    private final LogbookRepository logbookRepository;
    private final PenilaianRepository penilaianRepository;
    private final DosenRepository dosenRepository;
    private final PenempatanMagangRepository penempatanMagangRepository;

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

    @Override
    public ApiResponse<?> getStatusMagangUserSaatIni() {
        return getStatusMagang();
    }

    // --- FITUR PENGAJUAN MAGANG (MAHASISWA) ---
    @Override
    public ApiResponse<?> ajukanMagang(PengajuanMagangRequest request, MultipartFile dokumen) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User tidak ditemukan"));

        Mahasiswa mahasiswa = mahasiswaRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Profil mahasiswa belum dilengkapi!"));

        Optional<PengajuanMagang> pengajuanAktif = pengajuanMagangRepository
                .findFirstByMahasiswaIdOrderByTanggalPengajuanDesc(mahasiswa.getId());
        
        if (pengajuanAktif.isPresent()) {
            String status = pengajuanAktif.get().getStatus();
            if (status.equals("MENUNGGU_VERIFIKASI") || status.equals("DITERIMA")) {
                return new ApiResponse<>(false, "Kamu masih memiliki pengajuan magang dengan status: " + status, null);
            }
        }

        // Simpan dokumen pengajuan
        String pathDokumen = simpanFile(dokumen);

        // Buat entitas baru dan isi SEMUA kolom yang nullable = false
        PengajuanMagang pengajuanBaru = new PengajuanMagang();
        pengajuanBaru.setMahasiswa(mahasiswa);
        
        // Data dari request DTO
        pengajuanBaru.setPerusahaan(request.getPerusahaan());
        pengajuanBaru.setAlamat(request.getAlamat());
        pengajuanBaru.setTanggalMulai(request.getTanggalMulai());
        pengajuanBaru.setTanggalSelesai(request.getTanggalSelesai());
        
        // Data file dan status
        pengajuanBaru.setDokumen(pathDokumen); 
        pengajuanBaru.setStatus("MENUNGGU_VERIFIKASI");
        pengajuanBaru.setTanggalPengajuan(LocalDateTime.now());

        pengajuanMagangRepository.save(pengajuanBaru);

        return new ApiResponse<>(true, "Pengajuan magang berhasil dikirim dan menunggu verifikasi Admin.", null);
    }

    @Override
    public ApiResponse<?> revisiPengajuan(Long id, PengajuanMagangRequest request, MultipartFile file1, MultipartFile file2) {
        return new ApiResponse<>(false, "Fitur belum diimplementasikan", null);
    }

    // --- FITUR ADMIN: MELIHAT SEMUA PENGAJUAN DENGAN PAGINATION ---
    @Override
    public ApiResponse<?> getAllPengajuan(int page, int size) {
        // Konfigurasi pagination (halaman dimulai dari 0)
        Pageable pageable = PageRequest.of(page, size);

        // Tarik data dari repository (Spring otomatis membungkusnya dalam objek Page)
        Page<PengajuanMagang> listPengajuan = pengajuanMagangRepository.findAll(pageable);

        return new ApiResponse<>(true, "Daftar pengajuan magang berhasil diambil.", listPengajuan);
    }

    @Override
    public ApiResponse<?> verifikasiPengajuan(Long id, VerifikasiRequest request) {
        // 1. Cari pengajuan magang berdasarkan ID
        PengajuanMagang pengajuan = pengajuanMagangRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Data pengajuan magang tidak ditemukan"));

        // 2. Update status dan berikan catatan dari admin
        pengajuan.setStatus(request.getStatus().toUpperCase()); // Pastikan formatnya selalu kapital
        pengajuan.setCatatanAdmin(request.getCatatan());

        // 3. Simpan perubahan ke database
        pengajuanMagangRepository.save(pengajuan);

        return new ApiResponse<>(true, "Pengajuan magang berhasil diverifikasi dengan status: " + request.getStatus(), null);
    }

    @Override
    public ApiResponse<?> setPenempatanMagang(PenempatanRequest request) {
        // 1. Cari data Mahasiswa dan Dosen berdasarkan ID
        Mahasiswa mahasiswa = mahasiswaRepository.findById(request.getMahasiswaId())
                .orElseThrow(() -> new RuntimeException("Data Mahasiswa tidak ditemukan"));

        Dosen dosen = dosenRepository.findById(request.getDosenId())
                .orElseThrow(() -> new RuntimeException("Data Dosen Pembimbing tidak ditemukan"));

        // 2. Validasi: Pastikan pengajuan magang mahasiswa tersebut statusnya "DITERIMA"
        Optional<PengajuanMagang> pengajuanAktif = pengajuanMagangRepository
                .findFirstByMahasiswaIdOrderByTanggalPengajuanDesc(mahasiswa.getId());
        
        if (pengajuanAktif.isEmpty() || !"DITERIMA".equalsIgnoreCase(pengajuanAktif.get().getStatus())) {
            return new ApiResponse<>(false, "Gagal: Mahasiswa ini belum memiliki pengajuan magang yang disetujui (DITERIMA).", null);
        }

        // 3. Buat dan simpan data Penempatan Magang
        PenempatanMagang penempatan = new PenempatanMagang();
        penempatan.setMahasiswa(mahasiswa);
        penempatan.setDosen(dosen);
        penempatan.setTanggalMulai(request.getTanggalMulai());
        penempatan.setTanggalSelesai(request.getTanggalSelesai());
        
        // Simpan ke database
        penempatanMagangRepository.save(penempatan);

        return new ApiResponse<>(true, "Penempatan magang dan penetapan Dosen Pembimbing berhasil disimpan.", null);
    }

    // --- METHOD BANTUAN UNTUK UPLOAD FILE ---
    private final String UPLOAD_DIR = "uploads/dokumen_magang/";

    private String simpanFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }
        try {
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            
            // Buat nama file unik agar tidak bentrok
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename().replace(" ", "_");
            Path filePath = uploadPath.resolve(fileName);
            
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            return fileName;
        } catch (IOException e) {
            throw new RuntimeException("Gagal menyimpan file: " + e.getMessage());
        }
    }
}