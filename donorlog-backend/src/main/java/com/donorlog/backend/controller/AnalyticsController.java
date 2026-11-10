package com.donorlog.backend.controller;

import com.donorlog.backend.dto.ApiResponse;
import com.donorlog.backend.entity.DonationEntity;
import com.donorlog.backend.repository.DonationRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/analytics")
public class AnalyticsController {

    private final DonationRepository donationRepository;

    public AnalyticsController(DonationRepository donationRepository) {
        this.donationRepository = donationRepository;
    }

    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getSummary() {
        List<DonationEntity> donations = donationRepository.findAll();

        int totalCount = donations.size();
        int totalVolumeMl = 0;
        int wholeBloodCount = 0;
        int plasmaCount = 0;
        int plateletsCount = 0;
        int sumHemoglobin = 0;
        int validHbCount = 0;
        int sumSystolic = 0;
        int sumDiastolic = 0;
        int validBpCount = 0;

        for (DonationEntity d : donations) {
            if (Boolean.TRUE.equals(d.getIsDeleted())) continue;

            if (d.getVolumeMl() != null) {
                totalVolumeMl += d.getVolumeMl();
            }
            String type = d.getComponentType() != null ? d.getComponentType().toUpperCase() : "";
            if (type.contains("WHOLE") || type.contains("КРОВЬ")) {
                wholeBloodCount++;
            } else if (type.contains("PLASMA") || type.contains("ПЛАЗМА")) {
                plasmaCount++;
            } else if (type.contains("PLATELET") || type.contains("ТРОМБО")) {
                plateletsCount++;
            }

            if (d.getHemoglobinGramsPerLiter() != null && d.getHemoglobinGramsPerLiter() > 50) {
                sumHemoglobin += d.getHemoglobinGramsPerLiter();
                validHbCount++;
            }

            if (d.getSystolicBp() != null && d.getDiastolicBp() != null && d.getSystolicBp() > 50) {
                sumSystolic += d.getSystolicBp();
                sumDiastolic += d.getDiastolicBp();
                validBpCount++;
            }
        }

        int estimatedLivesSaved = wholeBloodCount * 3 + plasmaCount;

        double avgHb = validHbCount > 0 ? Math.round((sumHemoglobin / (double) validHbCount) * 10.0) / 10.0 : 140.0;
        int avgSys = validBpCount > 0 ? sumSystolic / validBpCount : 120;
        int avgDia = validBpCount > 0 ? sumDiastolic / validBpCount : 80;

        int honoredPercent = Math.min(100, (wholeBloodCount * 100) / 40);

        Map<String, Object> data = new HashMap<>();
        data.put("totalDonations", totalCount);
        data.put("totalVolumeMl", totalVolumeMl);
        data.put("totalVolumeLiters", Math.round((totalVolumeMl / 1000.0) * 100.0) / 100.0);
        data.put("estimatedLivesSaved", estimatedLivesSaved);
        data.put("wholeBloodCount", wholeBloodCount);
        data.put("plasmaCount", plasmaCount);
        data.put("plateletsCount", plateletsCount);
        data.put("averageHemoglobin", avgHb);
        data.put("averageBloodPressure", avgSys + "/" + avgDia);
        data.put("honoredPercent", honoredPercent);

        return ResponseEntity.ok(ApiResponse.ok("Аналитика донорской активности рассчитана", data));
    }
}
