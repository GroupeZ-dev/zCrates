// Roulette style crate opening animation
// File: roulette.js

animations.register("roulette", {
    phases: [
        // Phase 1: Startup - Quick item preview
        {
            name: "startup",
            duration: 500,
            interval: 50,
            onTick: function(context, tickData) {
                var slots = [10, 11, 12, 13, 14, 15, 16];
                var currentSlot = slots[tickData.tickNumber() % slots.length];

                context.inventory().clear();
                context.inventory().setRandomItem(currentSlot);

                // Increasing pitch sound
                var pitch = 0.8 + (tickData.tickNumber() * 0.02);
                context.player().playSound("ui.button.click", 1.0, pitch);
            }
        },

        // Phase 2: Fast spinning with ease out
        {
            name: "spinning",
            duration: 2000,
            interval: 100,
            speedCurve: "EASE_OUT",
            onTick: function(context, tickData) {
                var slots = [10, 11, 12, 13, 14, 15, 16];
                context.inventory().rotateItems(slots);
                context.player().playSound("block.note_block.hat", 0.5, 1.0);
            }
        },

        // Phase 3: Slowdown
        {
            name: "slowdown",
            duration: 1500,
            interval: 150,
            onTick: function(context, tickData) {
                var slots = [10, 11, 12, 13, 14, 15, 16];
                context.inventory().rotateItems(slots);

                // Slower, lower pitch sound
                context.player().playSound("block.note_block.hat", 0.5, 0.8);
            }
        },

        // Phase 4: Reveal winner
        {
            name: "reveal",
            duration: 100,
            onStart: function(context) {
                // Set winning item in center
                context.inventory().setWinningItem(13, context.crate().getReward());

                // Highlight with glass pane
                let highlightSlots = [10, 11, 12, 14, 15, 16];
                highlightSlots.forEach(function(slot) {
                    context.inventory().highlightSlot(slot, "YELLOW_STAINED_GLASS_PANE");
                });

                // Success sounds
                context.player().playSound("entity.player.levelup", 1.0, 1.0);
            }
        }
    ],

    onComplete: function(context) {
        let crateName = context.crate().displayName();

        context.player().sendTitle(
            "<gold><bold>FÉLICITATIONS!",
            "<yellow>Vous avez ouvert: " + crateName,
            10,
            70,
            20
        );

        // Only close if no rerolls available
        if (!context.crate().hasRerolls()) {
            context.inventory().close(60);
        }
    },

    onCancel: function(context) {
        context.inventory().closeImmediately();
        context.player().sendMessage("<red>Animation annulée");
    }
});