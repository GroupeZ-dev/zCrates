// Simple animation - Quick reveal with minimal fanfare
// Good for quick openings or testing

animations.register("simple", {
    phases: [
        // Phase 1: Quick flash effect
        {
            name: "flash",
            duration: 500,
            interval: 5,
            onStart: function(context) {
                context.player().playSound("block.note_block.pling", 1.0, 1.0);
            },
            onTick: function(context, tickData) {
                // Flash different random items in center
                if (tickData.tickNumber() % 2 === 0) {
                    context.inventory().setRandomItem(13);
                }
            }
        },

        // Phase 2: Reveal the prize
        {
            name: "reveal",
            duration: 100,
            onStart: function(context) {
                // Clear inventory and show winning item
                context.inventory().clear();
                context.inventory().setWinningItem(13, context.crate().getReward());

                // Play success sound
                context.player().playSound("entity.experience_orb.pickup", 1.0, 1.2);
            }
        }
    ],

    onComplete: function(context) {
        context.player().sendMessage("<green><bold>✓</bold> You received a reward!");

        // Only close if no rerolls available
        if (!context.crate().hasRerolls()) {
            context.inventory().close(20);
        }
    },

    onCancel: function(context) {
        context.inventory().closeImmediately();
        context.player().sendMessage("<red>Animation cancelled");
    }
});