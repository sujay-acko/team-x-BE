package com.example.translation.models;

import com.example.translation.enums.AdminStatus;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Table(name = "translation_table_1")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class Translations {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(name = "id", updatable = false, nullable = false)
    private String id;

    @Column(name = "text_id", updatable = false, nullable = false)
    private String textId;

    @Enumerated(EnumType.STRING)
    @Column(name = "admin_status", updatable = false, nullable = false)
    private AdminStatus adminStatus;

    @Column(name = "language_code", updatable = false, nullable = false)
    private String languageCode;

    @Column(name = "translation", nullable = false)
    private String translation;

    @Column(name = "original_text", nullable = false)
    private String originalText;
}
