package com.donorlog.backend.controller;

import com.donorlog.backend.dto.ApiResponse;
import com.donorlog.backend.dto.DonorProfileDto;
import com.donorlog.backend.service.DonorProfileService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/donor/profile")
public class DonorProfileController {

    private final DonorProfileService profileService;

    public DonorProfileController(DonorProfileService profileService) {
        this.profileService = profileService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<DonorProfileDto>> getProfile(
            @RequestParam(defaultValue = "default_user") String userId) {
        DonorProfileDto profile = profileService.getProfile(userId);
        return ResponseEntity.ok(ApiResponse.ok(profile));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<DonorProfileDto>> saveOrUpdate(@RequestBody DonorProfileDto dto) {
        if (dto.getUserId() == null || dto.getUserId().trim().isEmpty()) {
            dto.setUserId("default_user");
        }
        DonorProfileDto saved = profileService.saveOrUpdate(dto);
        return ResponseEntity.ok(ApiResponse.ok("Профиль донора обновлен", saved));
    }
}
