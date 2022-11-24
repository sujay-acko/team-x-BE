package com.example.translation.repositories;

import com.example.translation.models.TextContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TextContentRepository extends JpaRepository<TextContent, String> {
}
