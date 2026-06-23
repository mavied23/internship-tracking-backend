package com.magang.tracking.service;

import com.magang.tracking.dto.request.MonitoringRequest;
import com.magang.tracking.dto.response.ApiResponse;

public interface MonitoringService {
    ApiResponse<?> inputMonitoring(MonitoringRequest request);
}