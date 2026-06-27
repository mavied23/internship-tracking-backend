package com.magang.tracking.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.magang.tracking.dto.request.MonitoringRequest;
import com.magang.tracking.dto.request.PenilaianRequest;
import com.magang.tracking.dto.request.VerifikasiRequest;
import com.magang.tracking.service.LogbookService;
import com.magang.tracking.service.MonitoringService;
import com.magang.tracking.service.PenilaianService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/dosen")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@PreAuthorize("hasRole('ROLE_DOSEN')")
public class DosenController {

    private final LogbookService logbookService;
    private final MonitoringService monitoringService;
    private final PenilaianService penilaianService;

    // ==================== LOGBOOK ====================

    @GetMapping("/logbook/{mahasiswaId}")
    public ResponseEntity<?> getLogbookMahasiswa(@PathVariable Long mahasiswaId) {
        return ResponseEntity.ok(logbookService.getLogbookByMahasiswaId(mahasiswaId));
    }

    @PutMapping("/logbook/{logbookId}/verifikasi")
    public ResponseEntity<?> verifikasiLogbook(
            @PathVariable Long logbookId,
            @RequestBody VerifikasiRequest request) {
        return ResponseEntity.ok(logbookService.verifikasiLogbook(logbookId, request));
    }

    // ==================== MONITORING ====================

    @GetMapping("/monitoring")
    public ResponseEntity<?> getMonitoringOlehDosen() {
        return ResponseEntity.ok(monitoringService.getMonitoringByDosen());
    }

    @GetMapping("/monitoring/{mahasiswaId}")
    public ResponseEntity<?> getMonitoringMahasiswa(@PathVariable Long mahasiswaId) {
        return ResponseEntity.ok(monitoringService.getMonitoringByMahasiswaId(mahasiswaId));
    }

    @PostMapping("/monitoring")
    public ResponseEntity<?> inputMonitoring(@RequestBody MonitoringRequest request) {
        return ResponseEntity.ok(monitoringService.inputMonitoring(request));
    }

    // ==================== PENILAIAN ====================

    @GetMapping("/penilaian/{mahasiswaId}")
    public ResponseEntity<?> getPenilaian(@PathVariable Long mahasiswaId) {
        return ResponseEntity.ok(penilaianService.getPenilaianByMahasiswaId(mahasiswaId));
    }

    @PostMapping("/penilaian")
    public ResponseEntity<?> inputPenilaian(@RequestBody PenilaianRequest request) {
        return ResponseEntity.ok(penilaianService.inputPenilaian(request));
    }
}