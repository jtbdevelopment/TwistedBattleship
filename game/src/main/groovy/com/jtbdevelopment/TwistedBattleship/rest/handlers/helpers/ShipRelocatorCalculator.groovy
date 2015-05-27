package com.jtbdevelopment.TwistedBattleship.rest.handlers.helpers

import com.jtbdevelopment.TwistedBattleship.state.TBGame
import com.jtbdevelopment.TwistedBattleship.state.grid.GridCoordinate
import com.jtbdevelopment.TwistedBattleship.state.grid.GridSizeUtil
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

/**
 * Date: 5/26/15
 * Time: 7:02 PM
 */
@CompileStatic
@Component
class ShipRelocatorCalculator {
    @Autowired
    GridSizeUtil gridSizeUtil

    List<GridCoordinate> relocateShip(
            final TBGame game,
            final List<GridCoordinate> initialCoordinates,
            final Set<GridCoordinate> otherShipCoordinates,
            final boolean moveRows,
            final List<Integer> adjustmentSequenceToTry
    ) {
        List<GridCoordinate> newCoordinates = null
        adjustmentSequenceToTry.each {
            Integer move ->
                if (newCoordinates == null) {
                    GridCoordinate adjustment = moveRows ? new GridCoordinate(move, 0) : new GridCoordinate(0, move)
                    List<GridCoordinate> attempt = initialCoordinates.collect { it.add(adjustment) }
                    GridCoordinate invalid = attempt.find {
                        otherShipCoordinates.contains(it) || (!gridSizeUtil.isValidCoordinate(game, it))
                    }
                    if (!invalid) {
                        newCoordinates = attempt
                        newCoordinates.sort()
                    }
                }
        }

        newCoordinates
    }
}
