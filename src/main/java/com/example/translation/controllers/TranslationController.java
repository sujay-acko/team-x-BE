package com.example.translation.controllers;

import com.example.translation.constants.Constants;
import com.example.translation.dtos.request.GetTranslationRequest;
import com.example.translation.dtos.response.GetTranslationResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(Constants.Url.API_V1)
public class TranslationController {

    @PostMapping(
            path = Constants.Url.TRANSLATION_ENDPOINT,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<GetTranslationResponse> getOpportunity(@RequestBody GetTranslationRequest requestDto) {
        return ResponseEntity.ok(null);
    }
}
