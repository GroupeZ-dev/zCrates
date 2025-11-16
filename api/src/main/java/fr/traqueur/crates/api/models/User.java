package fr.traqueur.crates.api.models;

import java.util.Map;
import java.util.UUID;

public interface User {

    UUID uuid();

    int getKeyCount(String keyName);

    void addKeys(String keyName, int amount);

    void removeKeys(String keyName, int amount);

    boolean hasKey(String keyName);

    Map<String, Integer> getAllKeys();

}
