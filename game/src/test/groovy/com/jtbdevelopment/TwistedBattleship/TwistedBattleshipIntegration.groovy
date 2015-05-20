package com.jtbdevelopment.TwistedBattleship

import com.jtbdevelopment.TwistedBattleship.dao.GameRepository
import com.jtbdevelopment.TwistedBattleship.rest.GameFeatureInfo
import com.jtbdevelopment.TwistedBattleship.rest.Target
import com.jtbdevelopment.TwistedBattleship.rest.services.messages.FeaturesAndPlayers
import com.jtbdevelopment.TwistedBattleship.state.GameFeature
import com.jtbdevelopment.TwistedBattleship.state.TBGame
import com.jtbdevelopment.TwistedBattleship.state.grid.Grid
import com.jtbdevelopment.TwistedBattleship.state.grid.GridCellState
import com.jtbdevelopment.TwistedBattleship.state.grid.GridCoordinate
import com.jtbdevelopment.TwistedBattleship.state.masked.TBMaskedGame
import com.jtbdevelopment.TwistedBattleship.state.ships.Ship
import com.jtbdevelopment.core.hazelcast.caching.HazelcastCacheManager
import com.jtbdevelopment.games.dev.utilities.integrationtesting.AbstractGameIntegration
import com.jtbdevelopment.games.players.Player
import com.jtbdevelopment.games.state.GamePhase
import com.jtbdevelopment.games.state.PlayerState
import org.bson.types.ObjectId
import org.junit.BeforeClass
import org.junit.Test

import javax.ws.rs.client.Entity
import javax.ws.rs.client.WebTarget
import javax.ws.rs.core.GenericType
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

/**
 * Date: 4/26/15
 * Time: 10:36 AM
 */
class TwistedBattleshipIntegration extends AbstractGameIntegration<TBMaskedGame> {

    static HazelcastCacheManager cacheManager
    static GameRepository gameRepository

    @BeforeClass
    static void setup() {
        cacheManager = context.getBean(HazelcastCacheManager.class)
        gameRepository = context.getBean(GameRepository.class)
    }

    @Test
    void testGetFeatures() {
        def client = createAPITarget(TEST_PLAYER2)
        def features = client.path("features").request(MediaType.APPLICATION_JSON_TYPE).get(
                new GenericType<List<GameFeatureInfo>>() {
                })
        assert features == [
                new GameFeatureInfo(GameFeature.GridSize,
                        [
                                new GameFeatureInfo.Detail(GameFeature.Grid10x10),
                                new GameFeatureInfo.Detail(GameFeature.Grid15x15),
                                new GameFeatureInfo.Detail(GameFeature.Grid20x20),
                        ]),
                new GameFeatureInfo(GameFeature.FogOfWar,
                        [
                                new GameFeatureInfo.Detail(GameFeature.SharedIntel),
                                new GameFeatureInfo.Detail(GameFeature.IsolatedIntel),
                        ]),
                new GameFeatureInfo(GameFeature.ECM,
                        [
                                new GameFeatureInfo.Detail(GameFeature.ECMEnabled),
                                new GameFeatureInfo.Detail(GameFeature.ECMDisabled),
                        ]),
                new GameFeatureInfo(GameFeature.Spy,
                        [
                                new GameFeatureInfo.Detail(GameFeature.SpyEnabled),
                                new GameFeatureInfo.Detail(GameFeature.SpyDisabled),
                        ]),
                new GameFeatureInfo(GameFeature.EvasiveManeuvers,
                        [
                                new GameFeatureInfo.Detail(GameFeature.EMEnabled),
                                new GameFeatureInfo.Detail(GameFeature.EMDisabled),
                        ]),
                new GameFeatureInfo(GameFeature.EmergencyRepairs,
                        [
                                new GameFeatureInfo.Detail(GameFeature.EREnabled),
                                new GameFeatureInfo.Detail(GameFeature.ERDisabled),
                        ]),
                new GameFeatureInfo(GameFeature.ActionsPerTurn,
                        [
                                new GameFeatureInfo.Detail(GameFeature.PerShip),
                                new GameFeatureInfo.Detail(GameFeature.Single),
                        ]),
                new GameFeatureInfo(GameFeature.Critical,
                        [
                                new GameFeatureInfo.Detail(GameFeature.CriticalEnabled),
                                new GameFeatureInfo.Detail(GameFeature.CriticalDisabled),
                        ]),
        ]
    }

    @Test
    void testCreateNewGame() {
        def P3 = createPlayerAPITarget(TEST_PLAYER3)
        TBMaskedGame game = newGame(P3,
                new FeaturesAndPlayers(
                        features: [
                                GameFeature.Grid20x20,
                                GameFeature.IsolatedIntel,
                                GameFeature.ECMEnabled,
                                GameFeature.EREnabled,
                                GameFeature.EMDisabled,
                                GameFeature.CriticalEnabled,
                                GameFeature.SpyDisabled,
                                GameFeature.PerShip
                        ] as Set,
                        players: [TEST_PLAYER2.md5, TEST_PLAYER3.md5, TEST_PLAYER1.md5],
                ))
        assert game != null
        assert game.playerStates == [
                (TEST_PLAYER1.md5): PlayerState.Pending,
                (TEST_PLAYER2.md5): PlayerState.Pending,
                (TEST_PLAYER3.md5): PlayerState.Accepted
        ]
        assert game.playersAlive == [
                (TEST_PLAYER1.md5): false,
                (TEST_PLAYER2.md5): false,
                (TEST_PLAYER3.md5): false
        ]
        assert game.playersSetup == [
                (TEST_PLAYER1.md5): false,
                (TEST_PLAYER2.md5): false,
                (TEST_PLAYER3.md5): false
        ]
        assert game.playersScore == [
                (TEST_PLAYER1.md5): 0,
                (TEST_PLAYER2.md5): 0,
                (TEST_PLAYER3.md5): 0
        ]
        assert game.maskedPlayersState.activeShipsRemaining == 0
        assert game.maskedPlayersState.opponentGrids == [
                (TEST_PLAYER1.md5): new Grid(20),
                (TEST_PLAYER2.md5): new Grid(20)
        ]
        assert game.maskedPlayersState.opponentViews == [
                (TEST_PLAYER1.md5): new Grid(20),
                (TEST_PLAYER2.md5): new Grid(20)
        ]
        assert game.maskedPlayersState.spysRemaining == 0
        assert game.maskedPlayersState.ecmsRemaining == 2
        assert game.maskedPlayersState.emergencyRepairsRemaining == 2
        assert game.maskedPlayersState.evasiveManeuversRemaining == 0
        assert game.gamePhase == GamePhase.Challenged

        //  Clear cache and force a load from db to confirm full round trip
        cacheManager.cacheNames.each {
            cacheManager.getCache(it).clear()
        }

        game = getGame(createGameTarget(P3, game))
        assert game.playersScore == [
                (TEST_PLAYER1.md5): 0,
                (TEST_PLAYER2.md5): 0,
                (TEST_PLAYER3.md5): 0
        ]
        assert game.maskedPlayersState.activeShipsRemaining == 0
        assert game.maskedPlayersState.opponentGrids == [
                (TEST_PLAYER1.md5): new Grid(20),
                (TEST_PLAYER2.md5): new Grid(20)
        ]
        assert game.maskedPlayersState.opponentViews == [
                (TEST_PLAYER1.md5): new Grid(20),
                (TEST_PLAYER2.md5): new Grid(20)
        ]
        assert game.maskedPlayersState.spysRemaining == 0
        assert game.maskedPlayersState.ecmsRemaining == 2
        assert game.maskedPlayersState.emergencyRepairsRemaining == 2
        assert game.maskedPlayersState.evasiveManeuversRemaining == 0
        assert game.gamePhase == GamePhase.Challenged
    }

    @Test
    void testCreateAndRejectNewGame() {
        def P3 = createPlayerAPITarget(TEST_PLAYER3)
        TBMaskedGame game = newGame(P3, STANDARD_PLAYERS_AND_FEATURES)
        def P1G = createGameTarget(createPlayerAPITarget(TEST_PLAYER1), game)
        assert game != null
        assert game.playerStates == [
                (TEST_PLAYER1.md5): PlayerState.Pending,
                (TEST_PLAYER2.md5): PlayerState.Pending,
                (TEST_PLAYER3.md5): PlayerState.Accepted
        ]
        assert game.gamePhase == GamePhase.Challenged

        game = rejectGame(P1G)
        assert game.playerStates == [
                (TEST_PLAYER1.md5): PlayerState.Rejected,
                (TEST_PLAYER2.md5): PlayerState.Pending,
                (TEST_PLAYER3.md5): PlayerState.Accepted
        ]
        assert game.gamePhase == GamePhase.Declined
    }

    @Test
    void testCreateAndQuitNewGame() {
        def P3 = createPlayerAPITarget(TEST_PLAYER3)
        TBMaskedGame game = newGame(P3, STANDARD_PLAYERS_AND_FEATURES)
        def P1G = createGameTarget(createPlayerAPITarget(TEST_PLAYER1), game)
        def P2G = createGameTarget(createPlayerAPITarget(TEST_PLAYER2), game)
        acceptGame(P1G)
        acceptGame(P2G)
        game = quitGame(P1G)
        assert game.playerStates == [
                (TEST_PLAYER1.md5): PlayerState.Quit,
                (TEST_PLAYER2.md5): PlayerState.Accepted,
                (TEST_PLAYER3.md5): PlayerState.Accepted
        ]
    }

    @Test
    void testSetupGame() {
        def P3 = createPlayerAPITarget(TEST_PLAYER3)
        TBMaskedGame game = newGame(P3, STANDARD_PLAYERS_AND_FEATURES)
        assert game != null
        def P3G = createGameTarget(P3, game)
        def P1G = createGameTarget(createPlayerAPITarget(TEST_PLAYER1), game)
        def P2G = createGameTarget(createPlayerAPITarget(TEST_PLAYER2), game)


        game = acceptGame(P1G)
        assert game != null
        assert game.playerStates == [
                (TEST_PLAYER1.md5): PlayerState.Accepted,
                (TEST_PLAYER2.md5): PlayerState.Pending,
                (TEST_PLAYER3.md5): PlayerState.Accepted
        ]
        assert game.gamePhase == GamePhase.Challenged

        game = acceptGame(P2G)
        assert game != null
        assert game.playerStates == [
                (TEST_PLAYER1.md5): PlayerState.Accepted,
                (TEST_PLAYER2.md5): PlayerState.Accepted,
                (TEST_PLAYER3.md5): PlayerState.Accepted
        ]
        assert game.playersSetup == [
                (TEST_PLAYER1.md5): false,
                (TEST_PLAYER2.md5): false,
                (TEST_PLAYER3.md5): false
        ]
        assert game.gamePhase == GamePhase.Setup

        game = setup(P3G, P3POSITIONS)
        assert game.playersSetup == [
                (TEST_PLAYER1.md5): false,
                (TEST_PLAYER2.md5): false,
                (TEST_PLAYER3.md5): true
        ]
        assert game.gamePhase == GamePhase.Setup

        game = setup(P1G, P1POSITIONS)
        assert game.playersSetup == [
                (TEST_PLAYER1.md5): true,
                (TEST_PLAYER2.md5): false,
                (TEST_PLAYER3.md5): true
        ]
        assert game.gamePhase == GamePhase.Setup

        game = setup(P2G, P2POSITIONS)
        assert game.playersSetup == [
                (TEST_PLAYER1.md5): true,
                (TEST_PLAYER2.md5): true,
                (TEST_PLAYER3.md5): true
        ]
        assert game.gamePhase == GamePhase.Playing
        assert 5 == game.remainingMoves
        assert [TEST_PLAYER2.md5, TEST_PLAYER1.md5, TEST_PLAYER3.md5].contains(game.currentPlayer)
    }

    @Test
    void testFireForTurnInGame() {
        def P3 = createPlayerAPITarget(TEST_PLAYER3)
        TBMaskedGame game = newGame(P3, STANDARD_PLAYERS_AND_FEATURES)
        assert game != null
        def P3G = createGameTarget(P3, game)
        def P1G = createGameTarget(createPlayerAPITarget(TEST_PLAYER1), game)
        def P2G = createGameTarget(createPlayerAPITarget(TEST_PLAYER2), game)

        acceptGame(P1G)
        acceptGame(P2G)
        setup(P3G, P3POSITIONS)
        setup(P1G, P1POSITIONS)
        setup(P2G, P2POSITIONS)

        //  Force turn to P2
        TBGame dbGame = gameRepository.findOne(new ObjectId(game.idAsString))
        dbGame.currentPlayer = TEST_PLAYER2.id
        gameRepository.save(dbGame)
        cacheManager.cacheNames.each {
            cacheManager.getCache(it).clear()
        }

        game = fire(P2G, TEST_PLAYER1, new GridCoordinate(7, 7))
        assert GamePhase.Playing == game.gamePhase
        assert "Direct hit at (7,7)!" == game.maskedPlayersState.lastActionMessage
        assert 1 == game.playersScore[TEST_PLAYER2.md5]
        assert 4 == game.remainingMoves
        assert TEST_PLAYER2.md5 == game.currentPlayer
        game = fire(P2G, TEST_PLAYER1, new GridCoordinate(7, 8))
        assert "No enemy at (7,8)." == game.maskedPlayersState.lastActionMessage
        game = fire(P2G, TEST_PLAYER1, new GridCoordinate(7, 6))
        assert "No enemy at (7,6)." == game.maskedPlayersState.lastActionMessage
        game = fire(P2G, TEST_PLAYER1, new GridCoordinate(8, 7))
        assert "Direct hit at (8,7)!" == game.maskedPlayersState.lastActionMessage
        assert 1 == game.remainingMoves
        game = fire(P2G, TEST_PLAYER1, new GridCoordinate(9, 7))
        assert "Direct hit at (9,7)!" == game.maskedPlayersState.lastActionMessage
        assert 3 == game.playersScore[TEST_PLAYER2.md5]
        assert 5 == game.remainingMoves
        assert TEST_PLAYER2.md5 != game.currentPlayer
    }

    @Test
    void testSpyForTurnInGame() {
        def P3 = createPlayerAPITarget(TEST_PLAYER3)
        TBMaskedGame game = newGame(P3, STANDARD_PLAYERS_AND_FEATURES)
        assert game != null
        def P3G = createGameTarget(P3, game)
        def P1G = createGameTarget(createPlayerAPITarget(TEST_PLAYER1), game)
        def P2G = createGameTarget(createPlayerAPITarget(TEST_PLAYER2), game)

        acceptGame(P1G)
        acceptGame(P2G)
        setup(P3G, P3POSITIONS)
        setup(P1G, P1POSITIONS)
        setup(P2G, P2POSITIONS)

        //  Force turn to P2
        TBGame dbGame = gameRepository.findOne(new ObjectId(game.idAsString))
        dbGame.currentPlayer = TEST_PLAYER2.id
        gameRepository.save(dbGame)
        cacheManager.cacheNames.each {
            cacheManager.getCache(it).clear()
        }

        game = fire(P2G, TEST_PLAYER1, new GridCoordinate(7, 7))

        game = spy(P2G, TEST_PLAYER1, new GridCoordinate(7, 8))
        assert GamePhase.Playing == game.gamePhase
        assert "TEST PLAYER2 spied on TEST PLAYER1 at (7,8)." == game.maskedPlayersState.lastActionMessage
        assert 2 == game.remainingMoves
        assert 1 == game.maskedPlayersState.spysRemaining
        assert TEST_PLAYER2.md5 == game.currentPlayer
        game = spy(P2G, TEST_PLAYER1, new GridCoordinate(2, 6))
        assert "TEST PLAYER2 spied on TEST PLAYER1 at (2,6)." == game.maskedPlayersState.lastActionMessage
        assert 1 == game.playersScore[TEST_PLAYER2.md5]
        assert 0 == game.maskedPlayersState.spysRemaining
        assert TEST_PLAYER2.md5 != game.currentPlayer

        //Sample checks, not full
        assert GridCellState.KnownByHit == game.maskedPlayersState.opponentGrids[TEST_PLAYER1.md5].get(7, 7)
        assert GridCellState.KnownShip == game.maskedPlayersState.opponentGrids[TEST_PLAYER1.md5].get(8, 7)
        assert GridCellState.KnownShip == game.maskedPlayersState.opponentGrids[TEST_PLAYER1.md5].get(0, 6)
        assert GridCellState.KnownEmpty == game.maskedPlayersState.opponentGrids[TEST_PLAYER1.md5].get(7, 8)
        game = getGame(P1G)
        assert GridCellState.KnownByHit == game.maskedPlayersState.opponentViews[TEST_PLAYER2.md5].get(7, 7)
        assert GridCellState.KnownShip == game.maskedPlayersState.opponentViews[TEST_PLAYER2.md5].get(8, 7)
        assert GridCellState.KnownShip == game.maskedPlayersState.opponentViews[TEST_PLAYER2.md5].get(0, 6)
        assert GridCellState.KnownEmpty == game.maskedPlayersState.opponentViews[TEST_PLAYER2.md5].get(7, 8)
        assert GridCellState.KnownByHit == game.maskedPlayersState.consolidatedOpponentView.get(7, 7)
        assert GridCellState.KnownShip == game.maskedPlayersState.consolidatedOpponentView.get(8, 7)
        assert GridCellState.KnownShip == game.maskedPlayersState.consolidatedOpponentView.get(0, 6)
        assert GridCellState.KnownEmpty == game.maskedPlayersState.consolidatedOpponentView.get(7, 8)
    }

    @Test
    void testRepairForTurnInGame() {
        def P3 = createPlayerAPITarget(TEST_PLAYER3)
        TBMaskedGame game = newGame(P3, STANDARD_PLAYERS_AND_FEATURES)
        assert game != null
        def P3G = createGameTarget(P3, game)
        def P1G = createGameTarget(createPlayerAPITarget(TEST_PLAYER1), game)
        def P2G = createGameTarget(createPlayerAPITarget(TEST_PLAYER2), game)

        acceptGame(P1G)
        acceptGame(P2G)
        setup(P3G, P3POSITIONS)
        setup(P1G, P1POSITIONS)
        setup(P2G, P2POSITIONS)

        //  Force turn to P2
        TBGame dbGame = gameRepository.findOne(new ObjectId(game.idAsString))
        dbGame.currentPlayer = TEST_PLAYER2.id
        gameRepository.save(dbGame)
        cacheManager.cacheNames.each {
            cacheManager.getCache(it).clear()
        }

        game = fire(P2G, TEST_PLAYER1, new GridCoordinate(7, 7))
        game = fire(P2G, TEST_PLAYER1, new GridCoordinate(7, 0))

        //  Force turn to P1
        dbGame = gameRepository.findOne(new ObjectId(game.idAsString))
        dbGame.currentPlayer = TEST_PLAYER1.id
        dbGame.remainingMoves = 5
        gameRepository.save(dbGame)
        cacheManager.cacheNames.each {
            cacheManager.getCache(it).clear()
        }

        game = fire(P1G, TEST_PLAYER2, new GridCoordinate(7, 0))
        game = repair(P1G, TEST_PLAYER1, new GridCoordinate(8, 7))
        assert 2 == game.remainingMoves
        assert "TEST PLAYER1 repaired their Aircraft Carrier."
        assert 5 == game.maskedPlayersState.shipStates[Ship.Carrier].healthRemaining
        assert [false, false, false, false, false] == game.maskedPlayersState.shipStates[Ship.Carrier].shipSegmentHit
        assert GridCellState.KnownShip == game.maskedPlayersState.consolidatedOpponentView.get(7, 7)
        assert 1 == game.maskedPlayersState.emergencyRepairsRemaining
        assert 2 == game.remainingMoves


        game = repair(P1G, TEST_PLAYER1, new GridCoordinate(8, 0))
        assert "TEST PLAYER1 repaired their Cruiser."
        assert 3 == game.maskedPlayersState.shipStates[Ship.Cruiser].healthRemaining
        assert [false, false, false] == game.maskedPlayersState.shipStates[Ship.Cruiser].shipSegmentHit
        assert GridCellState.KnownShip == game.maskedPlayersState.consolidatedOpponentView.get(7, 0)
        assert GamePhase.Playing == game.gamePhase
        assert 0 == game.playersScore[TEST_PLAYER1.md5]
        assert 0 == game.maskedPlayersState.emergencyRepairsRemaining
        assert TEST_PLAYER1.md5 != game.currentPlayer
    }

    @Test
    void testCreatingInvalidGames() {
        def P3 = createConnection(TEST_PLAYER3).target(PLAYER_API)
        def entity = Entity.entity(
                new FeaturesAndPlayers(
                        features: [
                                GameFeature.Grid20x20,
                                GameFeature.IsolatedIntel,
                                GameFeature.ECMEnabled,
                                GameFeature.SpyDisabled,
                                GameFeature.PerShip
                        ] as Set,
                        players: [TEST_PLAYER2.md5, TEST_PLAYER3.md5, TEST_PLAYER1.md5]
                ),
                MediaType.APPLICATION_JSON)


        Response response = P3.path("new")
                .request(MediaType.APPLICATION_JSON)
                .post(entity)
        assert response != null
        assert response.statusInfo.statusCode == 400

        entity = Entity.entity(
                new FeaturesAndPlayers(
                        features: [
                                GameFeature.Grid20x20,
                                GameFeature.IsolatedIntel,
                                GameFeature.ECMEnabled,
                                GameFeature.ECMDisabled,  // Double
                                GameFeature.EREnabled,
                                GameFeature.EMDisabled,
                                GameFeature.CriticalEnabled,
                                GameFeature.SpyDisabled,
                                GameFeature.PerShip
                        ] as Set,
                        players: [TEST_PLAYER2.md5, TEST_PLAYER3.md5, TEST_PLAYER1.md5]
                ),
                MediaType.APPLICATION_JSON)


        response = P3.path("new")
                .request(MediaType.APPLICATION_JSON)
                .post(entity)
        assert response != null
        assert response.statusInfo.statusCode == 400
    }

    protected TBMaskedGame newGame(WebTarget target, FeaturesAndPlayers featuresAndPlayers) {
        def entity = Entity.entity(
                featuresAndPlayers,
                MediaType.APPLICATION_JSON)
        target.path("new")
                .request(MediaType.APPLICATION_JSON)
                .post(entity, returnedGameClass())
    }

    protected TBMaskedGame setup(WebTarget target, LinkedHashMap<Ship, ArrayList<GridCoordinate>> positions) {
        def placement = Entity.entity(
                positions,
                MediaType.APPLICATION_JSON
        )
        target.path("setup").request(MediaType.APPLICATION_JSON).put(placement, returnedGameClass())
    }

    protected TBMaskedGame fire(WebTarget target, Player player, GridCoordinate coordinate) {
        makeMove(player, coordinate, target, "fire")
    }

    protected TBMaskedGame spy(WebTarget target, Player player, GridCoordinate coordinate) {
        makeMove(player, coordinate, target, "spy")
    }

    protected TBMaskedGame repair(WebTarget target, Player player, GridCoordinate coordinate) {
        makeMove(player, coordinate, target, "repair")
    }

    protected TBMaskedGame makeMove(Player player, GridCoordinate coordinate, WebTarget target, String command) {
        def placement = Entity.entity(
                new Target(player: player.md5, coordinate: coordinate),
                MediaType.APPLICATION_JSON
        )
        target.path(command).request(MediaType.APPLICATION_JSON).put(placement, returnedGameClass())
    }

    private static final LinkedHashMap<Ship, ArrayList<GridCoordinate>> P1POSITIONS = [
            (Ship.Carrier)   : [
                    new GridCoordinate(5, 7),
                    new GridCoordinate(6, 7),
                    new GridCoordinate(7, 7),
                    new GridCoordinate(8, 7),
                    new GridCoordinate(9, 7),
            ],
            (Ship.Battleship): [
                    new GridCoordinate(0, 6),
                    new GridCoordinate(0, 7),
                    new GridCoordinate(0, 8),
                    new GridCoordinate(0, 9),
            ],
            (Ship.Cruiser)   : [
                    new GridCoordinate(7, 0),
                    new GridCoordinate(8, 0),
                    new GridCoordinate(9, 0),
            ],
            (Ship.Submarine) : [
                    new GridCoordinate(7, 14),
                    new GridCoordinate(8, 14),
                    new GridCoordinate(9, 14),
            ],
            (Ship.Destroyer) : [
                    new GridCoordinate(14, 7),
                    new GridCoordinate(14, 8),
            ],
    ]

    private static final LinkedHashMap<Ship, ArrayList<GridCoordinate>> P2POSITIONS = [
            (Ship.Carrier)   : [
                    new GridCoordinate(0, 0),
                    new GridCoordinate(0, 1),
                    new GridCoordinate(0, 2),
                    new GridCoordinate(0, 3),
                    new GridCoordinate(0, 4),
            ],
            (Ship.Battleship): [
                    new GridCoordinate(14, 0),
                    new GridCoordinate(14, 1),
                    new GridCoordinate(14, 2),
                    new GridCoordinate(14, 3),
            ],
            (Ship.Cruiser)   : [
                    new GridCoordinate(0, 14),
                    new GridCoordinate(0, 13),
                    new GridCoordinate(0, 12),
            ],
            (Ship.Submarine) : [
                    new GridCoordinate(14, 14),
                    new GridCoordinate(14, 13),
                    new GridCoordinate(14, 12),
            ],
            (Ship.Destroyer) : [
                    new GridCoordinate(7, 8),
                    new GridCoordinate(7, 9),
            ],
    ]

    private static final LinkedHashMap<Ship, ArrayList<GridCoordinate>> P3POSITIONS = [
            (Ship.Carrier)   : [
                    new GridCoordinate(0, 0),
                    new GridCoordinate(0, 1),
                    new GridCoordinate(0, 2),
                    new GridCoordinate(0, 3),
                    new GridCoordinate(0, 4),
            ],
            (Ship.Battleship): [
                    new GridCoordinate(1, 0),
                    new GridCoordinate(1, 1),
                    new GridCoordinate(1, 2),
                    new GridCoordinate(1, 3),
            ],
            (Ship.Cruiser)   : [
                    new GridCoordinate(2, 1),
                    new GridCoordinate(2, 2),
                    new GridCoordinate(2, 3),
            ],
            (Ship.Submarine) : [
                    new GridCoordinate(3, 4),
                    new GridCoordinate(3, 2),
                    new GridCoordinate(3, 3),
            ],
            (Ship.Destroyer) : [
                    new GridCoordinate(0, 14),
                    new GridCoordinate(1, 14),
            ],
    ]

    public static final FeaturesAndPlayers STANDARD_PLAYERS_AND_FEATURES = new FeaturesAndPlayers(
            features: [
                    GameFeature.Grid15x15,
                    GameFeature.SharedIntel,
                    GameFeature.ECMEnabled,
                    GameFeature.EREnabled,
                    GameFeature.EMEnabled,
                    GameFeature.CriticalEnabled,
                    GameFeature.SpyEnabled,
                    GameFeature.PerShip
            ] as Set,
            players: [TEST_PLAYER2.md5, TEST_PLAYER3.md5, TEST_PLAYER1.md5]
    )

    @Override
    Class<TBMaskedGame> returnedGameClass() {
        return TBMaskedGame.class
    }
}
