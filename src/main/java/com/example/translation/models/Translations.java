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
@Table(name = "translations")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class Translations {
    @Id
    @Column(name = "text_id", updatable = false, nullable = false)
    private String textId;

    @Column(name = "language_id", updatable = false, nullable = false)
    private String languageId;

    @Column(name = "translation", nullable = false)
    private String translation;
}
