algorithms.register("pity_system", function(context) {
    var rewards = context.getRewards();

    // Define which rewards are considered "legendary" (weight <= 5)
    var legendaryRewards = context.filterByMaxWeight(5.0);

    if (legendaryRewards.length === 0) {
        // No legendary rewards defined, fall back to weighted random
        return context.weightedRandom(rewards);
    }

    // Check how many openings have occurred since last legendary
    var openingsSinceLegendary = -1;

    for (var i = 0; i < legendaryRewards.length; i++) {
        var rewardId = legendaryRewards[i].id();
        var count = context.countOpeningsSinceReward(rewardId);

        // If this legendary was never obtained, count is -1
        // Use total openings instead
        if (count === -1) {
            count = context.getTotalOpenings();
        }

        // Track the maximum openings since any legendary
        if (count > openingsSinceLegendary) {
            openingsSinceLegendary = count;
        }
    }

    // Pity threshold: guarantee a legendary after 10 openings
    var pityThreshold = 10;

    if (openingsSinceLegendary >= pityThreshold) {
        // Guarantee a legendary reward using weighted random among legendaries
        return context.weightedRandom(legendaryRewards);
    }

    // Otherwise, use standard weighted random
    return context.weightedRandom(rewards);
});
