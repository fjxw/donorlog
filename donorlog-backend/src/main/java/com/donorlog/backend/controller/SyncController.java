package com.donorlog.backend.controller;

import com.donorlog.backend.dto.ApiResponse;
import com.donorlog.backend.dto.SyncRequestDto;
import com.donorlog.backend.dto.SyncResponseDto;
import com.donorlog.backend.service.SyncService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/sync")
public class SyncController {

    private final SyncService syncService;

    public SyncController(SyncService syncService) {
        this.syncService = syncService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<SyncResponseDto>> sync(@RequestBody SyncRequestDto request) {
        SyncResponseDto response = syncService.processSync(request);
        return ResponseEntity.ok(ApiResponse.ok("Синхронизация успешно выполнена", response));
    }
}
