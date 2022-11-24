package com.example.translation.repositories;

import com.example.translation.enums.AdminStatus;
import com.example.translation.models.Translations;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TranslationsRepository extends JpaRepository<Translations, String> {

    List<Translations> findByTextIdInAndLanguageCode(List<String> textIds, String languageCode);
    List<Translations> findByAdminStatus(AdminStatus adminStatus);
}
