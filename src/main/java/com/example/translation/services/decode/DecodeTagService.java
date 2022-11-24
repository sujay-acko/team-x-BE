package com.example.translation.services.decode;

import com.example.translation.models.DecodeTextResponse;


public interface DecodeTagService {

    DecodeTextResponse getTagDecodedText(String text) throws Exception;
}
