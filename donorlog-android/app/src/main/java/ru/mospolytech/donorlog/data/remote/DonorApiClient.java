package ru.mospolytech.donorlog.data.remote;

import android.os.Handler;
import android.os.Looper;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import ru.mospolytech.donorlog.data.model.BloodStationEntity;
import ru.mospolytech.donorlog.data.model.DonationEntity;

public class DonorApiClient {

    private static final String BASE_URL = "http://10.0.2.2:8080/api/v1";
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    private final OkHttpClient client;
    private final Gson gson;
    private final Handler mainHandler;

    public interface ApiCallback<T> {
        void onSuccess(T result);
        void onError(String message);
    }

    public DonorApiClient() {
        this.client = new OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .readTimeout(5, TimeUnit.SECONDS)
                .build();
        this.gson = new Gson();
        this.mainHandler = new Handler(Looper.getMainLooper());
    }

    public void checkHealth(ApiCallback<Boolean> callback) {
        Request request = new Request.Builder()
                .url(BASE_URL + "/health")
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                mainHandler.post(() -> callback.onError(e.getMessage()));
            }

            @Override
            public void onResponse(Call call, Response response) {
                boolean isUp = response.isSuccessful();
                mainHandler.post(() -> callback.onSuccess(isUp));
            }
        });
    }

    public void fetchBloodStations(ApiCallback<List<BloodStationEntity>> callback) {
        Request request = new Request.Builder()
                .url(BASE_URL + "/stations")
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                mainHandler.post(() -> callback.onError(e.getMessage()));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful() || response.body() == null) {
                    mainHandler.post(() -> callback.onError("Ошибка сервера: " + response.code()));
                    return;
                }

                String responseData = response.body().string();
                try {
                    Type mapType = new TypeToken<Map<String, Object>>() {}.getType();
                    Map<String, Object> map = gson.fromJson(responseData, mapType);
                    Object dataObj = map.get("data");
                    String dataJson = gson.toJson(dataObj);

                    Type listType = new TypeToken<List<BloodStationDto>>() {}.getType();
                    List<BloodStationDto> dtos = gson.fromJson(dataJson, listType);

                    List<BloodStationEntity> entities = new ArrayList<>();
                    if (dtos != null) {
                        for (BloodStationDto dto : dtos) {
                            entities.add(new BloodStationEntity(
                                    dto.stationName != null ? dto.stationName : "",
                                    dto.city != null ? dto.city : "",
                                    dto.address != null ? dto.address : "",
                                    dto.phone != null ? dto.phone : "",
                                    dto.schedule != null ? dto.schedule : "",
                                    dto.needStatus != null ? dto.needStatus : "GREEN",
                                    dto.oPosStatus != null ? dto.oPosStatus : "GREEN",
                                    dto.oNegStatus != null ? dto.oNegStatus : "YELLOW",
                                    dto.aPosStatus != null ? dto.aPosStatus : "GREEN",
                                    dto.aNegStatus != null ? dto.aNegStatus : "RED",
                                    dto.bPosStatus != null ? dto.bPosStatus : "YELLOW",
                                    dto.bNegStatus != null ? dto.bNegStatus : "GREEN",
                                    dto.abPosStatus != null ? dto.abPosStatus : "GREEN",
                                    dto.abNegStatus != null ? dto.abNegStatus : "YELLOW"
                            ));
                        }
                    }
                    mainHandler.post(() -> callback.onSuccess(entities));
                } catch (Exception ex) {
                    mainHandler.post(() -> callback.onError("Ошибка обработки ответа: " + ex.getMessage()));
                }
            }
        });
    }

    public void syncDonations(List<DonationEntity> localDonations, ApiCallback<Integer> callback) {
        Map<String, Object> syncPayload = new HashMap<>();
        syncPayload.put("donorId", 1);
        syncPayload.put("clientTimestamp", System.currentTimeMillis());

        List<Map<String, Object>> donationList = new ArrayList<>();
        if (localDonations != null) {
            for (DonationEntity d : localDonations) {
                Map<String, Object> item = new HashMap<>();
                item.put("clientUuid", "local-" + d.getId());
                item.put("componentType", d.getComponentType());
                item.put("donationDate", d.getDonationDate());
                item.put("volumeMl", d.getVolumeMl());
                item.put("stationName", d.getStationName());
                item.put("hemoglobin", d.getHemoglobin());
                item.put("systolicBp", d.getSystolicBp());
                item.put("diastolicBp", d.getDiastolicBp());
                item.put("pulse", d.getPulse());
                item.put("gratuitous", d.isGratuitous());
                item.put("notes", d.getNotes());
                item.put("updatedAt", d.getUpdatedAt());
                donationList.add(item);
            }
        }
        syncPayload.put("donations", donationList);

        String jsonBody = gson.toJson(syncPayload);
        RequestBody body = RequestBody.create(jsonBody, JSON);

        Request request = new Request.Builder()
                .url(BASE_URL + "/sync")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                mainHandler.post(() -> callback.onError(e.getMessage()));
            }

            @Override
            public void onResponse(Call call, Response response) {
                if (response.isSuccessful()) {
                    mainHandler.post(() -> callback.onSuccess(localDonations != null ? localDonations.size() : 0));
                } else {
                    mainHandler.post(() -> callback.onError("Ошибка синхронизации: " + response.code()));
                }
            }
        });
    }

    private static class BloodStationDto {
        public Long id;
        public String stationName;
        public String city;
        public String address;
        public String phone;
        public String schedule;
        public String needStatus;
        public String oPosStatus;
        public String oNegStatus;
        public String aPosStatus;
        public String aNegStatus;
        public String bPosStatus;
        public String bNegStatus;
        public String abPosStatus;
        public String abNegStatus;
    }
}
