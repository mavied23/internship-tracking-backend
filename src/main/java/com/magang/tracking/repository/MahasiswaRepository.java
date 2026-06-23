package com.magang.tracking.repository;

import com.magang.tracking.entity.Mahasiswa;
import com.magang.tracking.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MahasiswaRepository extends JpaRepository<Mahasiswa, Long>{
    Optional<Mahasiswa> findByUser(User user);
    Optional<Mahasiswa> findByNim(String nim);    
} 

