package fr.traqueur.crates.models;

import fr.traqueur.crates.api.models.crates.Crate;
import fr.traqueur.crates.api.models.crates.Reward;
import fr.traqueur.crates.api.models.animations.Animation;
import fr.traqueur.crates.api.settings.models.ItemStackWrapper;
import fr.traqueur.structura.annotations.validation.Max;
import fr.traqueur.structura.annotations.validation.Min;
import fr.traqueur.structura.api.Loadable;

import java.util.List;

public record ZCrate(String id,
                     String displayName,
                     @Min(9) @Max(54) int size,
                     Animation animation,
                     String title,
                     List<Reward> rewards,
                     String relatedMenu) implements Crate, Loadable {

    public ZCrate {
        if (size % 9 != 0) {
            throw new IllegalArgumentException("Crate size must be a multiple of 9");
        }
    }

    @Override
    public ItemStackWrapper randomDisplay() {
        double randomIndex = Math.random() * rewards.size();
        return rewards.get((int) randomIndex).displayItem();
    }

    @Override
    public Reward generateReward() {
        double totalWeight = rewards.stream().mapToDouble(Reward::weight).sum();
        double randomValue = Math.random() * totalWeight;
        double cumulativeWeight = 0.0;

        for (Reward reward : rewards) {
            cumulativeWeight += reward.weight();
            if (randomValue <= cumulativeWeight) {
                return reward;
            }
        }

        return rewards.getLast();
    }

}
