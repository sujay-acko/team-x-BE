package com.example.translation.services.encode;

import java.util.ArrayList;

public interface EncodeTagService {

    String getTagEncodedText(String text, ArrayList<String> paramValues);
}
