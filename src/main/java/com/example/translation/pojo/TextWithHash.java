package com.example.translation.pojo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TextWithHash {
    private String text;
    private String hash;
}
