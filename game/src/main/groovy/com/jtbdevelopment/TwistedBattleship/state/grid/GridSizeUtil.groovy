package com.jtbdevelopment.TwistedBattleship.state.grid

import com.jtbdevelopment.TwistedBattleship.state.GameFeature
import groovy.transform.CompileStatic
import org.springframework.stereotype.Component

/**
 * Date: 4/2/15
 * Time: 5:15 PM
 */
@CompileStatic
@Component
class GridSizeUtil {
    int getSize(final GameFeature gridSize) {
        switch (gridSize) {
            case GameFeature.Grid10x10:
                return 10
            case GameFeature.Grid15x15:
                return 15
            case GameFeature.Grid20x20:
                return 20
        }
        throw new IllegalArgumentException('Unhandled size ' + gridSize)
    }
}
