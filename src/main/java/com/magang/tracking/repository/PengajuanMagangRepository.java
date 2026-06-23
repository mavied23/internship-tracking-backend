package com.magang.tracking.repository;

import com.magang.tracking.entity.PengajuanMagang;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PengajuanMagangRepository extends JpaRepository<PengajuanMagang, Long> {
    // Mencari pengajuan terakhir berdasarkan ID mahasiswa
    Optional<PengajuanMagang> findFirstByMahasiswaIdOrderByTanggalPengajuanDesc(Long mahasiswaId);
}