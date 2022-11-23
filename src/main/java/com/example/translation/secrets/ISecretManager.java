package com.example.translation.secrets;

import com.example.translation.pojo.DBCredentials;

public interface ISecretManager {
    DBCredentials getDBCredentials(String dbname);
}
