package com.example.translation.services;

import com.example.translation.constants.Regexes;
import com.example.translation.constants.Tags;
import com.example.translation.dtos.request.GetTranslationRequest;
import com.example.translation.dtos.response.HtmlTextEncodedResponse;
import com.example.translation.models.DecodeTextResponse;
import com.example.translation.dtos.response.GetTranslationResponse;
import com.example.translation.models.Translations;
import com.example.translation.pojo.TextWithHash;
import com.example.translation.repositories.TranslationsRepository;
import com.example.translation.services.decode.DecodeTagService;
import com.example.translation.services.decode.DecodeTagServiceImpl;
import com.example.translation.utils.CommonUtils;
import com.google.cloud.translate.Translate;
import com.google.cloud.translate.Translation;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Log4j2
public class TranslationService {

    private final TranslationsRepository translationsRepository;
    private final Translate translate;

    public GetTranslationResponse getTranslation(GetTranslationRequest request) throws Exception {

        HtmlTextEncodedResponse htmlTextEncodedResponse = getFilteredSentence(request.getHtmlTextData());
        List<DecodeTextResponse> decodeTextResponseList = htmlTextEncodedResponse.getDecodeTextResponseList();
        String encodedHtmlResponse = htmlTextEncodedResponse.getEncodedHtmlResponse();

        /**
         * Fetch translation from DB
         * */
        List<String> translatedString = null;
        String finalHtmlResponsePage = getFinalHtmlResponsePage(translatedString,encodedHtmlResponse);

        List<String> translationTextList= null;
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

    public HtmlTextEncodedResponse getFilteredSentence(String text) throws Exception {

        Matcher matcher = Pattern.compile(Regexes.HTML_PARSER_REGEX, Pattern.CASE_INSENSITIVE).matcher(text);

        StringBuffer stringBuffer = new StringBuffer();
        List<String> encodedTranslationTextList = new ArrayList<>();
        while (matcher.find()) {
            encodedTranslationTextList.add(matcher.group(0));
            matcher.appendReplacement(stringBuffer, Tags.HTML_PARSER_TAG);
        }
        matcher.appendTail(stringBuffer);

        List<DecodeTextResponse> decodedTranslationTextList = new ArrayList<>();
        DecodeTagService decodeTagService = new DecodeTagServiceImpl();
        for (String encodedText: encodedTranslationTextList){
            decodedTranslationTextList.add(decodeTagService.getTagDecodedText(encodedText));
        }
        HtmlTextEncodedResponse htmlTextEncodedResponse = HtmlTextEncodedResponse.builder()
                .encodedHtmlResponse(stringBuffer.toString())
                .decodeTextResponseList(decodedTranslationTextList)
                .build();
        return htmlTextEncodedResponse;
    }

    public String getFinalHtmlResponsePage(List<String> translatedTextResponseList, String encodedHtmlResponse ){

        Matcher matcher = Pattern.compile(Tags.HTML_PARSER_TAG, Pattern.CASE_INSENSITIVE).matcher(encodedHtmlResponse);

        StringBuffer stringBuffer = new StringBuffer();
        int count=0;
        while (matcher.find()) {
            matcher.appendReplacement(stringBuffer, translatedTextResponseList.get(count));
            count++;
        }
        matcher.appendTail(stringBuffer);

        return stringBuffer.toString();
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
}
