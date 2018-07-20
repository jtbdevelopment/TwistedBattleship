package com.jtbdevelopment.TwistedBattleship.rest.handlers.helpers;

import com.jtbdevelopment.TwistedBattleship.exceptions.NoRoomForEmergencyManeuverException;
import com.jtbdevelopment.TwistedBattleship.state.TBGame;
import com.jtbdevelopment.TwistedBattleship.state.TBPlayerState;
import com.jtbdevelopment.TwistedBattleship.state.grid.GridCoordinate;
import com.jtbdevelopment.TwistedBattleship.state.ships.Ship;
import com.jtbdevelopment.TwistedBattleship.state.ships.ShipState;
import org.bson.types.ObjectId;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;

import java.util.*;

import static org.mockito.Mockito.mock;

/**
 * Date: 5/27/15
 * Time: 6:43 AM
 */
public class ShipRelocatorTest {
    private ShipRotater rotater = mock(ShipRotater.class);
    private ShipRelocatorCalculator calculator = mock(ShipRelocatorCalculator.class);
    private ShipRelocator relocator = new ShipRelocator(calculator, rotater);
    private Set<GridCoordinate> otherCoords = new HashSet<>(Arrays.asList(new GridCoordinate(5, 5), new GridCoordinate(6, 6), new GridCoordinate(7, 7)));
    private ShipState shipState = new ShipState(Ship.Battleship, new TreeSet<>(Arrays.asList(new GridCoordinate(1, 1), new GridCoordinate(2, 2), new GridCoordinate(3, 3))));
    private int randomToReturn;
    private Boolean booleanToReturn = true;
    private TBGame game;
    private TBPlayerState playerState = mock(TBPlayerState.class);

    @Before
    public void setup() {
        game = new TBGame();
        game.setId(new ObjectId());

        Map<GridCoordinate, ShipState> results = new HashMap<>();
        otherCoords.forEach(oc -> results.put(oc, null));
        shipState.getShipGridCells().forEach(c -> results.put(c, null));
        Mockito.when(playerState.getCoordinateShipMap()).thenReturn(results);
        relocator.generator = new Random() {
            @Override
            public boolean nextBoolean() {
                return booleanToReturn;
            }

            @Override
            public int nextInt(int max) {
                return randomToReturn;
            }

        };
    }

    @Test
    public void testSimpleCaseOnFirstAttemptWithRotateAndMoveByRows() {
        List<GridCoordinate> newcoords = Arrays.asList(new GridCoordinate(-1, -1), new GridCoordinate(-15, 15));
        List<GridCoordinate> rotated = Arrays.asList(new GridCoordinate(4, 4), new GridCoordinate(3, 3));
        Mockito.when(rotater.rotateShip(shipState)).thenReturn(rotated);
        Mockito.when(calculator.relocateShip(Matchers.eq(game), Matchers.eq(shipState), Matchers.eq(rotated), Matchers.eq(otherCoords), Matchers.eq(true), Matchers.eq(Arrays.asList(2, -2, 1, -1, 0)))).thenReturn(newcoords);
        randomToReturn = 2;

        Assert.assertSame(newcoords, relocator.relocateShip(game, playerState, shipState));
    }

    @Test
    public void testSimpleCaseOnFirstAttemptWithoutRotateAndMoveByCols() {
        List<GridCoordinate> newcoords = Arrays.asList(new GridCoordinate(-1, -1), new GridCoordinate(-15, 15));
        randomToReturn = 4;
        booleanToReturn = false;
        Mockito.when(calculator.relocateShip(Matchers.eq(game), Matchers.eq(shipState), Matchers.eq(shipState.getShipGridCells()), Matchers.eq(otherCoords), Matchers.eq(false), Matchers.eq(Arrays.asList(2, -1, -2, 1, 0)))).thenReturn(newcoords);

        Assert.assertSame(newcoords, relocator.relocateShip(game, playerState, shipState));
    }

    @Test(expected = NoRoomForEmergencyManeuverException.class)
    public void testTrysAllVariantsWithInitiallySetToRotateAndMoveByRows() {
        List<GridCoordinate> rotated = Arrays.asList(new GridCoordinate(4, 4), new GridCoordinate(3, 3));
        randomToReturn = 5;
        Mockito.when(rotater.rotateShip(shipState)).thenReturn(rotated);
        List<Integer> expectedSequence = Arrays.asList(0, 1, -2, -1, 2);
        Mockito.when(calculator.relocateShip(Matchers.eq(game), Matchers.eq(shipState), Matchers.eq(rotated), Matchers.eq(otherCoords), Matchers.eq(true), Matchers.eq(expectedSequence))).thenReturn(null);
        Mockito.when(calculator.relocateShip(Matchers.eq(game), Matchers.eq(shipState), Matchers.eq(rotated), Matchers.eq(otherCoords), Matchers.eq(false), Matchers.eq(expectedSequence))).thenReturn(null);
        Mockito.when(calculator.relocateShip(Matchers.eq(game), Matchers.eq(shipState), Matchers.eq(shipState.getShipGridCells()), Matchers.eq(otherCoords), Matchers.eq(true), Matchers.eq(expectedSequence))).thenReturn(null);
        Mockito.when(calculator.relocateShip(Matchers.eq(game), Matchers.eq(shipState), Matchers.eq(shipState.getShipGridCells()), Matchers.eq(otherCoords), Matchers.eq(false), Matchers.eq(expectedSequence))).thenReturn(null);

        relocator.relocateShip(game, playerState, shipState);
    }

    @Test(expected = NoRoomForEmergencyManeuverException.class)
    public void testTrysAllVariantsWithInitiallySetToNoRotateAndMoveByCols() {
        List<GridCoordinate> rotated = Arrays.asList(new GridCoordinate(4, 4), new GridCoordinate(3, 3));
        randomToReturn = 5;
        List<Integer> expectedSequence = Arrays.asList(0, 1, -2, -1, 2);
        Mockito.when(rotater.rotateShip(shipState)).thenReturn(rotated);
        Mockito.when(calculator.relocateShip(Matchers.eq(game), Matchers.eq(shipState), Matchers.eq(rotated), Matchers.eq(otherCoords), Matchers.eq(true), Matchers.eq(expectedSequence))).thenReturn(null);
        Mockito.when(calculator.relocateShip(Matchers.eq(game), Matchers.eq(shipState), Matchers.eq(rotated), Matchers.eq(otherCoords), Matchers.eq(false), Matchers.eq(expectedSequence))).thenReturn(null);
        Mockito.when(calculator.relocateShip(Matchers.eq(game), Matchers.eq(shipState), Matchers.eq(shipState.getShipGridCells()), Matchers.eq(otherCoords), Matchers.eq(true), Matchers.eq(expectedSequence))).thenReturn(null);
        Mockito.when(calculator.relocateShip(Matchers.eq(game), Matchers.eq(shipState), Matchers.eq(shipState.getShipGridCells()), Matchers.eq(otherCoords), Matchers.eq(false), Matchers.eq(expectedSequence))).thenReturn(null);

        relocator.relocateShip(game, playerState, shipState);
    }
}
