package com.jtbdevelopment.TwistedBattleship.rest.handlers.helpers

import com.jtbdevelopment.TwistedBattleship.exceptions.NoRoomForEmergencyManeuverException
import com.jtbdevelopment.TwistedBattleship.state.TBGame
import com.jtbdevelopment.TwistedBattleship.state.TBPlayerState
import com.jtbdevelopment.TwistedBattleship.state.grid.GridCoordinate
import com.jtbdevelopment.TwistedBattleship.state.ships.ShipState
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

/**
 * Date: 5/26/15
 * Time: 7:13 PM
 */
@CompileStatic
@Component
class ShipRelocator {
    Random generator = new Random()

    @Autowired
    ShipRelocatorCalculator calculator

    @Autowired
    ShipRotater rotater

    //  We move the ship 0-2 cells in either direction and may also rotate it
    //  TODO - add more
    private static final List<List<Integer>> SHUFFLED_ADJUSTMENT_TRIALS = [
            [-2, -1, 0, 1, 2],
            [2, 1, 0, -1, 2],
            [2, -2, 1, -1, 0],
            [0, -1, 1, -2, 2],
            [2, -1, -2, 1, 0],
            [0, 1, -2, -1, 2]
    ]


    List<GridCoordinate> relocateShip(
            final TBGame game,
            final TBPlayerState playerState,
            final ShipState ship
    ) {
        Set<GridCoordinate> otherShipCoordinates = new HashSet<>(playerState.coordinateShipMap.keySet())
        otherShipCoordinates.removeAll(ship.shipGridCells)
        boolean rotate = generator.nextBoolean()
        boolean moveRows = generator.nextBoolean()
        List<Integer> adjustmentSequenceToTry = SHUFFLED_ADJUSTMENT_TRIALS[generator.nextInt(SHUFFLED_ADJUSTMENT_TRIALS.size() - 1)]

        List<GridCoordinate> initialCoordinates = getInitialCoordinates(ship, rotate)
        List<GridCoordinate> newCoordinates = calculator.relocateShip(game, ship, initialCoordinates, otherShipCoordinates, moveRows, adjustmentSequenceToTry)
        if (newCoordinates == null) {
            //  try columns instead
            newCoordinates = calculator.relocateShip(game, ship, initialCoordinates, otherShipCoordinates, !moveRows, adjustmentSequenceToTry)
            if (newCoordinates == null) {
                //  swap rotate flag and try rows again
                initialCoordinates = getInitialCoordinates(ship, !rotate)
                newCoordinates = calculator.relocateShip(game, ship, initialCoordinates, otherShipCoordinates, moveRows, adjustmentSequenceToTry)
                if (newCoordinates == null) {
                    //  last attempt
                    newCoordinates = calculator.relocateShip(game, ship, initialCoordinates, otherShipCoordinates, !moveRows, adjustmentSequenceToTry)
                    if (newCoordinates == null) {
                        throw new NoRoomForEmergencyManeuverException()
                    }
                }
            }
        }
        newCoordinates
    }

    @SuppressWarnings("GrMethodMayBeStatic")
    protected List<GridCoordinate> getInitialCoordinates(final ShipState ship, final boolean rotate) {
        if (rotate) {
            return rotater.rotateShip(ship)
        } else {
            new LinkedList<GridCoordinate>(ship.shipGridCells)
        }
    }

}
