package com.example.translation.repositories;

import com.example.translation.models.Translations;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TranslationsRepository extends JpaRepository<Translations, String> {
}
