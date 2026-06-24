package com.magang.tracking.controller;

import com.magang.tracking.dto.request.PengajuanMagangRequest;
import com.magang.tracking.service.MagangService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;


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
    @PostMapping(value = "/pengajuan", consumes = {"multipart/form-data"})
    @PreAuthorize("hasRole('ROLE_MAHASISWA')")
    public ResponseEntity<?> ajukanMagang(
            @ModelAttribute PengajuanMagangRequest request,
            @RequestParam("dokumen") MultipartFile dokumen) {
        
        return ResponseEntity.ok(magangService.ajukanMagang(request, dokumen));
    }
    // Endpoint Detail
    @GetMapping("/pengajuan/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> getDetailPengajuan(@PathVariable Long id) {
        return ResponseEntity.ok(magangService.getDetailPengajuan(id));
    }

    // Endpoint Bulk Approve
    @PostMapping("/pengajuan/bulk-approve")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> bulkApprove(@RequestBody List<Long> ids) {
        return ResponseEntity.ok(magangService.bulkApprove(ids));
    }
}