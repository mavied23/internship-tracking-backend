package com.magang.tracking.service.impl;

import com.magang.tracking.dto.request.MonitoringRequest;
import com.magang.tracking.dto.response.ApiResponse;
import com.magang.tracking.service.MonitoringService;
import org.springframework.stereotype.Service;

@Service
public class MonitoringServiceImpl implements MonitoringService {

    @Override
    public ApiResponse<?> inputMonitoring(MonitoringRequest request) {
        // Simpan evaluasi dan catatan dosen pembimbing ke MonitoringRepository
        return new ApiResponse<>(true, "Data monitoring evaluasi berhasil disimpan");
    }
}