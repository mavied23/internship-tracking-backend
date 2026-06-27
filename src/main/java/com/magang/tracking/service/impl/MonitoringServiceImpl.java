package com.magang.tracking.service.impl;

import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.magang.tracking.dto.request.MonitoringRequest;
import com.magang.tracking.dto.response.ApiResponse;
import com.magang.tracking.entity.Dosen;
import com.magang.tracking.entity.Mahasiswa;
import com.magang.tracking.entity.Monitoring;
import com.magang.tracking.entity.User;
import com.magang.tracking.exception.ResourceNotFoundException;
import com.magang.tracking.repository.DosenRepository;
import com.magang.tracking.repository.MahasiswaRepository;
import com.magang.tracking.repository.MonitoringRepository;
import com.magang.tracking.repository.UserRepository;
import com.magang.tracking.service.MonitoringService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MonitoringServiceImpl implements MonitoringService {

    private final MonitoringRepository monitoringRepository;
    private final MahasiswaRepository mahasiswaRepository;
    private final DosenRepository dosenRepository;
    private final UserRepository userRepository;

    @Override
    public ApiResponse<?> inputMonitoring(MonitoringRequest request) {
        // 1. Ambil dosen yang sedang login
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User tidak ditemukan"));
        Dosen dosen = dosenRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Profil dosen tidak ditemukan"));

        // 2. Cari mahasiswa yang dimonitoring berdasarkan ID dari request
        Mahasiswa mahasiswa = mahasiswaRepository.findById(request.getMahasiswaId())
                .orElseThrow(() -> new ResourceNotFoundException("Mahasiswa tidak ditemukan"));

        // 3. Validasi nilai evaluasi harus 1-10
        if (request.getEvaluasi() < 1 || request.getEvaluasi() > 10) {
            return new ApiResponse<>(false, "Nilai evaluasi harus antara 1 sampai 10.", null);
        }

        // 4. Buat objek Monitoring dan simpan ke DB
        Monitoring monitoring = new Monitoring();
        monitoring.setMahasiswa(mahasiswa);
        monitoring.setDosen(dosen);
        monitoring.setEvaluasi(request.getEvaluasi());
        monitoring.setCatatan(request.getCatatan());
        monitoring.setTanggal(request.getTanggal());
        monitoringRepository.save(monitoring);

        return new ApiResponse<>(true, "Data monitoring berhasil disimpan.", monitoring);
    }

    @Override
    public ApiResponse<?> getMonitoringByMahasiswaId(Long mahasiswaId) {
        List<Monitoring> list = monitoringRepository.findByMahasiswaId(mahasiswaId);
        return new ApiResponse<>(true, "Riwayat monitoring berhasil diambil.", list);
    }

    @Override
    public ApiResponse<?> getMonitoringByDosen() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User tidak ditemukan"));
        Dosen dosen = dosenRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Profil dosen tidak ditemukan"));

        List<Monitoring> list = monitoringRepository.findByDosenId(dosen.getId());
        return new ApiResponse<>(true, "Daftar monitoring oleh dosen berhasil diambil.", list);
    }
}