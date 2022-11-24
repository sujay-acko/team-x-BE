package com.example.translation.services.decode;

import com.example.translation.constants.Regexes;
import com.example.translation.constants.Tags;
import com.example.translation.dtos.response.DecodeTextResponse;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DecodeTagServiceImpl implements DecodeTagService{

    @Override
    public DecodeTextResponse getTagDecodedText(String encodedText) {
        encodedText = encodedText.trim();
        if (!(encodedText.contains(Tags.TRANSLATE_START_TAG)
                || encodedText.contains(Tags.PARAM_START_TAG)
                || encodedText.contains(Tags.TRANSLATE_END_TAG)
                || encodedText.contains(Tags.PARAM_END_TAG)))
        {
            return DecodeTextResponse.builder()
                    .text(encodedText)
                    .build();
        }
        if (!validateEncodedText(encodedText)){
            throw new IllegalArgumentException("Invalid Encoded Text");
        }
        List<String> paramValues = new ArrayList<>();
        Matcher matcher = generatePatternObject(Regexes.INTERNAL_TAG_DECODING_REGEX, Pattern.DOTALL).matcher(encodedText);
        while (matcher.find()) {
            paramValues.add(matcher.group(1));
        }
        String decodedText = matcher.replaceAll(Tags.DECODE_PARAM_TAG);
        matcher = generatePatternObject(Regexes.EXTERNAL_TAG_DECODING_REGEX, Pattern.CASE_INSENSITIVE).matcher(decodedText);
        while (matcher.find()) {
            decodedText = matcher.group(1);
            break;
        }
        DecodeTextResponse decodeTextResponse = DecodeTextResponse.builder()
                .paramValues(paramValues)
                .text(decodedText.isEmpty()? encodedText : decodedText.trim())
                .build();
        return decodeTextResponse;
    }

    private Pattern generatePatternObject(String targetRegex, int flag){
        return Pattern.compile(targetRegex, flag);
    }

    private Boolean validateEncodedText(String encodedText) {
        String[] subEncodedText = encodedText.split("\\s");

        Deque<String> TranslationTags = new ArrayDeque<>();
        for (String subString: subEncodedText) {
            if (subString.equals(Tags.TRANSLATE_START_TAG) || subString.equals(Tags.PARAM_START_TAG)) {
                TranslationTags.push(subString);
                continue;
            }

            if (TranslationTags.isEmpty())
                return false;
            String OpeningTags;
            switch (subString) {
                case Tags.TRANSLATE_END_TAG:
                    OpeningTags = TranslationTags.pop();
                    if (OpeningTags.equals(Tags.PARAM_START_TAG))
                        return false;
                    break;

                case Tags.PARAM_END_TAG:
                    OpeningTags = TranslationTags.pop();
                    if (OpeningTags.equals(Tags.TRANSLATE_START_TAG))
                        return false;
                    break;
            }
        }
        return (TranslationTags.isEmpty());
    }
}
