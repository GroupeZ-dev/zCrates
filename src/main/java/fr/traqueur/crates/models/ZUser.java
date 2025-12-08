package fr.traqueur.crates.models;

import fr.traqueur.crates.api.models.CrateOpening;
import fr.traqueur.crates.api.models.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public record ZUser(UUID uuid, Map<String, Integer> keys, List<CrateOpening> crateOpenings) implements User {

    public ZUser(UUID uuid) {
        this(uuid, new HashMap<>(), new ArrayList<>());
    }

    public ZUser(UUID uuid, Map<String, Integer> keys, List<CrateOpening> crateOpenings) {
        this.uuid = uuid;
        this.keys = new HashMap<>(keys);
        this.crateOpenings = new ArrayList<>(crateOpenings);
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

    @Override
    public List<CrateOpening> crateOpenings() {
        return new ArrayList<>(this.crateOpenings);
    }

    @Override
    public CrateOpening addCrateOpening(String crateId, String rewardId) {
        long timestamp = System.currentTimeMillis();
        CrateOpening opening = new CrateOpening(UUID.randomUUID(), this.uuid, crateId, rewardId, timestamp);
        this.crateOpenings.add(opening);
        return opening;
    }
}
