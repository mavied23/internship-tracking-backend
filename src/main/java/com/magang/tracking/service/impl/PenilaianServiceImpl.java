package com.magang.tracking.service.impl;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.magang.tracking.dto.request.PenilaianRequest;
import com.magang.tracking.dto.response.ApiResponse;
import com.magang.tracking.entity.Dosen;
import com.magang.tracking.entity.User;
import com.magang.tracking.entity.Mahasiswa;
import com.magang.tracking.entity.Penilaian;
import com.magang.tracking.exception.ResourceNotFoundException;
import com.magang.tracking.repository.DosenRepository;
import com.magang.tracking.repository.MahasiswaRepository;
import com.magang.tracking.repository.PenilaianRepository;
import com.magang.tracking.repository.UserRepository;
import com.magang.tracking.service.PenilaianService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PenilaianServiceImpl implements PenilaianService {

    private final PenilaianRepository penilaianRepository;
    private final MahasiswaRepository mahasiswaRepository;
    private final DosenRepository dosenRepository;
    private final UserRepository userRepository;

    @Override
    public ApiResponse<?> inputPenilaian(PenilaianRequest request) {
        // 1. Cari mahasiswa berdasarkan ID dari request
        Mahasiswa mahasiswa = mahasiswaRepository.findById(request.getMahasiswaId())
                .orElseThrow(() -> new ResourceNotFoundException("Mahasiswa tidak ditemukan"));

        // 2. Hitung nilai akhir rata-rata
        Double nilaiAkhir = (
                request.getDisiplin() +
                request.getKerjasama() +
                request.getKemampuanTeknis() +
                request.getKomunikasi() +
                request.getLaporan() +
                request.getPresentasi() +
                request.getMonitoring()
        ) / 7.0;

        // 3. Cari penilaian yang sudah ada, kalau belum ada buat baru
        Penilaian penilaian = penilaianRepository.findByMahasiswaId(mahasiswa.getId())
                .orElse(new Penilaian());

        penilaian.setMahasiswa(mahasiswa);
        penilaian.setDisiplin(request.getDisiplin());
        penilaian.setKerjasama(request.getKerjasama());
        penilaian.setKemampuanTeknis(request.getKemampuanTeknis());
        penilaian.setKomunikasi(request.getKomunikasi());
        penilaian.setLaporan(request.getLaporan());
        penilaian.setPresentasi(request.getPresentasi());
        penilaian.setMonitoring(request.getMonitoring());
        penilaian.setNilaiAkhir(nilaiAkhir);

        // Set dosen yang sedang login
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username).orElseThrow(() -> new ResourceNotFoundException("User tidak ditemukan"));
        Dosen dosen = dosenRepository.findByUser(user).orElseThrow(() -> new ResourceNotFoundException("Profil dosen tidak ditemukan"));
        penilaian.setDosen(dosen);

        penilaianRepository.save(penilaian);

        return new ApiResponse<>(true, "Penilaian berhasil disimpan. Nilai Akhir: " + String.format("%.2f", nilaiAkhir), penilaian);
    }

    @Override
    public ApiResponse<?> getPenilaianByMahasiswaId(Long mahasiswaId) {
        Penilaian penilaian = penilaianRepository.findByMahasiswaId(mahasiswaId)
                .orElse(null);
        if (penilaian == null) {
            return new ApiResponse<>(false, "Belum ada penilaian untuk mahasiswa ini.", null);
        }
        return new ApiResponse<>(true, "Penilaian berhasil diambil.", penilaian);
    }
}