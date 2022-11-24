package com.example.translation.services.encode;

import com.example.translation.constants.Regexes;
import com.example.translation.constants.Tags;

import java.util.ArrayList;

public class EncodeTagServiceImpl implements EncodeTagService{

    @Override
    public String getTagEncodedText(String text, ArrayList<String> paramValues) {
        text = text.trim();
        String tagEncodedText = text;
        String[] subtexts = text.split(Regexes.ENCODING_REGEX);

        if ((subtexts.length - 1) <= paramValues.size()){
            String encodeText = encodeText(paramValues, subtexts);
            tagEncodedText = encodeText.isEmpty() ? text : encodeText;
        } else if ((subtexts.length - 1) != paramValues.size()){
            throw new IllegalArgumentException("Number of parameter values less than defined parameter tags in text");
        }
        return tagEncodedText.trim();
    }

    private String encodeText(ArrayList<String> paramValues, String[] subtexts){
        String tagEncodedText = "";
        if (subtexts.length == 1){
            tagEncodedText+= Tags.TRANSLATE_START_TAG +" "+subtexts[0]+" "+Tags.TRANSLATE_END_TAG;
        } else {
            tagEncodedText+=Tags.TRANSLATE_START_TAG;
            for (int index=0; index < (subtexts.length-1); index++) {
                tagEncodedText+=" "+subtexts[index]+Tags.PARAM_START_TAG +" "+paramValues.get(index)+" "+Tags.PARAM_END_TAG;
            }
            tagEncodedText+=subtexts[subtexts.length -1]+" "+Tags.TRANSLATE_END_TAG;
        }
        return tagEncodedText;
    }
}
