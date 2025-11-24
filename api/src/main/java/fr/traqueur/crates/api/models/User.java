package fr.traqueur.crates.api.models;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface User {

    UUID uuid();

    int getKeyCount(String keyName);

    void addKeys(String keyName, int amount);

    void removeKeys(String keyName, int amount);

    boolean hasKey(String keyName);

    Map<String, Integer> getAllKeys();

    List<CrateOpening> getCrateOpenings();

    /**
     * Adds a crate opening to this user's history.
     *
     * @param crateId the crate ID
     * @param rewardId the reward ID
     * @return the created CrateOpening record
     */
    CrateOpening addCrateOpening(String crateId, String rewardId);

}
