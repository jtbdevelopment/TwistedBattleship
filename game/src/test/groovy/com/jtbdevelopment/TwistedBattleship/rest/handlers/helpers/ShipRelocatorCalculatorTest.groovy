package com.jtbdevelopment.TwistedBattleship.rest.handlers.helpers

import com.jtbdevelopment.TwistedBattleship.state.TBGame
import com.jtbdevelopment.TwistedBattleship.state.grid.GridCoordinate
import com.jtbdevelopment.TwistedBattleship.state.grid.GridSizeUtil

/**
 * Date: 5/26/15
 * Time: 7:05 PM
 */
class ShipRelocatorCalculatorTest extends GroovyTestCase {
    ShipRelocatorCalculator relocator = new ShipRelocatorCalculator(gridSizeUtil: new GridSizeUtil())

    TBGame game = new TBGame(gridSize: 15)

    List<GridCoordinate> initialCoordinates = [new GridCoordinate(7, 7), new GridCoordinate(7, 8), new GridCoordinate(7, 9)]

    void testMovesByRows() {
        assert relocator.relocateShip(game, initialCoordinates, [new GridCoordinate(4, 4)] as Set, true, [4]) == [new GridCoordinate(11, 7), new GridCoordinate(11, 8), new GridCoordinate(11, 9)]
    }

    void testMovesByCols() {
        assert relocator.relocateShip(game, initialCoordinates, [new GridCoordinate(4, 4)] as Set, false, [4]) == [new GridCoordinate(7, 11), new GridCoordinate(7, 12), new GridCoordinate(7, 13)]
    }

    void testMovingBeyondGrid() {
        assertNull relocator.relocateShip(game, initialCoordinates, [] as Set, false, [6])
        assertNull relocator.relocateShip(game, initialCoordinates, [] as Set, true, [-8])
    }

    void testHittingSharedCell() {
        assertNull relocator.relocateShip(game, initialCoordinates, [new GridCoordinate(7, 12)] as Set, false, [4])
    }

    void testKeepsTrying() {
        assert relocator.relocateShip(game, initialCoordinates, [new GridCoordinate(4, 4)] as Set, false, [6, -8, 4]) == [new GridCoordinate(7, 11), new GridCoordinate(7, 12), new GridCoordinate(7, 13)]
        assert relocator.relocateShip(game, initialCoordinates, [new GridCoordinate(7, 12)] as Set, false, [4, -2, -3]) == [new GridCoordinate(7, 5), new GridCoordinate(7, 6), new GridCoordinate(7, 7)]
    }

}
