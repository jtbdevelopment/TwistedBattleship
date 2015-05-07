package com.jtbdevelopment.TwistedBattleship

import com.jtbdevelopment.TwistedBattleship.dao.GameRepository
import com.jtbdevelopment.TwistedBattleship.rest.GameFeatureInfo
import com.jtbdevelopment.TwistedBattleship.rest.services.messages.FeaturesAndPlayers
import com.jtbdevelopment.TwistedBattleship.state.GameFeature
import com.jtbdevelopment.TwistedBattleship.state.TBGame
import com.jtbdevelopment.TwistedBattleship.state.grid.Grid
import com.jtbdevelopment.TwistedBattleship.state.grid.GridCoordinate
import com.jtbdevelopment.TwistedBattleship.state.masked.TBMaskedGame
import com.jtbdevelopment.TwistedBattleship.state.ships.Ship
import com.jtbdevelopment.core.hazelcast.caching.HazelcastCacheManager
import com.jtbdevelopment.games.dev.utilities.integrationtesting.AbstractGameIntegration
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

    @BeforeClass
    static void setup() {
        cacheManager = context.getBean(HazelcastCacheManager.class)
    }

    @Test
    void testGetFeatures() {
        def client = createConnection(TEST_PLAYER2)
        def features = client
                .target(API_URI)
                .path("features")
                .request(MediaType.APPLICATION_JSON_TYPE)
                .get(new GenericType<List<GameFeatureInfo>>() {
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
        def P3 = createAPITarget(TEST_PLAYER3)
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
        assert game.maskedPlayersState.ecmsRemaining == 3
        assert game.maskedPlayersState.emergencyRepairsRemaining == 3
        assert game.maskedPlayersState.evasiveManeuversRemaining == 0
        assert game.gamePhase == GamePhase.Challenged

        //  Clear cache and force a load from db to confirm loads
        cacheManager.cacheNames.each {
            cacheManager.getCache(it).clear()
        }

        TBGame dbGame = context.getBean(GameRepository.class).findOne(new ObjectId(game.id))
        assert dbGame != null
    }

    @Test
    void testCreateAndRejectNewGame() {
        def P3 = createAPITarget(TEST_PLAYER3)
        TBMaskedGame game = newGame(P3, STANDARD_PLAYERS_AND_FEATURES)
        def P1G = createGameTarget(createAPITarget(TEST_PLAYER1), game)
        assert game != null
        assert game.playerStates == [
                (TEST_PLAYER1.md5): PlayerState.Pending,
                (TEST_PLAYER2.md5): PlayerState.Pending,
                (TEST_PLAYER3.md5): PlayerState.Accepted
        ]
        assert game.gamePhase == GamePhase.Challenged

        game = reject(P1G)
        assert game.playerStates == [
                (TEST_PLAYER1.md5): PlayerState.Rejected,
                (TEST_PLAYER2.md5): PlayerState.Pending,
                (TEST_PLAYER3.md5): PlayerState.Accepted
        ]
        assert game.gamePhase == GamePhase.Declined
    }

    @Test
    void testCreateAndQuitNewGame() {
        def P3 = createAPITarget(TEST_PLAYER3)
        TBMaskedGame game = newGame(P3, STANDARD_PLAYERS_AND_FEATURES)
        def P1G = createGameTarget(createAPITarget(TEST_PLAYER1), game)
        def P2G = createGameTarget(createAPITarget(TEST_PLAYER2), game)
        accept(P1G)
        accept(P2G)
        game = quit(P1G)
        assert game.playerStates == [
                (TEST_PLAYER1.md5): PlayerState.Quit,
                (TEST_PLAYER2.md5): PlayerState.Accepted,
                (TEST_PLAYER3.md5): PlayerState.Accepted
        ]
    }

    @Test
    void testSetupGame() {
        def P3 = createAPITarget(TEST_PLAYER3)
        TBMaskedGame game = newGame(P3, STANDARD_PLAYERS_AND_FEATURES)
        assert game != null
        def P3G = createGameTarget(P3, game)
        def P1G = createGameTarget(createAPITarget(TEST_PLAYER1), game)
        def P2G = createGameTarget(createAPITarget(TEST_PLAYER2), game)


        game = accept(P1G)
        assert game != null
        assert game.playerStates == [
                (TEST_PLAYER1.md5): PlayerState.Accepted,
                (TEST_PLAYER2.md5): PlayerState.Pending,
                (TEST_PLAYER3.md5): PlayerState.Accepted
        ]
        assert game.gamePhase == GamePhase.Challenged

        game = accept(P2G)
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
        TBMaskedGame game
        def entity = Entity.entity(
                featuresAndPlayers,
                MediaType.APPLICATION_JSON)
        game = target.path("new")
                .request(MediaType.APPLICATION_JSON)
                .post(entity, TBMaskedGame.class)
        game
    }

    protected TBMaskedGame setup(WebTarget target, LinkedHashMap<Ship, ArrayList<GridCoordinate>> positions) {
        TBMaskedGame game
        def placement = Entity.entity(
                positions,
                MediaType.APPLICATION_JSON
        )
        game = target.path("setup").request(MediaType.APPLICATION_JSON).put(placement, TBMaskedGame.class)
        game
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
