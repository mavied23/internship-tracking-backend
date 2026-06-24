package com.magang.tracking.controller;

import com.magang.tracking.dto.request.LogbookRequest;
import com.magang.tracking.dto.request.PengajuanMagangRequest;
import com.magang.tracking.service.LaporanService;
import com.magang.tracking.service.LogbookService;
import com.magang.tracking.service.MagangService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/mahasiswa")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@PreAuthorize("hasRole('ROLE_MAHASISWA')")
public class MahasiswaController {

    private final MagangService magangService;
    private final LogbookService logbookService;
    private final LaporanService laporanService;

    @PostMapping(value = "/pengajuan", consumes = {"multipart/form-data"})
    public ResponseEntity<?> ajukanMagang(
            @ModelAttribute PengajuanMagangRequest request,
            @RequestParam("dokumen") MultipartFile dokumen) {
        
        return ResponseEntity.ok(magangService.ajukanMagang(request, dokumen));
    }

    @PutMapping(value = "/pengajuan/{id}", consumes = {"multipart/form-data"})
    public ResponseEntity<?> revisiPengajuan(
            @PathVariable Long id,
            @ModelAttribute PengajuanMagangRequest request,
            @RequestPart(value = "suratPengantar", required = false) MultipartFile suratPengantar,
            @RequestPart(value = "proposal", required = false) MultipartFile proposal) {
        return ResponseEntity.ok(magangService.revisiPengajuan(id, request, suratPengantar, proposal));
    }

    @PostMapping(value = "/logbook", consumes = {"multipart/form-data"})
    public ResponseEntity<?> inputLogbook(
            @ModelAttribute LogbookRequest request,
            @RequestPart(value = "dokumentasi", required = false) MultipartFile dokumentasi) {
        return ResponseEntity.ok(logbookService.inputLogbook(request, dokumentasi));
    }

    @PostMapping(value = "/laporan-akhir", consumes = {"multipart/form-data"})
    public ResponseEntity<?> uploadLaporanAkhir(
            @RequestPart("fileLaporan") MultipartFile fileLaporan) {
        return ResponseEntity.ok(laporanService.uploadLaporan(fileLaporan));
    }
}