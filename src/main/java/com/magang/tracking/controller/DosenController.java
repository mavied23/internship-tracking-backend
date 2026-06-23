package com.magang.tracking.controller;

import com.magang.tracking.dto.request.MonitoringRequest;
import com.magang.tracking.dto.request.PenilaianRequest;
import com.magang.tracking.dto.request.VerifikasiRequest;
import com.magang.tracking.service.LogbookService;
import com.magang.tracking.service.MonitoringService;
import com.magang.tracking.service.PenilaianService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dosen")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@PreAuthorize("hasRole('ROLE_DOSEN')")
public class DosenController {

    private final LogbookService logbookService;
    private final MonitoringService monitoringService;
    private final PenilaianService penilaianService;

    @PutMapping("/logbook/{logbookId}/verifikasi")
    public ResponseEntity<?> verifikasiLogbook(
            @PathVariable Long logbookId, 
            @RequestBody VerifikasiRequest request) {
        return ResponseEntity.ok(logbookService.verifikasiLogbook(logbookId, request));
    }

    @PostMapping("/monitoring")
    public ResponseEntity<?> inputMonitoring(@RequestBody MonitoringRequest request) {
        return ResponseEntity.ok(monitoringService.inputMonitoring(request));
    }

    @PostMapping("/penilaian")
    public ResponseEntity<?> inputPenilaian(@RequestBody PenilaianRequest request) {
        return ResponseEntity.ok(penilaianService.inputPenilaian(request));
    }
}