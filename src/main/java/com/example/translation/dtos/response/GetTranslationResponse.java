package com.example.translation.dtos.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class GetTranslationResponse {
    private String textData;
    private String targetLang;
    private Boolean fromThirdParty;
}
