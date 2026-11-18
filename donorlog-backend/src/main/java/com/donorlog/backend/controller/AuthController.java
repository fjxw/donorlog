package com.donorlog.backend.controller;

import com.donorlog.backend.dto.ApiResponse;
import com.donorlog.backend.entity.DonorProfileEntity;
import com.donorlog.backend.entity.UserEntity;
import com.donorlog.backend.repository.DonorProfileRepository;
import com.donorlog.backend.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final DonorProfileRepository profileRepository;

    public AuthController(UserRepository userRepository, DonorProfileRepository profileRepository) {
        this.userRepository = userRepository;
        this.profileRepository = profileRepository;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserEntity>> register(@RequestBody Map<String, String> body) {
        String email = body.getOrDefault("email", "").trim().toLowerCase();
        String fullName = body.getOrDefault("fullName", "").trim();
        String password = body.getOrDefault("password", "");
        String bloodGroup = body.getOrDefault("bloodGroup", "A(II)");
        String rhFactor = body.getOrDefault("rhFactor", "Rh+");
        String region = body.getOrDefault("region", "г. Москва");

        if (email.isEmpty()) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Email обязателен"));
        }
        if (fullName.isEmpty()) {
            fullName = email.split("@")[0];
        }

        // 1. Сохраняем в таблице users
        UserEntity user = userRepository.findByEmail(email).orElseGet(UserEntity::new);
        user.setEmail(email);
        user.setFullName(fullName);
        user.setBloodGroup(bloodGroup);
        user.setRhFactor(rhFactor);
        user.setRegion(region);
        user.setPassword(password);
        if (user.getCreatedAt() == null) {
            user.setCreatedAt(System.currentTimeMillis());
        }
        UserEntity savedUser = userRepository.save(user);

        // 2. Синхронизируем с таблицей donor_profiles
        DonorProfileEntity profile = profileRepository.findByUserId(email).orElseGet(DonorProfileEntity::new);
        profile.setUserId(email);
        profile.setFullName(fullName);
        profile.setBloodGroup(bloodGroup);
        profile.setRhFactor(rhFactor);
        profile.setUpdatedAt(System.currentTimeMillis());
        profileRepository.save(profile);

        return ResponseEntity.ok(ApiResponse.ok("Пользователь успешно зарегистрирован", savedUser));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<UserEntity>> login(@RequestBody Map<String, String> body) {
        String email = body.getOrDefault("email", "").trim().toLowerCase();
        String password = body.getOrDefault("password", "");

        if (email.isEmpty()) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Email обязателен"));
        }

        Optional<UserEntity> opt = userRepository.findByEmail(email);
        if (opt.isPresent()) {
            UserEntity user = opt.get();
            return ResponseEntity.ok(ApiResponse.ok("Успешный вход", user));
        }

        // Если пользователя еще нет в БД (первый вход) - создаем запись с переданным email
        String derivedName = email.split("@")[0];
        if (derivedName.length() > 1) {
            derivedName = Character.toUpperCase(derivedName.charAt(0)) + derivedName.substring(1);
        }
        String defaultFull = derivedName + " Донор";

        UserEntity newUser = new UserEntity(email, defaultFull, "A(II)", "Rh+", "г. Москва", System.currentTimeMillis());
        newUser.setPassword(password);
        UserEntity saved = userRepository.save(newUser);

        DonorProfileEntity profile = new DonorProfileEntity();
        profile.setUserId(email);
        profile.setFullName(defaultFull);
        profile.setBloodGroup("A(II)");
        profile.setRhFactor("Rh+");
        profile.setUpdatedAt(System.currentTimeMillis());
        profileRepository.save(profile);

        return ResponseEntity.ok(ApiResponse.ok("Успешный вход (создан новый профиль)", saved));
    }

    @GetMapping("/users")
    public ResponseEntity<ApiResponse<List<UserEntity>>> listUsers() {
        return ResponseEntity.ok(ApiResponse.ok(userRepository.findAll()));
    }
}
