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
@Table(name = "language_code")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class Language {

    @Id
    @Column(name = "language_code", updatable = false, nullable = false)
    private String languageCode;

    @Column(name = "language_name", updatable = false, nullable = false)
    private String languageName;

    @Column(name = "enabled", nullable = false)
    private Boolean enabled;

}
