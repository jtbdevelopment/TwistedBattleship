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
import com.jtbdevelopment.games.dao.AbstractGameRepository
import com.jtbdevelopment.games.dev.utilities.integrationtesting.AbstractGameIntegration
import com.jtbdevelopment.games.mongo.players.MongoManualPlayer
import com.jtbdevelopment.games.mongo.players.MongoPlayer
import com.jtbdevelopment.games.players.Player
import com.jtbdevelopment.games.state.GamePhase
import com.jtbdevelopment.games.state.PlayerState
import org.bson.types.ObjectId
import org.junit.Assert
import org.junit.BeforeClass
import org.junit.Test

import javax.ws.rs.client.Client
import javax.ws.rs.client.Entity
import javax.ws.rs.client.WebTarget
import javax.ws.rs.core.GenericType
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

import static org.junit.Assert.assertNotNull

/**
 * Date: 4/26/15
 * Time: 10:36 AM
 */
class TwistedBattleshipIntegration extends AbstractGameIntegration<TBGame, TBMaskedGame> {

    private static HazelcastCacheManager cacheManager
    private static GameRepository gameRepository

    @BeforeClass
    static void setup() {
        cacheManager = applicationContext.getBean(HazelcastCacheManager.class)
        gameRepository = applicationContext.getBean(GameRepository.class)
    }

    @Test
    void testPlayerTheme() {
        ((TBPlayerAttributes) TEST_PLAYER2.gameSpecificPlayerAttributes).setAvailableThemes(new HashSet<String>(['default', 'new-theme']))
        playerRepository.save(TEST_PLAYER2)
        Client client = createConnection(TEST_PLAYER2)
        def p = client.target(PLAYER_API).request(MediaType.APPLICATION_JSON).get(MongoManualPlayer.class);
        Assert.assertEquals 'default-theme', ((TBPlayerAttributes) p.gameSpecificPlayerAttributes).theme
        MongoPlayer updated = client.target(PLAYER_API).path('changeTheme').path('new-theme').request(MediaType.APPLICATION_JSON).put(EMPTY_PUT_POST, MongoManualPlayer.class)
        Assert.assertEquals 'new-theme', ((TBPlayerAttributes) updated.gameSpecificPlayerAttributes).theme
    }

    @Test
    void testGetCircleSizes() {
        def client = createAPITarget(TEST_PLAYER2)
        def sizes = client.path("circles").request(MediaType.APPLICATION_JSON_TYPE).get(
                new GenericType<Map<Integer, Set<GridCoordinate>>>() {
                })
        Assert.assertEquals GridCircleUtil.CIRCLE_OFFSETS, sizes
    }

    @Test
    void testGetCellStates() {
        def client = createAPITarget(TEST_PLAYER2)
        def sizes = client.path("states").request(MediaType.APPLICATION_JSON_TYPE).get(
                new GenericType<List<GridCellState>>() {
                })
        Assert.assertEquals GridCellState.values().toList(), sizes
    }

    @Test
    void testGetShips() {
        def client = createAPITarget(TEST_PLAYER3)
        def ships = client.path("ships").request(MediaType.APPLICATION_JSON_TYPE).get(
                new GenericType<List<ShipInfo>>() {
                })
        Assert.assertEquals(Ship.values().collect { Ship it -> new ShipInfo(it) }, ships)
    }

    @Test
    void testGetFeatures() {
        def client = createAPITarget(TEST_PLAYER2)
        def features = client.path("features").request(MediaType.APPLICATION_JSON_TYPE).get(
                new GenericType<List<GameFeatureInfo>>() {
                })
        Assert.assertEquals features, [
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
                new GameFeatureInfo(GameFeature.StartingShips,
                        [
                                new GameFeatureInfo.Detail(GameFeature.StandardShips),
                                new GameFeatureInfo.Detail(GameFeature.AllCarriers),
                                new GameFeatureInfo.Detail(GameFeature.AllDestroyers),
                                new GameFeatureInfo.Detail(GameFeature.AllSubmarines),
                                new GameFeatureInfo.Detail(GameFeature.AllCruisers),
                                new GameFeatureInfo.Detail(GameFeature.AllBattleships),
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
                new GameFeatureInfo(GameFeature.CruiseMissile,
                        [
                                new GameFeatureInfo.Detail(GameFeature.CruiseMissileEnabled),
                                new GameFeatureInfo.Detail(GameFeature.CruiseMissileDisabled),
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
                                GameFeature.PerShip,
                                GameFeature.CruiseMissileDisabled
                        ] as Set,
                        players: [TEST_PLAYER2.md5, TEST_PLAYER3.md5, TEST_PLAYER1.md5],
                ))
        assertNotNull game
        Assert.assertEquals 20, game.gridSize
        Assert.assertEquals 2, game.movesForSpecials
        Assert.assertEquals([
                (TEST_PLAYER1.md5): PlayerState.Pending,
                (TEST_PLAYER2.md5): PlayerState.Pending,
                (TEST_PLAYER3.md5): PlayerState.Accepted
        ], game.playerStates)
        Assert.assertEquals([
                (TEST_PLAYER1.md5): true,
                (TEST_PLAYER2.md5): true,
                (TEST_PLAYER3.md5): true
        ], game.playersAlive)
        Assert.assertEquals([
                (TEST_PLAYER1.md5): false,
                (TEST_PLAYER2.md5): false,
                (TEST_PLAYER3.md5): false
        ], game.playersSetup)
        Assert.assertEquals([
                (TEST_PLAYER1.md5): 0,
                (TEST_PLAYER2.md5): 0,
                (TEST_PLAYER3.md5): 0
        ], game.playersScore)
        Assert.assertEquals 5, game.maskedPlayersState.activeShipsRemaining
        Assert.assertEquals([
                (TEST_PLAYER1.md5): new Grid(20),
                (TEST_PLAYER2.md5): new Grid(20)
        ], game.maskedPlayersState.opponentGrids)
        Assert.assertEquals([
                (TEST_PLAYER1.md5): new Grid(20),
                (TEST_PLAYER2.md5): new Grid(20)
        ], game.maskedPlayersState.opponentViews)
        Assert.assertEquals 0, game.maskedPlayersState.spysRemaining
        Assert.assertEquals 2, game.maskedPlayersState.ecmsRemaining
        Assert.assertEquals 2, game.maskedPlayersState.emergencyRepairsRemaining
        Assert.assertEquals 0, game.maskedPlayersState.evasiveManeuversRemaining
        Assert.assertEquals GamePhase.Challenged, game.gamePhase

        //  Clear cache and force a load from db to confirm full round trip
        cacheManager.cacheNames.each {
            cacheManager.getCache(it).clear()
        }

        game = getGame(createGameTarget(P3, game))
        Assert.assertEquals([
                (TEST_PLAYER1.md5): 0,
                (TEST_PLAYER2.md5): 0,
                (TEST_PLAYER3.md5): 0
        ], game.playersScore)
        Assert.assertEquals 5, game.maskedPlayersState.activeShipsRemaining
        Assert.assertEquals([
                (TEST_PLAYER1.md5): new Grid(20),
                (TEST_PLAYER2.md5): new Grid(20)
        ], game.maskedPlayersState.opponentGrids)
        Assert.assertEquals([
                (TEST_PLAYER1.md5): new Grid(20),
                (TEST_PLAYER2.md5): new Grid(20)
        ], game.maskedPlayersState.opponentViews)
        Assert.assertEquals 0, game.maskedPlayersState.spysRemaining
        Assert.assertEquals 2, game.maskedPlayersState.ecmsRemaining
        Assert.assertEquals 2, game.maskedPlayersState.emergencyRepairsRemaining
        Assert.assertEquals 0, game.maskedPlayersState.evasiveManeuversRemaining
        Assert.assertEquals GamePhase.Challenged, game.gamePhase
    }

    @Test
    void testCreateAndRejectNewGame() {
        def P3 = createPlayerAPITarget(TEST_PLAYER3)
        TBMaskedGame game = newGame(P3, STANDARD_PLAYERS_AND_FEATURES)
        def P1G = createGameTarget(createPlayerAPITarget(TEST_PLAYER1), game)
        Assert.assertNotNull game
        Assert.assertEquals([
                (TEST_PLAYER1.md5): PlayerState.Pending,
                (TEST_PLAYER2.md5): PlayerState.Pending,
                (TEST_PLAYER3.md5): PlayerState.Accepted
        ], game.playerStates)
        Assert.assertEquals GamePhase.Challenged, game.gamePhase

        game = rejectGame(P1G)
        Assert.assertEquals([
                (TEST_PLAYER1.md5): PlayerState.Rejected,
                (TEST_PLAYER2.md5): PlayerState.Pending,
                (TEST_PLAYER3.md5): PlayerState.Accepted
        ], game.playerStates)
        Assert.assertEquals GamePhase.Declined, game.gamePhase
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
        Assert.assertEquals([
                (TEST_PLAYER1.md5): PlayerState.Quit,
                (TEST_PLAYER2.md5): PlayerState.Accepted,
                (TEST_PLAYER3.md5): PlayerState.Accepted
        ], game.playerStates)
    }

    @Test
    void testSetupGame() {
        def P3 = createPlayerAPITarget(TEST_PLAYER3)
        TBMaskedGame game = newGame(P3, STANDARD_PLAYERS_AND_FEATURES)
        Assert.assertNotNull game
        def P3G = createGameTarget(P3, game)
        def P1G = createGameTarget(createPlayerAPITarget(TEST_PLAYER1), game)
        def P2G = createGameTarget(createPlayerAPITarget(TEST_PLAYER2), game)


        game = acceptGame(P1G)
        Assert.assertNotNull game
        Assert.assertEquals([
                (TEST_PLAYER1.md5): PlayerState.Accepted,
                (TEST_PLAYER2.md5): PlayerState.Pending,
                (TEST_PLAYER3.md5): PlayerState.Accepted
        ], game.playerStates)
        Assert.assertEquals GamePhase.Challenged, game.gamePhase

        game = acceptGame(P2G)
        Assert.assertNotNull game
        Assert.assertEquals([
                (TEST_PLAYER1.md5): PlayerState.Accepted,
                (TEST_PLAYER2.md5): PlayerState.Accepted,
                (TEST_PLAYER3.md5): PlayerState.Accepted
        ], game.playerStates)
        Assert.assertEquals([
                (TEST_PLAYER1.md5): false,
                (TEST_PLAYER2.md5): false,
                (TEST_PLAYER3.md5): false
        ], game.playersSetup)
        Assert.assertEquals GamePhase.Setup, game.gamePhase

        game = setup(P3G, P3POSITIONS)
        Assert.assertEquals([
                (TEST_PLAYER1.md5): false,
                (TEST_PLAYER2.md5): false,
                (TEST_PLAYER3.md5): true
        ], game.playersSetup)
        Assert.assertEquals GamePhase.Setup, game.gamePhase

        game = setup(P1G, P1POSITIONS)
        Assert.assertEquals([
                (TEST_PLAYER1.md5): true,
                (TEST_PLAYER2.md5): false,
                (TEST_PLAYER3.md5): true
        ], game.playersSetup)
        Assert.assertEquals GamePhase.Setup, game.gamePhase

        game = setup(P2G, P2POSITIONS)
        Assert.assertEquals([
                (TEST_PLAYER1.md5): true,
                (TEST_PLAYER2.md5): true,
                (TEST_PLAYER3.md5): true
        ], game.playersSetup)
        Assert.assertEquals GamePhase.Playing, game.gamePhase
        Assert.assertEquals 5, game.remainingMoves
        Assert.assertTrue([TEST_PLAYER2.md5, TEST_PLAYER1.md5, TEST_PLAYER3.md5].contains(game.currentPlayer))
        Assert.assertEquals TBActionLogEntry.TBActionType.Begin, game.maskedPlayersState.actionLog[0].actionType
        Assert.assertEquals "Game ready to play.", game.maskedPlayersState.actionLog[0].description
        Assert.assertNotEquals(0, game.maskedPlayersState.actionLog[0].timestamp)
    }

    @Test
    void testFireForTurnInGame() {
        def P3 = createPlayerAPITarget(TEST_PLAYER3)
        TBMaskedGame game = newGame(P3, STANDARD_PLAYERS_AND_FEATURES)
        Assert.assertNotNull game
        def P3G = createGameTarget(P3, game)
        def P1G = createGameTarget(createPlayerAPITarget(TEST_PLAYER1), game)
        def P2G = createGameTarget(createPlayerAPITarget(TEST_PLAYER2), game)

        acceptGame(P1G)
        acceptGame(P2G)
        setup(P3G, P3POSITIONS)
        setup(P1G, P1POSITIONS)
        setup(P2G, P2POSITIONS)

        //  Force turn to P2
        TBGame dbGame = gameRepository.findById(new ObjectId(game.id)).get()
        dbGame.currentPlayer = TEST_PLAYER2.id
        gameRepository.save(dbGame)
        cacheManager.cacheNames.each {
            cacheManager.getCache(it).clear()
        }

        game = fire(P2G, TEST_PLAYER1, new GridCoordinate(7, 7))
        Assert.assertEquals GamePhase.Playing, game.gamePhase
        Assert.assertEquals "You fired at TEST PLAYER1 (7,7) and hit!", game.maskedPlayersState.actionLog[-1].description
        Assert.assertEquals TBActionLogEntry.TBActionType.Fired, game.maskedPlayersState.actionLog[-1].actionType
        Assert.assertEquals 1, game.playersScore[TEST_PLAYER2.md5]
        Assert.assertEquals 4, game.remainingMoves
        Assert.assertEquals TEST_PLAYER2.md5, game.currentPlayer
        game = fire(P2G, TEST_PLAYER1, new GridCoordinate(7, 8))
        Assert.assertEquals "You fired at TEST PLAYER1 (7,8) and missed.", game.maskedPlayersState.actionLog[-1].description
        game = fire(P2G, TEST_PLAYER1, new GridCoordinate(7, 6))
        Assert.assertEquals "You fired at TEST PLAYER1 (7,6) and missed.", game.maskedPlayersState.actionLog[-1].description
        game = fire(P2G, TEST_PLAYER1, new GridCoordinate(8, 7))
        Assert.assertEquals "You fired at TEST PLAYER1 (8,7) and hit!", game.maskedPlayersState.actionLog[-1].description
        Assert.assertEquals 1, game.remainingMoves
        game = fire(P2G, TEST_PLAYER1, new GridCoordinate(9, 7))
        Assert.assertEquals "You fired at TEST PLAYER1 (9,7) and hit!", game.maskedPlayersState.actionLog[-1].description
        Assert.assertEquals 3, game.playersScore[TEST_PLAYER2.md5]
        Assert.assertEquals 5, game.remainingMoves
        Assert.assertNotEquals TEST_PLAYER2.md5, game.currentPlayer
    }

    @Test
    void testCruiseMissileForTurnInGame() {
        def P3 = createPlayerAPITarget(TEST_PLAYER3)
        TBMaskedGame game = newGame(P3, STANDARD_PLAYERS_AND_FEATURES)
        Assert.assertNotNull game
        def P3G = createGameTarget(P3, game)
        def P1G = createGameTarget(createPlayerAPITarget(TEST_PLAYER1), game)
        def P2G = createGameTarget(createPlayerAPITarget(TEST_PLAYER2), game)

        acceptGame(P1G)
        acceptGame(P2G)
        setup(P3G, P3POSITIONS)
        setup(P1G, P1POSITIONS)
        setup(P2G, P2POSITIONS)

        //  Force turn to P2
        TBGame dbGame = gameRepository.findById(new ObjectId(game.id)).get()
        dbGame.currentPlayer = TEST_PLAYER2.id
        gameRepository.save(dbGame)
        cacheManager.cacheNames.each {
            cacheManager.getCache(it).clear()
        }

        fire(P2G, TEST_PLAYER1, new GridCoordinate(7, 7))
        game = cruiseMissile(P2G, TEST_PLAYER1, new GridCoordinate(7, 7))
        Assert.assertEquals GamePhase.Playing, game.gamePhase
        Assert.assertEquals "You fired a cruise missile at TEST PLAYER1 (7,7) and hit!", game.maskedPlayersState.actionLog[-7].description
        Assert.assertEquals "You fired at TEST PLAYER1 (5,7) and hit!", game.maskedPlayersState.actionLog[-6].description
        Assert.assertEquals "You fired at TEST PLAYER1 (6,7) and hit!", game.maskedPlayersState.actionLog[-5].description
        Assert.assertEquals "You fired at TEST PLAYER1 (7,7) and hit an already damaged area!", game.maskedPlayersState.actionLog[-4].description
        Assert.assertEquals "You fired at TEST PLAYER1 (8,7) and hit!", game.maskedPlayersState.actionLog[-3].description
        Assert.assertEquals "You fired at TEST PLAYER1 (9,7) and hit!", game.maskedPlayersState.actionLog[-2].description
        Assert.assertEquals "You sunk a Aircraft Carrier for TEST PLAYER1!", game.maskedPlayersState.actionLog[-1].description
        Assert.assertEquals TBActionLogEntry.TBActionType.CruiseMissile, game.maskedPlayersState.actionLog[-7].actionType
        Assert.assertEquals 10, game.playersScore[TEST_PLAYER2.md5]
        Assert.assertEquals 2, game.remainingMoves
        Assert.assertEquals 0, game.maskedPlayersState.cruiseMissilesRemaining
        Assert.assertEquals TEST_PLAYER2.md5, game.currentPlayer
        game = fire(P2G, TEST_PLAYER1, new GridCoordinate(7, 8))
        Assert.assertEquals "You fired at TEST PLAYER1 (7,8) and missed.", game.maskedPlayersState.actionLog[-1].description
        Assert.assertEquals 1, game.remainingMoves
        game = fire(P2G, TEST_PLAYER1, new GridCoordinate(9, 7))
        Assert.assertEquals "You fired at TEST PLAYER1 (9,7) and hit an already damaged area!", game.maskedPlayersState.actionLog[-1].description
        Assert.assertEquals 10, game.playersScore[TEST_PLAYER2.md5]
        Assert.assertEquals 5, game.remainingMoves
        Assert.assertNotEquals TEST_PLAYER2.md5, game.currentPlayer
    }

    @Test
    void testSpyForTurnInGame() {
        def P3 = createPlayerAPITarget(TEST_PLAYER3)
        TBMaskedGame game = newGame(P3, STANDARD_PLAYERS_AND_FEATURES)
        Assert.assertNotNull game
        def P3G = createGameTarget(P3, game)
        def P1G = createGameTarget(createPlayerAPITarget(TEST_PLAYER1), game)
        def P2G = createGameTarget(createPlayerAPITarget(TEST_PLAYER2), game)

        acceptGame(P1G)
        acceptGame(P2G)
        setup(P3G, P3POSITIONS)
        setup(P1G, P1POSITIONS)
        setup(P2G, P2POSITIONS)

        //  Force turn to P2
        TBGame dbGame = gameRepository.findById(new ObjectId(game.id)).get()
        dbGame.currentPlayer = TEST_PLAYER2.id
        gameRepository.save(dbGame)
        cacheManager.cacheNames.each {
            cacheManager.getCache(it).clear()
        }

        fire(P2G, TEST_PLAYER1, new GridCoordinate(7, 7))

        game = spy(P2G, TEST_PLAYER1, new GridCoordinate(7, 8))
        Assert.assertEquals GamePhase.Playing, game.gamePhase
        Assert.assertEquals "You spied on TEST PLAYER1 at (7,8).", game.maskedPlayersState.actionLog[-1].description
        Assert.assertEquals 2, game.remainingMoves
        Assert.assertEquals 1, game.maskedPlayersState.spysRemaining
        Assert.assertEquals TEST_PLAYER2.md5, game.currentPlayer
        game = spy(P2G, TEST_PLAYER1, new GridCoordinate(2, 6))
        Assert.assertEquals "You spied on TEST PLAYER1 at (2,6).", game.maskedPlayersState.actionLog[-1].description
        Assert.assertEquals 1, game.playersScore[TEST_PLAYER2.md5]
        Assert.assertEquals 0, game.maskedPlayersState.spysRemaining
        Assert.assertNotEquals TEST_PLAYER2.md5, game.currentPlayer

        //Sample checks, not full
        Assert.assertEquals GridCellState.KnownByHit, game.maskedPlayersState.opponentGrids[TEST_PLAYER1.md5].get(7, 7)
        Assert.assertEquals GridCellState.KnownShip, game.maskedPlayersState.opponentGrids[TEST_PLAYER1.md5].get(8, 7)
        Assert.assertEquals GridCellState.KnownShip, game.maskedPlayersState.opponentGrids[TEST_PLAYER1.md5].get(0, 6)
        Assert.assertEquals GridCellState.KnownEmpty, game.maskedPlayersState.opponentGrids[TEST_PLAYER1.md5].get(7, 8)
        game = getGame(P1G)
        Assert.assertEquals GridCellState.KnownByHit, game.maskedPlayersState.opponentViews[TEST_PLAYER2.md5].get(7, 7)
        Assert.assertEquals GridCellState.KnownShip, game.maskedPlayersState.opponentViews[TEST_PLAYER2.md5].get(8, 7)
        Assert.assertEquals GridCellState.KnownShip, game.maskedPlayersState.opponentViews[TEST_PLAYER2.md5].get(0, 6)
        Assert.assertEquals GridCellState.KnownEmpty, game.maskedPlayersState.opponentViews[TEST_PLAYER2.md5].get(7, 8)
        Assert.assertEquals GridCellState.KnownByHit, game.maskedPlayersState.consolidatedOpponentView.get(7, 7)
        Assert.assertEquals GridCellState.KnownShip, game.maskedPlayersState.consolidatedOpponentView.get(8, 7)
        Assert.assertEquals GridCellState.KnownShip, game.maskedPlayersState.consolidatedOpponentView.get(0, 6)
        Assert.assertEquals GridCellState.KnownEmpty, game.maskedPlayersState.consolidatedOpponentView.get(7, 8)
    }

    @Test
    void testRepairForTurnInGame() {
        def P3 = createPlayerAPITarget(TEST_PLAYER3)
        TBMaskedGame game = newGame(P3, STANDARD_PLAYERS_AND_FEATURES)
        Assert.assertNotNull game
        def P3G = createGameTarget(P3, game)
        def P1G = createGameTarget(createPlayerAPITarget(TEST_PLAYER1), game)
        def P2G = createGameTarget(createPlayerAPITarget(TEST_PLAYER2), game)

        acceptGame(P1G)
        acceptGame(P2G)
        setup(P3G, P3POSITIONS)
        setup(P1G, P1POSITIONS)
        setup(P2G, P2POSITIONS)

        //  Force turn to P2
        TBGame dbGame = gameRepository.findById(new ObjectId(game.id)).get()
        dbGame.currentPlayer = TEST_PLAYER2.id
        gameRepository.save(dbGame)
        cacheManager.cacheNames.each {
            cacheManager.getCache(it).clear()
        }

        fire(P2G, TEST_PLAYER1, new GridCoordinate(7, 7))
        game = fire(P2G, TEST_PLAYER1, new GridCoordinate(7, 0))

        //  Force turn to P1
        dbGame = gameRepository.findById(new ObjectId(game.id)).get()
        dbGame.currentPlayer = TEST_PLAYER1.id
        dbGame.remainingMoves = 5
        gameRepository.save(dbGame)
        cacheManager.cacheNames.each {
            cacheManager.getCache(it).clear()
        }

        fire(P1G, TEST_PLAYER2, new GridCoordinate(7, 0))
        game = repair(P1G, TEST_PLAYER1, new GridCoordinate(8, 7))
        Assert.assertEquals 2, game.remainingMoves
        Assert.assertEquals "TEST PLAYER1 repaired their Aircraft Carrier.", game.maskedPlayersState.actionLog[-1].description
        Assert.assertEquals 5, game.maskedPlayersState.shipStates.find { it.ship == Ship.Carrier }.healthRemaining
        Assert.assertEquals([false, false, false, false, false], game.maskedPlayersState.shipStates.find {
            it.ship == Ship.Carrier
        }.shipSegmentHit)
        Assert.assertEquals GridCellState.KnownShip, game.maskedPlayersState.consolidatedOpponentView.get(7, 7)
        Assert.assertEquals 1, game.maskedPlayersState.emergencyRepairsRemaining
        Assert.assertEquals 2, game.remainingMoves


        game = repair(P1G, TEST_PLAYER1, new GridCoordinate(8, 0))
        Assert.assertEquals "TEST PLAYER1 repaired their Cruiser.", game.maskedPlayersState.actionLog[-1].description
        Assert.assertEquals 3, game.maskedPlayersState.shipStates.find { it.ship == Ship.Cruiser }.healthRemaining
        Assert.assertEquals([false, false, false], game.maskedPlayersState.shipStates.find {
            it.ship == Ship.Cruiser
        }.shipSegmentHit)
        Assert.assertEquals GridCellState.KnownShip, game.maskedPlayersState.consolidatedOpponentView.get(7, 0)
        Assert.assertEquals GamePhase.Playing, game.gamePhase
        Assert.assertEquals 0, game.playersScore[TEST_PLAYER1.md5]
        Assert.assertEquals 0, game.maskedPlayersState.emergencyRepairsRemaining
        Assert.assertNotEquals TEST_PLAYER1.md5, game.currentPlayer
    }

    @Test
    void testECMForTurnInGame() {
        def P3 = createPlayerAPITarget(TEST_PLAYER3)
        TBMaskedGame game = newGame(P3, STANDARD_PLAYERS_AND_FEATURES)
        Assert.assertNotNull game
        def P3G = createGameTarget(P3, game)
        def P1G = createGameTarget(createPlayerAPITarget(TEST_PLAYER1), game)
        def P2G = createGameTarget(createPlayerAPITarget(TEST_PLAYER2), game)

        acceptGame(P1G)
        acceptGame(P2G)
        setup(P3G, P3POSITIONS)
        setup(P1G, P1POSITIONS)
        setup(P2G, P2POSITIONS)

        //  Force turn to P2
        TBGame dbGame = gameRepository.findById(new ObjectId(game.id)).get()
        dbGame.currentPlayer = TEST_PLAYER2.id
        gameRepository.save(dbGame)
        cacheManager.cacheNames.each {
            cacheManager.getCache(it).clear()
        }

        fire(P2G, TEST_PLAYER1, new GridCoordinate(7, 7))
        game = fire(P2G, TEST_PLAYER1, new GridCoordinate(7, 0))

        //  Force turn to P1
        dbGame = gameRepository.findById(new ObjectId(game.id)).get()
        dbGame.currentPlayer = TEST_PLAYER1.id
        dbGame.remainingMoves = 5
        gameRepository.save(dbGame)
        cacheManager.cacheNames.each {
            cacheManager.getCache(it).clear()
        }

        game = fire(P1G, TEST_PLAYER2, new GridCoordinate(7, 0))
        Assert.assertEquals GridCellState.KnownByHit, game.maskedPlayersState.opponentViews[TEST_PLAYER2.md5].get(7, 7)
        Assert.assertEquals GridCellState.KnownByHit, game.maskedPlayersState.opponentViews[TEST_PLAYER2.md5].get(7, 0)
        game = ecm(P1G, TEST_PLAYER1, new GridCoordinate(8, 7))
        Assert.assertEquals 2, game.remainingMoves
        Assert.assertEquals "TEST PLAYER1 deployed an ECM.", game.maskedPlayersState.actionLog[-1].description
        Assert.assertEquals 1, game.maskedPlayersState.ecmsRemaining
        Assert.assertEquals GridCellState.HiddenHit, game.maskedPlayersState.opponentViews[TEST_PLAYER2.md5].get(7, 7)
        Assert.assertEquals GridCellState.KnownByHit, game.maskedPlayersState.opponentViews[TEST_PLAYER2.md5].get(7, 0)


        game = ecm(P1G, TEST_PLAYER1, new GridCoordinate(6, 0))
        Assert.assertEquals "TEST PLAYER1 deployed an ECM.", game.maskedPlayersState.actionLog[-1].description
        Assert.assertEquals GamePhase.Playing, game.gamePhase
        Assert.assertEquals GridCellState.HiddenHit, game.maskedPlayersState.opponentViews[TEST_PLAYER2.md5].get(7, 7)
        Assert.assertEquals GridCellState.HiddenHit, game.maskedPlayersState.opponentViews[TEST_PLAYER2.md5].get(7, 0)
        Assert.assertEquals 0, game.playersScore[TEST_PLAYER1.md5]
        Assert.assertEquals 0, game.maskedPlayersState.ecmsRemaining
        Assert.assertNotEquals TEST_PLAYER1.md5, game.currentPlayer
    }

    //  TODO - Unfortunately, for the possibility of random == ship remaining in place this test can fail randomly
    @Test
    void testMoveForTurnInGame() {
        def P3 = createPlayerAPITarget(TEST_PLAYER3)
        TBMaskedGame game = newGame(P3, STANDARD_PLAYERS_AND_FEATURES)
        Assert.assertNotNull game
        def P3G = createGameTarget(P3, game)
        def P1G = createGameTarget(createPlayerAPITarget(TEST_PLAYER1), game)
        def P2G = createGameTarget(createPlayerAPITarget(TEST_PLAYER2), game)

        acceptGame(P1G)
        acceptGame(P2G)
        setup(P3G, P3POSITIONS)
        setup(P1G, P1POSITIONS)
        setup(P2G, P2POSITIONS)

        //  Force turn to P2
        TBGame dbGame = gameRepository.findById(new ObjectId(game.id)).get()
        dbGame.currentPlayer = TEST_PLAYER2.id
        gameRepository.save(dbGame)
        cacheManager.cacheNames.each {
            cacheManager.getCache(it).clear()
        }

        fire(P2G, TEST_PLAYER1, new GridCoordinate(7, 7))
        game = fire(P2G, TEST_PLAYER1, new GridCoordinate(7, 0))

        //  Force turn to P1
        dbGame = gameRepository.findById(new ObjectId(game.id)).get()
        dbGame.currentPlayer = TEST_PLAYER1.id
        dbGame.remainingMoves = 5
        gameRepository.save(dbGame)
        cacheManager.cacheNames.each {
            cacheManager.getCache(it).clear()
        }

        game = fire(P1G, TEST_PLAYER2, new GridCoordinate(7, 0))
        Assert.assertEquals GridCellState.KnownByHit, game.maskedPlayersState.opponentViews[TEST_PLAYER2.md5].get(7, 7)
        Assert.assertEquals GridCellState.KnownByHit, game.maskedPlayersState.opponentViews[TEST_PLAYER2.md5].get(7, 0)
        Assert.assertEquals([new GridCoordinate(5, 7),
                             new GridCoordinate(6, 7),
                             new GridCoordinate(7, 7),
                             new GridCoordinate(8, 7),
                             new GridCoordinate(9, 7)],
                game.maskedPlayersState.shipStates.find { it.ship == Ship.Carrier }.shipGridCells
        )
        game = move(P1G, TEST_PLAYER1, new GridCoordinate(8, 7))
        Assert.assertEquals 2, game.remainingMoves
        Assert.assertEquals "TEST PLAYER1 performed evasive maneuvers.", game.maskedPlayersState.actionLog[-1].description
        Assert.assertEquals 1, game.maskedPlayersState.evasiveManeuversRemaining
        Assert.assertEquals GridCellState.ObscuredHit, game.maskedPlayersState.opponentViews[TEST_PLAYER2.md5].get(7, 7)
        Assert.assertEquals GridCellState.KnownByHit, game.maskedPlayersState.opponentViews[TEST_PLAYER2.md5].get(7, 0)
        Assert.assertNotEquals([new GridCoordinate(5, 7),
                                new GridCoordinate(6, 7),
                                new GridCoordinate(7, 7),
                                new GridCoordinate(8, 7),
                                new GridCoordinate(9, 7),], game.maskedPlayersState.shipStates.find {
            it.ship == Ship.Carrier
        }.shipGridCells)


        Assert.assertEquals([new GridCoordinate(7, 0),
                             new GridCoordinate(8, 0),
                             new GridCoordinate(9, 0)],
                game.maskedPlayersState.shipStates.find { it.ship == Ship.Cruiser }.shipGridCells
        )
        game = move(P1G, TEST_PLAYER1, new GridCoordinate(7, 0))
        //Assert.assertEquals "TEST PLAYER1 performed evasive maneuvers."
        Assert.assertNotEquals([new GridCoordinate(7, 0),
                                new GridCoordinate(8, 0),
                                new GridCoordinate(9, 0),], game.maskedPlayersState.shipStates.find {
            it.ship == Ship.Cruiser
        }.shipGridCells)
        Assert.assertEquals GamePhase.Playing, game.gamePhase
        Assert.assertEquals GridCellState.ObscuredHit, game.maskedPlayersState.opponentViews[TEST_PLAYER2.md5].get(7, 7)
        Assert.assertEquals GridCellState.ObscuredHit, game.maskedPlayersState.opponentViews[TEST_PLAYER2.md5].get(7, 0)

        Assert.assertEquals 0, game.playersScore[TEST_PLAYER1.md5]
        Assert.assertEquals 0, game.maskedPlayersState.evasiveManeuversRemaining
        Assert.assertNotEquals TEST_PLAYER1.md5, game.currentPlayer
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
                        GameFeature.CruiseMissileDisabled,
                        GameFeature.PerShip
                ] as Set,
                players: [TEST_PLAYER2.md5, TEST_PLAYER3.md5, TEST_PLAYER1.md5]
        ))
        Assert.assertNotNull game
        def P3G = createGameTarget(P3, game)
        def P1G = createGameTarget(createPlayerAPITarget(TEST_PLAYER1), game)
        def P2G = createGameTarget(createPlayerAPITarget(TEST_PLAYER2), game)

        acceptGame(P1G)
        acceptGame(P2G)
        setup(P3G, P3POSITIONS)
        setup(P1G, P1POSITIONS)
        setup(P2G, P2POSITIONS)

        //  Force turn and order
        TBGame dbGame = gameRepository.findById(new ObjectId(game.id)).get()
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
        Assert.assertEquals 10, game.playersScore[TEST_PLAYER1.md5]

        fire(P2G, TEST_PLAYER3, new GridCoordinate(1, 14))
        fire(P2G, TEST_PLAYER3, new GridCoordinate(0, 14))
        fire(P2G, TEST_PLAYER3, new GridCoordinate(2, 14))
        fire(P2G, TEST_PLAYER3, new GridCoordinate(3, 14))
        game = fire(P2G, TEST_PLAYER3, new GridCoordinate(4, 14))
        Assert.assertEquals 7, game.playersScore[TEST_PLAYER2.md5]

        fire(P3G, TEST_PLAYER1, new GridCoordinate(12, 12))
        fire(P3G, TEST_PLAYER2, new GridCoordinate(7, 8))
        game = fire(P3G, TEST_PLAYER2, new GridCoordinate(7, 7))
        Assert.assertEquals 1, game.playersScore[TEST_PLAYER3.md5]

        fire(P1G, TEST_PLAYER3, new GridCoordinate(1, 0))
        fire(P1G, TEST_PLAYER3, new GridCoordinate(1, 1))
        fire(P1G, TEST_PLAYER3, new GridCoordinate(1, 2))
        fire(P1G, TEST_PLAYER3, new GridCoordinate(1, 3))
        game = fire(P1G, TEST_PLAYER3, new GridCoordinate(1, 5))
        Assert.assertEquals 19, game.playersScore[TEST_PLAYER1.md5]

        fire(P2G, TEST_PLAYER3, new GridCoordinate(2, 0))
        fire(P2G, TEST_PLAYER3, new GridCoordinate(2, 1))
        fire(P2G, TEST_PLAYER3, new GridCoordinate(2, 2))
        fire(P2G, TEST_PLAYER3, new GridCoordinate(2, 3))
        game = fire(P2G, TEST_PLAYER3, new GridCoordinate(2, 4))
        Assert.assertEquals 15, game.playersScore[TEST_PLAYER2.md5]

        game = fire(P3G, TEST_PLAYER2, new GridCoordinate(7, 9))
        Assert.assertEquals 7, game.playersScore[TEST_PLAYER3.md5]

        fire(P1G, TEST_PLAYER3, new GridCoordinate(3, 2))
        fire(P1G, TEST_PLAYER3, new GridCoordinate(3, 3))
        game = fire(P1G, TEST_PLAYER3, new GridCoordinate(3, 4))
        Assert.assertEquals 27, game.playersScore[TEST_PLAYER1.md5]
        Assert.assertFalse game.playersAlive[TEST_PLAYER3.md5]
        Assert.assertTrue game.playersAlive[TEST_PLAYER2.md5]
        Assert.assertTrue game.playersAlive[TEST_PLAYER1.md5]
        spy(P1G, TEST_PLAYER2, new GridCoordinate(2, 2))

        fire(P2G, TEST_PLAYER1, new GridCoordinate(7, 7))
        fire(P2G, TEST_PLAYER1, new GridCoordinate(7, 8))
        fire(P2G, TEST_PLAYER1, new GridCoordinate(7, 6))
        game = fire(P2G, TEST_PLAYER1, new GridCoordinate(6, 7))
        Assert.assertEquals 17, game.playersScore[TEST_PLAYER2.md5]

        fire(P1G, TEST_PLAYER2, new GridCoordinate(14, 14))
        fire(P1G, TEST_PLAYER2, new GridCoordinate(14, 13))
        game = fire(P1G, TEST_PLAYER2, new GridCoordinate(14, 12))
        Assert.assertEquals 35, game.playersScore[TEST_PLAYER1.md5]
        repair(P1G, TEST_PLAYER1, new GridCoordinate(7, 7))

        fire(P2G, TEST_PLAYER1, new GridCoordinate(7, 7))
        fire(P2G, TEST_PLAYER1, new GridCoordinate(6, 7))
        game = fire(P2G, TEST_PLAYER1, new GridCoordinate(5, 7))
        Assert.assertEquals 20, game.playersScore[TEST_PLAYER2.md5]

        fire(P1G, TEST_PLAYER2, new GridCoordinate(0, 0))
        fire(P1G, TEST_PLAYER2, new GridCoordinate(0, 1))
        fire(P1G, TEST_PLAYER2, new GridCoordinate(0, 2))
        fire(P1G, TEST_PLAYER2, new GridCoordinate(0, 3))
        game = fire(P1G, TEST_PLAYER2, new GridCoordinate(0, 4))
        Assert.assertEquals 45, game.playersScore[TEST_PLAYER1.md5]

        fire(P2G, TEST_PLAYER1, new GridCoordinate(8, 7))
        game = fire(P2G, TEST_PLAYER1, new GridCoordinate(9, 7))
        Assert.assertEquals 27, game.playersScore[TEST_PLAYER2.md5]

        fire(P1G, TEST_PLAYER2, new GridCoordinate(1, 13))
        fire(P1G, TEST_PLAYER2, new GridCoordinate(0, 14))
        fire(P1G, TEST_PLAYER2, new GridCoordinate(0, 13))
        game = fire(P1G, TEST_PLAYER2, new GridCoordinate(0, 12))
        Assert.assertEquals 53, game.playersScore[TEST_PLAYER1.md5]

        game = fire(P2G, TEST_PLAYER1, new GridCoordinate(10, 7))
        Assert.assertEquals 27, game.playersScore[TEST_PLAYER2.md5]

        fire(P1G, TEST_PLAYER2, new GridCoordinate(14, 0))
        fire(P1G, TEST_PLAYER2, new GridCoordinate(14, 1))
        fire(P1G, TEST_PLAYER2, new GridCoordinate(14, 2))
        game = fire(P1G, TEST_PLAYER2, new GridCoordinate(14, 3))
        Assert.assertEquals GamePhase.RoundOver, game.gamePhase
        Assert.assertFalse game.playersAlive[TEST_PLAYER2.md5]
        Assert.assertTrue game.playersAlive[TEST_PLAYER1.md5]
        Assert.assertEquals 72, game.playersScore[TEST_PLAYER1.md5]
        Assert.assertEquals "TEST PLAYER1 defeated all challengers!", game.maskedPlayersState.actionLog[-1].description
        Assert.assertEquals TEST_PLAYER1.md5, game.winningPlayer

        TBMaskedGame newGame = rematchGame(P1G)
        Assert.assertEquals GamePhase.Challenged, newGame.gamePhase
        Assert.assertNotEquals game.id, newGame.id
        P2G = createGameTarget(createPlayerAPITarget(TEST_PLAYER2), newGame)
        newGame = rejectGame(P2G)
        Assert.assertEquals GamePhase.Declined, newGame.gamePhase
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
        Assert.assertNotNull response
        Assert.assertEquals 409, response.statusInfo.statusCode

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
                                GameFeature.PerShip,
                                GameFeature.CruiseMissileDisabled
                        ] as Set,
                        players: [TEST_PLAYER2.md5, TEST_PLAYER3.md5, TEST_PLAYER1.md5]
                ),
                MediaType.APPLICATION_JSON)


        response = P3.path("new")
                .request(MediaType.APPLICATION_JSON)
                .post(entity)
        Assert.assertNotNull response
        Assert.assertEquals 409, response.statusInfo.statusCode
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

    Class<TBGame> internalGameClass() {
        return TBGame.class
    }

    TBGame newGame() {
        return new TBGame()
    }

    AbstractGameRepository gameRepository() {
        return gameRepository
    }

    @Override
    void testGetMultiplayerGames() {
        //  Do nothing - tested in other ways
    }
}
