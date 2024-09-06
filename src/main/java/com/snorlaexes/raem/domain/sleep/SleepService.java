package com.snorlaexes.raem.domain.sleep;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.snorlaexes.raem.domain.OpenAI.GPTService;
import com.snorlaexes.raem.domain.sleep.Enums.BadAwakeReason;
import com.snorlaexes.raem.domain.sleep.Enums.Range;
import com.snorlaexes.raem.domain.sleep.entities.*;
import com.snorlaexes.raem.domain.sleep.repository.*;
import com.snorlaexes.raem.domain.user.UserEntity;
import com.snorlaexes.raem.domain.user.UserRepository;
import com.snorlaexes.raem.global.apiPayload.code.status.ErrorStatus;
import com.snorlaexes.raem.global.apiPayload.exception.ExceptionHandler;
import com.snorlaexes.raem.global.aws.s3.AmazonS3Manager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class SleepService {
    private final SleepDataUrlRepository sleepDataUrlRepository;
    private final UserRepository userRepository;
    private final SleepDataRepository sleepDataRepository;
    private final AmazonS3Manager s3Manager;
    private final LearningDataRepository learningDataRepository;
    private final AnalysisDataRepository analysisDataRepository;
    private final InsightRepository insightRepository;
    private final GPTService gptService;
    private final ObjectMapper objectMapper;

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

        LocalDateTime sleptAt = req.getSleptAt();
        LocalDate sleptDate = sleptAt.toLocalDate();
        LocalTime sleptTime = sleptAt.toLocalTime();

        //잠들기까지 걸린 시간 계산
        Duration fellAsleepDuration = Duration.between(sleptTime, req.getFellAsleepAt());
        LocalTime fellAsleepTime = LocalTime.of(0,0).plusSeconds(fellAsleepDuration.getSeconds());

        //침대에 있던 시간 계산
        Duration sleepTimeDuration = Duration.between(LocalTime.MIDNIGHT, req.getSleepTime());
        LocalTime inBedTime = LocalTime.of(0,0).plusSeconds(sleepTimeDuration.getSeconds()).plusSeconds(fellAsleepDuration.getSeconds());

        SleepDataEntity newSleepData = SleepDataEntity.builder()
                .sleptAt(sleptDate)
                .score(req.getScore())
                .awakeTime(req.getAwakeAt())
                .timeOnBed(inBedTime)
                .sleepTime(req.getSleepTime())
                .fellAsleepTime(fellAsleepTime)
                .rem(req.getRem())
                .user(user)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        return sleepDataRepository.save(newSleepData);
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

    @Transactional
    public void updateAnalysisDatas(String userId, SleepDataEntity sleepData) throws IOException {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ExceptionHandler(ErrorStatus._USER_NOT_FOUND));

        Map<String, Integer> currentWeekOfMonth = getCurrentWeekOfMonth(sleepData.getSleptAt());

        //주간
        String weeklyTag = sleepData.getSleptAt().getYear() + "_" + currentWeekOfMonth.get("Month") + "_" + currentWeekOfMonth.get("WeekNum");
        AnalysisDataEntity weeklyData = analysisDataRepository.findByUserAndTag(user, weeklyTag);
        if (weeklyData == null) { // 새로 추가
            AnalysisDataEntity newData = createAnalysisData(user, weeklyTag, Range.Weekly, sleepData);
            analysisDataRepository.save(newData);
            log.info("Weekly successfully created!");
        } else { // 데이터 업데이트
            AnalysisDataEntity updatedData = updateAnalysisData(weeklyData, sleepData);
            analysisDataRepository.save(updatedData);
            log.info("Weekly successfully updated!");
        }

        //월간
        String monthlyTag = sleepData.getSleptAt().getYear() + "_" + sleepData.getSleptAt().getMonthValue();
        AnalysisDataEntity monthlyData = analysisDataRepository.findByUserAndTag(user, monthlyTag);
        if (weeklyData == null) { // 새로 추가
            AnalysisDataEntity newData = createAnalysisData(user, monthlyTag, Range.Monthly, sleepData);
            analysisDataRepository.save(newData);
            log.info("Monthly successfully created!");
        } else { // 데이터 업데이트
            AnalysisDataEntity updatedData = updateAnalysisData(monthlyData, sleepData);
            analysisDataRepository.save(updatedData);
            log.info("Monthly successfully updated!");
        }

        // GPT Query Update
        String insightTag = String.valueOf(sleepData.getSleptAt().getYear());
        InsightEntity insight = insightRepository.findByUserAndTag(user, insightTag);
        List<AnalysisDataEntity> thisMonthDatas = analysisDataRepository.findByUserAndTagContaining(user, insightTag);
        String gptQueryData = generateQueryData(thisMonthDatas);
        String gptQueryResult = gptService.askChatGPT(gptQueryData);

        String[] gptQueryResultSplit = gptQueryResult.split("\n");
        if (insight == null) {
            InsightEntity newInsight = InsightEntity.builder()
                    .user(user)
                    .tag(insightTag)
                    .sleepPattern(gptQueryResultSplit[1])
                    .improvement(gptQueryResultSplit[4])
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();

            insightRepository.save(newInsight);

        } else {
            insight.setSleepPattern(gptQueryResultSplit[1]);
            insight.setImprovement(gptQueryResultSplit[4]);
            insight.setUpdatedAt(LocalDateTime.now());

            insightRepository.save(insight);
        }
    }

    private Map<String, Integer> getCurrentWeekOfMonth(LocalDate date) {
        // LocalDate -> Date
        Instant dateInstant = date.atStartOfDay(ZoneId.of("Asia/Seoul")).toInstant();
        Date formattedDate = Date.from(dateInstant);

        Calendar calendar = Calendar.getInstance(Locale.KOREA);
        calendar.setTime(formattedDate);
        int month = calendar.get(Calendar.MONTH) + 1; // calendar에서의 월은 0부터 시작
        int day = calendar.get(Calendar.DATE);

        // 한 주의 시작은 월요일이고, 첫 주에 4일이 포함되어있어야 첫 주 취급 (목/금/토/일)
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        calendar.setMinimalDaysInFirstWeek(4);

        int weekOfMonth = calendar.get(Calendar.WEEK_OF_MONTH);

        // 첫 주에 해당하지 않는 주의 경우 전 달 마지막 주차로 계산
        if (weekOfMonth == 0) {
            calendar.add(Calendar.DATE, -day); // 전 달의 마지막 날 기준
            LocalDate returnDate = calendar.getTime().toInstant().atZone(ZoneId.of("Asia/Seoul")).toLocalDate();
            return getCurrentWeekOfMonth(returnDate);
        }

        // 마지막 주차의 경우
        if (weekOfMonth == calendar.getActualMaximum(Calendar.WEEK_OF_MONTH)) {
            calendar.set(Calendar.DATE, calendar.getActualMaximum(Calendar.DATE)); // 이번 달의 마지막 날
            int lastDaysDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK); // 이번 달 마지막 날의 요일

            // 마지막 날이 월~수 사이이면 다음달 1주차로 계산
            if (lastDaysDayOfWeek >= Calendar.MONDAY && lastDaysDayOfWeek <= Calendar.WEDNESDAY) {
                calendar.add(Calendar.DATE, 1); // 마지막 날 + 1일 => 다음달 1일
                LocalDate returnDate = calendar.getTime().toInstant().atZone(ZoneId.of("Asia/Seoul")).toLocalDate();
                return getCurrentWeekOfMonth(returnDate);
            }
        }

        Map<String, Integer> monthAndWeek = new HashMap<>();
        monthAndWeek.put("Month", month);
        monthAndWeek.put("WeekNum", weekOfMonth);

        return monthAndWeek;
    }

    private AnalysisDataEntity createAnalysisData(UserEntity user, String tag, Range range, SleepDataEntity sleepData) {
        Map<BadAwakeReason, Integer> newBadReason = new HashMap<>();
        newBadReason.put(BadAwakeReason.COFFEE, 0);
        newBadReason.put(BadAwakeReason.EXERCISE, 0);
        newBadReason.put(BadAwakeReason.STRESS, 0);
        newBadReason.put(BadAwakeReason.ALCOHOL, 0);
        newBadReason.put(BadAwakeReason.SMARTPHONE, 0);

        if (sleepData.getBadAwakeReason() != null) {
            newBadReason.put(sleepData.getBadAwakeReason(), 1);
        }

        return AnalysisDataEntity.builder()
                .user(user)
                .dataCount(1)
                .type(range)
                .tag(tag)
                .scoreSum(sleepData.getScore())
                .awakeSum(sleepData.getAwakeTime())
                .inBedSum(sleepData.getTimeOnBed())
                .sleepTimeSum(sleepData.getSleepTime())
                .badAwakeReasonsCount(newBadReason)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    private AnalysisDataEntity updateAnalysisData(AnalysisDataEntity analysisData, SleepDataEntity sleepData){
        analysisData.setDataCount(analysisData.getDataCount() + 1);
        analysisData.setScoreSum(analysisData.getScoreSum() + sleepData.getScore());
        analysisData.setAwakeSum(analysisData.getAwakeSum().plusSeconds(sleepData.getAwakeTime().getSecond()));
        analysisData.setInBedSum(analysisData.getInBedSum().plusSeconds(sleepData.getTimeOnBed().getSecond()));
        analysisData.setSleepTimeSum(analysisData.getSleepTimeSum().plusSeconds(sleepData.getSleepTime().getSecond()));

        if (sleepData.getBadAwakeReason() != null) {
            Map<BadAwakeReason, Integer> currentBadReasonsCount = analysisData.getBadAwakeReasonsCount();
            currentBadReasonsCount.put(sleepData.getBadAwakeReason(), currentBadReasonsCount.get(sleepData.getBadAwakeReason()) + 1);
            analysisData.setBadAwakeReasonsCount(currentBadReasonsCount);
        }

        analysisData.setUpdatedAt(LocalDateTime.now());
        return analysisData;
    }

    private String generateQueryData(List<AnalysisDataEntity> analysisDataList) throws JsonProcessingException {
        // 월간 데이터 정리
        List<GPTQueryEntity> queryEntityList = analysisDataList.stream().map(data -> {
            Duration avgAwakeTimeDuration = Duration.between(LocalTime.MIN, data.getAwakeSum()).dividedBy(data.getDataCount());
            Duration avgInBedTimeDuration = Duration.between(LocalTime.MIN, data.getInBedSum()).dividedBy(data.getDataCount());
            Duration avgSleepTimeDuration = Duration.between(LocalTime.MIN, data.getSleepTimeSum()).dividedBy(data.getDataCount());

            StringBuilder reasonsBuilder = new StringBuilder();
            data.getBadAwakeReasonsCount().forEach((key, value) -> {
                if (value > data.getDataCount()) {
                    if (!reasonsBuilder.isEmpty()) {
                        reasonsBuilder.append(", ");
                    }
                    reasonsBuilder.append(key.name());
                }
            });

            return GPTQueryEntity.builder()
                    .yearAndMonth(data.getTag())
                    .avgSleepScore((float) (data.getScoreSum()/data.getDataCount()))
                    .avgAwakeTime(LocalTime.MIN.plus(avgAwakeTimeDuration))
                    .avgInBedTime(LocalTime.MIN.plus(avgInBedTimeDuration))
                    .avgSleepTime(LocalTime.MIN.plus(avgSleepTimeDuration))
                    .sleepDisturbance(reasonsBuilder.toString())
                    .build();

        }).toList();

        // query 생성
        return objectMapper.writeValueAsString(queryEntityList);
    }

    public SleepDataEntity retrieveDailyData(String userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ExceptionHandler(ErrorStatus._USER_NOT_FOUND));

        ZonedDateTime requestedDateTime = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));

        return sleepDataRepository.findByUserAndSleptAt(user, requestedDateTime.toLocalDate());
    }

    public List<SleepDataEntity> retrieveWeeklyData(String userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ExceptionHandler(ErrorStatus._USER_NOT_FOUND));

        ZonedDateTime requestedDateTime = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));

        LocalDate firstDateOfWeek = requestedDateTime.toLocalDate().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate lastDateOfWeek = requestedDateTime.toLocalDate().with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

        return sleepDataRepository.findAllByUserAndSleptAtBetween(user, firstDateOfWeek.minusDays(1), lastDateOfWeek);
    }

    public List<AnalysisDataEntity> retrieveAnalysisData(String userId, String range) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ExceptionHandler(ErrorStatus._USER_NOT_FOUND));
        
        ZonedDateTime requestedDateTime = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));
        String tag;

        if (range.equals("monthly")) {
            tag = requestedDateTime.getYear() + "_" + requestedDateTime.getMonthValue();
            return analysisDataRepository.findAllByUserAndTypeAndTagContaining(user, Range.Weekly, tag);
        } else {
            tag = requestedDateTime.format(DateTimeFormatter.ofPattern("yyyy"));
            return analysisDataRepository.findAllByUserAndTypeAndTagContaining(user, Range.Monthly, tag);
        }
    }

    public InsightEntity retrieveInsightEntity(String userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ExceptionHandler(ErrorStatus._USER_NOT_FOUND));

        ZonedDateTime requestedDateTime = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));
        String tag = requestedDateTime.format(DateTimeFormatter.ofPattern("yyyy"));

        return insightRepository.findByUserAndTag(user, tag);
    }

    @Transactional
    public String retrieveBestSleepTime(String userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ExceptionHandler(ErrorStatus._USER_NOT_FOUND));

        List<SleepDataEntity> sleepDataEntities = sleepDataRepository.findAllByUserAndScoreGreaterThan(user, 3);

        long sleepTimeSum = 0L;
        long count = 0L;
        for (SleepDataEntity data : sleepDataEntities) {
            sleepTimeSum += data.getSleepTime().toSecondOfDay();
            count += 1;
        }

        long sleepTimeAvg = sleepTimeSum / count;
        Duration bestSleepDuration = Duration.ofSeconds(sleepTimeAvg);

        return bestSleepDuration.toHoursPart() + "시간 " + bestSleepDuration.toMinutesPart() + "분";
    }
}
