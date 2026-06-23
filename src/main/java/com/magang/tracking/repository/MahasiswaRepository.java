package com.magang.tracking.repository;

import com.magang.tracking.entity.Mahasiswa;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface MahasiswaRepository extends JpaRepository<Mahasiswa, Long> {
    // Method ini akan kita gunakan untuk mencari mahasiswa berdasarkan user yang login
    Optional<Mahasiswa> findByUserId(Long userId);
}