package fr.traqueur.crates.api.models.crates;

import fr.traqueur.crates.api.models.User;
import fr.traqueur.crates.api.models.algorithms.RandomAlgorithm;
import fr.traqueur.crates.api.models.animations.Animation;
import fr.traqueur.crates.api.settings.models.ItemStackWrapper;

import java.util.List;

public interface Crate {

    String id();

    String displayName();

    Key key();

    Animation animation();

    RandomAlgorithm algorithm();

    String relatedMenu();

    List<Reward> rewards();

    int maxRerolls();

    /**
     * Gets the conditions that must be met to open this crate.
     *
     * @return the list of conditions, empty if no conditions
     */
    List<OpenCondition> conditions();

    ItemStackWrapper randomDisplay();

    Reward generateReward(User user);
}
