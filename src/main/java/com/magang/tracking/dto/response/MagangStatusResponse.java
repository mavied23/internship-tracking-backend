package com.magang.tracking.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MagangStatusResponse {
    private Long mahasiswaId;
    private String namaMahasiswa;
    private String nim;
    
    // Status alur dari PRD
    private String statusPengajuan; // Contoh: MENGUNGGU_VERIFIKASI, REVISI, DISETUJUI
    private String perusahaan;
    
    // Status Pembimbingan
    private String namaDosenPembimbing;
    private String statusMagang; // Contoh: BELUM_MULAI, AKTIF, SELESAI
    
    // Statistik & Nilai Akhir
    private Integer jumlahLogbookDisetujui;
    private Double nilaiAkhir;
}