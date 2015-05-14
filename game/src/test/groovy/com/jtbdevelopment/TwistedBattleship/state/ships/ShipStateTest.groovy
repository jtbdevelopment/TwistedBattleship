package com.jtbdevelopment.TwistedBattleship.state.ships

import com.jtbdevelopment.TwistedBattleship.state.grid.GridCoordinate

/**
 * Date: 4/4/15
 * Time: 7:16 PM
 */
class ShipStateTest extends GroovyTestCase {
    void testConstructor() {
        def coordinates = new TreeSet<GridCoordinate>(
                [
                        new GridCoordinate(0, 0),
                        new GridCoordinate(0, 1),
                        new GridCoordinate(0, 2)
                ]
        )
        ShipState shipState = new ShipState(
                Ship.Cruiser,
                coordinates
        )
        assert shipState.healthRemaining == Ship.Cruiser.gridSize
        assert shipState.shipSegmentHit == [false, false, false]
        assert shipState.shipGridCells == (coordinates as List).sort()
        assert shipState.ship == Ship.Cruiser
    }

    void testPersistenceConstructor() {
        def coordinates = [
                        new GridCoordinate(0, 0),
                        new GridCoordinate(0, 1),
                        new GridCoordinate(0, 2)
                ]

        def booleans = [true, false, true]
        ShipState shipState = new ShipState(
                Ship.Cruiser,
                1,
                coordinates,
                booleans
        )
        assert shipState.healthRemaining == 1
        assert shipState.shipSegmentHit == booleans
        assert shipState.shipGridCells == (coordinates as List).sort()
        assert shipState.ship == Ship.Cruiser
    }
}
