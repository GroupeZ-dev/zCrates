package fr.traqueur.crates.models;

import fr.traqueur.crates.api.models.User;
import fr.traqueur.crates.api.models.algorithms.AlgorithmContext;
import fr.traqueur.crates.api.models.algorithms.RandomAlgorithm;
import fr.traqueur.crates.api.models.crates.Crate;
import fr.traqueur.crates.api.models.crates.Key;
import fr.traqueur.crates.api.models.crates.OpenCondition;
import fr.traqueur.crates.api.models.crates.Reward;
import fr.traqueur.crates.api.models.animations.Animation;
import fr.traqueur.crates.api.settings.models.ItemStackWrapper;
import fr.traqueur.crates.models.wrappers.HistoryWrapper;
import fr.traqueur.crates.models.wrappers.RewardsWrapper;
import fr.traqueur.structura.annotations.Options;
import fr.traqueur.structura.annotations.defaults.DefaultInt;
import fr.traqueur.structura.api.Loadable;

import java.util.ArrayList;
import java.util.List;

public record ZCrate(String id,
                     String displayName,
                     Key key,
                     Animation animation,
                     RandomAlgorithm algorithm,
                     List<Reward> rewards,
                     String relatedMenu,
                     @Options(optional = true) @DefaultInt(0) int maxRerolls,
                     @Options(optional = true) @DefaultInt(0) int maxBatchSize,
                     @Options(optional = true) List<OpenCondition> conditions) implements Crate, Loadable {

    public ZCrate {
        if (conditions == null) {
            conditions = new ArrayList<>();
        }
    }


    @Override
    public ItemStackWrapper randomDisplay() {
        double randomIndex = Math.random() * rewards.size();
        return rewards.get((int) randomIndex).displayItem();
    }

    @Override
    public Reward generateReward(User user) {
        var history = user.crateOpenings().stream()
                .filter(opening -> opening.crateId().equals(id))
                .toList();

        var context = new AlgorithmContext(
                new RewardsWrapper(rewards),
                new HistoryWrapper(history),
                id,
                user.uuid().toString()
        );

        return algorithm.selector().apply(context);
    }

}
