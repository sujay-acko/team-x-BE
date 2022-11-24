package com.example.translation.services;

import com.example.translation.dtos.request.GetTranslationRequest;
import com.example.translation.dtos.response.GetTranslationResponse;
import com.example.translation.models.Translations;
import com.example.translation.repositories.TranslationsRepository;
import com.example.translation.utils.CommonUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Log4j2
public class TranslationService {

    private final TranslationsRepository translationsRepository;

    public GetTranslationResponse getTranslation(GetTranslationRequest request) {

        List<String> translationTextList = getFilteredSentence(request.getTextData());
        List<String> translationHex = translationTextList.stream()
                .map(CommonUtils::getMd5)
                .collect(Collectors.toList());

        List<Translations> translatedSentence =
                translationsRepository.findByTextIdInAndLanguageCode(translationHex, request.getTargetLang());

        return null;

    }

    public List<String> getFilteredSentence(String text) {
        return List.of("This is an house", "Hello World", "How can I do insurance claim");
    }
}
