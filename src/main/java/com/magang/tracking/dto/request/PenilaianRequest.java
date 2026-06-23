package com.magang.tracking.dto.request;

import lombok.Data;

@Data
public class PenilaianRequest {
    private Long mahasiswaId;
    private Double disiplin;
    private Double kerjasama;
    private Double kemampuanTeknis;
    private Double komunikasi;
    private Double laporan;
    private Double presentasi;
    private Double monitoring;
}