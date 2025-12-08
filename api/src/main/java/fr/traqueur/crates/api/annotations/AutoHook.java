package fr.traqueur.crates.api.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used to mark an {@link fr.traqueur.crates.api.hooks.Hook} for automatic registration.
 * When the specified plugin is present, the hook will be enabled automatically.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface AutoHook {

    /**
     * The name of the plugin that this hook integrates with.
     * This should match the exact plugin name as it appears in the plugin.yml.
     * The hook will only be enabled if a plugin with this name is present.
     *
     * @return the plugin name
     */
    String value();
}