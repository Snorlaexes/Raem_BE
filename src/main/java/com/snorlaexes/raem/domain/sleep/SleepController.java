package com.snorlaexes.raem.domain.sleep;

import com.snorlaexes.raem.domain.sleep.entities.SleepDataEntity;
import com.snorlaexes.raem.domain.sleep.entities.SleepDataUrlEntity;
import com.snorlaexes.raem.global.apiPayload.ApiResponse;
import com.snorlaexes.raem.global.apiPayload.code.status.ErrorStatus;
import com.snorlaexes.raem.global.apiPayload.exception.ExceptionHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Objects;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/sleep")
public class SleepController {
    private final SleepService sleepService;
    @PostMapping(value = "/data", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_OCTET_STREAM_VALUE}, params = "type=file")
    public ApiResponse<?> saveFile(@RequestPart LocalDate sleptAt, @RequestPart MultipartFile file, @AuthenticationPrincipal String userId) {
        SleepDataUrlEntity sleepDataUrlEntity = sleepService.sendS3AndSave(userId, sleptAt, file);

        String createdAt = sleepDataUrlEntity.getCreatedAt().toString().split("\\.")[0];
        String updatedAt = sleepDataUrlEntity.getUpdatedAt().toString().split("\\.")[0];

        if (Objects.equals(createdAt, updatedAt)) {
            return ApiResponse.onSuccess(SleepResDTO.SaveDataResponseDTO.saveDataResultDTO(sleepDataUrlEntity));
        } else {
            return ApiResponse.onSuccess(SleepResDTO.UpdateURLResponseDTO.updateURLResultDTO(sleepDataUrlEntity));
        }
    }

    @PostMapping(value = "/data", params = "type=json")
    public ApiResponse<SleepResDTO.SaveDataResponseDTO> saveData(@RequestBody SleepReqDTO.SaveDataDTO req, @AuthenticationPrincipal String userId) throws IOException {
        SleepDataEntity sleepData = sleepService.organizeSleepData(userId, req);

        //3점 이상이면 바로 분석 데이터 업데이트
        if (req.getScore() >= 3) {
            System.out.println("HI");
            sleepService.updateAnalysisDatas(userId, sleepData);
        }

        return ApiResponse.onSuccess(SleepResDTO.SaveDataResponseDTO.organizeDataResultDTO(sleepData));
    }

    @PatchMapping("/data")
    public ApiResponse<SleepResDTO.SaveReasonResponseDTO> saveBadReason(@AuthenticationPrincipal String userId, @RequestBody SleepReqDTO.SaveReasonDTO req) throws IOException {
        SleepDataEntity saveReasonResult = sleepService.saveReasonService(req);
        sleepService.updateAnalysisDatas(userId, saveReasonResult);
        return ApiResponse.onSuccess(SleepResDTO.SaveReasonResponseDTO.saveReasonResultDTO(saveReasonResult));
    }

    @GetMapping("/data")
    public ApiResponse<SleepResDTO.GetDataUrlListResponseDTO> getDataUrl(@AuthenticationPrincipal String userId, @RequestBody SleepReqDTO.GetDataUrlDTO req) {
        return ApiResponse.onSuccess(SleepResDTO.GetDataUrlListResponseDTO.getDataUrlListResponseDTO(sleepService.getDataUrlService(userId, req)));
    }

    @GetMapping(value = "/analysis")
    public ApiResponse<?> getRangeData(@RequestParam("range") String range, @AuthenticationPrincipal String userId) {
        return switch (range) {
            case "weekly" ->
                    ApiResponse.onSuccess(SleepResDTO.GetWeeklyDataDTO.getWeeklyDataDTO(sleepService.retrieveWeeklyData(userId)));
            case "monthly", "annually" ->
                    ApiResponse.onSuccess(SleepResDTO.GetOverMonthDataListDTO.getOverMonthDataListDTO(range, sleepService.retrieveAnalysisData(userId, range)));
            case "daily" ->
                    ApiResponse.onSuccess(SleepResDTO.GetDailyDataDTO.getDailyDataDTO(sleepService.retrieveDailyData(userId)));
            default -> throw new ExceptionHandler(ErrorStatus._WRONG_PARAM);
        };
    }

    @GetMapping("/analysis/insight")
    public ApiResponse<SleepResDTO.GetInsightDTO> getInsight(@AuthenticationPrincipal String userId) {
        return ApiResponse.onSuccess(SleepResDTO.GetInsightDTO.getInsightDTO(sleepService.retrieveInsightEntity(userId)));
    }

    @GetMapping("/best")
    public ApiResponse<SleepResDTO.GetBestSleepTimeDTO> getBestSleepTime(@AuthenticationPrincipal String userId) {
        return ApiResponse.onSuccess(SleepResDTO.GetBestSleepTimeDTO.getBestSleepTimeDTO(sleepService.retrieveBestSleepTime(userId)));
    }
}
