package com.jtbdevelopment.TwistedBattleship.state.grid

import com.jtbdevelopment.TwistedBattleship.state.GameFeature
import com.jtbdevelopment.TwistedBattleship.state.TBGame
import com.jtbdevelopment.games.state.Game
import groovy.transform.CompileStatic
import org.springframework.stereotype.Component

/**
 * Date: 4/2/15
 * Time: 5:15 PM
 */
@CompileStatic
@Component
class GridSizeUtil {
    private final static Map<GameFeature, Integer> sizeMap = [
            (GameFeature.Grid10x10): 10,
            (GameFeature.Grid15x15): 15,
            (GameFeature.Grid20x20): 20
    ]

    int getSize(final TBGame game) {
        GameFeature size = game.features.find { GameFeature it -> it.group == GameFeature.GridSize }
        if(sizeMap.containsKey(size)) {
            return sizeMap[size]
        }
        throw new IllegalArgumentException('Unhandled size ' + size)
    }
}
