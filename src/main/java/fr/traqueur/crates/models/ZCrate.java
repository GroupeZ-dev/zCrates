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
                     Animation animation,
                     List<Reward> rewards,
                     String relatedMenu) implements Crate, Loadable {


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
