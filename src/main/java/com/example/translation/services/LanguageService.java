package com.example.translation.services;

import com.example.translation.dtos.request.UpdateLanguageRequest;
import com.example.translation.dtos.response.UpdateLanguageResponse;
import com.example.translation.models.Language;
import com.example.translation.repositories.LanguageRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Component
@AllArgsConstructor
public class LanguageService {

    public final LanguageRepository languageRepository;

    public List<Language> getLanguage() {
        return languageRepository.findAll();
    }

    public UpdateLanguageResponse updateLanguage(UpdateLanguageRequest requestDto) {
        Optional<Language> languageOptional = languageRepository.findById(requestDto.getLangCode());
        if (languageOptional.isEmpty()) {
            throw new RuntimeException();
        }
        Language language = languageOptional.get();
        if (Objects.equals(language.getEnabled(), requestDto.getEnabled())) {
            return UpdateLanguageResponse.builder()
                    .success(true)
                    .message("Same language changes").build();
        }
        language.setEnabled(requestDto.getEnabled());
        languageRepository.save(language);
        return UpdateLanguageResponse.builder()
                .success(true)
                .message("Translation in progress").build();
    }
}
