package com.example.translation.controllers;

import com.example.translation.constants.URLConstants;
import com.example.translation.dtos.request.GetTranslationRequest;
import com.example.translation.dtos.response.GetTranslationResponse;
import com.example.translation.models.TranslationDetails;
import com.example.translation.services.TranslationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping(
            path = Constants.Url.GET_ALL_TRANSLATION,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<List<TranslationDetails>> getAllTranslations(
           @RequestParam(name = "pending_approval", defaultValue = "true", required = false) Boolean pendingApproval
    ) {
        return ResponseEntity.ok(translationService.getAllTranslations(pendingApproval));
    }
}
