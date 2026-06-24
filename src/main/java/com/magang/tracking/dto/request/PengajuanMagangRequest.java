package com.magang.tracking.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;

@Data
@NoArgsConstructor // PENTING untuk @ModelAttribute
@AllArgsConstructor
public class PengajuanMagangRequest {
    private String perusahaan;
    private String alamat;
    
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate tanggalMulai;
    
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate tanggalSelesai;
}