// Simple animation for testing basic loading
// File: simple.js

animations.register("simple", {
    phases: [
        {
            name: "test_phase",
            duration: 1000,
            interval: 100,
            onStart: function(context) {
                context.player().sendMessage("<green>Animation started!");
            },
            onTick: function(context, tickData) {
                context.player().sendMessage("<gray>Tick " + tickData.tickNumber() + " - Progress: " + Math.round(tickData.progress() * 100) + "%");
            },
            onComplete: function(context) {
                context.player().sendMessage("<green>Phase completed!");
            }
        }
    ],
    onComplete: function(context) {
        context.player().sendMessage("<gold><bold>Animation finished!");

        // Only close if no rerolls available
        if (!context.crate().hasRerolls()) {
            context.inventory().close(20);
        }
    },
    onCancel: function(context) {
        context.player().sendMessage("<red>Animation cancelled!");
    }
});