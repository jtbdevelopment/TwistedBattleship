package com.jtbdevelopment.TwistedBattleship.state

import groovy.transform.CompileStatic
import org.springframework.stereotype.Component

/**
 * Date: 4/2/15
 * Time: 5:15 PM
 */
@CompileStatic
@Component
class GridSizeUtil {
    int getSize(final GameFeatures gridSize) {
        switch (gridSize) {
            case GameFeatures.Grid10x10:
                return 10
            case GameFeatures.Grid15x15:
                return 15
            case GameFeatures.Grid20x20:
                return 20
        }
        throw new IllegalArgumentException('Unhandled size ' + gridSize)
    }
}
