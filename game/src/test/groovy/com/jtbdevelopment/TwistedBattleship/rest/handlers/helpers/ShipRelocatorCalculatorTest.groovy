package com.jtbdevelopment.TwistedBattleship.rest.handlers.helpers

import com.jtbdevelopment.TwistedBattleship.state.TBGame
import com.jtbdevelopment.TwistedBattleship.state.grid.GridCoordinate
import com.jtbdevelopment.TwistedBattleship.state.grid.GridSizeUtil
import com.jtbdevelopment.TwistedBattleship.state.ships.Ship
import com.jtbdevelopment.TwistedBattleship.state.ships.ShipState

/**
 * Date: 5/26/15
 * Time: 7:05 PM
 */
class ShipRelocatorCalculatorTest extends GroovyTestCase {
    ShipRelocatorCalculator relocator = new ShipRelocatorCalculator(gridSizeUtil: new GridSizeUtil())

    TBGame game = new TBGame(gridSize: 15)

    List<GridCoordinate> initialCoordinates = [new GridCoordinate(7, 7), new GridCoordinate(7, 8), new GridCoordinate(7, 9)]
    ShipState shipState = new ShipState(Ship.Cruiser, initialCoordinates as SortedSet<GridCoordinate>)

    void testMovesByRows() {
        assert [new GridCoordinate(11, 7), new GridCoordinate(11, 8), new GridCoordinate(11, 9)] == relocator.relocateShip(game, shipState, initialCoordinates, [new GridCoordinate(4, 4)] as Set, true, [4])
    }

    void testMovesByCols() {
        assert [new GridCoordinate(7, 11), new GridCoordinate(7, 12), new GridCoordinate(7, 13)] == relocator.relocateShip(game, shipState, initialCoordinates, [new GridCoordinate(4, 4)] as Set, false, [4])
    }

    void testShipMustMoveForCoordinates() {
        assertNull relocator.relocateShip(game, shipState, initialCoordinates, [] as Set, true, [0])
        assert [new GridCoordinate(7, 11), new GridCoordinate(7, 12), new GridCoordinate(7, 13)] == relocator.relocateShip(game, shipState, initialCoordinates, [new GridCoordinate(4, 4)] as Set, false, [0, 4])
    }

    void testMovingBeyondGrid() {
        assertNull relocator.relocateShip(game, shipState, initialCoordinates, [] as Set, false, [6])
        assertNull relocator.relocateShip(game, shipState, initialCoordinates, [] as Set, true, [-8])
    }

    void testHittingSharedCell() {
        assertNull relocator.relocateShip(game, shipState, initialCoordinates, [new GridCoordinate(7, 12)] as Set, false, [4])
    }

    void testKeepsTrying() {
        assert [new GridCoordinate(7, 11), new GridCoordinate(7, 12), new GridCoordinate(7, 13)] == relocator.relocateShip(game, shipState, initialCoordinates, [new GridCoordinate(4, 4)] as Set, false, [6, -8, 4])
        assert [new GridCoordinate(7, 5), new GridCoordinate(7, 6), new GridCoordinate(7, 7)] == relocator.relocateShip(game, shipState, initialCoordinates, [new GridCoordinate(7, 12)] as Set, false, [4, -2, -3])
    }

}
