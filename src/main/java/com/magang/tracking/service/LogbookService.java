package com.magang.tracking.service;

import com.magang.tracking.dto.request.LogbookRequest;
import com.magang.tracking.dto.request.VerifikasiRequest;
import com.magang.tracking.dto.response.ApiResponse;
import org.springframework.web.multipart.MultipartFile;

public interface LogbookService {
    ApiResponse<?> inputLogbook(LogbookRequest request, MultipartFile dokumentasi);
    ApiResponse<?> verifikasiLogbook(Long logbookId, VerifikasiRequest request);
}