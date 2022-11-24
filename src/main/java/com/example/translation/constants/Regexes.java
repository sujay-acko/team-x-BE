package com.example.translation.constants;

public class Regexes {

    public final static String ENCODING_REGEX                        = "\\$parameters";
    public final static String INTERNAL_TAG_DECODING_REGEX           = Tags.PARAM_START_TAG+"(.+?)"+Tags.PARAM_END_TAG;
    public final static String EXTERNAL_TAG_DECODING_REGEX           = "^"+Tags.TRANSLATE_START_TAG+"(.+?)"+Tags.TRANSLATE_END_TAG+"$";
}

