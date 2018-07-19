package com.jtbdevelopment.TwistedBattleship.rest.handlers;

import com.jtbdevelopment.TwistedBattleship.exceptions.GameIsNotInSetupPhaseException;
import com.jtbdevelopment.TwistedBattleship.exceptions.ShipNotInitializedCorrectlyException;
import com.jtbdevelopment.TwistedBattleship.exceptions.ShipPlacementsNotValidException;
import com.jtbdevelopment.TwistedBattleship.state.GameFeature;
import com.jtbdevelopment.TwistedBattleship.state.TBGame;
import com.jtbdevelopment.TwistedBattleship.state.TBPlayerState;
import com.jtbdevelopment.TwistedBattleship.state.grid.GridCoordinate;
import com.jtbdevelopment.TwistedBattleship.state.ships.Ship;
import com.jtbdevelopment.TwistedBattleship.state.ships.ShipPlacementValidator;
import com.jtbdevelopment.TwistedBattleship.state.ships.ShipState;
import com.jtbdevelopment.games.mongo.MongoGameCoreTestCase;
import com.jtbdevelopment.games.state.GamePhase;
import org.bson.types.ObjectId;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.*;

import static org.junit.Assert.fail;

/**
 * Date: 5/5/15
 * Time: 6:44 AM
 */
public class SetupShipsHandlerTest extends MongoGameCoreTestCase {
    private ShipPlacementValidator validator = Mockito.mock(ShipPlacementValidator.class);
    private SetupShipsHandler setupShipsHandler = new SetupShipsHandler(validator, null, null, null, null, null, null);
    private TBGame game;
    private List<ShipState> VALID_PLACEMENTS = Arrays.asList(
            new ShipState(
                    Ship.Battleship,
                    new TreeSet<>(Arrays.asList(new GridCoordinate(0, 0), new GridCoordinate(1, 0), new GridCoordinate(2, 0), new GridCoordinate(3, 0)))),
            new ShipState(
                    Ship.Carrier,
                    new TreeSet<>(Arrays.asList(new GridCoordinate(0, 1), new GridCoordinate(0, 2), new GridCoordinate(0, 3), new GridCoordinate(0, 4), new GridCoordinate(0, 5)))),
            new ShipState(
                    Ship.Cruiser,
                    new TreeSet<>(Arrays.asList(new GridCoordinate(0, 6), new GridCoordinate(1, 6), new GridCoordinate(2, 6)))),
            new ShipState(
                    Ship.Submarine,
                    new TreeSet<>(Arrays.asList(new GridCoordinate(0, 7), new GridCoordinate(0, 8), new GridCoordinate(0, 9)))),
            new ShipState(
                    Ship.Destroyer,
                    new TreeSet<>(Arrays.asList(new GridCoordinate(0, 10), new GridCoordinate(1, 10)))));

    @Test
    public void testValidPlacementOfShips() {
        TBGame game = new TBGame();
        Map<ObjectId, TBPlayerState> map = new HashMap<>();
        map.put(PONE.getId(), new TBPlayerState());
        map.put(PTWO.getId(), new TBPlayerState());

        game.setId(new ObjectId());
        game.setPlayers(Arrays.asList(PONE, PTWO));
        game.setGamePhase(GamePhase.Setup);
        game.setPlayerDetails(map);
        game.setStartingShips(Arrays.asList(Ship.values()));
        game.getPlayerDetails().values().forEach(ss -> ss.setStartingShips(game.getStartingShips()));
        TBGame returned = setupShipsHandler.handleActionInternal(PONE, game, VALID_PLACEMENTS);
        Assert.assertEquals(returned, game);
        Assert.assertEquals(VALID_PLACEMENTS, game.getPlayerDetails().get(PONE.getId()).getShipStates());
        Assert.assertTrue(game.getPlayerDetails().get(PONE.getId()).isAlive());
        Assert.assertTrue(game.getPlayerDetails().get(PONE.getId()).isSetup());
        Assert.assertFalse(game.getPlayerDetails().get(PTWO.getId()).isAlive());
        Assert.assertFalse(game.getPlayerDetails().get(PTWO.getId()).isSetup());
        Mockito.verify(validator).validateShipPlacementsForGame(game, VALID_PLACEMENTS);
    }

    @Test(expected = ShipPlacementsNotValidException.class)
    public void testCallsPlacementValidatorAndPassesOnException() {
        Mockito.doThrow(new ShipPlacementsNotValidException()).when(validator).validateShipPlacementsForGame(game, VALID_PLACEMENTS);
        setupShipsHandler.handleActionInternal(PONE, game, VALID_PLACEMENTS);
    }

    @Test
    public void testRejectsNonSetupPhaseGames() {
        Arrays.stream(GamePhase.values()).filter(p -> !GamePhase.Setup.equals(p)).forEach(phase -> {
            TBGame game = new TBGame();
            game.setGamePhase(phase);
            try {
                setupShipsHandler.handleActionInternal(PONE, game, null);
                fail("should have");
            } catch (GameIsNotInSetupPhaseException e) {
                //
            }
        });
    }

    @Test(expected = ShipNotInitializedCorrectlyException.class)
    public void testShipIncorrectSegmentsHit() {
        setupShipsHandler.handleActionInternal(PONE, game, Arrays.asList(
                new ShipState(Ship.Battleship, 4, Arrays.asList(new GridCoordinate(0, 0), new GridCoordinate(1, 0), new GridCoordinate(2, 0), new GridCoordinate(3, 0)), Arrays.asList(false, false, false)),
                new ShipState(Ship.Carrier, new TreeSet<>(Arrays.asList(new GridCoordinate(0, 1), new GridCoordinate(0, 2), new GridCoordinate(0, 3), new GridCoordinate(0, 4), new GridCoordinate(0, 5)))),
                new ShipState(Ship.Cruiser, new TreeSet<>(Arrays.asList(new GridCoordinate(0, 6), new GridCoordinate(1, 6), new GridCoordinate(2, 6)))),
                new ShipState(Ship.Submarine, new TreeSet<>(Arrays.asList(new GridCoordinate(0, 7), new GridCoordinate(0, 8), new GridCoordinate(0, 9)))),
                new ShipState(Ship.Destroyer, new TreeSet<>(Arrays.asList(new GridCoordinate(0, 10), new GridCoordinate(1, 10))))));
    }

    @Test(expected = ShipNotInitializedCorrectlyException.class)
    public void testShipMarkedHit() {
        setupShipsHandler.handleActionInternal(PONE, game,
                Arrays.asList(
                        new ShipState(Ship.Battleship, 5, Arrays.asList(new GridCoordinate(0, 0), new GridCoordinate(1, 0), new GridCoordinate(2, 0), new GridCoordinate(3, 0)), Arrays.asList(false, false, false, true)),
                        new ShipState(Ship.Carrier, new TreeSet<>(Arrays.asList(new GridCoordinate(0, 1), new GridCoordinate(0, 2), new GridCoordinate(0, 3), new GridCoordinate(0, 4), new GridCoordinate(0, 5)))),
                        new ShipState(Ship.Cruiser, new TreeSet<>(Arrays.asList(new GridCoordinate(0, 6), new GridCoordinate(1, 6), new GridCoordinate(2, 6)))),
                        new ShipState(Ship.Submarine, new TreeSet<>(Arrays.asList(new GridCoordinate(0, 7), new GridCoordinate(0, 8), new GridCoordinate(0, 9)))),
                        new ShipState(Ship.Destroyer, new TreeSet<>(Arrays.asList(new GridCoordinate(0, 10), new GridCoordinate(1, 10))))));
    }

    @Test(expected = ShipNotInitializedCorrectlyException.class)
    public void testShipIncorrectHealth() {
        setupShipsHandler.handleActionInternal(PONE, game,
                Arrays.asList(
                        new ShipState(Ship.Battleship, 3, Arrays.asList(new GridCoordinate(0, 0), new GridCoordinate(1, 0), new GridCoordinate(2, 0), new GridCoordinate(3, 0)), Arrays.asList(false, false, false, false)),
                        new ShipState(Ship.Carrier, new TreeSet<>(Arrays.asList(new GridCoordinate(0, 1), new GridCoordinate(0, 2), new GridCoordinate(0, 3), new GridCoordinate(0, 4), new GridCoordinate(0, 5)))),
                        new ShipState(Ship.Cruiser, new TreeSet<>(Arrays.asList(new GridCoordinate(0, 6), new GridCoordinate(1, 6), new GridCoordinate(2, 6)))),
                        new ShipState(Ship.Submarine, new TreeSet<>(Arrays.asList(new GridCoordinate(0, 7), new GridCoordinate(0, 8), new GridCoordinate(0, 9)))),
                        new ShipState(Ship.Destroyer, new TreeSet<>(Arrays.asList(new GridCoordinate(0, 10), new GridCoordinate(1, 10))))));
    }

    @Test(expected = ShipNotInitializedCorrectlyException.class)
    public void testShipIncorrectKeyValueMap() {
        setupShipsHandler.handleActionInternal(PONE, game,
                Arrays.asList(
                        new ShipState(Ship.Cruiser, new TreeSet<>(Arrays.asList(new GridCoordinate(0, 0), new GridCoordinate(1, 0), new GridCoordinate(2, 0), new GridCoordinate(3, 0)))),
                        new ShipState(Ship.Carrier, new TreeSet<>(Arrays.asList(new GridCoordinate(0, 1), new GridCoordinate(0, 2), new GridCoordinate(0, 3), new GridCoordinate(0, 4), new GridCoordinate(0, 5)))),
                        new ShipState(Ship.Cruiser, new TreeSet<>(Arrays.asList(new GridCoordinate(0, 6), new GridCoordinate(1, 6), new GridCoordinate(2, 6)))),
                        new ShipState(Ship.Submarine, new TreeSet<>(Arrays.asList(new GridCoordinate(0, 7), new GridCoordinate(0, 8), new GridCoordinate(0, 9)))),
                        new ShipState(Ship.Destroyer, new TreeSet<>(Arrays.asList(new GridCoordinate(0, 10), new GridCoordinate(1, 10))))))
        ;
    }

    @Before
    public void setup() {
        game = new TBGame();
        game.setId(new ObjectId());
        game.setFeatures(new HashSet<>(Collections.singletonList(GameFeature.Grid15x15)));
        game.setGamePhase(GamePhase.Setup);
        game.setStartingShips(Arrays.asList(Ship.values()));
    }

}
