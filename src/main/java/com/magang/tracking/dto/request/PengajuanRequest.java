package com.magang.tracking.dto.request;

import lombok.Data;
import java.time.LocalDate;

@Data
public class PengajuanRequest {
    private String perusahaan;
    private String alamat;
    private LocalDate tanggalMulai;
    private LocalDate tanggalSelesai;
}