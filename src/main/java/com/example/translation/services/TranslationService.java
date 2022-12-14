package com.example.translation.services;

import com.example.translation.constants.Regexes;
import com.example.translation.constants.Tags;
import com.example.translation.dtos.request.GetTranslationRequest;
import com.example.translation.dtos.request.UpdatetranslationRequest;
import com.example.translation.dtos.response.HtmlTextEncodedResponse;
import com.example.translation.dtos.response.UpdatetranslationResponse;
import com.example.translation.models.DecodeTextResponse;
import com.example.translation.dtos.response.GetTranslationResponse;
import com.example.translation.enums.AdminStatus;
import com.example.translation.models.Language;
import com.example.translation.models.TranslatedData;
import com.example.translation.models.TranslationDetails;
import com.example.translation.models.Translations;
import com.example.translation.pojo.TextWithHash;
import com.example.translation.repositories.LanguageRepository;
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
@Log4j2
public class TranslationService {

    private final TranslationsRepository translationsRepository;
    private final LanguageRepository languageRepository;
    private final Translate translate;

    public GetTranslationResponse getTranslation(GetTranslationRequest request) throws Exception {

        log.info("Language Translation Started {} ", request.getHtmlTextData());
        HtmlTextEncodedResponse htmlTextEncodedResponse = getFilteredSentence(request.getHtmlTextData());

        //checking if language is enabled
        request.setTargetLang(checkIfLanguageExist(request.getTargetLang()));

        Map<String, Integer> sequenceMap = new HashMap<>();
        Map<String, List<String>> paramValueMap = new HashMap<>();
        Map<String, String> translationMap = new HashMap<>();
        List<String> originalText = new ArrayList<>();
        List<List<String>> originalParamList = new ArrayList<>();
        int index = 0;
        for (DecodeTextResponse decodeTextResponse : htmlTextEncodedResponse.getDecodeTextResponseList()) {
            String hexCode = CommonUtils.getMd5(decodeTextResponse.getText());
            sequenceMap.put(hexCode, index++);
            translationMap.put(hexCode, decodeTextResponse.getText());
            paramValueMap.put(hexCode, decodeTextResponse.getParamValues());

            originalText.add(decodeTextResponse.getText());
            originalParamList.add(decodeTextResponse.getParamValues());
        }

        List<String> translationHex = new ArrayList<>(translationMap.keySet());

        if (translationHex.isEmpty()) {
            // no translation
            return GetTranslationResponse.builder()
                    .textData(request.getHtmlTextData())
                    .targetLang(request.getTargetLang())
                    .fromThirdParty(Boolean.FALSE)
                    .build();
        }
        System.out.println(translationHex);
        System.out.println(request);


        String[] translatedEncodedString = new String[0];
        ArrayList[] paramList = new ArrayList[0];

        if (!request.getTargetLang().equalsIgnoreCase("en")) {

            List<Translations> translatedSentence =
                    translationsRepository.findByTextIdInAndLanguageCode(translationHex, request.getTargetLang());

            List<String> translationAvailableHex = translatedSentence.stream().map(Translations::getTextId).collect(Collectors.toList());

            // unmatched strings
            List<TextWithHash> unmatchedString = translationHex.stream()
                    .filter(t -> !translationAvailableHex.contains(t))
                    .map(t -> TextWithHash.builder().text(translationMap.get(t)).hash(t).build())
                    .collect(Collectors.toList());

            List<Translations> translationsList = new ArrayList<>();
            if (!unmatchedString.isEmpty()) {
                // sentence found in db
                List<TextWithHash> translatedList = createTranslation(unmatchedString, request.getTargetLang());

                translationsList = translatedList.stream()
                        .map(t -> Translations.builder()
                                .textId(t.getHash())
                                .languageCode(request.getTargetLang())
                                .originalText(translationMap.get(t.getHash()))
                                .translation(t.getText())
                                .adminStatus(AdminStatus.pending)
                                .build()
                        )
                        .collect(Collectors.toList());
                translationsRepository.saveAll(translationsList);
            }
            List<Translations> finalTranslatedString = Stream.concat(translationsList.stream(), translatedSentence.stream())
                    .collect(Collectors.toList());

            translatedEncodedString = new String[finalTranslatedString.size()];
            paramList = new ArrayList[finalTranslatedString.size()];
            String[] finalTranslatedEncodedString = translatedEncodedString;
            ArrayList[] finalParamList = paramList;
            finalTranslatedString.forEach(t -> {
                finalTranslatedEncodedString[sequenceMap.get(t.getTextId())] = t.getTranslation();
                finalParamList[sequenceMap.get(t.getTextId())] = (ArrayList) paramValueMap.get(t.getTextId());
            });
            originalText = Arrays.asList(translatedEncodedString);
            originalParamList = Arrays.asList(paramList);
        }
        List<String> translatedString = getSubstitutedTranslationList(originalText, originalParamList);

        String encodedHtmlResponse = htmlTextEncodedResponse.getEncodedHtmlResponse();
        String finalHtmlResponsePage = getFinalHtmlResponsePage(translatedString, encodedHtmlResponse);
        GetTranslationResponse getTranslationResponse = GetTranslationResponse.builder()
                .textData(finalHtmlResponsePage)
                .targetLang(request.getTargetLang())
                .fromThirdParty(Boolean.FALSE)
                .build();

        log.info("Language Translation Done {} ", getTranslationResponse.getTextData());
        return getTranslationResponse;
    }

    public String checkIfLanguageExist(String targetLang) {
        Optional<Language> optionalLanguage = languageRepository.findById(targetLang);
        if (optionalLanguage.isEmpty()) {
            log.warn("--NO TRANSLATION--");
            return "en";
        }
        Language language = optionalLanguage.get();
        if (language.getEnabled()) {
            return language.getLanguageCode();
        }
        log.warn("--NO TRANSLATION--");
        return "en";
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

    public List<String> getSubstitutedTranslationList(List<String> translatedEncodedStringList, List<List<String>> paramValuesList ){
        List<String> translatedString = new ArrayList<>();
        Pattern pattern = Pattern.compile(Tags.DECODE_PARAM_TAG, Pattern.CASE_INSENSITIVE);

        int paramListCounter = 0;
        for (String translatedEncodedString: translatedEncodedStringList) {
            Matcher matcher = pattern.matcher(translatedEncodedString);

            StringBuffer stringBuffer = new StringBuffer();
            int paramValuesListCounter=0;
            while (matcher.find()) {
                matcher.appendReplacement(stringBuffer, paramValuesList.get(paramListCounter).get(paramValuesListCounter));
                paramValuesListCounter++;
            }
            matcher.appendTail(stringBuffer);
            translatedString.add(stringBuffer.toString());
            paramListCounter++;
        }
        return translatedString;
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

    public List<TranslationDetails> getAllTranslations(Boolean pendingApproval){
        List<Translations> translationsList = pendingApproval ? translationsRepository.findByAdminStatus(AdminStatus.pending): translationsRepository.findAll();

        // When no date available
        if (translationsList.isEmpty())
            return new ArrayList<>();

        Map<String, List<TranslatedData>> translationListMap = new HashMap<>();
        Map<String, String> originalTextMaP = new HashMap<>();

        for (int i = 0; i < translationsList.size(); i++) {
            String textId = translationsList.get(i).getTextId();
            TranslatedData translatedData = TranslatedData.builder()
                    .languageCode(translationsList.get(i).getLanguageCode())
                    .text(translationsList.get(i).getTranslation())
                    .adminStatus(translationsList.get(i).getAdminStatus())
                    .build();

            List<TranslatedData> translatedDataList = translationListMap.getOrDefault(textId, new ArrayList<>());
            translatedDataList.add(translatedData);
            translationListMap.put(textId, translatedDataList);
            originalTextMaP.put(textId, translationsList.get(i).getOriginalText());
        }
        List<TranslationDetails>  translationDetailsList = new ArrayList<>();
        for (Map.Entry<String,String> entry : originalTextMaP.entrySet())
            translationDetailsList.add(TranslationDetails.builder()
                    .translatedData(translationListMap.get(entry.getKey()))
                    .originalText(entry.getValue())
                    .textId(entry.getKey()).build()
                    );

        return translationDetailsList;
    }

    public UpdatetranslationResponse updateTranslations(UpdatetranslationRequest updatetranslationRequest){
        List<Translations> translationsList = new ArrayList<>();
        for (TranslatedData translatedData: updatetranslationRequest.getTranslatedData()) {
            List<Translations> translationList = translationsRepository.findByTextIdInAndLanguageCode(List.of(updatetranslationRequest.getTextId()), translatedData.getLanguageCode());
            Translations translations = null;
            if(!translationList.isEmpty()){
                translations = translationList.get(0);
                translations.setTranslation(translatedData.getText());
                translations.setAdminStatus(translatedData.getAdminStatus());
                translations.setOriginalText(updatetranslationRequest.getOriginalText());

                translationsList.add(translations);
                continue;
            }
            translations = Translations.builder()
                    .textId(updatetranslationRequest.getTextId())
                    .languageCode(translatedData.getLanguageCode())
                    .translation(translatedData.getText())
                    .adminStatus(translatedData.getAdminStatus())
                    .originalText(updatetranslationRequest.getOriginalText())
                    .build();
            translationsList.add(translations);
        }
        translationsRepository.saveAll(translationsList);

        return UpdatetranslationResponse.builder()
                .success(true)
                .message("Translation Saved")
                .build();
    }
}
