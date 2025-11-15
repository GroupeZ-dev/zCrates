package fr.traqueur.crates.api.models;

import fr.traqueur.crates.api.settings.models.ItemStackWrapper;
import fr.traqueur.structura.annotations.Polymorphic;
import fr.traqueur.structura.api.Loadable;
import org.bukkit.entity.Player;

@Polymorphic()
public interface Reward extends Loadable {

    String id();

    double weight();

    ItemStackWrapper displayItem();

    void give(Player player);

}
