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
        assert shipState.horizontal
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
        assert shipState.horizontal
    }

    void testSettingGridCellsRecomputesHorizontal() {
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
        assert shipState.horizontal
        coordinates = new TreeSet<GridCoordinate>(
                [
                        new GridCoordinate(1, 0),
                        new GridCoordinate(2, 0),
                        new GridCoordinate(3, 0)
                ]
        )
        shipState.shipGridCells = (coordinates as List)
        assertFalse shipState.horizontal
    }

    void testExplicitlySettingHorizontalIgnored() {
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
        assert shipState.horizontal
        shipState.horizontal = false
        assert shipState.horizontal
    }

    void testSettingGridCellsToEmptyDoesntExplodeHorizontal() {
        def coordinates = new TreeSet<GridCoordinate>(
                [
                        new GridCoordinate(0, 0)
                ]
        )
        ShipState shipState = new ShipState(
                Ship.Cruiser,
                coordinates
        )
        assertFalse shipState.horizontal
    }
}
