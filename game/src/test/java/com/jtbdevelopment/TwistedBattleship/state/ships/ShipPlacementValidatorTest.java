package com.jtbdevelopment.TwistedBattleship.state.ships;

import com.jtbdevelopment.TwistedBattleship.exceptions.NotAllShipsSetupException;
import com.jtbdevelopment.TwistedBattleship.exceptions.ShipPlacementsNotValidException;
import com.jtbdevelopment.TwistedBattleship.state.GameFeature;
import com.jtbdevelopment.TwistedBattleship.state.TBGame;
import com.jtbdevelopment.TwistedBattleship.state.grid.GridCoordinate;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.TreeSet;

/**
 * Date: 4/30/15
 * Time: 8:46 PM
 */
public class ShipPlacementValidatorTest {
    private ShipPlacementValidator validator = new ShipPlacementValidator();
    private TBGame game;

    @Before
    public void setup() {
        game = new TBGame();
        game.setFeatures(new HashSet<>(Collections.singletonList(GameFeature.Grid15x15)));
        game.setGridSize(15);
        game.setStartingShips(Arrays.asList(Ship.values()));
    }

    @Test
    public void testShipPositionsValid1() {
        validator.validateShipPlacementsForGame(game,
                Arrays.asList(
                        new ShipState(Ship.Battleship,
                                new TreeSet<>(Arrays.asList(
                                        new GridCoordinate(0, 0),
                                        new GridCoordinate(0, 1),
                                        new GridCoordinate(0, 2),
                                        new GridCoordinate(0, 3)))),
                        new ShipState(Ship.Carrier,
                                new TreeSet<>(Arrays.asList(
                                        new GridCoordinate(1, 0),
                                        new GridCoordinate(1, 1),
                                        new GridCoordinate(1, 2),
                                        new GridCoordinate(1, 3),
                                        new GridCoordinate(1, 4)))),
                        new ShipState(Ship.Cruiser,
                                new TreeSet<>(Arrays.asList(
                                        new GridCoordinate(2, 0),
                                        new GridCoordinate(2, 1),
                                        new GridCoordinate(2, 2)))),
                        new ShipState(Ship.Submarine,
                                new TreeSet<>(Arrays.asList(
                                        new GridCoordinate(3, 0),
                                        new GridCoordinate(3, 1),
                                        new GridCoordinate(3, 2)))),
                        new ShipState(Ship.Destroyer,
                                new TreeSet<>(Arrays.asList(
                                        new GridCoordinate(0, 4),
                                        new GridCoordinate(0, 5))))));
    }

    @Test
    public void testShipPositionsValid2() {
        validator.validateShipPlacementsForGame(game,
                Arrays.asList(
                        new ShipState(Ship.Battleship,
                                new TreeSet<>(Arrays.asList(
                                        new GridCoordinate(0, 0),
                                        new GridCoordinate(1, 0),
                                        new GridCoordinate(2, 0),
                                        new GridCoordinate(3, 0)))),
                        new ShipState(Ship.Carrier,
                                new TreeSet<>(Arrays.asList(
                                        new GridCoordinate(0, 1),
                                        new GridCoordinate(1, 1),
                                        new GridCoordinate(2, 1),
                                        new GridCoordinate(3, 1),
                                        new GridCoordinate(4, 1)))),
                        new ShipState(Ship.Cruiser,
                                new TreeSet<>(Arrays.asList(
                                        new GridCoordinate(0, 2),
                                        new GridCoordinate(1, 2),
                                        new GridCoordinate(2, 2)))),
                        new ShipState(Ship.Submarine,
                                new TreeSet<>(Arrays.asList(
                                        new GridCoordinate(0, 3),
                                        new GridCoordinate(0, 4),
                                        new GridCoordinate(0, 5)))),
                        new ShipState(Ship.Destroyer,
                                new TreeSet<>(Arrays.asList(
                                        new GridCoordinate(4, 0),
                                        new GridCoordinate(5, 0))))));
    }

    @Test
    public void testShipPositionsValid3() {
        validator.validateShipPlacementsForGame(game,
                Arrays.asList(
                        new ShipState(Ship.Battleship,
                                new TreeSet<>(Arrays.asList(
                                        new GridCoordinate(0, 0),
                                        new GridCoordinate(1, 0),
                                        new GridCoordinate(2, 0),
                                        new GridCoordinate(3, 0)))),
                        new ShipState(Ship.Carrier,
                                new TreeSet<>(Arrays.asList(
                                        new GridCoordinate(0, 1),
                                        new GridCoordinate(0, 2),
                                        new GridCoordinate(0, 3),
                                        new GridCoordinate(0, 4),
                                        new GridCoordinate(0, 5)))),
                        new ShipState(Ship.Cruiser,
                                new TreeSet<>(Arrays.asList(
                                        new GridCoordinate(0, 6),
                                        new GridCoordinate(1, 6),
                                        new GridCoordinate(2, 6)))),
                        new ShipState(Ship.Submarine,
                                new TreeSet<>(Arrays.asList(
                                        new GridCoordinate(0, 7),
                                        new GridCoordinate(0, 8),
                                        new GridCoordinate(0, 9)))),
                        new ShipState(Ship.Destroyer,
                                new TreeSet<>(Arrays.asList(
                                        new GridCoordinate(0, 10),
                                        new GridCoordinate(1, 10))))));
    }

    @Test(expected = ShipPlacementsNotValidException.class)
    public void testShipMissingCoordinates() {
        validator.validateShipPlacementsForGame(game,
                Arrays.asList(
                        new ShipState(Ship.Battleship, 4,
                                Arrays.asList(
                                        new GridCoordinate(0, 0),
                                        new GridCoordinate(1, 0),
                                        new GridCoordinate(2, 0)),
                                Arrays.asList(false, false, false, false)),
                        new ShipState(Ship.Carrier,
                                new TreeSet<>(Arrays.asList(
                                        new GridCoordinate(0, 1),
                                        new GridCoordinate(0, 2),
                                        new GridCoordinate(0, 3),
                                        new GridCoordinate(0, 4),
                                        new GridCoordinate(0, 5)))),
                        new ShipState(Ship.Cruiser,
                                new TreeSet<>(Arrays.asList(
                                        new GridCoordinate(0, 6),
                                        new GridCoordinate(1, 6),
                                        new GridCoordinate(2, 6)))),
                        new ShipState(Ship.Submarine,
                                new TreeSet<>(Arrays.asList(
                                        new GridCoordinate(0, 7),
                                        new GridCoordinate(0, 8),
                                        new GridCoordinate(0, 9)))),
                        new ShipState(Ship.Destroyer,
                                new TreeSet<>(Arrays.asList(
                                        new GridCoordinate(0, 10),
                                        new GridCoordinate(1, 10))))));
    }

    @Test(expected = ShipPlacementsNotValidException.class)
    public void testShipExtraCoordinates() {
        validator.validateShipPlacementsForGame(game,
                Arrays.asList(new ShipState(Ship.Battleship, 4,
                                Arrays.asList(
                                        new GridCoordinate(0, 0),
                                        new GridCoordinate(1, 0),
                                        new GridCoordinate(2, 0),
                                        new GridCoordinate(3, 0),
                                        new GridCoordinate(4, 0)),
                                Arrays.asList(false, false, false, false)),
                        new ShipState(Ship.Carrier,
                                new TreeSet<>(Arrays.asList(
                                        new GridCoordinate(0, 1),
                                        new GridCoordinate(0, 2),
                                        new GridCoordinate(0, 3),
                                        new GridCoordinate(0, 4),
                                        new GridCoordinate(0, 5)))),
                        new ShipState(Ship.Cruiser,
                                new TreeSet<>(Arrays.asList(
                                        new GridCoordinate(0, 6),
                                        new GridCoordinate(1, 6),
                                        new GridCoordinate(2, 6)))),
                        new ShipState(Ship.Submarine,
                                new TreeSet<>(Arrays.asList(
                                        new GridCoordinate(0, 7),
                                        new GridCoordinate(0, 8),
                                        new GridCoordinate(0, 9)))),
                        new ShipState(Ship.Destroyer,
                                new TreeSet<>(Arrays.asList(
                                        new GridCoordinate(0, 10),
                                        new GridCoordinate(1, 10))))));
    }

    @Test(expected = ShipPlacementsNotValidException.class)
    public void testShipPositionsOutsideBoundsPositive() {
        validator.validateShipPlacementsForGame(game,
                Arrays.asList(new ShipState(Ship.Battleship,
                                new TreeSet<>(Arrays.asList(
                                        new GridCoordinate(0, 0),
                                        new GridCoordinate(1, 0),
                                        new GridCoordinate(2, 0),
                                        new GridCoordinate(3, 0)))),
                        new ShipState(Ship.Carrier,
                                new TreeSet<>(Arrays.asList(
                                        new GridCoordinate(0, 1),
                                        new GridCoordinate(1, 1),
                                        new GridCoordinate(2, 1),
                                        new GridCoordinate(3, 1),
                                        new GridCoordinate(4, 1)))),
                        new ShipState(Ship.Cruiser,
                                new TreeSet<>(Arrays.asList(
                                        new GridCoordinate(0, 2),
                                        new GridCoordinate(1, 2),
                                        new GridCoordinate(2, 2)))),
                        new ShipState(Ship.Submarine,
                                new TreeSet<>(Arrays.asList(
                                        new GridCoordinate(13, 3),
                                        new GridCoordinate(14, 4),
                                        new GridCoordinate(15, 5)))),
                        new ShipState(Ship.Destroyer,
                                new TreeSet<>(Arrays.asList(
                                        new GridCoordinate(4, 0),
                                        new GridCoordinate(5, 0))))));
    }

    @Test(expected = ShipPlacementsNotValidException.class)
    public void testShipPositionsOutsideBoundsNegative() {
        validator.validateShipPlacementsForGame(game,
                Arrays.asList(
                        new ShipState(Ship.Battleship,
                                new TreeSet<>(Arrays.asList(
                                        new GridCoordinate(0, 0),
                                        new GridCoordinate(1, 0),
                                        new GridCoordinate(2, 0),
                                        new GridCoordinate(3, 0)))),
                        new ShipState(Ship.Carrier,
                                new TreeSet<>(Arrays.asList(
                                        new GridCoordinate(0, 1),
                                        new GridCoordinate(1, 1),
                                        new GridCoordinate(2, 1),
                                        new GridCoordinate(3, 1),
                                        new GridCoordinate(4, 1)))),
                        new ShipState(Ship.Cruiser,
                                new TreeSet<>(Arrays.asList(
                                        new GridCoordinate(0, 2),
                                        new GridCoordinate(1, 2),
                                        new GridCoordinate(2, 2)))),
                        new ShipState(Ship.Submarine,
                                new TreeSet<>(Arrays.asList(
                                        new GridCoordinate(-1, 3),
                                        new GridCoordinate(0, 3),
                                        new GridCoordinate(1, 3)))),
                        new ShipState(Ship.Destroyer,
                                new TreeSet<>(Arrays.asList(
                                        new GridCoordinate(4, 0),
                                        new GridCoordinate(5, 0))))));
    }

    @Test(expected = NotAllShipsSetupException.class)
    public void testNotAllShipsInList() {
        validator.validateShipPlacementsForGame(game,
                Arrays.asList(
                        new ShipState(Ship.Carrier,
                                new TreeSet<>(Arrays.asList(
                                        new GridCoordinate(0, 1),
                                        new GridCoordinate(1, 1),
                                        new GridCoordinate(2, 1),
                                        new GridCoordinate(3, 1),
                                        new GridCoordinate(4, 1)))),
                        new ShipState(Ship.Cruiser,
                                new TreeSet<>(Arrays.asList(
                                        new GridCoordinate(0, 2),
                                        new GridCoordinate(1, 2),
                                        new GridCoordinate(2, 2)))),
                        new ShipState(Ship.Submarine,
                                new TreeSet<>(Arrays.asList(
                                        new GridCoordinate(0, 3),
                                        new GridCoordinate(15, 3),
                                        new GridCoordinate(0, 3)))),
                        new ShipState(Ship.Destroyer,
                                new TreeSet<>(Arrays.asList(
                                        new GridCoordinate(4, 0),
                                        new GridCoordinate(5, 0))))));
    }

    @Test(expected = ShipPlacementsNotValidException.class)
    public void testShipPositionsOverlap() {
        validator.validateShipPlacementsForGame(game,
                Arrays.asList(new ShipState(Ship.Battleship,
                                new TreeSet<>(Arrays.asList(
                                        new GridCoordinate(0, 0),
                                        new GridCoordinate(1, 0),
                                        new GridCoordinate(2, 0),
                                        new GridCoordinate(3, 0)))),
                        new ShipState(Ship.Carrier,
                                new TreeSet<>(Arrays.asList(
                                        new GridCoordinate(0, 1),
                                        new GridCoordinate(1, 1),
                                        new GridCoordinate(2, 1),
                                        new GridCoordinate(3, 1),
                                        new GridCoordinate(4, 1)))),
                        new ShipState(Ship.Cruiser,
                                new TreeSet<>(Arrays.asList(
                                        new GridCoordinate(0, 2),
                                        new GridCoordinate(1, 2),
                                        new GridCoordinate(2, 2)))),
                        new ShipState(Ship.Submarine,
                                new TreeSet<>(Arrays.asList(
                                        new GridCoordinate(0, 3),
                                        new GridCoordinate(0, 4),
                                        new GridCoordinate(0, 5)))),
                        new ShipState(Ship.Destroyer,
                                new TreeSet<>(Arrays.asList(
                                        new GridCoordinate(3, 0),
                                        new GridCoordinate(4, 0))))));
    }

    @Test(expected = ShipPlacementsNotValidException.class)
    public void testShipPositionsNonContiguousVertical() {
        validator.validateShipPlacementsForGame(game,
                Arrays.asList(new ShipState(Ship.Battleship,
                                new TreeSet<>(Arrays.asList(
                                        new GridCoordinate(0, 0),
                                        new GridCoordinate(1, 0),
                                        new GridCoordinate(2, 11),
                                        new GridCoordinate(3, 0)))),
                        new ShipState(Ship.Carrier,
                                new TreeSet<>(Arrays.asList(
                                        new GridCoordinate(0, 1),
                                        new GridCoordinate(1, 1),
                                        new GridCoordinate(2, 1),
                                        new GridCoordinate(3, 1),
                                        new GridCoordinate(4, 1)))),
                        new ShipState(Ship.Cruiser,
                                new TreeSet<>(Arrays.asList(
                                        new GridCoordinate(0, 2),
                                        new GridCoordinate(1, 2),
                                        new GridCoordinate(2, 2)))),
                        new ShipState(Ship.Submarine,
                                new TreeSet<>(Arrays.asList(
                                        new GridCoordinate(0, 4),
                                        new GridCoordinate(0, 5),
                                        new GridCoordinate(0, 6)))),
                        new ShipState(Ship.Destroyer,
                                new TreeSet<>(Arrays.asList(
                                        new GridCoordinate(4, 0),
                                        new GridCoordinate(5, 0))))));
    }

    @Test(expected = ShipPlacementsNotValidException.class)
    public void testShipPositionsNonContiguousVertical2() {
        validator.validateShipPlacementsForGame(game,
                Arrays.asList(
                        new ShipState(Ship.Battleship,
                                new TreeSet<>(
                                        Arrays.asList(
                                                new GridCoordinate(0, 0),
                                                new GridCoordinate(1, 0),
                                                new GridCoordinate(2, 0),
                                                new GridCoordinate(10, 0)))),
                        new ShipState(Ship.Carrier,
                                new TreeSet<>(
                                        Arrays.asList(
                                                new GridCoordinate(0, 1),
                                                new GridCoordinate(1, 1),
                                                new GridCoordinate(2, 1),
                                                new GridCoordinate(3, 1),
                                                new GridCoordinate(4, 1)))),
                        new ShipState(Ship.Cruiser,
                                new TreeSet<>(

                                        Arrays.asList(
                                                new GridCoordinate(0, 2),
                                                new GridCoordinate(1, 2),
                                                new GridCoordinate(2, 2)))),
                        new ShipState(Ship.Submarine,
                                new TreeSet<>(Arrays.asList(
                                        new GridCoordinate(0, 4),
                                        new GridCoordinate(0, 5),
                                        new GridCoordinate(0, 6)))),
                        new ShipState(Ship.Destroyer,
                                new TreeSet<>(Arrays.asList(
                                        new GridCoordinate(4, 0),
                                        new GridCoordinate(5, 0))))));
    }

    @Test(expected = ShipPlacementsNotValidException.class)
    public void testShipPositionsNonContiguousHorizontal() {
        validator.validateShipPlacementsForGame(game,
                Arrays.asList(
                        new ShipState(Ship.Battleship,
                                new TreeSet<>(Arrays.asList(
                                        new GridCoordinate(10, 0),
                                        new GridCoordinate(10, 1),
                                        new GridCoordinate(10, 2),
                                        new GridCoordinate(11, 3)))),
                        new ShipState(Ship.Carrier,
                                new TreeSet<>(Arrays.asList(
                                        new GridCoordinate(1, 0),
                                        new GridCoordinate(1, 1),
                                        new GridCoordinate(1, 2),
                                        new GridCoordinate(1, 3),
                                        new GridCoordinate(1, 4)))),
                        new ShipState(Ship.Cruiser,
                                new TreeSet<>(Arrays.asList(
                                        new GridCoordinate(2, 0),
                                        new GridCoordinate(2, 1),
                                        new GridCoordinate(2, 2)))),
                        new ShipState(Ship.Submarine,
                                new TreeSet<>(Arrays.asList(
                                        new GridCoordinate(3, 0),
                                        new GridCoordinate(3, 1),
                                        new GridCoordinate(3, 2)))),
                        new ShipState(Ship.Destroyer,
                                new TreeSet<>(Arrays.asList(
                                        new GridCoordinate(0, 4),
                                        new GridCoordinate(0, 5))))));
    }

    @Test(expected = ShipPlacementsNotValidException.class)
    public void testShipPositionsNonContiguousHorizontal2() {
        validator.validateShipPlacementsForGame(game,
                Arrays.asList(
                        new ShipState(Ship.Battleship,
                                new TreeSet<>(Arrays.asList(
                                        new GridCoordinate(10, 0),
                                        new GridCoordinate(10, 1),
                                        new GridCoordinate(10, 2),
                                        new GridCoordinate(10, 5)))),
                        new ShipState(Ship.Carrier,
                                new TreeSet<>(Arrays.asList(
                                        new GridCoordinate(1, 0),
                                        new GridCoordinate(1, 1),
                                        new GridCoordinate(1, 2),
                                        new GridCoordinate(1, 3),
                                        new GridCoordinate(1, 4)))),
                        new ShipState(Ship.Cruiser,
                                new TreeSet<>(Arrays.asList(
                                        new GridCoordinate(2, 0),
                                        new GridCoordinate(2, 1),
                                        new GridCoordinate(2, 2)))),
                        new ShipState(Ship.Submarine,
                                new TreeSet<>(Arrays.asList(
                                        new GridCoordinate(3, 0),
                                        new GridCoordinate(3, 1),
                                        new GridCoordinate(3, 2)))),
                        new ShipState(Ship.Destroyer,
                                new TreeSet<>(Arrays.asList(
                                        new GridCoordinate(0, 4),
                                        new GridCoordinate(0, 5))))));
    }
}
