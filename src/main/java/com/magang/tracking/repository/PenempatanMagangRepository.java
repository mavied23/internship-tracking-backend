package com.magang.tracking.repository;

import com.magang.tracking.entity.PenempatanMagang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PenempatanMagangRepository extends JpaRepository<PenempatanMagang, Long> {
    Optional<PenempatanMagang> findByMahasiswaId(Long mahasiswaId);
    List<PenempatanMagang> findByDosenId(Long dosenId);
}