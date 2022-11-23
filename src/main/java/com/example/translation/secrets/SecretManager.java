package com.example.translation.secrets;

import com.example.translation.pojo.DBCredentials;
import com.example.translation.pojo.Secrets;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class SecretManager implements ISecretManager{

    private Map<String, DBCredentials> dbCredentials;

    @Value(value = "${secrets.path.file}")
    private String filePath;

    @PostConstruct
    void loadSecrets() throws IOException {

        try (InputStream stream = new FileInputStream(filePath)) {
            Secrets secrets = new ObjectMapper().readValue(stream, Secrets.class);
            dbCredentials = secrets.getDbCredentials()
                    .stream()
                    .collect(Collectors.toMap(DBCredentials::getName, dbCredential -> dbCredential));
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException("secret_file does not exists: " + filePath);
        }
    }

    @Override
    public DBCredentials getDBCredentials(String dbname) {
        return dbCredentials.get(dbname);
    }
}
