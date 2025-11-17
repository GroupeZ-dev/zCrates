package fr.traqueur.crates.models;

import fr.traqueur.crates.api.models.User;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ZUser implements User {

    private final UUID uuid;
    private final Map<String, Integer> keys;

    public ZUser(UUID uuid) {
        this.uuid = uuid;
        this.keys = new HashMap<>();
    }

    public ZUser(UUID uuid, Map<String, Integer> keys) {
        this.uuid = uuid;
        this.keys = new HashMap<>(keys);
    }

    @Override
    public UUID uuid() {
        return this.uuid;
    }

    @Override
    public int getKeyCount(String keyName) {
        return this.keys.getOrDefault(keyName, 0);
    }

    @Override
    public void addKeys(String keyName, int amount) {
        if (amount <= 0) return;
        this.keys.put(keyName, this.getKeyCount(keyName) + amount);
    }

    @Override
    public void removeKeys(String keyName, int amount) {
        if (amount <= 0) return;
        int currentCount = this.getKeyCount(keyName);
        int newCount = Math.max(0, currentCount - amount);
        this.keys.put(keyName, newCount);
    }

    @Override
    public boolean hasKey(String keyName) {
        return this.getKeyCount(keyName) > 0;
    }

    @Override
    public Map<String, Integer> getAllKeys() {
        return new HashMap<>(this.keys);
    }
}
