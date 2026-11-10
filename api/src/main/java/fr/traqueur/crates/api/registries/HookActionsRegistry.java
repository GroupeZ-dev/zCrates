package fr.traqueur.crates.api.registries;

import fr.traqueur.crates.api.models.animations.HookContext;

import java.util.function.Function;

/**
 * Registry for hook-provided action namespaces accessible from JavaScript animations.
 * <p>
 * Hooks register a factory under a namespace name. At animation execution time,
 * the factory receives the {@link HookContext} of the running animation and returns a bound
 * Java object whose public methods become callable from JavaScript via
 * {@code context.hook("namespaceName").method(...)}.
 * <p>
 * The factory is called again for each script callback of the animation: an object that
 * must keep state across callbacks keeps it elsewhere (for instance keyed by player).
 * <p>
 * This registry is intentionally never cleared on hot-reload: hooks register
 * their factories once at startup via {@link fr.traqueur.crates.api.hooks.Hook#onEnable()},
 * or from another plugin's own enable.
 */
public interface HookActionsRegistry extends Registry<String, Function<HookContext, Object>> {
}
