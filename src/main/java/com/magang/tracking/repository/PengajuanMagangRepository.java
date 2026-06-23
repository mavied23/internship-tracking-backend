package com.magang.tracking.repository;

import com.magang.tracking.entity.PengajuanMagang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PengajuanMagangRepository extends JpaRepository<PengajuanMagang, Long> {
    List<PengajuanMagang> findByMahasiswaId(Long mahasiswaId);
    List<PengajuanMagang> findByStatus(String status);
}