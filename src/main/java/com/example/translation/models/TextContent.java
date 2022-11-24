package com.example.translation.models;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Table(name = "text_content")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class TextContent {
    @Id
    @Column(name = "text_content_id", updatable = false, nullable = false)
    private String textContentId;

    @Column(name = "original_text", updatable = false, nullable = false)
    private String originalText;

    @Column(name = "original_language_id", updatable = false, nullable = false)
    private String originalLanguageId;

}
