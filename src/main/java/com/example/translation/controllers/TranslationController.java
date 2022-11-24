package com.example.translation.controllers;

import com.example.translation.constants.URLConstants;
import com.example.translation.dtos.request.GetTranslationRequest;
import com.example.translation.dtos.response.GetTranslationResponse;
import com.example.translation.services.TranslationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(URLConstants.Url.API_V1)
@RequiredArgsConstructor
public class TranslationController {

    private final TranslationService translationService;
    @PostMapping(
            path = URLConstants.Url.TRANSLATION_ENDPOINT,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<GetTranslationResponse> getOpportunity(@RequestBody GetTranslationRequest requestDto) throws Exception {
        return ResponseEntity.ok(translationService.getTranslation(requestDto));
    }
}
