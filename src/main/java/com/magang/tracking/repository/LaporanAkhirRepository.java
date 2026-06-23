package com.magang.tracking.repository;

import com.magang.tracking.entity.LaporanAkhir;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LaporanAkhirRepository extends JpaRepository<LaporanAkhir, Long> {
    Optional<LaporanAkhir> findByMahasiswaId(Long mahasiswaId);
}