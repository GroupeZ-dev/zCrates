package fr.traqueur.crates.models;

import fr.traqueur.crates.api.models.User;
import fr.traqueur.crates.api.models.algorithms.AlgorithmContext;
import fr.traqueur.crates.api.models.algorithms.RandomAlgorithm;
import fr.traqueur.crates.api.models.crates.Crate;
import fr.traqueur.crates.api.models.crates.Key;
import fr.traqueur.crates.api.models.crates.Reward;
import fr.traqueur.crates.api.models.animations.Animation;
import fr.traqueur.crates.api.settings.models.ItemStackWrapper;
import fr.traqueur.crates.models.algorithms.ZAlgorithmContext;
import fr.traqueur.structura.annotations.validation.Max;
import fr.traqueur.structura.annotations.validation.Min;
import fr.traqueur.structura.api.Loadable;

import java.util.List;

public record ZCrate(String id,
                     String displayName,
                     Key key,
                     Animation animation,
                     RandomAlgorithm algorithm,
                     List<Reward> rewards,
                     String relatedMenu) implements Crate, Loadable {


    @Override
    public ItemStackWrapper randomDisplay() {
        double randomIndex = Math.random() * rewards.size();
        return rewards.get((int) randomIndex).displayItem();
    }

    @Override
    public Reward generateReward(User user) {
        // Create algorithm context with player history
        AlgorithmContext context = new ZAlgorithmContext(
                rewards,
                id,
                user.getCrateOpenings().stream()
                        .filter(opening -> opening.crateId().equals(id))
                        .toList(),
                user.uuid().toString()
        );

        // Delegate to the algorithm
        return algorithm.selector().apply(context);
    }

}
