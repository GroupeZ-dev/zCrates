animations.register("instant", {
    phases: [
        {
            name: "reveal",
            duration: 500,
            onStart: function(context) {
                // Immediately show the reward
                context.inventory().clear();
                context.inventory().setWinningItem(13, context.crate().getReward());

                // Play sound
                context.player().playSound("ENTITY_FIREWORK_ROCKET_BLAST", 1.0, 1.2);
            },
            onComplete: function(context) {
                context.player().sendMessage("<green>✓ Récompense obtenue!");
            }
        }
    ],

    onComplete: function(context) {
        // Close immediately
        context.inventory().close(10);
    },

    onCancel: function(context) {
        context.inventory().closeImmediately();
    }
});