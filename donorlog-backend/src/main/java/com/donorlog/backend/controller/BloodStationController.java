package com.donorlog.backend.controller;

import com.donorlog.backend.dto.ApiResponse;
import com.donorlog.backend.dto.BloodStationDto;
import com.donorlog.backend.service.BloodStationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/v1/stations")
public class BloodStationController {

    private final BloodStationService stationService;

    public BloodStationController(BloodStationService stationService) {
        this.stationService = stationService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<BloodStationDto>>> getAll() {
        List<BloodStationDto> list = stationService.getAll();
        return ResponseEntity.ok(ApiResponse.ok(list));
    }
}
