// Roulette animation - Classic slot machine style
// Displays rewards spinning horizontally with dramatic slowdown

animations.register("roulette", {
    phases: [
        // Phase 1: Fast initial spin
        {
            name: "fast-spin",
            duration: 2000,
            interval: 50,
            speedCurve: "LINEAR",
            onStart: function(context) {
                // Initialize slots with random items
                var slots = [10, 11, 12, 13, 14, 15, 16];
                for (var i = 0; i < slots.length; i++) {
                    context.inventory().setRandomItem(slots[i]);
                }

                // Play start sound
                context.player().playSound("block.note_block.pling", 1.0, 0.5);
            },
            onTick: function(context, tickData) {
                var slots = [10, 11, 12, 13, 14, 15, 16];

                // Rotate items through slots
                context.inventory().rotateItems(slots);

                // Play tick sound
                if (tickData.tickNumber() % 3 === 0) {
                    var pitch = 0.8 + (tickData.progress() * 0.4);
                    context.player().playSound("block.note_block.hat", 0.3, pitch);
                }
            }
        },

        // Phase 2: Medium spin with ease out
        {
            name: "medium-spin",
            duration: 2500,
            interval: 100,
            speedCurve: "EASE_OUT",
            onTick: function(context, tickData) {
                var slots = [10, 11, 12, 13, 14, 15, 16];
                context.inventory().rotateItems(slots);

                // Slower ticking sound
                var pitch = 1.0 - (tickData.progress() * 0.2);
                context.player().playSound("block.note_block.hat", 0.5, pitch);
            }
        },

        // Phase 3: Dramatic slowdown
        {
            name: "slowdown",
            duration: 3000,
            interval: 200,
            speedCurve: "EASE_OUT",
            onTick: function(context, tickData) {
                var slots = [10, 11, 12, 13, 14, 15, 16];
                context.inventory().rotateItems(slots);

                // Deep, slow tick sounds
                var pitch = 1.0 - (tickData.progress() * 0.4);
                context.player().playSound("block.note_block.bass", 0.7, pitch);
            }
        },

        // Phase 4: Final slowdown
        {
            name: "final-slowdown",
            duration: 2000,
            interval: 400,
            speedCurve: "EASE_OUT",
            onTick: function(context, tickData) {
                var slots = [10, 11, 12, 13, 14, 15, 16];
                context.inventory().rotateItems(slots);

                // Very slow, deep sounds
                var pitch = 0.6 - (tickData.progress() * 0.2);
                context.player().playSound("block.note_block.bass", 0.8, pitch);
            }
        },

        // Phase 5: Reveal - clear outer slots and show winner
        {
            name: "reveal",
            duration: 100,
            onStart: function(context) {
                // Clear all slots except center
                context.inventory().clear(10);
                context.inventory().clear(11);
                context.inventory().clear(15);
                context.inventory().clear(16);

                // Show winning item in center
                context.inventory().setWinningItem(13, context.crate().getReward());

                // Highlight with glass borders
                context.inventory().highlightSlot(12, "LIME_STAINED_GLASS_PANE");
                context.inventory().highlightSlot(14, "LIME_STAINED_GLASS_PANE");

                // Victory sound
                context.player().playSound("entity.player.levelup", 1.0, 1.0);
                context.player().playSound("block.note_block.chime", 1.0, 1.5);
            }
        },

        // Phase 6: Celebration
        {
            name: "celebrate",
            duration: 1500,
            interval: 200,
            onTick: function(context, tickData) {
                // Blinking highlight effect
                if (tickData.tickNumber() % 2 === 0) {
                    context.inventory().highlightSlot(12, "YELLOW_STAINED_GLASS_PANE");
                    context.inventory().highlightSlot(14, "YELLOW_STAINED_GLASS_PANE");
                } else {
                    context.inventory().highlightSlot(12, "LIME_STAINED_GLASS_PANE");
                    context.inventory().highlightSlot(14, "LIME_STAINED_GLASS_PANE");
                }
            }
        }
    ],

    onComplete: function(context) {
        var crateName = context.crate().displayName();

        context.player().sendTitle(
            "<gradient:#FFD700:#FFA500>CONGRATULATIONS!",
            "<yellow>You opened: " + crateName,
            10,
            60,
            20
        );

        // Only close if no rerolls available
        if (!context.crate().hasRerolls()) {
            context.inventory().close(40);
        }
    },

    onCancel: function(context) {
        context.inventory().closeImmediately();
        context.player().sendMessage("<red>Animation cancelled");
    }
});