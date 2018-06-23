package com.jtbdevelopment.TwistedBattleship.rest.handlers

import com.jtbdevelopment.TwistedBattleship.exceptions.GameIsNotInSetupPhaseException
import com.jtbdevelopment.TwistedBattleship.exceptions.ShipNotInitializedCorrectlyException
import com.jtbdevelopment.TwistedBattleship.exceptions.ShipPlacementsNotValidException
import com.jtbdevelopment.TwistedBattleship.state.GameFeature
import com.jtbdevelopment.TwistedBattleship.state.TBGame
import com.jtbdevelopment.TwistedBattleship.state.TBPlayerState
import com.jtbdevelopment.TwistedBattleship.state.grid.GridCoordinate
import com.jtbdevelopment.TwistedBattleship.state.ships.Ship
import com.jtbdevelopment.TwistedBattleship.state.ships.ShipPlacementValidator
import com.jtbdevelopment.TwistedBattleship.state.ships.ShipState
import com.jtbdevelopment.games.mongo.MongoGameCoreTestCase
import com.jtbdevelopment.games.state.GamePhase
import org.bson.types.ObjectId
import org.junit.Test
import org.mockito.Mockito

import static org.junit.Assert.assertFalse

/**
 * Date: 5/5/15
 * Time: 6:44 AM
 */
class SetupShipsHandlerTest extends MongoGameCoreTestCase {
    private ShipPlacementValidator validator = Mockito.mock(ShipPlacementValidator.class)
    SetupShipsHandler setupShipsHandler = new SetupShipsHandler(validator, null, null, null, null, null, null)
    TBGame game = new TBGame(id: new ObjectId(), features: [GameFeature.Grid15x15], gamePhase: GamePhase.Setup, startingShips: Ship.values())

    @Test
    void testValidPlacementOfShips() {
        TBGame game = new TBGame(
                id: new ObjectId(),
                players: [PONE, PTWO],
                gamePhase: GamePhase.Setup,
                playerDetails: [(PONE.id): new TBPlayerState(), (PTWO.id): new TBPlayerState()]
        )
        game.startingShips = Ship.values().toList()
        game.playerDetails.values().each {
            it.startingShips = game.startingShips
        }
        TBGame returned = setupShipsHandler.handleActionInternal(PONE, game, VALID_PLACEMENTS)
        assert returned.is(game)
        assert game.playerDetails[PONE.id].shipStates == VALID_PLACEMENTS
        assert game.playerDetails[PONE.id].alive
        assert game.playerDetails[PONE.id].setup
        assertFalse game.playerDetails[PTWO.id].alive
        assertFalse game.playerDetails[PTWO.id].setup
        Mockito.verify(validator).validateShipPlacementsForGame(game, VALID_PLACEMENTS)
    }

    @Test(expected = ShipPlacementsNotValidException.class)
    void testCallsPlacementValidatorAndPassesOnException() {
        Mockito.when(validator.validateShipPlacementsForGame(game, VALID_PLACEMENTS)).thenThrow(new ShipPlacementsNotValidException())
        setupShipsHandler.handleActionInternal(PONE, game, VALID_PLACEMENTS)
    }

    @Test
    void testRejectsNonSetupPhaseGames() {
        GamePhase.values().findAll { it != GamePhase.Setup }.each {
            TBGame game = new TBGame(gamePhase: it)
            try {
                setupShipsHandler.handleActionInternal(PONE, game, null)
            } catch (GameIsNotInSetupPhaseException e) {
                //
            }
        }
    }

    @Test(expected = ShipNotInitializedCorrectlyException.class)
    void testShipIncorrectSegmentsHit() {
        setupShipsHandler.handleActionInternal(
                PONE,
                game,
                [
                        new ShipState(Ship.Battleship,
                                4,
                                [
                                        new GridCoordinate(0, 0),
                                        new GridCoordinate(1, 0),
                                        new GridCoordinate(2, 0),
                                        new GridCoordinate(3, 0)
                                ],
                                [false, false, false]             //wrong
                        ),
                        new ShipState(Ship.Carrier,
                                new TreeSet([
                                        new GridCoordinate(0, 1),
                                        new GridCoordinate(0, 2),
                                        new GridCoordinate(0, 3),
                                        new GridCoordinate(0, 4),
                                        new GridCoordinate(0, 5)
                                ])
                        ),
                        new ShipState(Ship.Cruiser,
                                new TreeSet([
                                        new GridCoordinate(0, 6),
                                        new GridCoordinate(1, 6),
                                        new GridCoordinate(2, 6),
                                ])
                        ),
                        new ShipState(Ship.Submarine,
                                new TreeSet([
                                        new GridCoordinate(0, 7),
                                        new GridCoordinate(0, 8),
                                        new GridCoordinate(0, 9),
                                ])
                        ),
                        new ShipState(Ship.Destroyer,
                                new TreeSet([
                                        new GridCoordinate(0, 10),
                                        new GridCoordinate(1, 10)
                                ])
                        ),
                ])
    }

    @Test(expected = ShipNotInitializedCorrectlyException.class)
    void testShipMarkedHit() {
        setupShipsHandler.handleActionInternal(
                PONE,
                game,
                [
                        new ShipState(Ship.Battleship,
                                5,
                                [
                                        new GridCoordinate(0, 0),
                                        new GridCoordinate(1, 0),
                                        new GridCoordinate(2, 0),
                                        new GridCoordinate(3, 0)
                                ],
                                [false, false, false, true]                // wrong
                        ),
                        new ShipState(Ship.Carrier,
                                new TreeSet([
                                        new GridCoordinate(0, 1),
                                        new GridCoordinate(0, 2),
                                        new GridCoordinate(0, 3),
                                        new GridCoordinate(0, 4),
                                        new GridCoordinate(0, 5)
                                ])
                        ),
                        new ShipState(Ship.Cruiser,
                                new TreeSet([
                                        new GridCoordinate(0, 6),
                                        new GridCoordinate(1, 6),
                                        new GridCoordinate(2, 6),
                                ])
                        ),
                        new ShipState(Ship.Submarine,
                                new TreeSet([
                                        new GridCoordinate(0, 7),
                                        new GridCoordinate(0, 8),
                                        new GridCoordinate(0, 9),
                                ])
                        ),
                        new ShipState(Ship.Destroyer,
                                new TreeSet([
                                        new GridCoordinate(0, 10),
                                        new GridCoordinate(1, 10)
                                ])
                        ),
                ])
    }

    @Test(expected = ShipNotInitializedCorrectlyException.class)
    void testShipIncorrectHealth() {
        setupShipsHandler.handleActionInternal(
                PONE,
                game,
                [
                        new ShipState(Ship.Battleship,
                                3,                                   // wrong
                                [
                                        new GridCoordinate(0, 0),
                                        new GridCoordinate(1, 0),
                                        new GridCoordinate(2, 0),
                                        new GridCoordinate(3, 0)
                                ],
                                [false, false, false, false]
                        ),
                        new ShipState(Ship.Carrier,
                                new TreeSet([
                                        new GridCoordinate(0, 1),
                                        new GridCoordinate(0, 2),
                                        new GridCoordinate(0, 3),
                                        new GridCoordinate(0, 4),
                                        new GridCoordinate(0, 5)
                                ])
                        ),
                        new ShipState(Ship.Cruiser,
                                new TreeSet([
                                        new GridCoordinate(0, 6),
                                        new GridCoordinate(1, 6),
                                        new GridCoordinate(2, 6),
                                ])
                        ),
                        new ShipState(Ship.Submarine,
                                new TreeSet([
                                        new GridCoordinate(0, 7),
                                        new GridCoordinate(0, 8),
                                        new GridCoordinate(0, 9),
                                ])
                        ),
                        new ShipState(Ship.Destroyer,
                                new TreeSet([
                                        new GridCoordinate(0, 10),
                                        new GridCoordinate(1, 10)
                                ])
                        ),
                ])
    }

    @Test(expected = ShipNotInitializedCorrectlyException.class)
    void testShipIncorrectKeyValueMap() {
        setupShipsHandler.handleActionInternal(
                PONE,
                game,
                [
                        new ShipState(Ship.Cruiser,        //wrong
                                new TreeSet([
                                        new GridCoordinate(0, 0),
                                        new GridCoordinate(1, 0),
                                        new GridCoordinate(2, 0),
                                        new GridCoordinate(3, 0)
                                ]),
                        ),
                        new ShipState(Ship.Carrier,
                                new TreeSet([
                                        new GridCoordinate(0, 1),
                                        new GridCoordinate(0, 2),
                                        new GridCoordinate(0, 3),
                                        new GridCoordinate(0, 4),
                                        new GridCoordinate(0, 5)
                                ])
                        ),
                        new ShipState(Ship.Cruiser,
                                new TreeSet([
                                        new GridCoordinate(0, 6),
                                        new GridCoordinate(1, 6),
                                        new GridCoordinate(2, 6),
                                ])
                        ),
                        new ShipState(Ship.Submarine,
                                new TreeSet([
                                        new GridCoordinate(0, 7),
                                        new GridCoordinate(0, 8),
                                        new GridCoordinate(0, 9),
                                ])
                        ),
                        new ShipState(Ship.Destroyer,
                                new TreeSet([
                                        new GridCoordinate(0, 10),
                                        new GridCoordinate(1, 10)
                                ])
                        ),
                ])
    }

    List<ShipState> VALID_PLACEMENTS = [
            new ShipState(Ship.Battleship,
                    new TreeSet([
                            new GridCoordinate(0, 0),
                            new GridCoordinate(1, 0),
                            new GridCoordinate(2, 0),
                            new GridCoordinate(3, 0)
                    ])
            ),
            new ShipState(Ship.Carrier,
                    new TreeSet([
                            new GridCoordinate(0, 1),
                            new GridCoordinate(0, 2),
                            new GridCoordinate(0, 3),
                            new GridCoordinate(0, 4),
                            new GridCoordinate(0, 5)
                    ])
            ),
            new ShipState(Ship.Cruiser,
                    new TreeSet([
                            new GridCoordinate(0, 6),
                            new GridCoordinate(1, 6),
                            new GridCoordinate(2, 6),
                    ])
            ),
            new ShipState(Ship.Submarine,
                    new TreeSet([
                            new GridCoordinate(0, 7),
                            new GridCoordinate(0, 8),
                            new GridCoordinate(0, 9),
                    ])
            ),
            new ShipState(Ship.Destroyer,
                    new TreeSet([
                            new GridCoordinate(0, 10),
                            new GridCoordinate(1, 10)
                    ])
            ),
    ]

}
