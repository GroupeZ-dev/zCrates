package fr.traqueur.crates.models;

import fr.traqueur.crates.api.models.Crate;
import fr.traqueur.crates.api.models.Wrapper;

/**
 * Wrapper for Crate that exposes only safe information for animations.
 * This prevents animations from modifying crate data.
 */
public class CrateWrapper extends Wrapper<Crate> {

    public CrateWrapper(Crate handle) {
        super(handle);
    }
}