package com.example.translation.models;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;
import java.util.List;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class TranslationDetails {
    private String textId;
    private String originalText;
    private List<TranslatedData> translatedData;
}
