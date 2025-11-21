/**
 * Pity System Algorithm
 *
 * Guarantees a legendary reward after a certain number of openings
 * without obtaining one. Uses weight threshold to identify legendary rewards.
 */
algorithms.register("pity_system", (context) => {
    const rewards = context.rewards();
    const history = context.history();
    const pityThreshold = 10;
    const legendaryWeightThreshold = 5.0;

    const legendaryRewards = rewards.filterByMaxWeight(legendaryWeightThreshold);

    if (legendaryRewards.size() === 0) {
        return rewards.weightedRandom();
    }

    const openingsSinceLegendary = findMaxOpeningsSinceLegendary(legendaryRewards, history);

    if (openingsSinceLegendary >= pityThreshold) {
        return rewards.weightedRandom(legendaryRewards);
    }

    return rewards.weightedRandom();
});

const findMaxOpeningsSinceLegendary = (legendaryRewards, history) => {
    let maxOpenings = -1;

    for (let i = 0; i < legendaryRewards.size(); i++) {
        let count = history.countOpeningsSinceReward(legendaryRewards.get(i).id());

        if (count === -1) {
            count = history.getTotalOpenings();
        }

        if (count > maxOpenings) {
            maxOpenings = count;
        }
    }

    return maxOpenings;
};
