/**
 * Progressive Luck Algorithm
 *
 * Gradually increases chances of rare rewards the more you open without getting one.
 * Every 3 openings without a rare reward increases the chance by 10%, capped at 80%.
 */
algorithms.register("progressive_luck", (context) => {
    const rewards = context.rewards();
    const history = context.history();

    const rareWeightThreshold = 10.0;
    const baseRareChance = 0.15;
    const boostPerCycle = 0.10;
    const cycleLength = 3;
    const maxRareChance = 0.80;

    const rareRewards = rewards.filterByMaxWeight(rareWeightThreshold);

    if (rareRewards.size() === 0) {
        return rewards.weightedRandom();
    }

    const openingsSinceRare = findMaxOpeningsSinceRare(rareRewards, history);
    const luckBoost = Math.floor(openingsSinceRare / cycleLength) * boostPerCycle;
    const rareChance = Math.min(baseRareChance + luckBoost, maxRareChance);

    if (Math.random() < rareChance) {
        return rewards.weightedRandom(rareRewards);
    }

    const commonRewards = rewards.filterByMinWeight(rareWeightThreshold);

    if (commonRewards.size() === 0) {
        return rewards.weightedRandom();
    }

    return rewards.weightedRandom(commonRewards);
});

const findMaxOpeningsSinceRare = (rareRewards, history) => {
    let maxOpenings = -1;

    for (let i = 0; i < rareRewards.size(); i++) {
        let count = history.countOpeningsSinceReward(rareRewards.get(i).id());

        if (count === -1) {
            count = history.getTotalOpenings();
        }

        if (count > maxOpenings) {
            maxOpenings = count;
        }
    }

    return maxOpenings;
};
