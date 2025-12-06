// Cascade animation - Items fall from top to bottom
// Creates a waterfall effect revealing the prize in the center

animations.register("cascade", {
    phases: [
        // Phase 1: Fill top row with cascade effect
        {
            name: "fill-top",
            duration: 900,
            interval: 5,
            speedCurve: "EASE_IN",
            onTick: function(context, tickData) {
                var tick = tickData.tickNumber();

                // Fill top row from left to right
                if (tick < 9) {
                    context.inventory().setRandomItem(tick);
                    var pitch = 1.0 + (tick * 0.1);
                    context.player().playSound("block.note_block.pling", 0.5, pitch);
                }
            }
        },

        // Phase 2: Fill middle row
        {
            name: "fill-middle",
            duration: 900,
            interval: 5,
            speedCurve: "EASE_IN",
            onTick: function(context, tickData) {
                var tick = tickData.tickNumber();

                // Fill middle row
                if (tick < 9) {
                    var slot = 9 + tick;
                    context.inventory().setRandomItem(slot);
                    var pitch = 1.2 + (tick * 0.05);
                    context.player().playSound("block.note_block.pling", 0.5, pitch);
                }
            }
        },

        // Phase 3: Fill bottom row
        {
            name: "fill-bottom",
            duration: 900,
            interval: 5,
            speedCurve: "EASE_IN",
            onTick: function(context, tickData) {
                var tick = tickData.tickNumber();

                // Fill bottom row
                if (tick < 9) {
                    var slot = 18 + tick;
                    context.inventory().setRandomItem(slot);
                    var pitch = 1.4 + (tick * 0.05);
                    context.player().playSound("block.note_block.pling", 0.5, pitch);
                }
            }
        },

        // Phase 4: Pulse effect on center slot
        {
            name: "pulse-center",
            duration: 600,
            interval: 6,
            onTick: function(context, tickData) {
                // Create pulsing effect with different colors
                var cycle = tickData.tickNumber() % 3;
                if (cycle === 0) {
                    context.inventory().highlightSlot(13, "YELLOW_STAINED_GLASS_PANE");
                } else if (cycle === 1) {
                    context.inventory().highlightSlot(13, "ORANGE_STAINED_GLASS_PANE");
                } else {
                    context.inventory().highlightSlot(13, "LIME_STAINED_GLASS_PANE");
                }

                if (tickData.tickNumber() % 2 === 0) {
                    context.player().playSound("block.note_block.bell", 0.4, 1.5);
                }
            }
        },

        // Phase 5: Clear outer slots, keep center area
        {
            name: "clear-outer",
            duration: 500,
            interval: 5,
            onStart: function(context) {
                // Clear all except center 3x3 area (slots 10-12, 13, 14-16)
                var keepSlots = [10, 11, 12, 13, 14, 15, 16];
                for (var i = 0; i < 27; i++) {
                    var shouldKeep = false;
                    for (var j = 0; j < keepSlots.length; j++) {
                        if (i === keepSlots[j]) {
                            shouldKeep = true;
                            break;
                        }
                    }
                    if (!shouldKeep) {
                        context.inventory().clear(i);
                    }
                }
                context.player().playSound("entity.item.pickup", 1.0, 0.5);
            },
            onTick: function(context, tickData) {
                // Continue highlighting center
                if (tickData.tickNumber() % 2 === 0) {
                    context.inventory().highlightSlot(13, "LIME_STAINED_GLASS_PANE");
                }
            }
        },

        // Phase 6: Final reveal
        {
            name: "reveal",
            duration: 100,
            onStart: function(context) {
                // Clear all except center
                for (var i = 0; i < 27; i++) {
                    if (i !== 13) {
                        context.inventory().clear(i);
                    }
                }

                // Show winning item
                context.inventory().setWinningItem(13, context.crate().getReward());

                // Add decorative borders
                context.inventory().highlightSlot(12, "LIME_STAINED_GLASS_PANE");
                context.inventory().highlightSlot(14, "LIME_STAINED_GLASS_PANE");

                // Victory sounds
                context.player().playSound("entity.player.levelup", 1.0, 1.0);
                context.player().playSound("ui.toast.challenge_complete", 1.0, 1.0);
            }
        }
    ],

    onComplete: function(context) {
        context.player().sendTitle(
            "<gradient:#00FF00:#00AA00>SUCCESS!",
            "<green>You won a prize!",
            5,
            50,
            10
        );

        // Only close if no rerolls available
        if (!context.crate().hasRerolls()) {
            context.inventory().close(40);
        }
    },

    onCancel: function(context) {
        context.inventory().closeImmediately();
        context.player().sendMessage("<red>Animation interrupted");
    }
});