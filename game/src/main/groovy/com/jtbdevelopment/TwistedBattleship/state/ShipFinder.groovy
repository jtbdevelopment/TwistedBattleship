package com.jtbdevelopment.TwistedBattleship.state

import com.jtbdevelopment.TwistedBattleship.state.grid.GridCoordinate
import com.jtbdevelopment.TwistedBattleship.state.ships.ShipState
import groovy.transform.CompileStatic
import org.springframework.stereotype.Component

/**
 * Date: 5/12/15
 * Time: 7:57 PM
 */
@Component
@CompileStatic
class ShipFinder {
    ShipState findShipForCoordinate(final TBPlayerState playerState, final GridCoordinate coordinate) {
        playerState.shipStates.find { it.value.shipGridCells.contains(coordinate) }?.value
    }
}
