package com.magang.tracking.repository;

import com.magang.tracking.entity.Monitoring;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MonitoringRepository extends JpaRepository<Monitoring, Long> {
    List<Monitoring> findByMahasiswaId(Long mahasiswaId);
    List<Monitoring> findByDosenId(Long dosenId);
}