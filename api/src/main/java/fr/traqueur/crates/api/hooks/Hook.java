package fr.traqueur.crates.api.hooks;

/**
 * Interface representing a hook that can be enabled.
 */
public interface Hook {

    /**
     * Method to be called when the hook is enabled.
     */
    void onEnable();

}
