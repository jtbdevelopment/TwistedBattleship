package com.jtbdevelopment.TwistedBattleship

import com.jtbdevelopment.TwistedBattleship.dao.GameRepository
import com.jtbdevelopment.TwistedBattleship.player.TBPlayerAttributes
import com.jtbdevelopment.TwistedBattleship.rest.GameFeatureInfo
import com.jtbdevelopment.TwistedBattleship.rest.ShipInfo
import com.jtbdevelopment.TwistedBattleship.rest.Target
import com.jtbdevelopment.TwistedBattleship.rest.services.messages.FeaturesAndPlayers
import com.jtbdevelopment.TwistedBattleship.rest.services.messages.ShipAndCoordinates
import com.jtbdevelopment.TwistedBattleship.state.GameFeature
import com.jtbdevelopment.TwistedBattleship.state.TBActionLogEntry
import com.jtbdevelopment.TwistedBattleship.state.TBGame
import com.jtbdevelopment.TwistedBattleship.state.grid.Grid
import com.jtbdevelopment.TwistedBattleship.state.grid.GridCellState
import com.jtbdevelopment.TwistedBattleship.state.grid.GridCircleUtil
import com.jtbdevelopment.TwistedBattleship.state.grid.GridCoordinate
import com.jtbdevelopment.TwistedBattleship.state.masked.TBMaskedGame
import com.jtbdevelopment.TwistedBattleship.state.ships.Ship
import com.jtbdevelopment.core.hazelcast.caching.HazelcastCacheManager
import com.jtbdevelopment.games.dev.utilities.integrationtesting.AbstractGameIntegration
import com.jtbdevelopment.games.mongo.players.MongoManualPlayer
import com.jtbdevelopment.games.mongo.players.MongoPlayer
import com.jtbdevelopment.games.players.Player
import com.jtbdevelopment.games.state.GamePhase
import com.jtbdevelopment.games.state.PlayerState
import org.bson.types.ObjectId
import org.junit.BeforeClass
import org.junit.Test

import javax.ws.rs.client.Client
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
    void testPlayerTheme() {
        ((TBPlayerAttributes) TEST_PLAYER2.gameSpecificPlayerAttributes).availableThemes = ['default', 'new-theme']
        playerRepository.save(TEST_PLAYER2)
        Client client = createConnection(TEST_PLAYER2)
        def p = client.target(PLAYER_API).request(MediaType.APPLICATION_JSON).get(MongoManualPlayer.class);
        assert 'default-theme' == ((TBPlayerAttributes) p.gameSpecificPlayerAttributes).theme
        MongoPlayer updated = client.target(PLAYER_API).path('changeTheme').path('new-theme').request(MediaType.APPLICATION_JSON).put(EMPTY_PUT_POST, MongoManualPlayer.class)
        assert 'new-theme' == ((TBPlayerAttributes) updated.gameSpecificPlayerAttributes).theme
    }

    @Test
    void testGetCircleSizes() {
        def client = createAPITarget(TEST_PLAYER2)
        def sizes = client.path("circles").request(MediaType.APPLICATION_JSON_TYPE).get(
                new GenericType<Map<Integer, Set<GridCoordinate>>>() {
                })
        assert GridCircleUtil.CIRCLE_OFFSETS == sizes
    }

    @Test
    void testGetCellStates() {
        def client = createAPITarget(TEST_PLAYER2)
        def sizes = client.path("states").request(MediaType.APPLICATION_JSON_TYPE).get(
                new GenericType<List<GridCellState>>() {
                })
        assert GridCellState.values().toList() == sizes
    }

    @Test
    void testGetShips() {
        def client = createAPITarget(TEST_PLAYER3)
        def ships = client.path("ships").request(MediaType.APPLICATION_JSON_TYPE).get(
                new GenericType<List<ShipInfo>>() {
                })
        assert Ship.values().collect { Ship it -> new ShipInfo(it) } == ships
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
                new GameFeatureInfo(GameFeature.ActionsPerTurn,
                        [
                                new GameFeatureInfo.Detail(GameFeature.PerShip),
                                new GameFeatureInfo.Detail(GameFeature.Single),
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
                new GameFeatureInfo(GameFeature.Spy,
                        [
                                new GameFeatureInfo.Detail(GameFeature.SpyEnabled),
                                new GameFeatureInfo.Detail(GameFeature.SpyDisabled),
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
                                GameFeature.SpyDisabled,
                                GameFeature.PerShip
                        ] as Set,
                        players: [TEST_PLAYER2.md5, TEST_PLAYER3.md5, TEST_PLAYER1.md5],
                ))
        assert game
        assert 20 == game.gridSize
        assert 2 == game.movesForSpecials
        assert [
                (TEST_PLAYER1.md5): PlayerState.Pending,
                (TEST_PLAYER2.md5): PlayerState.Pending,
                (TEST_PLAYER3.md5): PlayerState.Accepted
        ] == game.playerStates
        assert [
                (TEST_PLAYER1.md5): false,
                (TEST_PLAYER2.md5): false,
                (TEST_PLAYER3.md5): false
        ] == game.playersAlive
        assert [
                (TEST_PLAYER1.md5): false,
                (TEST_PLAYER2.md5): false,
                (TEST_PLAYER3.md5): false
        ] == game.playersSetup
        assert [
                (TEST_PLAYER1.md5): 0,
                (TEST_PLAYER2.md5): 0,
                (TEST_PLAYER3.md5): 0
        ] == game.playersScore
        assert 0 == game.maskedPlayersState.activeShipsRemaining
        assert [
                (TEST_PLAYER1.md5): new Grid(20),
                (TEST_PLAYER2.md5): new Grid(20)
        ] == game.maskedPlayersState.opponentGrids
        assert [
                (TEST_PLAYER1.md5): new Grid(20),
                (TEST_PLAYER2.md5): new Grid(20)
        ] == game.maskedPlayersState.opponentViews
        assert 0 == game.maskedPlayersState.spysRemaining
        assert 2 == game.maskedPlayersState.ecmsRemaining
        assert 2 == game.maskedPlayersState.emergencyRepairsRemaining
        assert 0 == game.maskedPlayersState.evasiveManeuversRemaining
        assert GamePhase.Challenged == game.gamePhase

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
        assert game
        assert [
                (TEST_PLAYER1.md5): PlayerState.Pending,
                (TEST_PLAYER2.md5): PlayerState.Pending,
                (TEST_PLAYER3.md5): PlayerState.Accepted
        ] == game.playerStates
        assert GamePhase.Challenged == game.gamePhase

        game = rejectGame(P1G)
        assert [
                (TEST_PLAYER1.md5): PlayerState.Rejected,
                (TEST_PLAYER2.md5): PlayerState.Pending,
                (TEST_PLAYER3.md5): PlayerState.Accepted
        ] == game.playerStates
        assert GamePhase.Declined == game.gamePhase
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
        assert [
                (TEST_PLAYER1.md5): PlayerState.Quit,
                (TEST_PLAYER2.md5): PlayerState.Accepted,
                (TEST_PLAYER3.md5): PlayerState.Accepted
        ] == game.playerStates
    }

    @Test
    void testSetupGame() {
        def P3 = createPlayerAPITarget(TEST_PLAYER3)
        TBMaskedGame game = newGame(P3, STANDARD_PLAYERS_AND_FEATURES)
        assert game
        def P3G = createGameTarget(P3, game)
        def P1G = createGameTarget(createPlayerAPITarget(TEST_PLAYER1), game)
        def P2G = createGameTarget(createPlayerAPITarget(TEST_PLAYER2), game)


        game = acceptGame(P1G)
        assert game
        assert [
                (TEST_PLAYER1.md5): PlayerState.Accepted,
                (TEST_PLAYER2.md5): PlayerState.Pending,
                (TEST_PLAYER3.md5): PlayerState.Accepted
        ] == game.playerStates
        assert GamePhase.Challenged == game.gamePhase

        game = acceptGame(P2G)
        assert game
        assert [
                (TEST_PLAYER1.md5): PlayerState.Accepted,
                (TEST_PLAYER2.md5): PlayerState.Accepted,
                (TEST_PLAYER3.md5): PlayerState.Accepted
        ] == game.playerStates
        assert [
                (TEST_PLAYER1.md5): false,
                (TEST_PLAYER2.md5): false,
                (TEST_PLAYER3.md5): false
        ] == game.playersSetup
        assert GamePhase.Setup == game.gamePhase

        game = setup(P3G, P3POSITIONS)
        assert [
                (TEST_PLAYER1.md5): false,
                (TEST_PLAYER2.md5): false,
                (TEST_PLAYER3.md5): true
        ] == game.playersSetup
        assert GamePhase.Setup == game.gamePhase

        game = setup(P1G, P1POSITIONS)
        assert [
                (TEST_PLAYER1.md5): true,
                (TEST_PLAYER2.md5): false,
                (TEST_PLAYER3.md5): true
        ] == game.playersSetup
        assert GamePhase.Setup == game.gamePhase

        game = setup(P2G, P2POSITIONS)
        assert [
                (TEST_PLAYER1.md5): true,
                (TEST_PLAYER2.md5): true,
                (TEST_PLAYER3.md5): true
        ] == game.playersSetup
        assert GamePhase.Playing == game.gamePhase
        assert 5 == game.remainingMoves
        assert [TEST_PLAYER2.md5, TEST_PLAYER1.md5, TEST_PLAYER3.md5].contains(game.currentPlayer)
        assert TBActionLogEntry.TBActionType.Begin == game.maskedPlayersState.actionLog[0].actionType
        assert "Game ready to play." == game.maskedPlayersState.actionLog[0].description
        assert 0 != game.maskedPlayersState.actionLog[0].timestamp
    }

    @Test
    void testFireForTurnInGame() {
        def P3 = createPlayerAPITarget(TEST_PLAYER3)
        TBMaskedGame game = newGame(P3, STANDARD_PLAYERS_AND_FEATURES)
        assert game
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
        assert "You fired at TEST PLAYER1 (7,7) and hit!" == game.maskedPlayersState.actionLog[-1].description
        assert TBActionLogEntry.TBActionType.Fired == game.maskedPlayersState.actionLog[-1].actionType
        assert 1 == game.playersScore[TEST_PLAYER2.md5]
        assert 4 == game.remainingMoves
        assert TEST_PLAYER2.md5 == game.currentPlayer
        game = fire(P2G, TEST_PLAYER1, new GridCoordinate(7, 8))
        assert "You fired at TEST PLAYER1 (7,8) and missed." == game.maskedPlayersState.actionLog[-1].description
        game = fire(P2G, TEST_PLAYER1, new GridCoordinate(7, 6))
        assert "You fired at TEST PLAYER1 (7,6) and missed." == game.maskedPlayersState.actionLog[-1].description
        game = fire(P2G, TEST_PLAYER1, new GridCoordinate(8, 7))
        assert "You fired at TEST PLAYER1 (8,7) and hit!" == game.maskedPlayersState.actionLog[-1].description
        assert 1 == game.remainingMoves
        game = fire(P2G, TEST_PLAYER1, new GridCoordinate(9, 7))
        assert "You fired at TEST PLAYER1 (9,7) and hit!" == game.maskedPlayersState.actionLog[-1].description
        assert 3 == game.playersScore[TEST_PLAYER2.md5]
        assert 5 == game.remainingMoves
        assert TEST_PLAYER2.md5 != game.currentPlayer
    }

    @Test
    void testCruiseMissileForTurnInGame() {
        def P3 = createPlayerAPITarget(TEST_PLAYER3)
        TBMaskedGame game = newGame(P3, STANDARD_PLAYERS_AND_FEATURES)
        assert game
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

        fire(P2G, TEST_PLAYER1, new GridCoordinate(7, 7))
        game = cruiseMissile(P2G, TEST_PLAYER1, new GridCoordinate(7, 7))
        assert GamePhase.Playing == game.gamePhase
        assert "You fired a cruise missile at TEST PLAYER1 (7,7) and hit!" == game.maskedPlayersState.actionLog[-7].description
        assert "You fired at TEST PLAYER1 (5,7) and hit!" == game.maskedPlayersState.actionLog[-6].description
        assert "You fired at TEST PLAYER1 (6,7) and hit!" == game.maskedPlayersState.actionLog[-5].description
        assert "You fired at TEST PLAYER1 (7,7) and hit an already damaged area!" == game.maskedPlayersState.actionLog[-4].description
        assert "You fired at TEST PLAYER1 (8,7) and hit!" == game.maskedPlayersState.actionLog[-3].description
        assert "You fired at TEST PLAYER1 (9,7) and hit!" == game.maskedPlayersState.actionLog[-2].description
        assert "You sunk a Aircraft Carrier for TEST PLAYER1!" == game.maskedPlayersState.actionLog[-1].description
        assert TBActionLogEntry.TBActionType.CruiseMissile == game.maskedPlayersState.actionLog[-7].actionType
        assert 10 == game.playersScore[TEST_PLAYER2.md5]
        assert 2 == game.remainingMoves
        assert 0 == game.maskedPlayersState.cruiseMissilesRemaining
        assert TEST_PLAYER2.md5 == game.currentPlayer
        game = fire(P2G, TEST_PLAYER1, new GridCoordinate(7, 8))
        assert "You fired at TEST PLAYER1 (7,8) and missed." == game.maskedPlayersState.actionLog[-1].description
        assert 1 == game.remainingMoves
        game = fire(P2G, TEST_PLAYER1, new GridCoordinate(9, 7))
        assert "You fired at TEST PLAYER1 (9,7) and hit an already damaged area!" == game.maskedPlayersState.actionLog[-1].description
        assert 10 == game.playersScore[TEST_PLAYER2.md5]
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

        fire(P2G, TEST_PLAYER1, new GridCoordinate(7, 7))

        game = spy(P2G, TEST_PLAYER1, new GridCoordinate(7, 8))
        assert GamePhase.Playing == game.gamePhase
        assert "You spied on TEST PLAYER1 at (7,8)." == game.maskedPlayersState.actionLog[-1].description
        assert 2 == game.remainingMoves
        assert 1 == game.maskedPlayersState.spysRemaining
        assert TEST_PLAYER2.md5 == game.currentPlayer
        game = spy(P2G, TEST_PLAYER1, new GridCoordinate(2, 6))
        assert "You spied on TEST PLAYER1 at (2,6)." == game.maskedPlayersState.actionLog[-1].description
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

        fire(P2G, TEST_PLAYER1, new GridCoordinate(7, 7))
        game = fire(P2G, TEST_PLAYER1, new GridCoordinate(7, 0))

        //  Force turn to P1
        dbGame = gameRepository.findOne(new ObjectId(game.idAsString))
        dbGame.currentPlayer = TEST_PLAYER1.id
        dbGame.remainingMoves = 5
        gameRepository.save(dbGame)
        cacheManager.cacheNames.each {
            cacheManager.getCache(it).clear()
        }

        fire(P1G, TEST_PLAYER2, new GridCoordinate(7, 0))
        game = repair(P1G, TEST_PLAYER1, new GridCoordinate(8, 7))
        assert 2 == game.remainingMoves
        assert "TEST PLAYER1 repaired their Aircraft Carrier." == game.maskedPlayersState.actionLog[-1].description
        assert 5 == game.maskedPlayersState.shipStates.find { it.ship == Ship.Carrier }.healthRemaining
        assert [false, false, false, false, false] == game.maskedPlayersState.shipStates.find {
            it.ship == Ship.Carrier
        }.shipSegmentHit
        assert GridCellState.KnownShip == game.maskedPlayersState.consolidatedOpponentView.get(7, 7)
        assert 1 == game.maskedPlayersState.emergencyRepairsRemaining
        assert 2 == game.remainingMoves


        game = repair(P1G, TEST_PLAYER1, new GridCoordinate(8, 0))
        assert "TEST PLAYER1 repaired their Cruiser." == game.maskedPlayersState.actionLog[-1].description
        assert 3 == game.maskedPlayersState.shipStates.find { it.ship == Ship.Cruiser }.healthRemaining
        assert [false, false, false] == game.maskedPlayersState.shipStates.find {
            it.ship == Ship.Cruiser
        }.shipSegmentHit
        assert GridCellState.KnownShip == game.maskedPlayersState.consolidatedOpponentView.get(7, 0)
        assert GamePhase.Playing == game.gamePhase
        assert 0 == game.playersScore[TEST_PLAYER1.md5]
        assert 0 == game.maskedPlayersState.emergencyRepairsRemaining
        assert TEST_PLAYER1.md5 != game.currentPlayer
    }

    @Test
    void testECMForTurnInGame() {
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

        fire(P2G, TEST_PLAYER1, new GridCoordinate(7, 7))
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
        assert GridCellState.KnownByHit == game.maskedPlayersState.opponentViews[TEST_PLAYER2.md5].get(7, 7)
        assert GridCellState.KnownByHit == game.maskedPlayersState.opponentViews[TEST_PLAYER2.md5].get(7, 0)
        game = ecm(P1G, TEST_PLAYER1, new GridCoordinate(8, 7))
        assert 2 == game.remainingMoves
        assert "TEST PLAYER1 deployed an ECM." == game.maskedPlayersState.actionLog[-1].description
        assert 1 == game.maskedPlayersState.ecmsRemaining
        assert GridCellState.HiddenHit == game.maskedPlayersState.opponentViews[TEST_PLAYER2.md5].get(7, 7)
        assert GridCellState.KnownByHit == game.maskedPlayersState.opponentViews[TEST_PLAYER2.md5].get(7, 0)


        game = ecm(P1G, TEST_PLAYER1, new GridCoordinate(6, 0))
        assert "TEST PLAYER1 deployed an ECM." == game.maskedPlayersState.actionLog[-1].description
        assert GamePhase.Playing == game.gamePhase
        assert GridCellState.HiddenHit == game.maskedPlayersState.opponentViews[TEST_PLAYER2.md5].get(7, 7)
        assert GridCellState.HiddenHit == game.maskedPlayersState.opponentViews[TEST_PLAYER2.md5].get(7, 0)
        assert 0 == game.playersScore[TEST_PLAYER1.md5]
        assert 0 == game.maskedPlayersState.ecmsRemaining
        assert TEST_PLAYER1.md5 != game.currentPlayer
    }

    //  TODO - Unfortunately, for the possibility of it remaining in place this test can fail randomly
    @Test
    void testMoveForTurnInGame() {
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

        fire(P2G, TEST_PLAYER1, new GridCoordinate(7, 7))
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
        assert GridCellState.KnownByHit == game.maskedPlayersState.opponentViews[TEST_PLAYER2.md5].get(7, 7)
        assert GridCellState.KnownByHit == game.maskedPlayersState.opponentViews[TEST_PLAYER2.md5].get(7, 0)
        assert [new GridCoordinate(5, 7),
                new GridCoordinate(6, 7),
                new GridCoordinate(7, 7),
                new GridCoordinate(8, 7),
                new GridCoordinate(9, 7),] == game.maskedPlayersState.shipStates.find {
            it.ship == Ship.Carrier
        }.shipGridCells
        game = move(P1G, TEST_PLAYER1, new GridCoordinate(8, 7))
        assert 2 == game.remainingMoves
        assert "TEST PLAYER1 performed evasive maneuvers." == game.maskedPlayersState.actionLog[-1].description
        assert 1 == game.maskedPlayersState.evasiveManeuversRemaining
        assert GridCellState.ObscuredHit == game.maskedPlayersState.opponentViews[TEST_PLAYER2.md5].get(7, 7)
        assert GridCellState.KnownByHit == game.maskedPlayersState.opponentViews[TEST_PLAYER2.md5].get(7, 0)
        assert [new GridCoordinate(5, 7),
                new GridCoordinate(6, 7),
                new GridCoordinate(7, 7),
                new GridCoordinate(8, 7),
                new GridCoordinate(9, 7),] != game.maskedPlayersState.shipStates.find {
            it.ship == Ship.Carrier
        }.shipGridCells


        assert [new GridCoordinate(7, 0),
                new GridCoordinate(8, 0),
                new GridCoordinate(9, 0),] == game.maskedPlayersState.shipStates.find {
            it.ship == Ship.Cruiser
        }.shipGridCells
        game = move(P1G, TEST_PLAYER1, new GridCoordinate(7, 0))
        assert "TEST PLAYER1 performed evasive maneuvers."
        assert [new GridCoordinate(7, 0),
                new GridCoordinate(8, 0),
                new GridCoordinate(9, 0),] != game.maskedPlayersState.shipStates.find {
            it.ship == Ship.Cruiser
        }.shipGridCells
        assert GamePhase.Playing == game.gamePhase
        assert GridCellState.ObscuredHit == game.maskedPlayersState.opponentViews[TEST_PLAYER2.md5].get(7, 7)
        assert GridCellState.ObscuredHit == game.maskedPlayersState.opponentViews[TEST_PLAYER2.md5].get(7, 0)

        assert 0 == game.playersScore[TEST_PLAYER1.md5]
        assert 0 == game.maskedPlayersState.evasiveManeuversRemaining
        assert TEST_PLAYER1.md5 != game.currentPlayer
    }

    @Test
    void testCompleteSimpleGameThroughRematch() {
        def P3 = createPlayerAPITarget(TEST_PLAYER3)
        TBMaskedGame game = newGame(P3, new FeaturesAndPlayers(
                features: [
                        GameFeature.Grid15x15,
                        GameFeature.SharedIntel,
                        GameFeature.ECMEnabled,
                        GameFeature.EREnabled,
                        GameFeature.EMEnabled,
                        GameFeature.SpyEnabled,
                        GameFeature.PerShip
                ] as Set,
                players: [TEST_PLAYER2.md5, TEST_PLAYER3.md5, TEST_PLAYER1.md5]
        ))
        assert game != null
        def P3G = createGameTarget(P3, game)
        def P1G = createGameTarget(createPlayerAPITarget(TEST_PLAYER1), game)
        def P2G = createGameTarget(createPlayerAPITarget(TEST_PLAYER2), game)

        acceptGame(P1G)
        acceptGame(P2G)
        setup(P3G, P3POSITIONS)
        setup(P1G, P1POSITIONS)
        setup(P2G, P2POSITIONS)

        //  Force turn and order
        TBGame dbGame = gameRepository.findOne(new ObjectId(game.idAsString))
        dbGame.currentPlayer = TEST_PLAYER1.id
        dbGame.players = [TEST_PLAYER1, TEST_PLAYER2, TEST_PLAYER3]
        gameRepository.save(dbGame)
        cacheManager.cacheNames.each {
            cacheManager.getCache(it).clear()
        }

        fire(P1G, TEST_PLAYER3, new GridCoordinate(0, 0))
        fire(P1G, TEST_PLAYER3, new GridCoordinate(0, 1))
        fire(P1G, TEST_PLAYER3, new GridCoordinate(0, 2))
        fire(P1G, TEST_PLAYER3, new GridCoordinate(0, 3))
        game = fire(P1G, TEST_PLAYER3, new GridCoordinate(0, 4))
        assert 10 == game.playersScore[TEST_PLAYER1.md5]

        fire(P2G, TEST_PLAYER3, new GridCoordinate(1, 14))
        fire(P2G, TEST_PLAYER3, new GridCoordinate(0, 14))
        fire(P2G, TEST_PLAYER3, new GridCoordinate(2, 14))
        fire(P2G, TEST_PLAYER3, new GridCoordinate(3, 14))
        game = fire(P2G, TEST_PLAYER3, new GridCoordinate(4, 14))
        assert 7 == game.playersScore[TEST_PLAYER2.md5]

        fire(P3G, TEST_PLAYER1, new GridCoordinate(12, 12))
        fire(P3G, TEST_PLAYER2, new GridCoordinate(7, 8))
        game = fire(P3G, TEST_PLAYER2, new GridCoordinate(7, 7))
        assert 1 == game.playersScore[TEST_PLAYER3.md5]

        fire(P1G, TEST_PLAYER3, new GridCoordinate(1, 0))
        fire(P1G, TEST_PLAYER3, new GridCoordinate(1, 1))
        fire(P1G, TEST_PLAYER3, new GridCoordinate(1, 2))
        fire(P1G, TEST_PLAYER3, new GridCoordinate(1, 3))
        game = fire(P1G, TEST_PLAYER3, new GridCoordinate(1, 5))
        assert 19 == game.playersScore[TEST_PLAYER1.md5]

        fire(P2G, TEST_PLAYER3, new GridCoordinate(2, 0))
        fire(P2G, TEST_PLAYER3, new GridCoordinate(2, 1))
        fire(P2G, TEST_PLAYER3, new GridCoordinate(2, 2))
        fire(P2G, TEST_PLAYER3, new GridCoordinate(2, 3))
        game = fire(P2G, TEST_PLAYER3, new GridCoordinate(2, 4))
        assert 15 == game.playersScore[TEST_PLAYER2.md5]

        game = fire(P3G, TEST_PLAYER2, new GridCoordinate(7, 9))
        assert 7 == game.playersScore[TEST_PLAYER3.md5]

        fire(P1G, TEST_PLAYER3, new GridCoordinate(3, 2))
        fire(P1G, TEST_PLAYER3, new GridCoordinate(3, 3))
        game = fire(P1G, TEST_PLAYER3, new GridCoordinate(3, 4))
        assert 27 == game.playersScore[TEST_PLAYER1.md5]
        assert !game.playersAlive[TEST_PLAYER3.md5]
        assert game.playersAlive[TEST_PLAYER2.md5]
        assert game.playersAlive[TEST_PLAYER1.md5]
        spy(P1G, TEST_PLAYER2, new GridCoordinate(2, 2))

        fire(P2G, TEST_PLAYER1, new GridCoordinate(7, 7))
        fire(P2G, TEST_PLAYER1, new GridCoordinate(7, 8))
        fire(P2G, TEST_PLAYER1, new GridCoordinate(7, 6))
        game = fire(P2G, TEST_PLAYER1, new GridCoordinate(6, 7))
        assert 17 == game.playersScore[TEST_PLAYER2.md5]

        fire(P1G, TEST_PLAYER2, new GridCoordinate(14, 14))
        fire(P1G, TEST_PLAYER2, new GridCoordinate(14, 13))
        game = fire(P1G, TEST_PLAYER2, new GridCoordinate(14, 12))
        assert 35 == game.playersScore[TEST_PLAYER1.md5]
        repair(P1G, TEST_PLAYER1, new GridCoordinate(7, 7))

        fire(P2G, TEST_PLAYER1, new GridCoordinate(7, 7))
        fire(P2G, TEST_PLAYER1, new GridCoordinate(6, 7))
        game = fire(P2G, TEST_PLAYER1, new GridCoordinate(5, 7))
        assert 20 == game.playersScore[TEST_PLAYER2.md5]

        fire(P1G, TEST_PLAYER2, new GridCoordinate(0, 0))
        fire(P1G, TEST_PLAYER2, new GridCoordinate(0, 1))
        fire(P1G, TEST_PLAYER2, new GridCoordinate(0, 2))
        fire(P1G, TEST_PLAYER2, new GridCoordinate(0, 3))
        game = fire(P1G, TEST_PLAYER2, new GridCoordinate(0, 4))
        assert 45 == game.playersScore[TEST_PLAYER1.md5]

        fire(P2G, TEST_PLAYER1, new GridCoordinate(8, 7))
        game = fire(P2G, TEST_PLAYER1, new GridCoordinate(9, 7))
        assert 27 == game.playersScore[TEST_PLAYER2.md5]

        fire(P1G, TEST_PLAYER2, new GridCoordinate(1, 13))
        fire(P1G, TEST_PLAYER2, new GridCoordinate(0, 14))
        fire(P1G, TEST_PLAYER2, new GridCoordinate(0, 13))
        game = fire(P1G, TEST_PLAYER2, new GridCoordinate(0, 12))
        assert 53 == game.playersScore[TEST_PLAYER1.md5]

        game = fire(P2G, TEST_PLAYER1, new GridCoordinate(10, 7))
        assert 27 == game.playersScore[TEST_PLAYER2.md5]

        fire(P1G, TEST_PLAYER2, new GridCoordinate(14, 0))
        fire(P1G, TEST_PLAYER2, new GridCoordinate(14, 1))
        fire(P1G, TEST_PLAYER2, new GridCoordinate(14, 2))
        game = fire(P1G, TEST_PLAYER2, new GridCoordinate(14, 3))
        assert GamePhase.RoundOver == game.gamePhase
        assert !game.playersAlive[TEST_PLAYER2.md5]
        assert game.playersAlive[TEST_PLAYER1.md5]
        assert 72 == game.playersScore[TEST_PLAYER1.md5]
        assert "TEST PLAYER1 defeated all challengers!" == game.maskedPlayersState.actionLog[-1].description
        assert TEST_PLAYER1.md5 == game.winningPlayer

        TBMaskedGame newGame = rematchGame(P1G)
        assert GamePhase.Challenged == newGame.gamePhase
        assert game.id != newGame.id
        P2G = createGameTarget(createPlayerAPITarget(TEST_PLAYER2), newGame)
        newGame = rejectGame(P2G)
        assert GamePhase.Declined == newGame.gamePhase
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

    protected TBMaskedGame setup(final WebTarget target, final List<ShipAndCoordinates> positions) {
        def placement = Entity.entity(
                positions,
                MediaType.APPLICATION_JSON
        )
        target.path("setup").request(MediaType.APPLICATION_JSON).put(placement, returnedGameClass())
    }

    protected TBMaskedGame fire(WebTarget target, Player player, GridCoordinate coordinate) {
        makeMove(player, coordinate, target, "fire")
    }

    protected TBMaskedGame cruiseMissile(WebTarget target, Player player, GridCoordinate coordinate) {
        makeMove(player, coordinate, target, "missile")
    }

    protected TBMaskedGame move(WebTarget target, Player player, GridCoordinate coordinate) {
        makeMove(player, coordinate, target, "move")
    }

    protected TBMaskedGame spy(WebTarget target, Player player, GridCoordinate coordinate) {
        makeMove(player, coordinate, target, "spy")
    }

    protected TBMaskedGame repair(WebTarget target, Player player, GridCoordinate coordinate) {
        makeMove(player, coordinate, target, "repair")
    }

    protected TBMaskedGame ecm(WebTarget target, Player player, GridCoordinate coordinate) {
        makeMove(player, coordinate, target, "ecm")
    }

    protected TBMaskedGame makeMove(Player player, GridCoordinate coordinate, WebTarget target, String command) {
        def placement = Entity.entity(
                new Target(player: player.md5, coordinate: coordinate),
                MediaType.APPLICATION_JSON
        )
        target.path(command).request(MediaType.APPLICATION_JSON).put(placement, returnedGameClass())
    }

    private static final List<ShipAndCoordinates> P1POSITIONS = [
            new ShipAndCoordinates(ship: Ship.Carrier, coordinates: [
                    new GridCoordinate(5, 7),
                    new GridCoordinate(6, 7),
                    new GridCoordinate(7, 7),
                    new GridCoordinate(8, 7),
                    new GridCoordinate(9, 7),
            ]),
            new ShipAndCoordinates(ship: Ship.Battleship, coordinates: [
                    new GridCoordinate(0, 6),
                    new GridCoordinate(0, 7),
                    new GridCoordinate(0, 8),
                    new GridCoordinate(0, 9),
            ]),
            new ShipAndCoordinates(ship: Ship.Cruiser, coordinates: [
                    new GridCoordinate(7, 0),
                    new GridCoordinate(8, 0),
                    new GridCoordinate(9, 0),
            ]),
            new ShipAndCoordinates(ship: Ship.Submarine, coordinates: [
                    new GridCoordinate(7, 14),
                    new GridCoordinate(8, 14),
                    new GridCoordinate(9, 14),
            ]),
            new ShipAndCoordinates(ship: Ship.Destroyer, coordinates: [
                    new GridCoordinate(14, 7),
                    new GridCoordinate(14, 8),
            ]),
    ]

    private static final List<ShipAndCoordinates> P2POSITIONS = [
            new ShipAndCoordinates(ship: Ship.Carrier, coordinates: [
                    new GridCoordinate(0, 0),
                    new GridCoordinate(0, 1),
                    new GridCoordinate(0, 2),
                    new GridCoordinate(0, 3),
                    new GridCoordinate(0, 4),
            ]),
            new ShipAndCoordinates(ship: Ship.Battleship, coordinates: [
                    new GridCoordinate(14, 0),
                    new GridCoordinate(14, 1),
                    new GridCoordinate(14, 2),
                    new GridCoordinate(14, 3),
            ]),
            new ShipAndCoordinates(ship: Ship.Cruiser, coordinates: [
                    new GridCoordinate(0, 14),
                    new GridCoordinate(0, 13),
                    new GridCoordinate(0, 12),
            ]),
            new ShipAndCoordinates(ship: Ship.Submarine, coordinates: [
                    new GridCoordinate(14, 14),
                    new GridCoordinate(14, 13),
                    new GridCoordinate(14, 12),
            ]),
            new ShipAndCoordinates(ship: Ship.Destroyer, coordinates: [
                    new GridCoordinate(7, 8),
                    new GridCoordinate(7, 9),
            ]),
    ]

    private static final List<ShipAndCoordinates> P3POSITIONS = [
            new ShipAndCoordinates(ship: Ship.Carrier, coordinates: [
                    new GridCoordinate(0, 0),
                    new GridCoordinate(0, 1),
                    new GridCoordinate(0, 2),
                    new GridCoordinate(0, 3),
                    new GridCoordinate(0, 4),
            ]),
            new ShipAndCoordinates(ship: Ship.Battleship, coordinates: [
                    new GridCoordinate(1, 0),
                    new GridCoordinate(1, 1),
                    new GridCoordinate(1, 2),
                    new GridCoordinate(1, 3),
            ]),
            new ShipAndCoordinates(ship: Ship.Cruiser, coordinates: [
                    new GridCoordinate(2, 1),
                    new GridCoordinate(2, 2),
                    new GridCoordinate(2, 3),
            ]),
            new ShipAndCoordinates(ship: Ship.Submarine, coordinates: [
                    new GridCoordinate(3, 4),
                    new GridCoordinate(3, 2),
                    new GridCoordinate(3, 3),
            ]),
            new ShipAndCoordinates(ship: Ship.Destroyer, coordinates: [
                    new GridCoordinate(0, 14),
                    new GridCoordinate(1, 14),
            ]),
    ]

    public static final FeaturesAndPlayers STANDARD_PLAYERS_AND_FEATURES = new FeaturesAndPlayers(
            features: [
                    GameFeature.Grid15x15,
                    GameFeature.SharedIntel,
                    GameFeature.ECMEnabled,
                    GameFeature.EREnabled,
                    GameFeature.EMEnabled,
                    GameFeature.CruiseMissileEnabled,
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
