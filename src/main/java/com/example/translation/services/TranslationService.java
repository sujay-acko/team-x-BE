package com.example.translation.services;

import com.example.translation.dtos.request.GetTranslationRequest;
import com.example.translation.dtos.response.GetTranslationResponse;
import com.example.translation.enums.AdminStatus;
import com.example.translation.models.TranslationDetails;
import com.example.translation.models.Translations;
import com.example.translation.pojo.TextWithHash;
import com.example.translation.repositories.TranslationsDetailsRepository;
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
    private final TranslationsDetailsRepository translationsDetailsRepository;
    private final Translate translate;

    public GetTranslationResponse getTranslation(GetTranslationRequest request) {

        List<String> translationTextList = getFilteredSentence(request.getTextData());
        Map<String, String> translationMap = translationTextList.stream()
                .collect(Collectors.toMap(CommonUtils::getMd5, s -> s));

        List<TextWithHash> translationRevObj = translationTextList.stream()
                .map(t -> TextWithHash.builder()
                        .text(t)
                        .hash(CommonUtils.getMd5(t))
                        .build())
                .collect(Collectors.toList());

        List<String> translationHex = new ArrayList<>(translationMap.keySet());
        List<Translations> translatedSentence =
                translationsRepository.findByTextIdInAndLanguageCode(translationHex, request.getTargetLang());

        List<String> translationAvailableHex = translatedSentence.stream().map(Translations::getTextId).collect(Collectors.toList());

        // unmatched strings
        List<TextWithHash> unmatchedString = translationHex.stream()
                .filter(t -> !translationAvailableHex.contains(t))
                .map(t -> TextWithHash.builder().text(translationMap.get(t)).hash(t).build())
                .collect(Collectors.toList());

        if (unmatchedString.isEmpty()) {
            // sentence found in db
            log.info("------ENDED-------");
            return null;
        }

        List<TextWithHash> translatedList = createTranslation(unmatchedString, request.getTargetLang());

        List<Translations> translationsList = translatedList.stream()
                .map(t -> Translations.builder()
                        .textId(t.getHash())
                        .languageCode(request.getTargetLang())
                        .translation(t.getText())
                        .build()
                )
                .collect(Collectors.toList());
        translationsRepository.saveAll(translationsList);
        return null;
    }

    public List<String> getFilteredSentence(String text) {
        return List.of("This is an house", "Hello world",
                "How can I do insurance claim", "I am playing football", "Health insurance",
                "acko insurance"
        );
    }

    public List<TextWithHash> createTranslation(List<TextWithHash> unmatchedObj, String targetLang) {

        List<String> unmatchedString = unmatchedObj.stream().map(TextWithHash::getText).collect(Collectors.toList());

        List<Translation> translation = translate.translate(
                unmatchedString,
                Translate.TranslateOption.sourceLanguage("en"),
                Translate.TranslateOption.targetLanguage(targetLang));
        log.info(translation);

        List<TextWithHash> result = new ArrayList<>();

        for (int i = 0; i < unmatchedObj.size(); i++) {
            result.add(
                    TextWithHash.builder()
                            .hash(unmatchedObj.get(i).getHash())
                            .text(translation.get(i).getTranslatedText())
                            .build()
            );
        }
        return result;
    }

    public List<TranslationDetails> getAllTranslations(Boolean pendingApproval){
        if (!pendingApproval){
            return translationsDetailsRepository.findByAdminStatus(AdminStatus.PENDING);
        }
        return translationsDetailsRepository.findAll();
    }
}
