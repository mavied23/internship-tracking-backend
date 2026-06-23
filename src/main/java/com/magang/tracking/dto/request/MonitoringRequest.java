package com.magang.tracking.dto.request;

import lombok.Data;
import java.time.LocalDate;

@Data
public class MonitoringRequest {
    private Long mahasiswaId;
    private Integer evaluasi;
    private String catatan;
    private LocalDate tanggal;
}