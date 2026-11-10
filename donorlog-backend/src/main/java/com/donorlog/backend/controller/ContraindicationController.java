package com.donorlog.backend.controller;

import com.donorlog.backend.dto.ApiResponse;
import com.donorlog.backend.dto.ContraindicationDto;
import com.donorlog.backend.entity.ContraindicationEntity;
import com.donorlog.backend.repository.ContraindicationRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/contraindications")
public class ContraindicationController {

    private final ContraindicationRepository repository;

    public ContraindicationController(ContraindicationRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ContraindicationDto>>> getAll(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String query) {

        List<ContraindicationEntity> entities;
        if (query != null && !query.trim().isEmpty()) {
            entities = repository.findByTitleContainingIgnoreCase(query.trim());
        } else if (category != null && !category.trim().isEmpty()) {
            entities = repository.findByCategory(category.trim().toUpperCase());
        } else {
            entities = repository.findAll();
        }

        List<ContraindicationDto> dtos = entities.stream()
                .map(e -> new ContraindicationDto(e.getId(), e.getTitle(), e.getCategory(), e.getDeferralDays(), e.getDescription(), e.getRecommendation()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.ok("Справочник противопоказаний загружен", dtos));
    }

    @GetMapping("/calculate")
    public ResponseEntity<ApiResponse<Map<String, Object>>> calculateDeferral(
            @RequestParam Long id,
            @RequestParam Long eventDateMillis) {

        return repository.findById(id).map(entity -> {
            Map<String, Object> result = new HashMap<>();
            result.put("contraindicationId", entity.getId());
            result.put("title", entity.getTitle());
            result.put("category", entity.getCategory());
            result.put("deferralDays", entity.getDeferralDays());

            if ("ABSOLUTE".equalsIgnoreCase(entity.getCategory())) {
                result.put("isEligible", false);
                result.put("eligibleDateMillis", null);
                result.put("daysRemaining", -1);
                result.put("message", "Абсолютное противопоказание к донорству крови и ее компонентов");
            } else {
                long deferralMillis = TimeUnit.DAYS.toMillis(entity.getDeferralDays());
                long eligibleMillis = eventDateMillis + deferralMillis;
                long now = System.currentTimeMillis();
                boolean isEligible = now >= eligibleMillis;
                long daysRemaining = isEligible ? 0 : (long) Math.ceil((double) (eligibleMillis - now) / TimeUnit.DAYS.toMillis(1));

                result.put("isEligible", isEligible);
                result.put("eligibleDateMillis", eligibleMillis);
                result.put("daysRemaining", daysRemaining);
                result.put("message", isEligible ? "Срок отвода истек. Допуск к донации разрешен"
                        : "Срок медицинского отвода: осталось " + daysRemaining + " дн.");
            }
            return ResponseEntity.ok(ApiResponse.ok("Расчет завершен", result));
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }
}
