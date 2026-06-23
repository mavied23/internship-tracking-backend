package com.magang.tracking.controller;

import com.magang.tracking.dto.request.PenempatanRequest;
import com.magang.tracking.dto.request.VerifikasiRequest;
import com.magang.tracking.service.MagangService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class AdminController {

    private final MagangService magangService;

    @GetMapping("/pengajuan")
    public ResponseEntity<?> getSemuaPengajuan(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(magangService.getAllPengajuan(page, size));
    }

    @PutMapping("/pengajuan/{id}/verifikasi")
    public ResponseEntity<?> verifikasiPengajuan(
            @PathVariable Long id, 
            @RequestBody VerifikasiRequest request) {
        return ResponseEntity.ok(magangService.verifikasiPengajuan(id, request));
    }

    @PostMapping("/penempatan")
    public ResponseEntity<?> setPenempatanMagang(@RequestBody PenempatanRequest request) {
        return ResponseEntity.ok(magangService.setPenempatanMagang(request));
    }
}