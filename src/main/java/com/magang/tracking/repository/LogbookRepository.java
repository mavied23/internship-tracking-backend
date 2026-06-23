package com.magang.tracking.repository;

import com.magang.tracking.entity.Logbook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LogbookRepository extends JpaRepository<Logbook, Long> {
    List<Logbook> findByMahasiswaId(Long mahasiswaId);
    List<Logbook> findByMahasiswaIdAndStatus(Long mahasiswaId, String status);

    // Tambahkan method ini untuk menghitung jumlah logbook yang disetujui
    // Asumsi: field statusSetuju di entitas Logbook bertipe boolean
    // Spring akan mengartikan: cari berdasarkan mahasiswaId DAN status yang SAMA DENGAN parameter
    long countByMahasiswaIdAndStatus(Long mahasiswaId, String status);
}