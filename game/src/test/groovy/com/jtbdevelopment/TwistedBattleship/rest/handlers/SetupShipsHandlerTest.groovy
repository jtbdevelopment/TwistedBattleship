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

/**
 * Date: 5/5/15
 * Time: 6:44 AM
 */
class SetupShipsHandlerTest extends MongoGameCoreTestCase {
    SetupShipsHandler setupShipsHandler = new SetupShipsHandler()
    TBGame game = new TBGame(features: [GameFeature.Grid15x15], gamePhase: GamePhase.Setup, startingShips: Ship.values())

    void testValidPlacementOfShips() {
        TBGame game = new TBGame(
                players: [PONE, PTWO],
                gamePhase: GamePhase.Setup,
                playerDetails: [(PONE.id): new TBPlayerState(), (PTWO.id): new TBPlayerState()]
        )
        game.startingShips = Ship.values().toList()
        game.playerDetails.values().each {
            it.startingShips = game.startingShips
        }
        setupShipsHandler.shipPlacementValidator = [
                validateShipPlacementsForGame: {
                    TBGame g, List<ShipState> m ->
                        assert g.is(game)
                        assert m.is(VALID_PLACEMENTS)
                }
        ] as ShipPlacementValidator
        TBGame returned = setupShipsHandler.handleActionInternal(PONE, game, VALID_PLACEMENTS)
        assert returned.is(game)
        assert game.playerDetails[PONE.id].shipStates == VALID_PLACEMENTS
        assert game.playerDetails[PONE.id].alive
        assert game.playerDetails[PONE.id].setup
        assertFalse game.playerDetails[PTWO.id].alive
        assertFalse game.playerDetails[PTWO.id].setup
    }

    void testCallsPlacementValidatorAndPassesOnException() {
        shouldFail(ShipPlacementsNotValidException.class, {
            setupShipsHandler.shipPlacementValidator = [
                    validateShipPlacementsForGame: {
                        TBGame g, List<ShipState> m ->
                            assert g.is(game)
                            assert m.is(VALID_PLACEMENTS)
                            throw new ShipPlacementsNotValidException()
                    }
            ] as ShipPlacementValidator
            setupShipsHandler.handleActionInternal(PONE, game, VALID_PLACEMENTS)
        })
    }

    void testRejectsNonSetupPhaseGames() {
        GamePhase.values().findAll { it != GamePhase.Setup }.each {
            TBGame game = new TBGame(gamePhase: it)
            shouldFail(GameIsNotInSetupPhaseException.class, {
                setupShipsHandler.handleActionInternal(PONE, game, null)
            })
        }
    }

    void testNonMatchingStartupShipsVsSetup() {
        shouldFail(ShipNotInitializedCorrectlyException.class, {
            TBGame game = new TBGame(
                    players: [PONE, PTWO],
                    gamePhase: GamePhase.Setup,
                    playerDetails: [(PONE.id): new TBPlayerState(), (PTWO.id): new TBPlayerState()]
            )
            game.startingShips = [Ship.Destroyer, Ship.Destroyer, Ship.Destroyer, Ship.Destroyer, Ship.Destroyer]
            game.playerDetails.values().each {
                it.startingShips = game.startingShips
            }
            setupShipsHandler.handleActionInternal(PONE, game, VALID_PLACEMENTS)
        })
    }

    void testShipIncorrectSegmentsHit() {
        shouldFail(ShipNotInitializedCorrectlyException.class, {
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
        })
    }

    void testShipMarkedHit() {
        shouldFail(ShipNotInitializedCorrectlyException.class, {
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
        })
    }

    void testShipIncorrectHealth() {
        shouldFail(ShipNotInitializedCorrectlyException.class, {
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
        })
    }

    void testShipIncorrectKeyValueMap() {
        shouldFail(ShipNotInitializedCorrectlyException.class, {
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
        })
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
