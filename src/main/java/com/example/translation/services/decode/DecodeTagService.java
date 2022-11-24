package com.example.translation.services.decode;

import com.example.translation.dtos.response.DecodeTextResponse;


public interface DecodeTagService {

    DecodeTextResponse getTagDecodedText(String text) throws Exception;
}
