package com.jtbdevelopment.TwistedBattleship.rest.handlers.helpers

import com.jtbdevelopment.TwistedBattleship.exceptions.NoRoomForEmergencyManeuverException
import com.jtbdevelopment.TwistedBattleship.state.TBGame
import com.jtbdevelopment.TwistedBattleship.state.TBPlayerState
import com.jtbdevelopment.TwistedBattleship.state.grid.GridCoordinate
import com.jtbdevelopment.TwistedBattleship.state.ships.Ship
import com.jtbdevelopment.TwistedBattleship.state.ships.ShipState
import org.bson.types.ObjectId
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito

import static org.junit.Assert.assertSame
import static org.mockito.Matchers.eq

/**
 * Date: 5/27/15
 * Time: 6:43 AM
 */
class ShipRelocatorTest {
    private ShipRelocator relocator = new ShipRelocator()

    private Set<GridCoordinate> otherCoords = [new GridCoordinate(5, 5), new GridCoordinate(6, 6), new GridCoordinate(7, 7)] as Set
    private ShipState shipState = new ShipState(Ship.Battleship, new TreeSet<GridCoordinate>([
            new GridCoordinate(1, 1),
            new GridCoordinate(2, 2),
            new GridCoordinate(3, 3)
    ]))
    private int randomToReturn;
    private booleanToReturn = true;
    private TBGame game = new TBGame(id: new ObjectId());
    private TBPlayerState playerState = Mockito.mock(TBPlayerState.class)

    @Before
    void setup() {
        Map<GridCoordinate, ShipState> results = [:]
        otherCoords.each { results.put(it, null) }
        shipState.shipGridCells.each { results.put(it, null) }
        Mockito.when(playerState.getCoordinateShipMap()).thenReturn(results)
        relocator.generator = new Random() {
            @Override
            boolean nextBoolean() {
                return booleanToReturn;
            }

            @Override
            int nextInt(int max) {
                return randomToReturn
            }
        }
        relocator.rotater = Mockito.mock(ShipRotater.class)
        relocator.calculator = Mockito.mock(ShipRelocatorCalculator.class);
    }

    @Test
    void testSimpleCaseOnFirstAttemptWithRotateAndMoveByRows() {
        List<GridCoordinate> newcoords = [new GridCoordinate(-1, -1), new GridCoordinate(-15, 15)]
        List<GridCoordinate> rotated = [new GridCoordinate(4, 4), new GridCoordinate(3, 3)]
        Mockito.when(relocator.rotater.rotateShip(shipState)).thenReturn(rotated);
        Mockito.when(relocator.calculator.relocateShip(
                eq(game),
                eq(shipState),
                eq(rotated),
                eq(otherCoords),
                eq(true),
                eq([2, -2, 1, -1, 0]))).thenReturn(newcoords);
        randomToReturn = 2;

        assertSame newcoords, relocator.relocateShip(game, playerState, shipState)
    }

    @Test
    void testSimpleCaseOnFirstAttemptWithoutRotateAndMoveByCols() {
        List<GridCoordinate> newcoords = [new GridCoordinate(-1, -1), new GridCoordinate(-15, 15)]
        randomToReturn = 4
        booleanToReturn = false
        Mockito.when(relocator.calculator.relocateShip(
                eq(game),
                eq(shipState),
                eq(shipState.shipGridCells),
                eq(otherCoords),
                eq(false),
                eq([2, -1, -2, 1, 0]))).thenReturn(newcoords);

        assertSame newcoords, relocator.relocateShip(game, playerState, shipState)
    }

    @Test(expected = NoRoomForEmergencyManeuverException.class)
    void testTrysAllVariantsWithInitiallySetToRotateAndMoveByRows() {
        List<GridCoordinate> rotated = [new GridCoordinate(4, 4), new GridCoordinate(3, 3)]
        randomToReturn = 5;
        Mockito.when(relocator.rotater.rotateShip(shipState)).thenReturn(rotated)
        def expectedSequence = [0, 1, -2, -1, 2]
        Mockito.when(relocator.calculator.relocateShip(eq(game), eq(shipState), eq(rotated), eq(otherCoords), eq(true), eq(expectedSequence))).thenReturn(null)
        Mockito.when(relocator.calculator.relocateShip(eq(game), eq(shipState), eq(rotated), eq(otherCoords), eq(false), eq(expectedSequence))).thenReturn(null)
        Mockito.when(relocator.calculator.relocateShip(eq(game), eq(shipState), eq(shipState.shipGridCells), eq(otherCoords), eq(true), eq(expectedSequence))).thenReturn(null)
        Mockito.when(relocator.calculator.relocateShip(eq(game), eq(shipState), eq(shipState.shipGridCells), eq(otherCoords), eq(false), eq(expectedSequence))).thenReturn(null)

        relocator.relocateShip(game, playerState, shipState)
    }

    @Test(expected = NoRoomForEmergencyManeuverException.class)
    void testTrysAllVariantsWithInitiallySetToNoRotateAndMoveByCols() {
        List<GridCoordinate> rotated = [new GridCoordinate(4, 4), new GridCoordinate(3, 3)]
        randomToReturn = 5;
        def expectedSequence = [0, 1, -2, -1, 2]
        Mockito.when(relocator.rotater.rotateShip(shipState)).thenReturn(rotated)
        Mockito.when(relocator.calculator.relocateShip(eq(game), eq(shipState), eq(rotated), eq(otherCoords), eq(true), eq(expectedSequence))).thenReturn(null)
        Mockito.when(relocator.calculator.relocateShip(eq(game), eq(shipState), eq(rotated), eq(otherCoords), eq(false), eq(expectedSequence))).thenReturn(null)
        Mockito.when(relocator.calculator.relocateShip(eq(game), eq(shipState), eq(shipState.shipGridCells), eq(otherCoords), eq(true), eq(expectedSequence))).thenReturn(null)
        Mockito.when(relocator.calculator.relocateShip(eq(game), eq(shipState), eq(shipState.shipGridCells), eq(otherCoords), eq(false), eq(expectedSequence))).thenReturn(null)

        relocator.relocateShip(game, playerState, shipState)
    }
}
