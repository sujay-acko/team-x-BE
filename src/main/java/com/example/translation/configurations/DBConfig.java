package com.example.translation.configurations;

import com.example.translation.constants.URLConstants;
import com.example.translation.secrets.ISecretManager;
import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import lombok.AllArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

@Configuration
@AllArgsConstructor
public class DBConfig {
    private final ISecretManager secretManager;

    @ConfigurationProperties(prefix = "spring.datasource")
    @Bean
    @Primary
    public DataSource getDataSource() {
        return DataSourceBuilder.create()
                .username(secretManager.getDBCredentials(URLConstants.DB_NAME).getUsername())
                .password(secretManager.getDBCredentials(URLConstants.DB_NAME).getPassword())
                .build();
    }

    @Bean
    public Translate getTranslate() {
        return TranslateOptions.newBuilder()
                .setApiKey("")
                .build()
                .getService();
    }

}
