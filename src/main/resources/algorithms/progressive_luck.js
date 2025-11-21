// Progressive luck algorithm
// File: progressive_luck.js
// Gradually increases chances of rare rewards the more you open without getting one

algorithms.register("progressive_luck", function(context) {
    var rewards = context.getRewards();
    var rareRewards = context.filterByMaxWeight(10.0); // Rewards with weight <= 10

    if (rareRewards.length === 0) {
        return context.weightedRandom(rewards);
    }

    // Count openings since last rare reward
    var openingsSinceRare = -1;
    for (var i = 0; i < rareRewards.length; i++) {
        var count = context.countOpeningsSinceReward(rareRewards[i].id());
        if (count === -1) {
            count = context.getTotalOpenings();
        }
        if (count > openingsSinceRare) {
            openingsSinceRare = count;
        }
    }

    // Every 3 openings without a rare, increase chance by 10%
    var luckBoost = Math.floor(openingsSinceRare / 3) * 0.10;
    var rareChance = 0.15 + luckBoost; // Base 15% chance for rare

    // Cap at 80% chance
    if (rareChance > 0.80) {
        rareChance = 0.80;
    }

    // Roll for rare vs common
    var roll = Math.random();
    if (roll < rareChance) {
        // Give a rare reward
        return context.weightedRandom(rareRewards);
    } else {
        // Give a common reward
        var commonRewards = context.filterByMinWeight(10.0);
        if (commonRewards.length === 0) {
            return context.weightedRandom(rewards);
        }
        return context.weightedRandom(commonRewards);
    }
});
