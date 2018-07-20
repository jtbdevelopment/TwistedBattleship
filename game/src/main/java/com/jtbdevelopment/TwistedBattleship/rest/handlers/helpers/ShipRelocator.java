package com.jtbdevelopment.TwistedBattleship.rest.handlers.helpers;

import com.jtbdevelopment.TwistedBattleship.exceptions.NoRoomForEmergencyManeuverException;
import com.jtbdevelopment.TwistedBattleship.state.TBGame;
import com.jtbdevelopment.TwistedBattleship.state.TBPlayerState;
import com.jtbdevelopment.TwistedBattleship.state.grid.GridCoordinate;
import com.jtbdevelopment.TwistedBattleship.state.ships.ShipState;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Date: 5/26/15
 * Time: 7:13 PM
 */
@Component
public class ShipRelocator {
    private static final List<List<Integer>> SHUFFLED_ADJUSTMENT_TRIALS =
            Arrays.asList(
                    Arrays.asList(-2, -1, 0, 1, 2),
                    Arrays.asList(2, 1, 0, -1, 2),
                    Arrays.asList(2, -2, 1, -1, 0),
                    Arrays.asList(0, -1, 1, -2, 2),
                    Arrays.asList(2, -1, -2, 1, 0),
                    Arrays.asList(0, 1, -2, -1, 2));
    private final ShipRelocatorCalculator calculator;
    private final ShipRotater rotater;
    Random generator = new Random();

    ShipRelocator(
            final ShipRelocatorCalculator calculator,
            final ShipRotater rotater) {
        this.calculator = calculator;
        this.rotater = rotater;
    }

    public List<GridCoordinate> relocateShip(final TBGame game, final TBPlayerState playerState, final ShipState ship) {
        Set<GridCoordinate> otherShipCoordinates = new HashSet<>(playerState.getCoordinateShipMap().keySet());
        otherShipCoordinates.removeAll(ship.getShipGridCells());
        boolean rotate = generator.nextBoolean();
        boolean moveRows = generator.nextBoolean();
        List<Integer> adjustmentSequenceToTry = SHUFFLED_ADJUSTMENT_TRIALS.get(generator.nextInt(SHUFFLED_ADJUSTMENT_TRIALS.size() - 1));

        List<GridCoordinate> initialCoordinates = getInitialCoordinates(ship, rotate);
        List<GridCoordinate> newCoordinates = calculator.relocateShip(game, ship, initialCoordinates, otherShipCoordinates, moveRows, adjustmentSequenceToTry);
        if (newCoordinates == null) {
            //  try columns instead
            newCoordinates = calculator.relocateShip(game, ship, initialCoordinates, otherShipCoordinates, !moveRows, adjustmentSequenceToTry);
            if (newCoordinates == null) {
                //  swap rotate flag and try rows again
                initialCoordinates = getInitialCoordinates(ship, !rotate);
                newCoordinates = calculator.relocateShip(game, ship, initialCoordinates, otherShipCoordinates, moveRows, adjustmentSequenceToTry);
                if (newCoordinates == null) {
                    //  last attempt
                    newCoordinates = calculator.relocateShip(game, ship, initialCoordinates, otherShipCoordinates, !moveRows, adjustmentSequenceToTry);
                    if (newCoordinates == null) {
                        throw new NoRoomForEmergencyManeuverException();
                    }

                }

            }

        }

        return newCoordinates;
    }

    private List<GridCoordinate> getInitialCoordinates(final ShipState ship, final boolean rotate) {
        if (rotate) {
            return rotater.rotateShip(ship);
        } else {
            return new LinkedList<>(ship.getShipGridCells());
        }

    }
}
