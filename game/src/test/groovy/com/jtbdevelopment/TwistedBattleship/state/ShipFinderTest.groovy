package com.jtbdevelopment.TwistedBattleship.state

import com.jtbdevelopment.TwistedBattleship.state.grid.GridCoordinate
import com.jtbdevelopment.TwistedBattleship.state.ships.Ship
import com.jtbdevelopment.TwistedBattleship.state.ships.ShipState

/**
 * Date: 5/13/15
 * Time: 6:35 AM
 */
class ShipFinderTest extends GroovyTestCase {
    private static final ShipState SUBMARINE = new ShipState(Ship.Submarine, [
            new GridCoordinate(2, 5),
            new GridCoordinate(2, 6),
            new GridCoordinate(2, 4)
    ] as SortedSet)
    private static final ShipState BATTLESHIP = new ShipState(Ship.Battleship, [
            new GridCoordinate(0, 0),
            new GridCoordinate(1, 0),
            new GridCoordinate(5, 0),
    ] as SortedSet<GridCoordinate>)
    private static final ShipState DESTROYER = new ShipState(Ship.Destroyer, [
            new GridCoordinate(10, 0),
            new GridCoordinate(11, 0),
            new GridCoordinate(12, 0)
    ] as SortedSet)
    private static final TBPlayerState PLAYER_STATE = new TBPlayerState(
            shipStates: [
                    (Ship.Battleship): BATTLESHIP,
                    (Ship.Submarine) : SUBMARINE,
                    (Ship.Destroyer) : DESTROYER,
            ]
    )
    ShipFinder finder = new ShipFinder()

    void testCanFindShip() {
        assert DESTROYER.is(finder.findShipForCoordinate(PLAYER_STATE, new GridCoordinate(11, 0)))
        assert DESTROYER.is(finder.findShipForCoordinate(PLAYER_STATE, new GridCoordinate(10, 0)))
        assert SUBMARINE.is(finder.findShipForCoordinate(PLAYER_STATE, new GridCoordinate(2, 4)))
        assert BATTLESHIP.is(finder.findShipForCoordinate(PLAYER_STATE, new GridCoordinate(5, 0)))
    }

    void testNullWhenCantFindShip() {
        assertNull finder.findShipForCoordinate(PLAYER_STATE, new GridCoordinate(13, 0))
        assertNull finder.findShipForCoordinate(PLAYER_STATE, new GridCoordinate(2, 3))
    }
}
