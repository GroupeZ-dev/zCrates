// Default weighted random algorithm
// File: weighted.js
// This is the default algorithm that uses standard weight-based random selection

algorithms.register("weighted", function(context) {
    // Simply use the built-in weighted random helper
    return context.weightedRandom(context.getRewards());
});
