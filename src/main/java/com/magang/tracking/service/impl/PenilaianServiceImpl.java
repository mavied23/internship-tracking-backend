package com.magang.tracking.service.impl;

import com.magang.tracking.dto.request.PenilaianRequest;
import com.magang.tracking.dto.response.ApiResponse;
import com.magang.tracking.entity.Penilaian;
import com.magang.tracking.repository.PenilaianRepository;
import com.magang.tracking.service.PenilaianService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PenilaianServiceImpl implements PenilaianService {

    // Inject Repository yang baru kita buat
    private final PenilaianRepository penilaianRepository;

    @Override
    public ApiResponse<?> inputPenilaian(PenilaianRequest request) {
        Double nilaiAkhir = (
                request.getDisiplin() + 
                request.getKerjasama() + 
                request.getKemampuanTeknis() + 
                request.getKomunikasi() + 
                request.getLaporan() + 
                request.getPresentasi() + 
                request.getMonitoring()
        ) / 7.0;

        Penilaian penilaian = new Penilaian();
        penilaian.setDisiplin(request.getDisiplin());
        penilaian.setKerjasama(request.getKerjasama());
        penilaian.setKemampuanTeknis(request.getKemampuanTeknis());
        penilaian.setKomunikasi(request.getKomunikasi());
        penilaian.setLaporan(request.getLaporan());
        penilaian.setPresentasi(request.getPresentasi());
        penilaian.setMonitoring(request.getMonitoring());
        penilaian.setNilaiAkhir(nilaiAkhir);

        // dihapus dan diganti dengan kode nyata untuk menyimpan ke DB:
        penilaianRepository.save(penilaian);

        return new ApiResponse<>(true, "Penilaian berhasil disimpan. Nilai Akhir: " + String.format("%.2f", nilaiAkhir), penilaian);
    }
}