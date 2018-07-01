package com.jtbdevelopment.TwistedBattleship.state.ships;

import com.jtbdevelopment.TwistedBattleship.state.grid.GridCoordinate;
import org.junit.Assert;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Date: 4/4/15
 * Time: 7:16 PM
 */
public class ShipStateTest {
    @Test
    public void testConstructor() {
        TreeSet<GridCoordinate> coordinates = new TreeSet<>(Arrays.asList(new GridCoordinate(0, 0), new GridCoordinate(0, 1), new GridCoordinate(0, 2)));
        ShipState shipState = new ShipState(Ship.Cruiser, coordinates);
        assertEquals(Ship.Cruiser.getGridSize(), shipState.getHealthRemaining());
        assertEquals(Arrays.asList(false, false, false), shipState.getShipSegmentHit());
        assertEquals(new LinkedList<>(coordinates), shipState.getShipGridCells());
        assertEquals(Ship.Cruiser, shipState.getShip());
        assertTrue(shipState.isHorizontal());
    }

    @Test
    public void testPersistenceConstructor() {
        List<GridCoordinate> coordinates = Arrays.asList(new GridCoordinate(0, 0), new GridCoordinate(0, 1), new GridCoordinate(0, 2));

        List<Boolean> booleans = Arrays.asList(true, false, true);
        ShipState shipState = new ShipState(Ship.Cruiser, 1, coordinates, booleans);
        assertEquals(1, shipState.getHealthRemaining());
        assertEquals(booleans, shipState.getShipSegmentHit());
        Collections.sort(coordinates);
        assertEquals(coordinates, shipState.getShipGridCells());
        assertEquals(Ship.Cruiser, shipState.getShip());
        assertTrue(shipState.isHorizontal());
    }

    @Test
    public void testSettingGridCellsRecomputesHorizontal() {
        TreeSet<GridCoordinate> coordinates = new TreeSet<>(Arrays.asList(new GridCoordinate(0, 0), new GridCoordinate(0, 1), new GridCoordinate(0, 2)));
        ShipState shipState = new ShipState(Ship.Cruiser, coordinates);
        assertTrue(shipState.isHorizontal());
        coordinates = new TreeSet<>(Arrays.asList(new GridCoordinate(1, 0), new GridCoordinate(2, 0), new GridCoordinate(3, 0)));
        shipState.setShipGridCells(new LinkedList<>(coordinates));
        Assert.assertFalse(shipState.isHorizontal());
    }

    @Test
    public void testExplicitlySettingHorizontalIgnored() {
        TreeSet<GridCoordinate> coordinates = new TreeSet<>(Arrays.asList(new GridCoordinate(0, 0), new GridCoordinate(0, 1), new GridCoordinate(0, 2)));
        ShipState shipState = new ShipState(Ship.Cruiser, coordinates);
        assertTrue(shipState.isHorizontal());
        shipState.setHorizontal(false);
        assertTrue(shipState.isHorizontal());
    }

    @Test
    public void testSettingGridCellsToEmptyDoesntExplodeHorizontal() {
        TreeSet<GridCoordinate> coordinates = new TreeSet<>(Collections.singletonList(new GridCoordinate(0, 0)));
        ShipState shipState = new ShipState(Ship.Cruiser, coordinates);
        Assert.assertFalse(shipState.isHorizontal());
    }

}
