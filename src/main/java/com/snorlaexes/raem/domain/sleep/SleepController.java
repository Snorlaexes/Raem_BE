package com.snorlaexes.raem.domain.sleep;

import com.snorlaexes.raem.domain.sleep.entities.AnalysisDataEntity;
import com.snorlaexes.raem.domain.sleep.entities.SleepDataEntity;
import com.snorlaexes.raem.domain.sleep.entities.SleepDataUrlEntity;
import com.snorlaexes.raem.global.apiPayload.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/sleep")
public class SleepController {
    private final SleepService sleepService;
    @PostMapping(value = "/data", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_OCTET_STREAM_VALUE}, params = "type=file")
    public ApiResponse<SleepResDTO.SaveDataResponseDTO> saveFile(@RequestPart LocalDate sleptAt, @RequestPart MultipartFile file, @AuthenticationPrincipal String userId) {
        SleepDataUrlEntity sleepDataUrlEntity = sleepService.sendS3AndSave(userId, sleptAt, file);
        return ApiResponse.onSuccess(SleepResDTO.SaveDataResponseDTO.saveDataResultDTO(sleepDataUrlEntity));
    }

    @PostMapping(value = "/data", params = "type=json")
    public ApiResponse<SleepResDTO.SaveDataResponseDTO> saveData(@RequestBody SleepReqDTO.SaveDataDTO req, @AuthenticationPrincipal String userId) {
        SleepDataEntity sleepData = sleepService.organizeSleepData(userId, req);
        return ApiResponse.onSuccess(SleepResDTO.SaveDataResponseDTO.organizeDataResultDTO(sleepData));
    }

    @PatchMapping("/data")
    public ApiResponse<SleepResDTO.SaveReasonResponseDTO> saveBadReason(@AuthenticationPrincipal String userId, @RequestBody SleepReqDTO.SaveReasonDTO req) throws IOException {
        SleepDataEntity saveReasonResult = sleepService.saveReasonService(req);
        sleepService.updateAnalysisDatas(userId, saveReasonResult);
        return ApiResponse.onSuccess(SleepResDTO.SaveReasonResponseDTO.saveReasonResultDTO(saveReasonResult));
    }

    @GetMapping("/data")
    public ApiResponse<SleepResDTO.GetDataUrlResponseDTO> getDataUrl(@RequestBody SleepReqDTO.GetDataUrlDTO req) {
        return ApiResponse.onSuccess(SleepResDTO.GetDataUrlResponseDTO.getDataUrlResponseDTO(sleepService.getDataUrlService(req)));
    }

    @GetMapping("/analysis")
    public ApiResponse<List<AnalysisDataEntity>> getRangeData(@RequestParam("range") String range, @AuthenticationPrincipal String userId) {
        return ApiResponse.onSuccess(sleepService.retrieveAnalysisData(userId, range));
    }
}
