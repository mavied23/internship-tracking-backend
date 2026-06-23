package com.magang.tracking.repository;

import com.magang.tracking.entity.Penilaian;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PenilaianRepository extends JpaRepository<Penilaian, Long> {
    Optional<Penilaian> findByMahasiswaId(Long mahasiswaId);
}