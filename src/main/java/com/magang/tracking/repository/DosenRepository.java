package com.magang.tracking.repository;

import com.magang.tracking.entity.Dosen;
import com.magang.tracking.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DosenRepository extends JpaRepository<Dosen, Long>{
  Optional<Dosen> findByUser(User user);
  Optional<Dosen> findByNidn(String nidn);
}
