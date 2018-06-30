package com.jtbdevelopment.TwistedBattleship.rest.handlers.helpers;

import com.jtbdevelopment.TwistedBattleship.state.TBGame;
import com.jtbdevelopment.TwistedBattleship.state.grid.GridCoordinate;
import com.jtbdevelopment.TwistedBattleship.state.ships.Ship;
import com.jtbdevelopment.TwistedBattleship.state.ships.ShipState;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.assertEquals;

/**
 * Date: 5/26/15
 * Time: 7:05 PM
 */
public class ShipRelocatorCalculatorTest {
    private ShipRelocatorCalculator relocator = new ShipRelocatorCalculator();
    private TBGame game;
    private List<GridCoordinate> initialCoordinates = Arrays.asList(new GridCoordinate(7, 7), new GridCoordinate(7, 8), new GridCoordinate(7, 9));
    private ShipState shipState = new ShipState(Ship.Cruiser, new TreeSet<>(initialCoordinates));

    @Test
    public void testMovesByRows() {
        assertEquals(
                Arrays.asList(new GridCoordinate(11, 7), new GridCoordinate(11, 8), new GridCoordinate(11, 9)),
                relocator.relocateShip(game, shipState, initialCoordinates, new HashSet<>(Collections.singletonList(new GridCoordinate(4, 4))), true, Collections.singletonList(4)));
    }

    @Test
    public void testMovesByCols() {
        assertEquals(
                Arrays.asList(new GridCoordinate(7, 11), new GridCoordinate(7, 12), new GridCoordinate(7, 13)),
                relocator.relocateShip(game, shipState, initialCoordinates, new HashSet<>(Collections.singletonList(new GridCoordinate(4, 4))), false, Collections.singletonList(4)));
    }

    @Test
    public void testShipMustMoveForCoordinates() {
        Assert.assertNull(relocator.relocateShip(game, shipState, initialCoordinates, new HashSet<>(), true, Collections.singletonList(0)));
        assertEquals(
                Arrays.asList(new GridCoordinate(7, 11), new GridCoordinate(7, 12), new GridCoordinate(7, 13)),
                relocator.relocateShip(game, shipState, initialCoordinates, new HashSet<>(Collections.singletonList(new GridCoordinate(4, 4))), false, Arrays.asList(0, 4)));
    }

    @Test
    public void testMovingBeyondGrid() {
        Assert.assertNull(relocator.relocateShip(game, shipState, initialCoordinates, new HashSet<>(), false, Collections.singletonList(6)));
        Assert.assertNull(relocator.relocateShip(game, shipState, initialCoordinates, new HashSet<>(), true, Collections.singletonList(-8)));
    }

    @Test
    public void testHittingSharedCell() {
        Assert.assertNull(relocator.relocateShip(game, shipState, initialCoordinates, new HashSet<>(Collections.singletonList(new GridCoordinate(7, 12))), false, Collections.singletonList(4)));
    }

    @Test
    public void testKeepsTrying() {
        assertEquals(
                Arrays.asList(new GridCoordinate(7, 11), new GridCoordinate(7, 12), new GridCoordinate(7, 13)),
                relocator.relocateShip(game, shipState, initialCoordinates, new HashSet<>(Collections.singletonList(new GridCoordinate(4, 4))), false, Arrays.asList(6, -8, 4)));
        assertEquals(
                Arrays.asList(new GridCoordinate(7, 5), new GridCoordinate(7, 6), new GridCoordinate(7, 7)),
                relocator.relocateShip(game, shipState, initialCoordinates, new HashSet<>(Collections.singletonList(new GridCoordinate(7, 12))), false, Arrays.asList(4, -2, -3)));
    }

    @Before
    public void setup() {
        game = new TBGame();
        game.setGridSize(15);
    }
}
