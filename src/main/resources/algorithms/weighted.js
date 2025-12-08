/**
 * Weighted Random Algorithm
 *
 * Default algorithm that uses standard weight-based random selection.
 * Higher weight rewards have proportionally higher chances of being selected.
 */
algorithms.register("weighted", (context) => context.rewards().weightedRandom());
