package com.magang.tracking.repository;

import com.magang.tracking.entity.Logbook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LogbookRepository extends JpaRepository<Logbook, Long> {
    List<Logbook> findByMahasiswaId(Long mahasiswaId);
    List<Logbook> findByMahasiswaIdAndStatus(Long mahasiswaId, String status);
}