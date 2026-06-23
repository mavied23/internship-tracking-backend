package com.magang.tracking.service;

import com.magang.tracking.dto.request.PenilaianRequest;
import com.magang.tracking.dto.response.ApiResponse;

public interface PenilaianService {
    ApiResponse<?> inputPenilaian(PenilaianRequest request);
}