package com.jtbdevelopment.TwistedBattleship.factory.gamevalidators;

import com.jtbdevelopment.TwistedBattleship.state.GameFeature;
import com.jtbdevelopment.TwistedBattleship.state.TBGame;
import com.jtbdevelopment.games.factory.GameValidator;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * Date: 4/20/15
 * Time: 6:48 PM
 */
@Component
public class OptionsValidator implements GameValidator<TBGame> {
    @Override
    public boolean validateGame(final TBGame game) {
        if (game.getFeatures().stream().anyMatch(f -> f.getGroup().equals(f))) {
            return false;
        }

        Map<GameFeature, GameFeature> groupToFeature = game.getFeatures().stream().collect(Collectors.toMap(GameFeature::getGroup, f -> f, (a, b) -> a));
        return groupToFeature.size() == GameFeature.getGroupedFeatures().size() &&
                groupToFeature.size() == game.getFeatures().size();

    }

    @Override
    public String errorMessage() {
        return "Invalid combination of options!";
    }

}
