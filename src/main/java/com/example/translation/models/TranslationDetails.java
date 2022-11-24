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

@Entity
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Table(name = "translation_table_1")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class TranslationDetails {

    @Column(name = "admin_status", nullable = false)
    private AdminStatus adminStatus;

    @Column(name = "text_id", updatable = true, nullable = false)
    private String textId;

    @Column(name = "original_text", updatable = true, nullable = false)
    private String originalText;

    @Column(name = "translated_data", updatable = true, nullable = false)
    private List<TranslatedData> translatedData;
}
