package com.jtbdevelopment.TwistedBattleship.state

import com.jtbdevelopment.TwistedBattleship.state.grid.GridCoordinate
import com.jtbdevelopment.TwistedBattleship.state.ships.Ship
import com.jtbdevelopment.TwistedBattleship.state.ships.ShipState

/**
 * Date: 4/2/15
 * Time: 5:23 PM
 */
class TBPlayerStateTest extends GroovyTestCase {
    TBPlayerState state = new TBPlayerState()

    void testInitialSettings() {
        assert [:] == state.opponentViews
        assert 0 == state.scoreFromLiving
        assert 0 == state.scoreFromSinks
        assert 0 == state.scoreFromHits
        assert 0 == state.totalScore
        assert 0 == state.activeShipsRemaining
        assert 0 == state.spysRemaining
        assert 0 == state.cruiseMissilesRemaining
        assert 0 == state.ecmsRemaining
        assert 0 == state.evasiveManeuversRemaining
        assert 0 == state.emergencyRepairsRemaining
        assert [:] == state.opponentGrids
        assert [] == state.shipStates
        assert [] == state.startingShips
        assert [] == state.actionLog
        assert [:] == state.coordinateShipMap
        assertFalse state.setup
        assertFalse state.alive
    }

    void testGetTotalScore() {
        state.scoreFromLiving = 5
        assert 5 == state.getTotalScore()
        state.scoreFromHits = 10
        assert 15 == state.totalScore
        state.scoreFromSinks = 20
        assert 35 == state.totalScore
        state.scoreFromLiving = 30
        assert 60 == state.totalScore
    }

    void testSetTotalScoreIgnoresSet() {
        state.scoreFromLiving = 5
        assert 5 == state.totalScore
        state.setTotalScore(20)
        assert 5 == state.totalScore
    }

    void testShipsRemainingAndAlive() {
        state.shipStates = [
                new ShipState(Ship.Cruiser, new TreeSet<GridCoordinate>()),
                new ShipState(Ship.Carrier, new TreeSet<GridCoordinate>())
        ]
        assert state.alive
        assert state.activeShipsRemaining == 2

        state.shipStates.find { it.ship == Ship.Carrier }.healthRemaining = 1
        assert state.alive
        assert state.activeShipsRemaining == 2

        state.shipStates.find { it.ship == Ship.Carrier }.healthRemaining = 0
        assert state.alive
        assert state.activeShipsRemaining == 1

        state.shipStates.find { it.ship == Ship.Cruiser }.healthRemaining = 0
        assertFalse state.alive
        assert state.activeShipsRemaining == 0
    }

    void testIgnoresSetAliveAndActiveShips() {
        state.setAlive(true)
        state.setActiveShipsRemaining(10)
        assertFalse state.alive
        assert state.activeShipsRemaining == 0
    }

    void testIsSetup() {
        assertFalse state.setup

        state.startingShips = Ship.values().toList()
        assertFalse state.setup

        Ship.values().findAll { Ship it -> it != Ship.Submarine }.each {
            Ship it ->
                state.shipStates += new ShipState(it, new TreeSet<GridCoordinate>())
                assertFalse state.setup
        }
        state.shipStates += new ShipState(Ship.Submarine, new TreeSet<GridCoordinate>())
        assert state.setup
    }

    void testIsSetupWhenShipsDoNotMatch() {
        assertFalse state.setup

        state.startingShips = [Ship.Carrier, Ship.Carrier, Ship.Carrier, Ship.Carrier, Ship.Carrier]
        assertFalse state.setup

        Ship.values().each {
            Ship it ->
                state.shipStates += new ShipState(it, new TreeSet<GridCoordinate>())
                assertFalse state.setup
        }
    }

    void testIsSetupNonStandardShips() {
        state.startingShips = [Ship.Carrier, Ship.Carrier, Ship.Destroyer]
        assertFalse state.setup

        state.shipStates += new ShipState(Ship.Carrier, new TreeSet<GridCoordinate>())
        assertFalse state.setup
        state.shipStates += new ShipState(Ship.Destroyer, new TreeSet<GridCoordinate>())
        assertFalse state.setup
        state.shipStates += new ShipState(Ship.Carrier, new TreeSet<GridCoordinate>())
        assert state.setup
    }

    void testIgnoresSettingIsSetup() {
        assertFalse state.setup
        state.setup = true
        assertFalse state.setup
    }

    void testSettingShipStatesSetsTransientMap() {
        assert 0 == state.coordinateShipMap.size()
        def shipState = new ShipState(Ship.Submarine, new TreeSet<GridCoordinate>(
                [new GridCoordinate(0, 0), new GridCoordinate(0, 1), new GridCoordinate(0, 2)]
        ))
        this.state.shipStates += [shipState]
        assert 3 == state.coordinateShipMap.size()
        assert shipState.is(state.coordinateShipMap[new GridCoordinate(0, 0)])
        assert shipState.is(state.coordinateShipMap[new GridCoordinate(0, 1)])
        assert shipState.is(state.coordinateShipMap[new GridCoordinate(0, 2)])
    }

    void testClearingMapCausesRecomputeOnNextFetch() {
        def shipState = new ShipState(Ship.Submarine, new TreeSet<GridCoordinate>(
                [new GridCoordinate(0, 0), new GridCoordinate(0, 1), new GridCoordinate(0, 2)]
        ))
        this.state.shipStates += [shipState]

        def firstMapReference = state.coordinateShipMap
        assert 3 == firstMapReference.size()
        assert shipState.is(firstMapReference[new GridCoordinate(0, 0)])
        assert shipState.is(firstMapReference[new GridCoordinate(0, 1)])
        assert shipState.is(firstMapReference[new GridCoordinate(0, 2)])
        firstMapReference.clear()
        assert firstMapReference.isEmpty()

        def secondMapReference = state.coordinateShipMap
        assertFalse firstMapReference.is(secondMapReference)
        assertFalse secondMapReference.isEmpty()
        assert firstMapReference.isEmpty()
    }
}
