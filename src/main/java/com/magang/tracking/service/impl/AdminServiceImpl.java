package com.magang.tracking.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.magang.tracking.dto.response.ApiResponse;
import com.magang.tracking.entity.Dosen;
import com.magang.tracking.entity.Logbook;
import com.magang.tracking.entity.Mahasiswa;
import com.magang.tracking.entity.PengajuanMagang;
import com.magang.tracking.entity.Penilaian;
import com.magang.tracking.exception.ResourceNotFoundException;
import com.magang.tracking.repository.DosenRepository;
import com.magang.tracking.repository.LogbookRepository;
import com.magang.tracking.repository.MahasiswaRepository;
import com.magang.tracking.repository.PengajuanMagangRepository;
import com.magang.tracking.repository.PenilaianRepository;
import com.magang.tracking.service.AdminService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final MahasiswaRepository mahasiswaRepository;
    private final DosenRepository dosenRepository;
    private final PengajuanMagangRepository pengajuanMagangRepository;
    private final LogbookRepository logbookRepository;
    private final PenilaianRepository penilaianRepository;

    @Override
    public ApiResponse<?> getAllMahasiswa() {
        List<Mahasiswa> list = mahasiswaRepository.findAll();
        return new ApiResponse<>(true, "Daftar mahasiswa berhasil diambil.", list);
    }

    @Override
    public ApiResponse<?> getDetailMahasiswa(Long mahasiswaId) {
        Mahasiswa mahasiswa = mahasiswaRepository.findById(mahasiswaId)
                .orElseThrow(() -> new ResourceNotFoundException("Mahasiswa tidak ditemukan"));

        Map<String, Object> detail = new HashMap<>();
        detail.put("mahasiswa", mahasiswa);

        Optional<PengajuanMagang> pengajuan = pengajuanMagangRepository
                .findFirstByMahasiswaIdOrderByTanggalPengajuanDesc(mahasiswaId);
        detail.put("pengajuanTerakhir", pengajuan.orElse(null));

        List<Logbook> logbooks = logbookRepository.findByMahasiswaId(mahasiswaId);
        detail.put("logbook", logbooks);
        detail.put("jumlahLogbookDisetujui",
                logbookRepository.countByMahasiswaIdAndStatus(mahasiswaId, "DISETUJUI"));

        Optional<Penilaian> penilaian = penilaianRepository.findByMahasiswaId(mahasiswaId);
        detail.put("penilaian", penilaian.orElse(null));

        return new ApiResponse<>(true, "Detail mahasiswa berhasil diambil.", detail);
    }

    @Override
    public ApiResponse<?> getAllDosen() {
        List<Dosen> list = dosenRepository.findAll();
        return new ApiResponse<>(true, "Daftar dosen berhasil diambil.", list);
    }

    @Override
    public ApiResponse<?> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();

        stats.put("totalMahasiswa", mahasiswaRepository.count());
        stats.put("totalDosen", dosenRepository.count());

        stats.put("pengajuanMenunggu", pengajuanMagangRepository.countByStatus("MENUNGGU_VERIFIKASI"));
        stats.put("pengajuanDiterima", pengajuanMagangRepository.countByStatus("DITERIMA"));
        stats.put("pengajuanRevisi",   pengajuanMagangRepository.countByStatus("REVISI"));
        stats.put("pengajuanDitolak",  pengajuanMagangRepository.countByStatus("DITOLAK"));

        return new ApiResponse<>(true, "Statistik dashboard berhasil diambil.", stats);
    }
}