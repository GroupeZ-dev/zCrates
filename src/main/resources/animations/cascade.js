animations.register("cascade", {
    phases: [
        // Fill inventory progressively
        {
            name: "fill",
            duration: 1400,
            interval: 100,
            speedCurve: "EASE_IN",
            onTick: function(context, tickData) {
                var tick = tickData.tickNumber;

                // Fill slots one by one
                if (tick < 9) {
                    context.inventory().setRandomItem(tick);
                    context.player().playSound("BLOCK_NOTE_BLOCK_PLING", 0.7, 1.0 + (tick * 0.1));
                } else if (tick < 18) {
                    var slot = 9 + (tick - 9);
                    context.inventory().setRandomItem(slot);
                    context.player().playSound("BLOCK_NOTE_BLOCK_PLING", 0.7, 1.0 + (tick * 0.05));
                } else if (tick < 27) {
                    var slot = 18 + (tick - 18);
                    context.inventory().setRandomItem(slot);
                    context.player().playSound("BLOCK_NOTE_BLOCK_PLING", 0.7, 1.5);
                }
            }
        },

        // Highlight center slot
        {
            name: "highlight",
            duration: 300,
            interval: 50,
            onTick: function(context, tickData) {
                // Blinking effect
                if (tickData.tickNumber % 2 === 0) {
                    context.inventory().highlightSlot(13, "YELLOW_STAINED_GLASS_PANE");
                } else {
                    context.inventory().highlightSlot(13, "ORANGE_STAINED_GLASS_PANE");
                }
            }
        },

        // Reveal winner
        {
            name: "reveal",
            duration: 200,
            onStart: function(context) {
                // Clear all except center
                for (var i = 0; i < 27; i++) {
                    if (i !== 13) {
                        context.inventory().clear(i);
                    }
                }

                // Set winning item
                context.inventory().setWinningItem(13, context.crate().getReward());
                context.player().playSound("ENTITY_PLAYER_LEVELUP", 1.0, 1.0);
            }
        }
    ],

    onComplete: function(context) {
        context.player().sendTitle(
            "<yellow><bold>★ GAGNÉ ★",
            "<gold>Nouvelle récompense!",
            5,
            40,
            10
        );
        context.inventory().close(50);
    },

    onCancel: function(context) {
        context.inventory().closeImmediately();
        context.player().sendMessage("<red>Animation interrompue");
    }
});