package com.snorlaexes.raem.domain.sleep;

import com.snorlaexes.raem.domain.sleep.entities.SleepDataEntity;
import com.snorlaexes.raem.domain.sleep.entities.SleepDataUrlEntity;
import com.snorlaexes.raem.global.apiPayload.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

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
    public ApiResponse<SleepResDTO.SaveReasonResponseDTO> saveBadReason(@RequestBody SleepReqDTO.SaveReasonDTO req) {
        return ApiResponse.onSuccess(SleepResDTO.SaveReasonResponseDTO.saveReasonResultDTO(sleepService.saveReasonService(req)));
    }

    @GetMapping("/data")
    public ApiResponse<SleepResDTO.GetDataUrlResponseDTO> getDataUrl(@RequestBody SleepReqDTO.GetDataUrlDTO req) {
        return ApiResponse.onSuccess(SleepResDTO.GetDataUrlResponseDTO.getDataUrlResponseDTO(sleepService.getDataUrlService(req)));
    }
}
