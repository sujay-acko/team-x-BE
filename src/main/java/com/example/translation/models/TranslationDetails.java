package com.example.translation.models;

import com.example.translation.enums.AdminStatus;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.List;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class TranslationDetails {
    private AdminStatus adminStatus;
    private String textId;
    private String originalText;
    private List<TranslatedData> translatedData;
}
