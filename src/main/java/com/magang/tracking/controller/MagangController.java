package com.magang.tracking.controller;

import com.magang.tracking.service.MagangService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/magang")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class MagangController {

    private final MagangService magangService;

    @GetMapping("/status")
    @PreAuthorize("hasAnyRole('ROLE_MAHASISWA', 'ROLE_DOSEN', 'ROLE_ADMIN')")
    public ResponseEntity<?> getStatusMagang() {
        return ResponseEntity.ok(magangService.getStatusMagangUserSaatIni());
    }
}