package fr.traqueur.crates.models.wrappers;

import fr.traqueur.crates.api.models.CrateOpening;
import fr.traqueur.crates.api.models.algorithms.AlgorithmContext;
import fr.traqueur.crates.api.models.crates.Reward;

import java.util.List;

/**
 * JavaScript-safe wrapper for algorithm context with helper functions.
 * Exposes utility methods for common algorithm patterns.
 */
public class AlgorithmWrapper {

    private final AlgorithmContext context;

    public AlgorithmWrapper(AlgorithmContext context) {
        this.context = context;
    }

    /**
     * Get all available rewards for this crate
     */
    public List<Reward> getRewards() {
        return context.rewards();
    }

    /**
     * Get the crate ID being opened
     */
    public String getCrateId() {
        return context.crateId();
    }

    /**
     * Get the complete opening history for this player and crate
     */
    public List<CrateOpening> getHistory() {
        return context.history();
    }

    /**
     * Get player UUID as string
     */
    public String getPlayerUuid() {
        return context.playerUuid();
    }

    /**
     * Helper: Perform weighted random selection from rewards
     * Uses the standard weight-based algorithm
     */
    public Reward weightedRandom(List<Reward> rewards) {
        if (rewards == null || rewards.isEmpty()) {
            return null;
        }

        double totalWeight = rewards.stream().mapToDouble(Reward::weight).sum();
        double randomValue = Math.random() * totalWeight;
        double cumulativeWeight = 0.0;

        for (Reward reward : rewards) {
            cumulativeWeight += reward.weight();
            if (randomValue <= cumulativeWeight) {
                return reward;
            }
        }

        return rewards.getLast();
    }

    /**
     * Helper: Find a reward by its ID
     */
    public Reward findRewardById(String rewardId) {
        return context.rewards().stream()
                .filter(r -> r.id().equals(rewardId))
                .findFirst()
                .orElse(null);
    }

    /**
     * Helper: Count how many openings have occurred since a specific reward was obtained
     * Returns -1 if the reward has never been obtained
     */
    public int countOpeningsSinceReward(String rewardId) {
        List<CrateOpening> history = context.history();

        // Find the last occurrence of this reward
        int lastIndex = -1;
        for (int i = history.size() - 1; i >= 0; i--) {
            if (history.get(i).rewardId().equals(rewardId)) {
                lastIndex = i;
                break;
            }
        }

        // If never obtained, return -1
        if (lastIndex == -1) {
            return -1;
        }

        // Count openings after that index
        return history.size() - lastIndex - 1;
    }

    /**
     * Helper: Count total number of times a reward has been obtained
     */
    public int countRewardObtained(String rewardId) {
        return (int) context.history().stream()
                .filter(opening -> opening.rewardId().equals(rewardId))
                .count();
    }

    /**
     * Helper: Get total number of crate openings
     */
    public int getTotalOpenings() {
        return context.history().size();
    }

    /**
     * Helper: Check if player has ever obtained a specific reward
     */
    public boolean hasObtainedReward(String rewardId) {
        return context.history().stream()
                .anyMatch(opening -> opening.rewardId().equals(rewardId));
    }

    /**
     * Helper: Filter rewards by minimum weight threshold
     */
    public List<Reward> filterByMinWeight(double minWeight) {
        return context.rewards().stream()
                .filter(r -> r.weight() >= minWeight)
                .toList();
    }

    /**
     * Helper: Filter rewards by maximum weight threshold (e.g., only rare rewards)
     */
    public List<Reward> filterByMaxWeight(double maxWeight) {
        return context.rewards().stream()
                .filter(r -> r.weight() <= maxWeight)
                .toList();
    }
}
