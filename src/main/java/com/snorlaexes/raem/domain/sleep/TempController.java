package com.snorlaexes.raem.domain.sleep;

import com.snorlaexes.raem.domain.sleep.entities.LearningDataEntity;
import com.snorlaexes.raem.global.apiPayload.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/temp")
public class TempController {
    private final SleepService sleepService;
    @PostMapping(value = "/{tag}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ApiResponse<SleepResDTO.SaveDataResponseDTO> saveFile(@RequestPart LocalDate sleptAt, @RequestPart MultipartFile file, @PathVariable("tag") String tag) {
        LearningDataEntity learningDataEntity = sleepService.tempDataSave(tag, sleptAt, file);
        return ApiResponse.onSuccess(SleepResDTO.SaveDataResponseDTO.saveLearningDataResultDTO(learningDataEntity));
    }
}
