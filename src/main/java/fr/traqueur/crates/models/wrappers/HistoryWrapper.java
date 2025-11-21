package fr.traqueur.crates.models.wrappers;

import fr.traqueur.crates.api.models.CrateOpening;
import fr.traqueur.crates.api.models.Wrapper;

import java.util.List;

/**
 * JavaScript-safe wrapper for crate opening history with helper methods.
 * Exposes utility methods for analyzing player's opening patterns.
 */
public class HistoryWrapper extends Wrapper<List<CrateOpening>> {

    public HistoryWrapper(List<CrateOpening> delegate) {
        super(delegate);
    }

    /**
     * Get the underlying history list
     */
    public List<CrateOpening> getAll() {
        return delegate;
    }

    /**
     * Helper: Count how many openings have occurred since a specific reward was obtained
     * Returns -1 if the reward has never been obtained
     */
    public int countOpeningsSinceReward(String rewardId) {
        // Find the last occurrence of this reward
        int lastIndex = -1;
        for (int i = delegate.size() - 1; i >= 0; i--) {
            if (delegate.get(i).rewardId().equals(rewardId)) {
                lastIndex = i;
                break;
            }
        }

        // If never obtained, return -1
        if (lastIndex == -1) {
            return -1;
        }

        // Count openings after that index
        return delegate.size() - lastIndex - 1;
    }

    /**
     * Helper: Count total number of times a reward has been obtained
     */
    public int countRewardObtained(String rewardId) {
        return (int) delegate.stream()
                .filter(opening -> opening.rewardId().equals(rewardId))
                .count();
    }

    /**
     * Helper: Get total number of crate openings
     */
    public int getTotalOpenings() {
        return delegate.size();
    }

    /**
     * Helper: Check if player has ever obtained a specific reward
     */
    public boolean hasObtainedReward(String rewardId) {
        return delegate.stream()
                .anyMatch(opening -> opening.rewardId().equals(rewardId));
    }
}