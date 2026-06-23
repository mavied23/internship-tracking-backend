package com.magang.tracking.dto.request;

import lombok.Data;

@Data
public class VerifikasiRequest {
    private String status; // Contoh: DISETUJUI, DITOLAK, REVISI
    private String catatan;
}