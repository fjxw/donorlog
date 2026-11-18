package com.donorlog.backend.config;

import com.donorlog.backend.entity.BloodStationEntity;
import com.donorlog.backend.entity.ContraindicationEntity;
import com.donorlog.backend.entity.DonationEntity;
import com.donorlog.backend.entity.DonorProfileEntity;
import com.donorlog.backend.entity.UserEntity;
import com.donorlog.backend.repository.BloodStationRepository;
import com.donorlog.backend.repository.ContraindicationRepository;
import com.donorlog.backend.repository.DonationRepository;
import com.donorlog.backend.repository.DonorProfileRepository;
import com.donorlog.backend.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.UUID;

@Configuration
public class DataInitializer implements CommandLineRunner {

    private final BloodStationRepository stationRepository;
    private final DonationRepository donationRepository;
    private final DonorProfileRepository profileRepository;
    private final ContraindicationRepository contraindicationRepository;
    private final UserRepository userRepository;

    public DataInitializer(BloodStationRepository stationRepository,
                           DonationRepository donationRepository,
                           DonorProfileRepository profileRepository,
                           ContraindicationRepository contraindicationRepository,
                           UserRepository userRepository) {
        this.stationRepository = stationRepository;
        this.donationRepository = donationRepository;
        this.profileRepository = profileRepository;
        this.contraindicationRepository = contraindicationRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) {
        long now = System.currentTimeMillis();

        if (userRepository.count() == 0) {
            userRepository.save(new UserEntity(
                    "alexey.ryakhovsky@example.com",
                    "Ряховский Алексей Александрович",
                    "A(II)",
                    "Rh+",
                    "г. Москва",
                    now
            ));
        }

        if (profileRepository.count() == 0) {
            DonorProfileEntity profile = new DonorProfileEntity();
            profile.setUserId("default_user");
            profile.setFullName("Ряховский Алексей Александрович");
            profile.setBloodGroup("A(II)");
            profile.setRhFactor("+");
            profile.setKellFactor("Kell-");
            profile.setGender("MALE");
            profile.setBirthDate(LocalDate.of(2004, 5, 15));
            profile.setUpdatedAt(now);
            profileRepository.save(profile);
        }

        if (stationRepository.count() == 0) {
            BloodStationEntity s1 = new BloodStationEntity(
                    "station-polikarpova",
                    "Центр крови имени О.К. Гаврилова (Поликарпова)",
                    "Москва",
                    "ул. Поликарпова, д. 14, корп. 2",
                    "+7 (495) 945-71-66",
                    "Пн-Вс: 08:00 - 13:30",
                    "{\"O_POS\":\"GREEN\",\"O_NEG\":\"RED\",\"A_POS\":\"YELLOW\",\"A_NEG\":\"RED\",\"B_POS\":\"GREEN\",\"B_NEG\":\"YELLOW\",\"AB_POS\":\"GREEN\",\"AB_NEG\":\"GREEN\"}",
                    now
            );

            BloodStationEntity s2 = new BloodStationEntity(
                    "station-tsaritsyno",
                    "Центр крови имени О.К. Гаврилова (Царицыно)",
                    "Москва",
                    "ул. Бакинская, д. 31",
                    "+7 (495) 326-98-36",
                    "Пн-Вс: 08:00 - 13:30",
                    "{\"O_POS\":\"GREEN\",\"O_NEG\":\"YELLOW\",\"A_POS\":\"GREEN\",\"A_NEG\":\"YELLOW\",\"B_POS\":\"GREEN\",\"B_NEG\":\"GREEN\",\"AB_POS\":\"GREEN\",\"AB_NEG\":\"GREEN\"}",
                    now
            );

            BloodStationEntity s3 = new BloodStationEntity(
                    "station-nmic",
                    "НМИЦ гематологии Минздрава России",
                    "Москва",
                    "Новый Зыковский проезд, д. 4",
                    "+7 (495) 612-35-33",
                    "Пн-Пт: 09:00 - 14:00",
                    "{\"O_POS\":\"YELLOW\",\"O_NEG\":\"RED\",\"A_POS\":\"RED\",\"A_NEG\":\"RED\",\"B_POS\":\"YELLOW\",\"B_NEG\":\"RED\",\"AB_POS\":\"GREEN\",\"AB_NEG\":\"RED\"}",
                    now
            );

            BloodStationEntity s4 = new BloodStationEntity(
                    "station-fmba",
                    "Центр крови ФМБА России",
                    "Москва",
                    "ул. Щукинская, д. 6, корп. 2",
                    "+7 (499) 190-60-25",
                    "Пн-Пт: 08:30 - 12:30",
                    "{\"O_POS\":\"GREEN\",\"O_NEG\":\"RED\",\"A_POS\":\"GREEN\",\"A_NEG\":\"YELLOW\",\"B_POS\":\"RED\",\"B_NEG\":\"YELLOW\",\"AB_POS\":\"GREEN\",\"AB_NEG\":\"GREEN\"}",
                    now
            );

            BloodStationEntity s5 = new BloodStationEntity(
                    "station-spb",
                    "Городская станция переливания крови (СПб)",
                    "Санкт-Петербург",
                    "Московский пр-т, д. 104",
                    "+7 (812) 635-68-85",
                    "Пн-Пт: 09:00 - 13:00",
                    "{\"O_POS\":\"GREEN\",\"O_NEG\":\"GREEN\",\"A_POS\":\"YELLOW\",\"A_NEG\":\"YELLOW\",\"B_POS\":\"GREEN\",\"B_NEG\":\"GREEN\",\"AB_POS\":\"GREEN\",\"AB_NEG\":\"GREEN\"}",
                    now
            );

            stationRepository.saveAll(Arrays.asList(s1, s2, s3, s4, s5));
        }

        if (donationRepository.count() == 0) {
            DonationEntity d1 = new DonationEntity(
                    UUID.randomUUID().toString(),
                    "alexey.ryakhovsky@example.com",
                    LocalDate.now().minusDays(65),
                    "WHOLE_BLOOD",
                    450,
                    "Центр крови им. О.К. Гаврилова (Поликарпова)",
                    142,
                    120,
                    80,
                    72,
                    "GOOD",
                    "Плановая безвозмездная сдача цельной крови. Самочувствие удовлетворительное.",
                    now,
                    false
            );

            DonationEntity d2 = new DonationEntity(
                    UUID.randomUUID().toString(),
                    "alexey.ryakhovsky@example.com",
                    LocalDate.now().minusDays(130),
                    "WHOLE_BLOOD",
                    450,
                    "Центр крови им. О.К. Гаврилова (Царицыно)",
                    138,
                    118,
                    78,
                    70,
                    "GOOD",
                    "Донация цельной крови. Осмотр терапевта пройден без замечаний.",
                    now,
                    false
            );

            DonationEntity d3 = new DonationEntity(
                    UUID.randomUUID().toString(),
                    "alexey.ryakhovsky@example.com",
                    LocalDate.now().minusDays(200),
                    "PLASMA",
                    600,
                    "НМИЦ гематологии Минздрава России",
                    140,
                    122,
                    82,
                    74,
                    "GOOD",
                    "Аппаратный плазмаферез. Процедура прошла в штатном режиме.",
                    now,
                    false
            );

            donationRepository.saveAll(Arrays.asList(d1, d2, d3));
        }

        if (contraindicationRepository.count() == 0) {
            contraindicationRepository.saveAll(Arrays.asList(
                    new ContraindicationEntity(
                            "Татуировки, пирсинг, перманентный макияж",
                            "TEMPORARY",
                            120,
                            "Нанесение татуировок, проведение пирсинга, татуажа или иглоукалывания.",
                            "Отвод составляет 120 календарных дней с момента завершения процедуры."
                    ),
                    new ContraindicationEntity(
                            "Оперативные хирургические вмешательства",
                            "TEMPORARY",
                            120,
                            "Любые плановые или экстренные полостные или эндоскопические операции.",
                            "Отвод 120 дней со дня проведения вмешательства при отсутствии осложнений."
                    ),
                    new ContraindicationEntity(
                            "ОРВИ, грипп, ангина, инфекции дыхательных путей",
                            "TEMPORARY",
                            30,
                            "Острые респираторные вирусные инфекции и воспалительные заболевания носоглотки.",
                            "Отвод 30 календарных дней после полного клинического выздоровления."
                    ),
                    new ContraindicationEntity(
                            "Удаление зуба (экстракция)",
                            "TEMPORARY",
                            10,
                            "Хирургическое удаление одного или нескольких зубов.",
                            "Отвод 10 календарных дней со дня стоматологической операции."
                    ),
                    new ContraindicationEntity(
                            "Прием антибиотиков",
                            "TEMPORARY",
                            14,
                            "Курсовое лечение антибактериальными препаратами любой группы.",
                            "Отвод 14 календарных дней после приема последней дозы препарата."
                    ),
                    new ContraindicationEntity(
                            "Прием анальгетиков и салицилатов (аспирин, ибупрофен)",
                            "TEMPORARY",
                            3,
                            "Прием обезболивающих и нестероидных противовоспалительных средств.",
                            "Отвод 3 календарных дня (72 часа) после последнего приема."
                    ),
                    new ContraindicationEntity(
                            "Вакцинация инактивированными вакцинами",
                            "TEMPORARY",
                            10,
                            "Прививки от гриппа, столбняка, дифтерии, гепатита B, полиомиелита (убитые).",
                            "Отвод 10 дней со дня вакцинации при отсутствии температурной реакции."
                    ),
                    new ContraindicationEntity(
                            "Вакцинация живыми вакцинами",
                            "TEMPORARY",
                            30,
                            "Прививки от кори, краснухи, паротита, БЦЖ, желтой лихорадки.",
                            "Отвод 30 дней со дня вакцинации."
                    ),
                    new ContraindicationEntity(
                            "Контакт с больными гепатитами B или C",
                            "TEMPORARY",
                            120,
                            "Бытовой или профессиональный контакт с вирусоносителем гепатита.",
                            "Отвод 120 дней с момента прекращения контакта."
                    ),
                    new ContraindicationEntity(
                            "Инфекция ВИЧ и СПИД",
                            "ABSOLUTE",
                            0,
                            "Вирус иммунодефицита человека, бессимптомная или манифестная стадия.",
                            "Абсолютный бессрочный запрет на донорство крови и ее компонентов."
                    ),
                    new ContraindicationEntity(
                            "Вирусные гепатиты B и C",
                            "ABSOLUTE",
                            0,
                            "Острые и хронические гепатиты, носительство антигенов HBsAg или HCV.",
                            "Абсолютный бессрочный отвод от донорства независимо от давности заболевания."
                    ),
                    new ContraindicationEntity(
                            "Туберкулез всех форм",
                            "ABSOLUTE",
                            0,
                            "Активный или перенесенный туберкулез любой локализации.",
                            "Абсолютное противопоказание к донорству."
                    ),
                    new ContraindicationEntity(
                            "Злокачественные новообразования (онкология)",
                            "ABSOLUTE",
                            0,
                            "Опухоли любой локализации независимо от стадии и радикальности лечения.",
                            "Абсолютное противопоказание к донорству."
                    )
            ));
        }
    }
}
