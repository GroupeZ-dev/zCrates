package fr.traqueur.crates.models.wrappers;

import fr.traqueur.crates.api.models.Wrapper;
import fr.traqueur.crates.api.models.crates.Reward;

import java.util.List;

/**
 * JavaScript-safe wrapper for rewards list with algorithm helper methods.
 * Exposes utility methods for filtering and selecting rewards.
 */
public class RewardsWrapper extends Wrapper<List<Reward>> {

    public RewardsWrapper(List<Reward> delegate) {
        super(delegate);
    }
    
    public int size() {
        return this.delegate.size();
    }

    /**
     * Get the underlying rewards list
     */
    public List<Reward> getAll() {
        return delegate;
    }

    /**
     * Helper: Perform weighted random selection from rewards
     * Uses the standard weight-based algorithm
     */
    public Reward weightedRandom() {
        return weightedRandom(delegate);
    }

    /**
     * Helper: Perform weighted random selection from a custom rewards list
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
    public Reward findById(String rewardId) {
        return delegate.stream()
                .filter(r -> r.id().equals(rewardId))
                .findFirst()
                .orElse(null);
    }

    /**
     * Helper: Filter rewards by minimum weight threshold
     */
    public List<Reward> filterByMinWeight(double minWeight) {
        return delegate.stream()
                .filter(r -> r.weight() >= minWeight)
                .toList();
    }

    /**
     * Helper: Filter rewards by maximum weight threshold (e.g., only rare rewards)
     */
    public List<Reward> filterByMaxWeight(double maxWeight) {
        return delegate.stream()
                .filter(r -> r.weight() <= maxWeight)
                .toList();
    }
}