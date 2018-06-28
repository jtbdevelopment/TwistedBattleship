package com.jtbdevelopment.TwistedBattleship.state;

import com.jtbdevelopment.TwistedBattleship.state.grid.GridCoordinate;
import com.jtbdevelopment.TwistedBattleship.state.ships.Ship;
import com.jtbdevelopment.TwistedBattleship.state.ships.ShipState;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

/**
 * Date: 4/2/15
 * Time: 5:23 PM
 */
public class TBPlayerStateTest {
    private TBPlayerState state = new TBPlayerState();

    @Test
    public void testInitialSettings() {
        assertEquals(new LinkedHashMap(), state.getOpponentViews());
        assertEquals(0, state.getScoreFromLiving());
        assertEquals(0, state.getScoreFromSinks());
        assertEquals(0, state.getScoreFromHits());
        assertEquals(0, state.getTotalScore());
        assertEquals(0, state.getActiveShipsRemaining());
        assertEquals(0, state.getSpysRemaining());
        assertEquals(0, state.getCruiseMissilesRemaining());
        assertEquals(0, state.getEcmsRemaining());
        assertEquals(0, state.getEvasiveManeuversRemaining());
        assertEquals(0, state.getEmergencyRepairsRemaining());
        assertEquals(new LinkedHashMap(), state.getOpponentGrids());
        assertEquals(new ArrayList(), state.getShipStates());
        assertEquals(new ArrayList(), state.getStartingShips());
        assertEquals(new ArrayList(), state.getActionLog());
        assertEquals(new LinkedHashMap(), state.getCoordinateShipMap());
        assertFalse(state.getSetup());
        assertFalse(state.getAlive());
    }

    @Test
    public void testGetTotalScore() {
        state.setScoreFromLiving(5);
        assertEquals(5, state.getTotalScore());
        state.setScoreFromHits(10);
        assertEquals(15, state.getTotalScore());
        state.setScoreFromSinks(20);
        assertEquals(35, state.getTotalScore());
        state.setScoreFromLiving(30);
        assertEquals(60, state.getTotalScore());
    }

    @Test
    public void testSetTotalScoreIgnoresSet() {
        state.setScoreFromLiving(5);
        assertEquals(5, state.getTotalScore());
        state.setTotalScore(20);
        assertEquals(5, state.getTotalScore());
    }

    @Test
    public void testShipsRemainingAndAlive() {
        state.setShipStates(Arrays.asList(new ShipState(Ship.Cruiser, new TreeSet<>()), new ShipState(Ship.Carrier, new TreeSet<>())));
        assertTrue(state.getAlive());
        assertEquals(2, state.getActiveShipsRemaining());

        state.getShipStates().stream().filter(ss -> Ship.Carrier.equals(ss.getShip())).findFirst().ifPresent(ss -> ss.setHealthRemaining(1));
        assertTrue(state.getAlive());
        assertEquals(2, state.getActiveShipsRemaining());

        state.getShipStates().stream().filter(ss -> Ship.Carrier.equals(ss.getShip())).findFirst().ifPresent(ss -> ss.setHealthRemaining(0));
        assertTrue(state.getAlive());
        assertEquals(1, state.getActiveShipsRemaining());

        state.getShipStates().stream().filter(ss -> Ship.Cruiser.equals(ss.getShip())).findFirst().ifPresent(ss -> ss.setHealthRemaining(0));
        assertFalse(state.getAlive());
        assertEquals(0, state.getActiveShipsRemaining());
    }

    @Test
    public void testIgnoresSetAliveAndActiveShips() {
        state.setAlive(true);
        state.setActiveShipsRemaining(10);
        assertFalse(state.getAlive());
        assert state.getActiveShipsRemaining() == 0;
    }

    @Test
    public void testIsSetupIsExplicit() {
        assertFalse(state.getSetup());

        state.setStartingShips(Arrays.asList(Ship.values()));
        assertFalse(state.getSetup());

        state.getShipStates().forEach(ss -> {
            ss.setShipGridCells(new LinkedList<>());
        });
        assertFalse(state.getSetup());
        state.setSetup(true);
        assertTrue(state.getSetup());
    }

    @Test
    public void testSettingShipStatesSetsTransientMap() {
        assertEquals(0, state.getCoordinateShipMap().size());
        ShipState shipState = new ShipState(Ship.Submarine, new TreeSet<>(Arrays.asList(new GridCoordinate(0, 0), new GridCoordinate(0, 1), new GridCoordinate(0, 2))));
        state.getShipStates().add(shipState);
        assertEquals(3, state.getCoordinateShipMap().size());
        assertSame(shipState, state.getCoordinateShipMap().get(new GridCoordinate(0, 0)));
        assertSame(shipState, state.getCoordinateShipMap().get(new GridCoordinate(0, 1)));
        assertSame(shipState, state.getCoordinateShipMap().get(new GridCoordinate(0, 2)));
    }

    @Test
    public void testClearingMapCausesRecomputeOnNextFetch() {
        ShipState shipState = new ShipState(Ship.Submarine, new TreeSet<>(Arrays.asList(new GridCoordinate(0, 0), new GridCoordinate(0, 1), new GridCoordinate(0, 2))));
        state.getShipStates().add(shipState);

        Map<GridCoordinate, ShipState> firstMapReference = state.getCoordinateShipMap();
        assertEquals(3, firstMapReference.size());
        assertSame(shipState, firstMapReference.get(new GridCoordinate(0, 0)));
        assertSame(shipState, firstMapReference.get(new GridCoordinate(0, 1)));
        assertSame(shipState, firstMapReference.get(new GridCoordinate(0, 2)));
        firstMapReference.clear();
        assertTrue(firstMapReference.isEmpty());

        Map<GridCoordinate, ShipState> secondMapReference = state.getCoordinateShipMap();
        assertNotSame(firstMapReference, secondMapReference);
        assertFalse(secondMapReference.isEmpty());
        assertTrue(firstMapReference.isEmpty());
    }
}
