package com.magang.tracking.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.magang.tracking.dto.response.ApiResponse;
import com.magang.tracking.entity.Dosen;
import com.magang.tracking.entity.Mahasiswa;
import com.magang.tracking.entity.User;
import com.magang.tracking.exception.ResourceNotFoundException;
import com.magang.tracking.repository.DosenRepository;
import com.magang.tracking.repository.MahasiswaRepository;
import com.magang.tracking.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ProfileController {

    private final UserRepository userRepository;
    private final MahasiswaRepository mahasiswaRepository;
    private final DosenRepository dosenRepository;

    @GetMapping("/me")
    public ResponseEntity<?> getProfilSaya() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User tidak ditemukan"));

        if ("ROLE_MAHASISWA".equals(user.getRole())) {
            Mahasiswa mahasiswa = mahasiswaRepository.findByUserId(user.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Profil mahasiswa belum dilengkapi"));
            return ResponseEntity.ok(new ApiResponse<>(true, "Profil mahasiswa berhasil diambil.", mahasiswa));
        }

        if ("ROLE_DOSEN".equals(user.getRole())) {
            Dosen dosen = dosenRepository.findByUser(user)
                    .orElseThrow(() -> new ResourceNotFoundException("Profil dosen tidak ditemukan"));
            return ResponseEntity.ok(new ApiResponse<>(true, "Profil dosen berhasil diambil.", dosen));
        }

        return ResponseEntity.ok(new ApiResponse<>(true, "Profil admin berhasil diambil.", user));
    }
}