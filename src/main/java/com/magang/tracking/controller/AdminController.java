package com.magang.tracking.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.magang.tracking.dto.request.PenempatanRequest;
import com.magang.tracking.dto.request.VerifikasiRequest;
import com.magang.tracking.service.AdminService;
import com.magang.tracking.service.MagangService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class AdminController {

    private final MagangService magangService;
    private final AdminService adminService;

    // ==================== PENGAJUAN ====================

    @GetMapping("/pengajuan")
    public ResponseEntity<?> getSemuaPengajuan(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(magangService.getAllPengajuan(page, size));
    }

    @GetMapping("/pengajuan/{id}")
    public ResponseEntity<?> getDetailPengajuan(@PathVariable Long id) {
        return ResponseEntity.ok(magangService.getDetailPengajuan(id));
    }

    @PutMapping("/pengajuan/{id}/verifikasi")
    public ResponseEntity<?> verifikasiPengajuan(
            @PathVariable Long id,
            @RequestBody VerifikasiRequest request) {
        return ResponseEntity.ok(magangService.verifikasiPengajuan(id, request));
    }

    @PostMapping("/pengajuan/bulk-approve")
    public ResponseEntity<?> bulkApprove(@RequestBody List<Long> ids) {
        return ResponseEntity.ok(magangService.bulkApprove(ids));
    }

    // ==================== PENEMPATAN ====================

    @PostMapping("/penempatan")
    public ResponseEntity<?> setPenempatanMagang(@RequestBody PenempatanRequest request) {
        return ResponseEntity.ok(magangService.setPenempatanMagang(request));
    }

    // ==================== MAHASISWA ====================

    @GetMapping("/mahasiswa")
    public ResponseEntity<?> getAllMahasiswa() {
        return ResponseEntity.ok(adminService.getAllMahasiswa());
    }

    @GetMapping("/mahasiswa/{id}")
    public ResponseEntity<?> getDetailMahasiswa(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.getDetailMahasiswa(id));
    }

    // ==================== DOSEN ====================

    @GetMapping("/dosen")
    public ResponseEntity<?> getAllDosen() {
        return ResponseEntity.ok(adminService.getAllDosen());
    }

    // ==================== DASHBOARD ====================

    @GetMapping("/dashboard")
    public ResponseEntity<?> getDashboard() {
        return ResponseEntity.ok(adminService.getDashboardStats());
    }
}