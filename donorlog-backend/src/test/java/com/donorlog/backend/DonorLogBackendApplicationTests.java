package com.donorlog.backend;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class DonorLogBackendApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testHealthEndpoint() throws Exception {
        mockMvc.perform(get("/api/v1/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("UP"));
    }

    @Test
    public void testStationsEndpoint() throws Exception {
        mockMvc.perform(get("/api/v1/stations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    public void testDonationsEndpoint() throws Exception {
        mockMvc.perform(get("/api/v1/donations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    public void testContraindicationsEndpoint() throws Exception {
        mockMvc.perform(get("/api/v1/contraindications"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    public void testSyncEndpoint() throws Exception {
        String jsonPayload = """
                {
                    "userId": "DONOR-77-231331",
                    "lastSyncTimestamp": 0,
                    "dirtyDonations": [
                        {
                            "id": "don-test-1",
                            "userId": "DONOR-77-231331",
                            "componentType": "WHOLE_BLOOD",
                            "donationDate": "2026-10-15",
                            "volumeMl": 450,
                            "stationName": "Центр крови Гаврилова",
                            "hemoglobinGramsPerLiter": 142,
                            "systolicBp": 120,
                            "diastolicBp": 80,
                            "pulseBpm": 72,
                            "wellbeing": "Отличное",
                            "notes": "Тестовая донация для синхронизации",
                            "updatedAt": 1793500000000,
                            "isDeleted": false
                        }
                    ]
                }
                """;

        mockMvc.perform(post("/api/v1/sync")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }
}
