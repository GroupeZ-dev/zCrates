package fr.traqueur.crates.api.models.crates;

import fr.traqueur.crates.api.models.animations.Animation;
import fr.traqueur.crates.api.settings.models.ItemStackWrapper;

import java.util.List;

public interface Crate {

    String id();

    String displayName();

    Animation animation();

    String relatedMenu();

    List<Reward> rewards();

    ItemStackWrapper randomDisplay();

    Reward generateReward();
}
