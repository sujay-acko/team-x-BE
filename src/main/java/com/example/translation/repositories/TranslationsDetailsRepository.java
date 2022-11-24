package com.example.translation.repositories;

import com.example.translation.enums.AdminStatus;
import com.example.translation.models.TranslationDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TranslationsDetailsRepository extends JpaRepository<TranslationDetails, String> {

    List<TranslationDetails> findByAdminStatus(AdminStatus adminStatus);
}
