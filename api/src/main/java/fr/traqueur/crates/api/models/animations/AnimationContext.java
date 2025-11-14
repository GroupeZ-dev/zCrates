package fr.traqueur.crates.api.models.animations;

import fr.traqueur.crates.api.models.Crate;
import fr.traqueur.crates.api.models.Wrapper;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public record AnimationContext(Wrapper<Player> player, Wrapper<Inventory> inventory, Wrapper<Crate> crate) { }
