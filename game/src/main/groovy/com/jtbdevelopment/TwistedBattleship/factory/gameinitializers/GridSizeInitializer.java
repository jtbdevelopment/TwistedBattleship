package com.jtbdevelopment.TwistedBattleship.factory.gameinitializers;

import com.jtbdevelopment.TwistedBattleship.state.GameFeature;
import com.jtbdevelopment.TwistedBattleship.state.TBGame;
import com.jtbdevelopment.games.factory.GameInitializer;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Date: 4/6/15
 * Time: 6:34 PM
 */
@Component
public class GridSizeInitializer implements GameInitializer<TBGame> {
    private final Map<GameFeature, Integer> sizeMap;

    public GridSizeInitializer() {
        sizeMap = new HashMap<>();
        sizeMap.put(GameFeature.Grid10x10, 10);
        sizeMap.put(GameFeature.Grid15x15, 15);
        sizeMap.put(GameFeature.Grid20x20, 20);
    }

    @Override
    public void initializeGame(final TBGame game) {

        //noinspection ConstantConditions
        GameFeature sizeFeature = game.getFeatures()
                .stream()
                .filter(f -> f.getGroup().equals(GameFeature.GridSize))
                .findFirst().get();
        game.setGridSize(sizeMap.get(sizeFeature));
    }

    public final int getOrder() {
        return DEFAULT_ORDER;
    }
}
