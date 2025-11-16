package fr.traqueur.crates.api.models.crates;

import fr.traqueur.structura.annotations.Polymorphic;
import fr.traqueur.structura.api.Loadable;
import org.bukkit.entity.Player;

@Polymorphic
public interface Key extends Loadable {

    String name();

    boolean has(Player player);

    void remove(Player player);

    void give(Player player);

}
