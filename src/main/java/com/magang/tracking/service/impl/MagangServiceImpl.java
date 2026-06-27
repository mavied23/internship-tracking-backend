package com.magang.tracking.service.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.magang.tracking.dto.request.PenempatanRequest;
import com.magang.tracking.dto.request.PengajuanMagangRequest;
import com.magang.tracking.dto.request.VerifikasiRequest;
import com.magang.tracking.dto.response.ApiResponse;
import com.magang.tracking.dto.response.MagangStatusResponse;
import com.magang.tracking.entity.Dosen;
import com.magang.tracking.entity.Mahasiswa;
import com.magang.tracking.entity.PenempatanMagang;
import com.magang.tracking.entity.PengajuanMagang;
import com.magang.tracking.entity.Penilaian;
import com.magang.tracking.entity.User;
import com.magang.tracking.repository.DosenRepository;
import com.magang.tracking.repository.LogbookRepository;
import com.magang.tracking.repository.MahasiswaRepository;
import com.magang.tracking.repository.PenempatanMagangRepository;
import com.magang.tracking.repository.PengajuanMagangRepository;
import com.magang.tracking.repository.PenilaianRepository;
import com.magang.tracking.repository.UserRepository;
import com.magang.tracking.service.MagangService;

import lombok.RequiredArgsConstructor;

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

        String pathDokumen = simpanFile(dokumen);

        PengajuanMagang pengajuanBaru = new PengajuanMagang();
        pengajuanBaru.setMahasiswa(mahasiswa);
        pengajuanBaru.setPerusahaan(request.getPerusahaan());
        pengajuanBaru.setAlamat(request.getAlamat());
        pengajuanBaru.setTanggalMulai(request.getTanggalMulai());
        pengajuanBaru.setTanggalSelesai(request.getTanggalSelesai());
        pengajuanBaru.setDokumen(pathDokumen);
        pengajuanBaru.setStatus("MENUNGGU_VERIFIKASI");
        pengajuanBaru.setTanggalPengajuan(LocalDateTime.now());

        pengajuanMagangRepository.save(pengajuanBaru);

        return new ApiResponse<>(true, "Pengajuan magang berhasil dikirim dan menunggu verifikasi Admin.", null);
    }

    // --- FITUR REVISI PENGAJUAN (MAHASISWA) ---
    @Override
    public ApiResponse<?> revisiPengajuan(Long id, PengajuanMagangRequest request, MultipartFile file1, MultipartFile file2) {
        // 1. Ambil mahasiswa yang sedang login
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User tidak ditemukan"));
        Mahasiswa mahasiswa = mahasiswaRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Profil mahasiswa belum dilengkapi"));

        // 2. Cari pengajuan berdasarkan ID
        PengajuanMagang pengajuan = pengajuanMagangRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pengajuan tidak ditemukan"));

        // 3. Validasi: hanya boleh revisi kalau pengajuan milik mahasiswa yg login
        if (!pengajuan.getMahasiswa().getId().equals(mahasiswa.getId())) {
            return new ApiResponse<>(false, "Anda tidak berhak merevisi pengajuan ini.", null);
        }

        // 4. Validasi: hanya boleh revisi kalau statusnya REVISI
        if (!"REVISI".equalsIgnoreCase(pengajuan.getStatus())) {
            return new ApiResponse<>(false, "Pengajuan ini tidak dalam status REVISI.", null);
        }

        // 5. Update data yang dikirim (kalau tidak diisi, tetap pakai data lama)
        if (request.getPerusahaan() != null) pengajuan.setPerusahaan(request.getPerusahaan());
        if (request.getAlamat() != null) pengajuan.setAlamat(request.getAlamat());
        if (request.getTanggalMulai() != null) pengajuan.setTanggalMulai(request.getTanggalMulai());
        if (request.getTanggalSelesai() != null) pengajuan.setTanggalSelesai(request.getTanggalSelesai());

        // 6. Update file dokumen kalau ada file baru
        if (file1 != null && !file1.isEmpty()) {
            pengajuan.setDokumen(simpanFile(file1));
        }

        // 7. Kembalikan status ke MENUNGGU_VERIFIKASI setelah direvisi
        pengajuan.setStatus("MENUNGGU_VERIFIKASI");
        pengajuan.setCatatanAdmin(null);
        pengajuan.setTanggalPengajuan(LocalDateTime.now());

        pengajuanMagangRepository.save(pengajuan);

        return new ApiResponse<>(true, "Revisi pengajuan berhasil dikirim ulang.", pengajuan);
    }

    // --- FITUR ADMIN: MELIHAT SEMUA PENGAJUAN DENGAN PAGINATION ---
    @Override
    public ApiResponse<?> getAllPengajuan(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<PengajuanMagang> listPengajuan = pengajuanMagangRepository.findAll(pageable);
        return new ApiResponse<>(true, "Daftar pengajuan magang berhasil diambil.", listPengajuan);
    }

    @Override
    public ApiResponse<?> verifikasiPengajuan(Long id, VerifikasiRequest request) {
        PengajuanMagang pengajuan = pengajuanMagangRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Data pengajuan magang tidak ditemukan"));

        pengajuan.setStatus(request.getStatus().toUpperCase());
        pengajuan.setCatatanAdmin(request.getCatatan());
        pengajuanMagangRepository.save(pengajuan);

        return new ApiResponse<>(true, "Pengajuan magang berhasil diverifikasi dengan status: " + request.getStatus(), null);
    }

    // --- FITUR ADMIN: DETAIL PENGAJUAN ---
    @Override
    public ApiResponse<?> getDetailPengajuan(Long id) {
        PengajuanMagang pengajuan = pengajuanMagangRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pengajuan tidak ditemukan"));
        return new ApiResponse<>(true, "Detail pengajuan berhasil diambil", pengajuan);
    }

    // --- FITUR ADMIN: BULK APPROVE ---
    @Override
    public ApiResponse<?> bulkApprove(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return new ApiResponse<>(false, "Tidak ada data yang dipilih", null);
        }

        List<PengajuanMagang> listPengajuan = pengajuanMagangRepository.findAllById(ids);

        for (PengajuanMagang pengajuan : listPengajuan) {
            pengajuan.setStatus("DITERIMA");
        }

        pengajuanMagangRepository.saveAll(listPengajuan);

        return new ApiResponse<>(true, "Berhasil menyetujui " + listPengajuan.size() + " pengajuan.", null);
    }

    @Override
    public ApiResponse<?> setPenempatanMagang(PenempatanRequest request) {
        Mahasiswa mahasiswa = mahasiswaRepository.findById(request.getMahasiswaId())
                .orElseThrow(() -> new RuntimeException("Data Mahasiswa tidak ditemukan"));

        Dosen dosen = dosenRepository.findById(request.getDosenId())
                .orElseThrow(() -> new RuntimeException("Data Dosen Pembimbing tidak ditemukan"));

        Optional<PengajuanMagang> pengajuanAktif = pengajuanMagangRepository
                .findFirstByMahasiswaIdOrderByTanggalPengajuanDesc(mahasiswa.getId());

        if (pengajuanAktif.isEmpty() || !"DITERIMA".equalsIgnoreCase(pengajuanAktif.get().getStatus())) {
            return new ApiResponse<>(false, "Gagal: Mahasiswa ini belum memiliki pengajuan magang yang disetujui (DITERIMA).", null);
        }

        PenempatanMagang penempatan = new PenempatanMagang();
        penempatan.setMahasiswa(mahasiswa);
        penempatan.setDosen(dosen);
        penempatan.setTanggalMulai(request.getTanggalMulai());
        penempatan.setTanggalSelesai(request.getTanggalSelesai());

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
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename().replace(" ", "_");
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            return fileName;
        } catch (IOException e) {
            throw new RuntimeException("Gagal menyimpan file: " + e.getMessage());
        }
    }
}