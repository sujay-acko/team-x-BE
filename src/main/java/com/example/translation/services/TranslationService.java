package com.example.translation.services;

import com.example.translation.dtos.request.GetTranslationRequest;
import com.example.translation.dtos.response.GetTranslationResponse;
import com.example.translation.models.Translations;
import com.example.translation.pojo.TextWithHash;
import com.example.translation.repositories.TranslationsRepository;
import com.example.translation.utils.CommonUtils;
import com.google.cloud.translate.Translate;
import com.google.cloud.translate.Translation;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Log4j2
public class TranslationService {

    private final TranslationsRepository translationsRepository;
    private final Translate translate;

    public GetTranslationResponse getTranslation(GetTranslationRequest request) {

        List<String> translationTextList = getFilteredSentence(request.getTextData());
        Map<String, String> translationMap = translationTextList.stream()
                .collect(Collectors.toMap(CommonUtils::getMd5, s -> s));

        List<String> translationHex = new ArrayList<>(translationMap.keySet());
        List<Translations> translatedSentence =
                translationsRepository.findByTextIdInAndLanguageCode(translationHex, request.getTargetLang());

        List<String> translationAvailableHex = translatedSentence.stream().map(Translations::getTextId).collect(Collectors.toList());

        // unmatched strings
        List<String> unmatchedString = translationHex.stream()
                .filter(t -> !translationAvailableHex.contains(t))
                .map(translationMap::get)
                .collect(Collectors.toList());

        List<String> translatedList = createTranslation(unmatchedString, request.getTargetLang());


    }

    public List<String> getFilteredSentence(String text) {
        return List.of("This is an house", "Hello world", "How can I do insurance claim", "I am playing football", "Health insurance");
    }

    public List<String> createTranslation(List<String> unmatchedString, String targetLang) {

        List<Translation> translation = translate.translate(
                unmatchedString,
                Translate.TranslateOption.sourceLanguage("en"),
                Translate.TranslateOption.targetLanguage(targetLang));
        log.info(translation);

        List<String> translatedList = translation.stream()
                .map(Translation::getTranslatedText)
                .collect(Collectors.toList());

        return translatedList;
    }

//    public
}
