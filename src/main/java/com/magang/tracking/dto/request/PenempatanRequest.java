package com.magang.tracking.dto.request;

import lombok.Data;
import java.time.LocalDate;

@Data
public class PenempatanRequest {
    private Long mahasiswaId;
    private Long dosenId;
    private LocalDate tanggalMulai;
    private LocalDate tanggalSelesai;
}