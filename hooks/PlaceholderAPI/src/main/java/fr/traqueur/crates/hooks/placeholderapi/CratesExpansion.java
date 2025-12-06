package fr.traqueur.crates.hooks.placeholderapi;

import fr.traqueur.crates.api.CratesPlugin;
import fr.traqueur.crates.api.managers.UsersManager;
import fr.traqueur.crates.api.models.User;
import fr.traqueur.crates.api.models.crates.Crate;
import fr.traqueur.crates.api.registries.CratesRegistry;
import fr.traqueur.crates.api.registries.Registry;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CratesExpansion extends PlaceholderExpansion {

    private static final String[] UNITS = {"", "K", "M", "B", "T", "Q"};
    private static final double LOG_1000 = Math.log(1000);

    private final CratesPlugin plugin;

    public CratesExpansion(CratesPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "zcrates";
    }

    @Override
    public @NotNull String getAuthor() {
        return String.join(", ", plugin.getDescription().getAuthors());
    }

    @Override
    public @NotNull String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public @Nullable String onPlaceholderRequest(Player player, @NotNull String params) {
        String[] split = params.split("_");
        String param = split[0];
        UsersManager usersManager = plugin.getManager(UsersManager.class);
        Crate crate = Registry.get(CratesRegistry.class).getById(param);
        if(crate != null) {
            User user = usersManager.getUser(player.getUniqueId());
            switch (split[1]) {
                case "keys" -> {
                    return formatCompact(crate.key().count(player));
                }
                case "keys-raw" -> {
                    return String.valueOf(crate.key().count(player));
                }
                case "opened" -> {
                    long opened = user.crateOpenings().stream()
                            .filter(crateOpening -> crateOpening.crateId().equals(param))
                            .count();
                    return formatCompact(opened);
                }
                case "opened-raw" -> {
                    return String.valueOf(user.crateOpenings().stream()
                            .filter(crateOpening -> crateOpening.crateId().equals(param))
                            .count());
                }
            }
        }

        if (params.startsWith("crates_opened")) {
            User user = usersManager.getUser(player.getUniqueId());
            long opened = user.crateOpenings().size();
            if(params.endsWith("_raw")) {
                return String.valueOf(opened);
            }
            return formatCompact(opened);
        }
        return null;
    }

    /**
     * Formats a number in compact notation (1.2K, 3.5M, etc.).
     *
     * <p>Uses logarithm base 1000 to determine the appropriate unit:</p>
     * <ul>
     *     <li>unitIndex = floor(log(value) / log(1000))</li>
     *     <li>Supported units: K (10^3), M (10^6), B (10^9), T (10^12), Q (10^15)</li>
     * </ul>
     *
     * @param value the number to format
     * @return the formatted string (e.g., "1.2K", "3.5M")
     */
    private String formatCompact(long value) {
        if (value < 1000) {
            return String.valueOf(value);
        }

        int unitIndex = (int) (Math.log(value) / LOG_1000);
        unitIndex = Math.min(unitIndex, UNITS.length - 1);

        double scaledValue = value / Math.pow(1000, unitIndex);

        if (scaledValue == (long) scaledValue) {
            return String.format("%d%s", (long) scaledValue, UNITS[unitIndex]);
        }
        return String.format("%.1f%s", scaledValue, UNITS[unitIndex]);
    }
}
