package com.jtbdevelopment.TwistedBattleship.factory.gameinitializers

import com.jtbdevelopment.TwistedBattleship.state.GameFeature
import com.jtbdevelopment.TwistedBattleship.state.TBGame
import com.jtbdevelopment.games.factory.GameInitializer
import groovy.transform.CompileStatic
import org.springframework.stereotype.Component

/**
 * Date: 4/6/15
 * Time: 6:34 PM
 */
@Component
@CompileStatic
class GridSizeInitializer implements GameInitializer<TBGame> {
    private final static Map<GameFeature, Integer> sizeMap = [
            (GameFeature.Grid10x10): 10,
            (GameFeature.Grid15x15): 15,
            (GameFeature.Grid20x20): 20
    ]

    final int order = DEFAULT_ORDER

    @Override
    void initializeGame(final TBGame game) {

        game.gridSize = sizeMap[game.features.find { GameFeature it -> it.group == GameFeature.GridSize }]
    }
}
