package com.snorlaexes.raem.domain.sleep;

import com.snorlaexes.raem.domain.sleep.Enums.BadAwakeReason;
import com.snorlaexes.raem.domain.sleep.entities.*;
import com.snorlaexes.raem.domain.sleep.repository.LearningDataRepository;
import com.snorlaexes.raem.domain.sleep.repository.SleepDataRepository;
import com.snorlaexes.raem.domain.sleep.repository.SleepDataUrlRepository;
import com.snorlaexes.raem.domain.user.UserEntity;
import com.snorlaexes.raem.domain.user.UserRepository;
import com.snorlaexes.raem.global.apiPayload.code.status.ErrorStatus;
import com.snorlaexes.raem.global.apiPayload.exception.ExceptionHandler;
import com.snorlaexes.raem.global.aws.s3.AmazonS3Manager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Transactional
@RequiredArgsConstructor
public class SleepService {
    private final SleepDataUrlRepository sleepDataUrlRepository;
    private final UserRepository userRepository;
    private final SleepDataRepository sleepDataRepository;
    private final AmazonS3Manager s3Manager;
    private final LearningDataRepository learningDataRepository;

    @Transactional
    public SleepDataUrlEntity sendS3AndSave(String userId, LocalDate sleptAt, MultipartFile file) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ExceptionHandler(ErrorStatus._USER_NOT_FOUND));

        // 확장자 검사
        assert file != null;
        String filename = file.getOriginalFilename();
        assert filename != null;
        if (!Objects.equals(filename.split("\\.")[1], "csv")) {
            throw new ExceptionHandler(ErrorStatus._WRONG_EXTENSION);
        }

        // 파일 사이즈 검사
        if (file.getSize() == 0) {
            throw new ExceptionHandler(ErrorStatus._DATA_SIZE_TOO_SMALL);
        }

        // S3 저장 및 url 저장
        String keyName = "personal/" + userId + "/" + sleptAt;
        String s3Url = s3Manager.uploadFile(keyName, file);

        SleepDataUrlEntity newEntity = SleepDataUrlEntity.builder()
                .url(s3Url)
                .user(user)
                .createdAt(LocalDateTime.now())
                .build();

        return sleepDataUrlRepository.save(newEntity);
    }

    @Transactional
    public SleepDataEntity organizeSleepData(String userId, SleepReqDTO.SaveDataDTO req) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ExceptionHandler(ErrorStatus._USER_NOT_FOUND));

        SleepReqDTO.TotalSleepDataChild totalSleepData = req.getTotalSleepData();
        List<InBedEntity> inBedList = totalSleepData.getInBed();
        List<SleepDataChildEntity> sleepDataChildList = totalSleepData.getSleepData();

        // In-Bed 누적 시간 계산
        Duration inBedDuration = Duration.ZERO;
        for (InBedEntity data : inBedList) {
            inBedDuration = inBedDuration.plus(Duration.between(data.getStartTime(), data.getEndTime()));
        }
        String inBedTime = timeFormatter(inBedDuration);

        // awake, sleep 누적 시간 계산
        Duration awakeDuration = Duration.ZERO;
        Duration sleepDuration = Duration.ZERO;
        for (SleepDataChildEntity data : sleepDataChildList) {
            if (data.getState().equals("Awake")) {
                awakeDuration = awakeDuration.plus(Duration.between(data.getStartTime(), data.getEndTime()));
            } else {
                sleepDuration = sleepDuration.plus(Duration.between(data.getStartTime(), data.getEndTime()));
            }
        }
        String awakeTime = timeFormatter(awakeDuration);
        String sleepTime = timeFormatter(sleepDuration);

        SleepDataEntity newSleepData = SleepDataEntity.builder()
                .sleptAt(req.getSleptAt())
                .score(req.getScore())
                .awakeTime(req.getAwakeAt())
                .timeOnBed(inBedTime)
                .sleepTime(awakeTime)
                .fellAsleepTime(sleepTime)
                .user(user)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        return sleepDataRepository.save(newSleepData);
    }

    private String timeFormatter(Duration duration) {
        long hours = duration.toHours();
        long minutes = duration.toMinutes() % 60;
        long seconds = duration.getSeconds() % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    @Transactional
    public SleepDataEntity saveReasonService(SleepReqDTO.SaveReasonDTO req) {
        BadAwakeReason reason = BadAwakeReason.valueOf(req.getReason().toUpperCase());
        SleepDataEntity sleepData = sleepDataRepository.findById(req.getSleepDataId())
                .orElseThrow(() -> new ExceptionHandler(ErrorStatus._SLEEP_DATA_NOT_FOUND));

        sleepData.setBadAwakeReason(reason);
        sleepData.setUpdatedAt(LocalDateTime.now());

        return sleepDataRepository.save(sleepData);
    }

    @Transactional
    public SleepDataUrlEntity getDataUrlService(SleepReqDTO.GetDataUrlDTO req) {
        return sleepDataUrlRepository.findById(req.getDataId())
                .orElseThrow(() -> new ExceptionHandler(ErrorStatus._URL_NOT_FOUND));
    }

    @Transactional
    public LearningDataEntity tempDataSave(String tag, LocalDate sleptAt, MultipartFile file) {
        // 확장자 검사
        assert file != null;
        String filename = file.getOriginalFilename();
        assert filename != null;
        if (!Objects.equals(filename.split("\\.")[1], "csv")) {
            throw new ExceptionHandler(ErrorStatus._WRONG_EXTENSION);
        }

        // 파일 사이즈 검사
        if (file.getSize() == 0) {
            throw new ExceptionHandler(ErrorStatus._DATA_SIZE_TOO_SMALL);
        }

        // S3 저장 및 url 저장
        String keyName = "learn/" + tag + "/" + sleptAt;
        String s3Url = s3Manager.uploadFile(keyName, file);

        LearningDataEntity newEntity = LearningDataEntity.builder()
                .url(s3Url)
                .tag(tag)
                .createdAt(LocalDateTime.now())
                .build();

        return learningDataRepository.save(newEntity);
    }
}
