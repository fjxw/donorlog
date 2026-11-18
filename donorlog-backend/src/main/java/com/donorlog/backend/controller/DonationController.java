package com.donorlog.backend.controller;

import com.donorlog.backend.dto.ApiResponse;
import com.donorlog.backend.dto.DonationDto;
import com.donorlog.backend.service.DonationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/v1/donations")
public class DonationController {

    private final DonationService donationService;

    public DonationController(DonationService donationService) {
        this.donationService = donationService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<DonationDto>>> getAll(
            @RequestParam(required = false) String userId) {
        String effectiveUserId = (userId != null && !userId.trim().isEmpty())
                ? userId
                : "alexey.ryakhovsky@example.com";
        List<DonationDto> list = donationService.getAllForUser(effectiveUserId);
        return ResponseEntity.ok(ApiResponse.ok(list));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DonationDto>> getById(@PathVariable String id) {
        DonationDto dto = donationService.getById(id);
        if (dto != null && !Boolean.TRUE.equals(dto.getIsDeleted())) {
            return ResponseEntity.ok(ApiResponse.ok(dto));
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<ApiResponse<DonationDto>> saveOrUpdate(@RequestBody DonationDto dto) {
        DonationDto saved = donationService.saveOrUpdate(dto);
        return ResponseEntity.ok(ApiResponse.ok("Донация успешно сохранена", saved));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable String id) {
        boolean deleted = donationService.delete(id);
        if (deleted) {
            return ResponseEntity.ok(ApiResponse.ok("Донация удалена", null));
        }
        return ResponseEntity.notFound().build();
    }
}
