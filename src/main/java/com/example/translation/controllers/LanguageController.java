package com.example.translation.controllers;

import com.example.translation.constants.Constants;
import com.example.translation.dtos.request.GetTranslationRequest;
import com.example.translation.dtos.request.UpdateLanguageRequest;
import com.example.translation.dtos.response.GetLanguageResponse;
import com.example.translation.dtos.response.GetTranslationResponse;
import com.example.translation.dtos.response.UpdateLanguageResponse;
import com.example.translation.models.Language;
import com.example.translation.services.LanguageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(Constants.Url.API_V1)
@RequiredArgsConstructor
public class LanguageController {

    public final LanguageService languageService;

    @GetMapping(
            path = Constants.Url.LANGUAGE_ENDPOINT,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<List<Language>> getLanguage() {
        return ResponseEntity.ok(languageService.getLanguage());
    }

    @PatchMapping(
            path = Constants.Url.LANGUAGE_ENDPOINT,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<UpdateLanguageResponse> updateOpportunity(
            @RequestBody UpdateLanguageRequest requestDto) {
        return ResponseEntity.ok(languageService.updateLanguage(requestDto));
    }
}
