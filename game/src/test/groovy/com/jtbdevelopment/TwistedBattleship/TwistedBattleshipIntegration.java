package com.jtbdevelopment.TwistedBattleship;

import com.jtbdevelopment.TwistedBattleship.dao.GameRepository;
import com.jtbdevelopment.TwistedBattleship.player.TBPlayerAttributes;
import com.jtbdevelopment.TwistedBattleship.rest.GameFeatureInfo;
import com.jtbdevelopment.TwistedBattleship.rest.ShipInfo;
import com.jtbdevelopment.TwistedBattleship.rest.Target;
import com.jtbdevelopment.TwistedBattleship.rest.services.messages.FeaturesAndPlayers;
import com.jtbdevelopment.TwistedBattleship.rest.services.messages.ShipAndCoordinates;
import com.jtbdevelopment.TwistedBattleship.state.GameFeature;
import com.jtbdevelopment.TwistedBattleship.state.TBActionLogEntry;
import com.jtbdevelopment.TwistedBattleship.state.TBGame;
import com.jtbdevelopment.TwistedBattleship.state.grid.Grid;
import com.jtbdevelopment.TwistedBattleship.state.grid.GridCellState;
import com.jtbdevelopment.TwistedBattleship.state.grid.GridCircleUtil;
import com.jtbdevelopment.TwistedBattleship.state.grid.GridCoordinate;
import com.jtbdevelopment.TwistedBattleship.state.masked.TBMaskedActionLogEntry;
import com.jtbdevelopment.TwistedBattleship.state.masked.TBMaskedGame;
import com.jtbdevelopment.TwistedBattleship.state.ships.Ship;
import com.jtbdevelopment.TwistedBattleship.state.ships.ShipState;
import com.jtbdevelopment.core.hazelcast.caching.HazelcastCacheManager;
import com.jtbdevelopment.games.dao.AbstractGameRepository;
import com.jtbdevelopment.games.dev.utilities.integrationtesting.AbstractGameIntegration;
import com.jtbdevelopment.games.mongo.players.MongoManualPlayer;
import com.jtbdevelopment.games.mongo.players.MongoPlayer;
import com.jtbdevelopment.games.players.Player;
import com.jtbdevelopment.games.state.GamePhase;
import com.jtbdevelopment.games.state.PlayerState;
import groovy.lang.Closure;
import org.bson.types.ObjectId;
import org.codehaus.groovy.runtime.DefaultGroovyMethods;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Date: 4/26/15
 * Time: 10:36 AM
 */
public class TwistedBattleshipIntegration extends AbstractGameIntegration<TBGame, TBMaskedGame> {
    private static final List<ShipAndCoordinates> P1POSITIONS;
    private static final List<ShipAndCoordinates> P2POSITIONS;
    private static final List<ShipAndCoordinates> P3POSITIONS;
    private static final FeaturesAndPlayers STANDARD_PLAYERS_AND_FEATURES;
    private static HazelcastCacheManager cacheManager;
    private static GameRepository gameRepository;

    static {
        ShipAndCoordinates coordinates = new ShipAndCoordinates();
        coordinates.setShip(Ship.Carrier);
        coordinates.setCoordinates(Arrays.asList(new GridCoordinate(5, 7), new GridCoordinate(6, 7), new GridCoordinate(7, 7), new GridCoordinate(8, 7), new GridCoordinate(9, 7)));
        ShipAndCoordinates coordinates1 = new ShipAndCoordinates();
        coordinates1.setShip(Ship.Battleship);
        coordinates1.setCoordinates(Arrays.asList(new GridCoordinate(0, 6), new GridCoordinate(0, 7), new GridCoordinate(0, 8), new GridCoordinate(0, 9)));

        ShipAndCoordinates coordinates2 = new ShipAndCoordinates();
        coordinates2.setShip(Ship.Cruiser);
        coordinates2.setCoordinates(Arrays.asList(new GridCoordinate(7, 0), new GridCoordinate(8, 0), new GridCoordinate(9, 0)));

        ShipAndCoordinates coordinates3 = new ShipAndCoordinates();
        coordinates3.setShip(Ship.Submarine);
        coordinates3.setCoordinates(Arrays.asList(new GridCoordinate(7, 14), new GridCoordinate(8, 14), new GridCoordinate(9, 14)));

        ShipAndCoordinates coordinates4 = new ShipAndCoordinates();
        coordinates4.setShip(Ship.Destroyer);
        coordinates4.setCoordinates(Arrays.asList(new GridCoordinate(14, 7), new GridCoordinate(14, 8)));

        P1POSITIONS = Arrays.asList(coordinates, coordinates1, coordinates2, coordinates3, coordinates4);
    }

    static {
        ShipAndCoordinates coordinates = new ShipAndCoordinates();
        coordinates.setShip(Ship.Carrier);
        coordinates.setCoordinates(Arrays.asList(new GridCoordinate(0, 0), new GridCoordinate(0, 1), new GridCoordinate(0, 2), new GridCoordinate(0, 3), new GridCoordinate(0, 4)));
        ShipAndCoordinates coordinates1 = new ShipAndCoordinates();
        coordinates1.setShip(Ship.Battleship);
        coordinates1.setCoordinates(Arrays.asList(new GridCoordinate(14, 0), new GridCoordinate(14, 1), new GridCoordinate(14, 2), new GridCoordinate(14, 3)));

        ShipAndCoordinates coordinates2 = new ShipAndCoordinates();
        coordinates2.setShip(Ship.Cruiser);
        coordinates2.setCoordinates(Arrays.asList(new GridCoordinate(0, 14), new GridCoordinate(0, 13), new GridCoordinate(0, 12)));


        ShipAndCoordinates coordinates3 = new ShipAndCoordinates();
        coordinates3.setShip(Ship.Submarine);
        coordinates3.setCoordinates(Arrays.asList(new GridCoordinate(14, 14), new GridCoordinate(14, 13), new GridCoordinate(14, 12)));


        ShipAndCoordinates coordinates4 = new ShipAndCoordinates();
        coordinates4.setShip(Ship.Destroyer);
        coordinates4.setCoordinates(Arrays.asList(new GridCoordinate(7, 8), new GridCoordinate(7, 9)));

        P2POSITIONS = Arrays.asList(coordinates, coordinates1, coordinates2, coordinates3, coordinates4);
    }

    static {
        ShipAndCoordinates coordinates = new ShipAndCoordinates();
        coordinates.setShip(Ship.Carrier);
        coordinates.setCoordinates(Arrays.asList(new GridCoordinate(0, 0), new GridCoordinate(0, 1), new GridCoordinate(0, 2), new GridCoordinate(0, 3), new GridCoordinate(0, 4)));

        ShipAndCoordinates coordinates1 = new ShipAndCoordinates();
        coordinates1.setShip(Ship.Battleship);
        coordinates1.setCoordinates(Arrays.asList(new GridCoordinate(1, 0), new GridCoordinate(1, 1), new GridCoordinate(1, 2), new GridCoordinate(1, 3)));


        ShipAndCoordinates coordinates2 = new ShipAndCoordinates();
        coordinates2.setShip(Ship.Cruiser);
        coordinates2.setCoordinates(Arrays.asList(new GridCoordinate(2, 1), new GridCoordinate(2, 2), new GridCoordinate(2, 3)));

        ShipAndCoordinates coordinates3 = new ShipAndCoordinates();
        coordinates3.setShip(Ship.Submarine);
        coordinates3.setCoordinates(Arrays.asList(new GridCoordinate(3, 4), new GridCoordinate(3, 2), new GridCoordinate(3, 3)));

        ShipAndCoordinates coordinates4 = new ShipAndCoordinates();
        coordinates4.setShip(Ship.Destroyer);
        coordinates4.setCoordinates(Arrays.asList(new GridCoordinate(0, 14), new GridCoordinate(1, 14)));

        P3POSITIONS = Arrays.asList(coordinates, coordinates1, coordinates2, coordinates3, coordinates4);
    }

    static {
        FeaturesAndPlayers players = new FeaturesAndPlayers();

        players.setFeatures(new HashSet<>(Arrays.asList(GameFeature.Grid15x15, GameFeature.SharedIntel, GameFeature.ECMEnabled, GameFeature.EREnabled, GameFeature.EMEnabled, GameFeature.CruiseMissileEnabled, GameFeature.SpyEnabled, GameFeature.PerShip)));
        players.setPlayers(Arrays.asList(TEST_PLAYER2.getMd5(), TEST_PLAYER3.getMd5(), TEST_PLAYER1.getMd5()));
        STANDARD_PLAYERS_AND_FEATURES = players;
    }

    @BeforeClass
    public static void setup() {
        cacheManager = applicationContext.getBean(HazelcastCacheManager.class);
        gameRepository = applicationContext.getBean(GameRepository.class);
    }

    @Test
    public void testPlayerTheme() {
        ((TBPlayerAttributes) TEST_PLAYER2.getGameSpecificPlayerAttributes()).setAvailableThemes(new HashSet<>(Arrays.asList("default", "new-theme")));
        playerRepository.save(TEST_PLAYER2);
        Client client = AbstractGameIntegration.createConnection(TEST_PLAYER2);
        MongoManualPlayer p = client.target(PLAYER_API).request(MediaType.APPLICATION_JSON).get(MongoManualPlayer.class);
        assertEquals("default-theme", ((TBPlayerAttributes) p.getGameSpecificPlayerAttributes()).getTheme());
        MongoPlayer updated = client.target(PLAYER_API).path("changeTheme").path("new-theme").request(MediaType.APPLICATION_JSON).put(EMPTY_PUT_POST, MongoManualPlayer.class);
        assertEquals("new-theme", ((TBPlayerAttributes) updated.getGameSpecificPlayerAttributes()).getTheme());
    }

    @Test
    public void testGetCircleSizes() {
        WebTarget client = AbstractGameIntegration.createAPITarget(TEST_PLAYER2);
        Map<Integer, Set<GridCoordinate>> sizes = client.path("circles").request(MediaType.APPLICATION_JSON_TYPE).get(new GenericType<Map<Integer, Set<GridCoordinate>>>() {
        });
        assertEquals(GridCircleUtil.CIRCLE_OFFSETS, sizes);
    }

    @Test
    public void testGetCellStates() {
        WebTarget client = AbstractGameIntegration.createAPITarget(TEST_PLAYER2);
        List<GridCellState> sizes = client.path("states").request(MediaType.APPLICATION_JSON_TYPE).get(new GenericType<List<GridCellState>>() {
        });
        assertEquals(DefaultGroovyMethods.toList(GridCellState.values()), sizes);
    }

    @Test
    public void testGetShips() {
        WebTarget client = AbstractGameIntegration.createAPITarget(TEST_PLAYER3);
        List<ShipInfo> ships = client.path("ships").request(MediaType.APPLICATION_JSON_TYPE).get(new GenericType<List<ShipInfo>>() {
        });
        assertEquals(DefaultGroovyMethods.collect(Ship.values(), new Closure<ShipInfo>(this, this) {
            public ShipInfo doCall(Ship it) {
                return new ShipInfo(it);
            }

        }), ships);
    }

    @Test
    public void testGetFeatures() {
        WebTarget client = AbstractGameIntegration.createAPITarget(TEST_PLAYER2);
        List<GameFeatureInfo> features = client.path("features").request(MediaType.APPLICATION_JSON_TYPE).get(new GenericType<List<GameFeatureInfo>>() {
        });
        assertEquals(features, Arrays.asList(new GameFeatureInfo(GameFeature.GridSize, new ArrayList<GameFeatureInfo.Detail>(Arrays.asList(new GameFeatureInfo.Detail(GameFeature.Grid10x10), new GameFeatureInfo.Detail(GameFeature.Grid15x15), new GameFeatureInfo.Detail(GameFeature.Grid20x20)))), new GameFeatureInfo(GameFeature.ActionsPerTurn, new ArrayList<GameFeatureInfo.Detail>(Arrays.asList(new GameFeatureInfo.Detail(GameFeature.PerShip), new GameFeatureInfo.Detail(GameFeature.Single)))), new GameFeatureInfo(GameFeature.FogOfWar, new ArrayList<GameFeatureInfo.Detail>(Arrays.asList(new GameFeatureInfo.Detail(GameFeature.SharedIntel), new GameFeatureInfo.Detail(GameFeature.IsolatedIntel)))), new GameFeatureInfo(GameFeature.StartingShips, new ArrayList<GameFeatureInfo.Detail>(Arrays.asList(new GameFeatureInfo.Detail(GameFeature.StandardShips), new GameFeatureInfo.Detail(GameFeature.AllCarriers), new GameFeatureInfo.Detail(GameFeature.AllDestroyers), new GameFeatureInfo.Detail(GameFeature.AllSubmarines), new GameFeatureInfo.Detail(GameFeature.AllCruisers), new GameFeatureInfo.Detail(GameFeature.AllBattleships)))), new GameFeatureInfo(GameFeature.ECM, new ArrayList<GameFeatureInfo.Detail>(Arrays.asList(new GameFeatureInfo.Detail(GameFeature.ECMEnabled), new GameFeatureInfo.Detail(GameFeature.ECMDisabled)))), new GameFeatureInfo(GameFeature.EvasiveManeuvers, new ArrayList<GameFeatureInfo.Detail>(Arrays.asList(new GameFeatureInfo.Detail(GameFeature.EMEnabled), new GameFeatureInfo.Detail(GameFeature.EMDisabled)))), new GameFeatureInfo(GameFeature.EmergencyRepairs, new ArrayList<GameFeatureInfo.Detail>(Arrays.asList(new GameFeatureInfo.Detail(GameFeature.EREnabled), new GameFeatureInfo.Detail(GameFeature.ERDisabled)))), new GameFeatureInfo(GameFeature.Spy, new ArrayList<GameFeatureInfo.Detail>(Arrays.asList(new GameFeatureInfo.Detail(GameFeature.SpyEnabled), new GameFeatureInfo.Detail(GameFeature.SpyDisabled)))), new GameFeatureInfo(GameFeature.CruiseMissile, new ArrayList<GameFeatureInfo.Detail>(Arrays.asList(new GameFeatureInfo.Detail(GameFeature.CruiseMissileEnabled), new GameFeatureInfo.Detail(GameFeature.CruiseMissileDisabled))))));
    }

    @Test
    public void testCreateNewGame() {
        WebTarget P3 = AbstractGameIntegration.createPlayerAPITarget(TEST_PLAYER3);
        FeaturesAndPlayers players = new FeaturesAndPlayers();
        players.setFeatures(new HashSet<>(Arrays.asList(GameFeature.Grid20x20, GameFeature.IsolatedIntel, GameFeature.ECMEnabled, GameFeature.EREnabled, GameFeature.EMDisabled, GameFeature.SpyDisabled, GameFeature.PerShip, GameFeature.CruiseMissileDisabled)));
        players.setPlayers(Arrays.asList(TEST_PLAYER2.getMd5(), TEST_PLAYER3.getMd5(), TEST_PLAYER1.getMd5()));
        TBMaskedGame game = newGame(P3, players);
        assertNotNull(game);
        assertEquals(20, game.getGridSize());
        assertEquals(2, game.getMovesForSpecials());
        LinkedHashMap<String, PlayerState> map = new LinkedHashMap<String, PlayerState>(3);
        map.put(TEST_PLAYER1.getMd5(), PlayerState.Pending);
        map.put(TEST_PLAYER2.getMd5(), PlayerState.Pending);
        map.put(TEST_PLAYER3.getMd5(), PlayerState.Accepted);
        assertEquals(map, game.getPlayerStates());
        LinkedHashMap<String, Boolean> map1 = new LinkedHashMap<String, Boolean>(3);
        map1.put(TEST_PLAYER1.getMd5(), true);
        map1.put(TEST_PLAYER2.getMd5(), true);
        map1.put(TEST_PLAYER3.getMd5(), true);
        assertEquals(map1, game.getPlayersAlive());
        LinkedHashMap<String, Boolean> map2 = new LinkedHashMap<String, Boolean>(3);
        map2.put(TEST_PLAYER1.getMd5(), false);
        map2.put(TEST_PLAYER2.getMd5(), false);
        map2.put(TEST_PLAYER3.getMd5(), false);
        assertEquals(map2, game.getPlayersSetup());
        LinkedHashMap<String, Integer> map3 = new LinkedHashMap<String, Integer>(3);
        map3.put(TEST_PLAYER1.getMd5(), 0);
        map3.put(TEST_PLAYER2.getMd5(), 0);
        map3.put(TEST_PLAYER3.getMd5(), 0);
        assertEquals(map3, game.getPlayersScore());
        assertEquals(5, game.getMaskedPlayersState().getActiveShipsRemaining());
        LinkedHashMap<String, Grid> map4 = new LinkedHashMap<String, Grid>(2);
        map4.put(TEST_PLAYER1.getMd5(), new Grid(20));
        map4.put(TEST_PLAYER2.getMd5(), new Grid(20));
        assertEquals(map4, game.getMaskedPlayersState().getOpponentGrids());
        LinkedHashMap<String, Grid> map5 = new LinkedHashMap<String, Grid>(2);
        map5.put(TEST_PLAYER1.getMd5(), new Grid(20));
        map5.put(TEST_PLAYER2.getMd5(), new Grid(20));
        assertEquals(map5, game.getMaskedPlayersState().getOpponentViews());
        assertEquals(0, game.getMaskedPlayersState().getSpysRemaining());
        assertEquals(2, game.getMaskedPlayersState().getEcmsRemaining());
        assertEquals(2, game.getMaskedPlayersState().getEmergencyRepairsRemaining());
        assertEquals(0, game.getMaskedPlayersState().getEvasiveManeuversRemaining());
        assertEquals(GamePhase.Challenged, game.getGamePhase());

        //  Clear cache and force a load from db to confirm full round trip
        DefaultGroovyMethods.each(cacheManager.getCacheNames(), new Closure<Object>(this, this) {
            void doCall(String it) {
                cacheManager.getCache(it).clear();
            }

            public void doCall() {
                doCall(null);
            }

        });

        game = getGame(AbstractGameIntegration.createGameTarget(P3, game));
        LinkedHashMap<String, Integer> map6 = new LinkedHashMap<String, Integer>(3);
        map6.put(TEST_PLAYER1.getMd5(), 0);
        map6.put(TEST_PLAYER2.getMd5(), 0);
        map6.put(TEST_PLAYER3.getMd5(), 0);
        assertEquals(map6, game.getPlayersScore());
        assertEquals(5, game.getMaskedPlayersState().getActiveShipsRemaining());
        LinkedHashMap<String, Grid> map7 = new LinkedHashMap<String, Grid>(2);
        map7.put(TEST_PLAYER1.getMd5(), new Grid(20));
        map7.put(TEST_PLAYER2.getMd5(), new Grid(20));
        assertEquals(map7, game.getMaskedPlayersState().getOpponentGrids());
        LinkedHashMap<String, Grid> map8 = new LinkedHashMap<String, Grid>(2);
        map8.put(TEST_PLAYER1.getMd5(), new Grid(20));
        map8.put(TEST_PLAYER2.getMd5(), new Grid(20));
        assertEquals(map8, game.getMaskedPlayersState().getOpponentViews());
        assertEquals(0, game.getMaskedPlayersState().getSpysRemaining());
        assertEquals(2, game.getMaskedPlayersState().getEcmsRemaining());
        assertEquals(2, game.getMaskedPlayersState().getEmergencyRepairsRemaining());
        assertEquals(0, game.getMaskedPlayersState().getEvasiveManeuversRemaining());
        assertEquals(GamePhase.Challenged, game.getGamePhase());
    }

    @Test
    public void testCreateAndRejectNewGame() {
        WebTarget P3 = AbstractGameIntegration.createPlayerAPITarget(TEST_PLAYER3);
        TBMaskedGame game = newGame(P3, STANDARD_PLAYERS_AND_FEATURES);
        WebTarget P1G = AbstractGameIntegration.createGameTarget(AbstractGameIntegration.createPlayerAPITarget(TEST_PLAYER1), game);
        assertNotNull(game);
        LinkedHashMap<String, PlayerState> map = new LinkedHashMap<String, PlayerState>(3);
        map.put(TEST_PLAYER1.getMd5(), PlayerState.Pending);
        map.put(TEST_PLAYER2.getMd5(), PlayerState.Pending);
        map.put(TEST_PLAYER3.getMd5(), PlayerState.Accepted);
        assertEquals(map, game.getPlayerStates());
        assertEquals(GamePhase.Challenged, game.getGamePhase());

        game = rejectGame(P1G);
        LinkedHashMap<String, PlayerState> map1 = new LinkedHashMap<String, PlayerState>(3);
        map1.put(TEST_PLAYER1.getMd5(), PlayerState.Rejected);
        map1.put(TEST_PLAYER2.getMd5(), PlayerState.Pending);
        map1.put(TEST_PLAYER3.getMd5(), PlayerState.Accepted);
        assertEquals(map1, game.getPlayerStates());
        assertEquals(GamePhase.Declined, game.getGamePhase());
    }

    @Test
    public void testCreateAndQuitNewGame() {
        WebTarget P3 = AbstractGameIntegration.createPlayerAPITarget(TEST_PLAYER3);
        TBMaskedGame game = newGame(P3, STANDARD_PLAYERS_AND_FEATURES);
        WebTarget P1G = AbstractGameIntegration.createGameTarget(AbstractGameIntegration.createPlayerAPITarget(TEST_PLAYER1), game);
        WebTarget P2G = AbstractGameIntegration.createGameTarget(AbstractGameIntegration.createPlayerAPITarget(TEST_PLAYER2), game);
        acceptGame(P1G);
        acceptGame(P2G);
        game = quitGame(P1G);
        LinkedHashMap<String, PlayerState> map = new LinkedHashMap<String, PlayerState>(3);
        map.put(TEST_PLAYER1.getMd5(), PlayerState.Quit);
        map.put(TEST_PLAYER2.getMd5(), PlayerState.Accepted);
        map.put(TEST_PLAYER3.getMd5(), PlayerState.Accepted);
        assertEquals(map, game.getPlayerStates());
    }

    @Test
    public void testSetupGame() {
        WebTarget P3 = AbstractGameIntegration.createPlayerAPITarget(TEST_PLAYER3);
        TBMaskedGame game = newGame(P3, STANDARD_PLAYERS_AND_FEATURES);
        assertNotNull(game);
        WebTarget P3G = AbstractGameIntegration.createGameTarget(P3, game);
        WebTarget P1G = AbstractGameIntegration.createGameTarget(AbstractGameIntegration.createPlayerAPITarget(TEST_PLAYER1), game);
        WebTarget P2G = AbstractGameIntegration.createGameTarget(AbstractGameIntegration.createPlayerAPITarget(TEST_PLAYER2), game);


        game = acceptGame(P1G);
        assertNotNull(game);
        LinkedHashMap<String, PlayerState> map = new LinkedHashMap<String, PlayerState>(3);
        map.put(TEST_PLAYER1.getMd5(), PlayerState.Accepted);
        map.put(TEST_PLAYER2.getMd5(), PlayerState.Pending);
        map.put(TEST_PLAYER3.getMd5(), PlayerState.Accepted);
        assertEquals(map, game.getPlayerStates());
        assertEquals(GamePhase.Challenged, game.getGamePhase());

        game = acceptGame(P2G);
        assertNotNull(game);
        LinkedHashMap<String, PlayerState> map1 = new LinkedHashMap<String, PlayerState>(3);
        map1.put(TEST_PLAYER1.getMd5(), PlayerState.Accepted);
        map1.put(TEST_PLAYER2.getMd5(), PlayerState.Accepted);
        map1.put(TEST_PLAYER3.getMd5(), PlayerState.Accepted);
        assertEquals(map1, game.getPlayerStates());
        LinkedHashMap<String, Boolean> map2 = new LinkedHashMap<String, Boolean>(3);
        map2.put(TEST_PLAYER1.getMd5(), false);
        map2.put(TEST_PLAYER2.getMd5(), false);
        map2.put(TEST_PLAYER3.getMd5(), false);
        assertEquals(map2, game.getPlayersSetup());
        assertEquals(GamePhase.Setup, game.getGamePhase());

        game = setup(P3G, P3POSITIONS);
        LinkedHashMap<String, Boolean> map3 = new LinkedHashMap<String, Boolean>(3);
        map3.put(TEST_PLAYER1.getMd5(), false);
        map3.put(TEST_PLAYER2.getMd5(), false);
        map3.put(TEST_PLAYER3.getMd5(), true);
        assertEquals(map3, game.getPlayersSetup());
        assertEquals(GamePhase.Setup, game.getGamePhase());

        game = setup(P1G, P1POSITIONS);
        LinkedHashMap<String, Boolean> map4 = new LinkedHashMap<String, Boolean>(3);
        map4.put(TEST_PLAYER1.getMd5(), true);
        map4.put(TEST_PLAYER2.getMd5(), false);
        map4.put(TEST_PLAYER3.getMd5(), true);
        assertEquals(map4, game.getPlayersSetup());
        assertEquals(GamePhase.Setup, game.getGamePhase());

        game = setup(P2G, P2POSITIONS);
        LinkedHashMap<String, Boolean> map5 = new LinkedHashMap<String, Boolean>(3);
        map5.put(TEST_PLAYER1.getMd5(), true);
        map5.put(TEST_PLAYER2.getMd5(), true);
        map5.put(TEST_PLAYER3.getMd5(), true);
        assertEquals(map5, game.getPlayersSetup());
        assertEquals(GamePhase.Playing, game.getGamePhase());
        assertEquals(5, game.getRemainingMoves());
        Assert.assertTrue(new ArrayList<String>(Arrays.asList(TEST_PLAYER2.getMd5(), TEST_PLAYER1.getMd5(), TEST_PLAYER3.getMd5())).contains(game.getCurrentPlayer()));
        assertEquals(TBActionLogEntry.TBActionType.Begin, game.getMaskedPlayersState().getActionLog().get(0).getActionType());
        assertEquals("Game ready to play.", game.getMaskedPlayersState().getActionLog().get(0).getDescription());
        Assert.assertNotEquals(0, game.getMaskedPlayersState().getActionLog().get(0).getTimestamp());
    }

    @Test
    public void testFireForTurnInGame() {
        WebTarget P3 = AbstractGameIntegration.createPlayerAPITarget(TEST_PLAYER3);
        TBMaskedGame game = newGame(P3, STANDARD_PLAYERS_AND_FEATURES);
        assertNotNull(game);
        WebTarget P3G = AbstractGameIntegration.createGameTarget(P3, game);
        WebTarget P1G = AbstractGameIntegration.createGameTarget(AbstractGameIntegration.createPlayerAPITarget(TEST_PLAYER1), game);
        WebTarget P2G = AbstractGameIntegration.createGameTarget(AbstractGameIntegration.createPlayerAPITarget(TEST_PLAYER2), game);

        acceptGame(P1G);
        acceptGame(P2G);
        setup(P3G, P3POSITIONS);
        setup(P1G, P1POSITIONS);
        setup(P2G, P2POSITIONS);

        //  Force turn to P2
        TBGame dbGame = gameRepository.findById(new ObjectId(game.getId())).get();
        dbGame.setCurrentPlayer(TEST_PLAYER2.getId());
        gameRepository.save(dbGame);
        DefaultGroovyMethods.each(cacheManager.getCacheNames(), new Closure<Object>(this, this) {
            void doCall(String it) {
                cacheManager.getCache(it).clear();
            }

            public void doCall() {
                doCall(null);
            }

        });

        game = fire(P2G, TEST_PLAYER1, new GridCoordinate(7, 7));
        assertEquals(GamePhase.Playing, game.getGamePhase());
        assertEquals("You fired at TEST PLAYER1 (7,7) and hit!", getLastEntry(game.getMaskedPlayersState().getActionLog()).getDescription());
        assertEquals(TBActionLogEntry.TBActionType.Fired, getLastEntry(game.getMaskedPlayersState().getActionLog()).getActionType());
        assertEquals(1, (int) game.getPlayersScore().get(TEST_PLAYER2.getMd5()));
        assertEquals(4, game.getRemainingMoves());
        assertEquals(TEST_PLAYER2.getMd5(), game.getCurrentPlayer());
        game = fire(P2G, TEST_PLAYER1, new GridCoordinate(7, 8));
        assertEquals("You fired at TEST PLAYER1 (7,8) and missed.", getLastEntry(game.getMaskedPlayersState().getActionLog()).getDescription());
        game = fire(P2G, TEST_PLAYER1, new GridCoordinate(7, 6));
        assertEquals("You fired at TEST PLAYER1 (7,6) and missed.", getLastEntry(game.getMaskedPlayersState().getActionLog()).getDescription());
        game = fire(P2G, TEST_PLAYER1, new GridCoordinate(8, 7));
        assertEquals("You fired at TEST PLAYER1 (8,7) and hit!", getLastEntry(game.getMaskedPlayersState().getActionLog()).getDescription());
        assertEquals(1, game.getRemainingMoves());
        game = fire(P2G, TEST_PLAYER1, new GridCoordinate(9, 7));
        assertEquals("You fired at TEST PLAYER1 (9,7) and hit!", getLastEntry(game.getMaskedPlayersState().getActionLog()).getDescription());
        assertEquals(3, (int) game.getPlayersScore().get(TEST_PLAYER2.getMd5()));
        assertEquals(5, game.getRemainingMoves());
        Assert.assertNotEquals(TEST_PLAYER2.getMd5(), game.getCurrentPlayer());
    }

    @Test
    public void testCruiseMissileForTurnInGame() {
        WebTarget P3 = AbstractGameIntegration.createPlayerAPITarget(TEST_PLAYER3);
        TBMaskedGame game = newGame(P3, STANDARD_PLAYERS_AND_FEATURES);
        assertNotNull(game);
        WebTarget P3G = AbstractGameIntegration.createGameTarget(P3, game);
        WebTarget P1G = AbstractGameIntegration.createGameTarget(AbstractGameIntegration.createPlayerAPITarget(TEST_PLAYER1), game);
        WebTarget P2G = AbstractGameIntegration.createGameTarget(AbstractGameIntegration.createPlayerAPITarget(TEST_PLAYER2), game);

        acceptGame(P1G);
        acceptGame(P2G);
        setup(P3G, P3POSITIONS);
        setup(P1G, P1POSITIONS);
        setup(P2G, P2POSITIONS);

        //  Force turn to P2
        TBGame dbGame = gameRepository.findById(new ObjectId(game.getId())).get();
        dbGame.setCurrentPlayer(TEST_PLAYER2.getId());
        gameRepository.save(dbGame);
        DefaultGroovyMethods.each(cacheManager.getCacheNames(), new Closure<Object>(this, this) {
            void doCall(String it) {
                cacheManager.getCache(it).clear();
            }

            public void doCall() {
                doCall(null);
            }

        });

        fire(P2G, TEST_PLAYER1, new GridCoordinate(7, 7));
        game = cruiseMissile(P2G, TEST_PLAYER1, new GridCoordinate(7, 7));
        assertEquals(GamePhase.Playing, game.getGamePhase());
        assertEquals("You fired a cruise missile at TEST PLAYER1 (7,7) and hit!", getNthLastEntry(game.getMaskedPlayersState().getActionLog(), 7).getDescription());
        assertEquals("You fired at TEST PLAYER1 (5,7) and hit!", getNthLastEntry(game.getMaskedPlayersState().getActionLog(), 6).getDescription());
        assertEquals("You fired at TEST PLAYER1 (6,7) and hit!", getNthLastEntry(game.getMaskedPlayersState().getActionLog(), 5).getDescription());
        assertEquals("You fired at TEST PLAYER1 (7,7) and hit an already damaged area!", getNthLastEntry(game.getMaskedPlayersState().getActionLog(), 4).getDescription());
        assertEquals("You fired at TEST PLAYER1 (8,7) and hit!", getNthLastEntry(game.getMaskedPlayersState().getActionLog(), 3).getDescription());
        assertEquals("You fired at TEST PLAYER1 (9,7) and hit!", getNthLastEntry(game.getMaskedPlayersState().getActionLog(), 2).getDescription());
        assertEquals("You sunk a Aircraft Carrier for TEST PLAYER1!", getLastEntry(game.getMaskedPlayersState().getActionLog()).getDescription());
        assertEquals(TBActionLogEntry.TBActionType.CruiseMissile, getNthLastEntry(game.getMaskedPlayersState().getActionLog(), 7).getActionType());
        assertEquals(10, (int) game.getPlayersScore().get(TEST_PLAYER2.getMd5()));
        assertEquals(2, game.getRemainingMoves());
        assertEquals(0, game.getMaskedPlayersState().getCruiseMissilesRemaining());
        assertEquals(TEST_PLAYER2.getMd5(), game.getCurrentPlayer());
        game = fire(P2G, TEST_PLAYER1, new GridCoordinate(7, 8));
        assertEquals("You fired at TEST PLAYER1 (7,8) and missed.", getLastEntry(game.getMaskedPlayersState().getActionLog()).getDescription());
        assertEquals(1, game.getRemainingMoves());
        game = fire(P2G, TEST_PLAYER1, new GridCoordinate(9, 7));
        assertEquals("You fired at TEST PLAYER1 (9,7) and hit an already damaged area!", getLastEntry(game.getMaskedPlayersState().getActionLog()).getDescription());
        assertEquals(10, (int) game.getPlayersScore().get(TEST_PLAYER2.getMd5()));
        assertEquals(5, game.getRemainingMoves());
        Assert.assertNotEquals(TEST_PLAYER2.getMd5(), game.getCurrentPlayer());
    }

    @Test
    public void testSpyForTurnInGame() {
        WebTarget P3 = AbstractGameIntegration.createPlayerAPITarget(TEST_PLAYER3);
        TBMaskedGame game = newGame(P3, STANDARD_PLAYERS_AND_FEATURES);
        assertNotNull(game);
        WebTarget P3G = AbstractGameIntegration.createGameTarget(P3, game);
        WebTarget P1G = AbstractGameIntegration.createGameTarget(AbstractGameIntegration.createPlayerAPITarget(TEST_PLAYER1), game);
        WebTarget P2G = AbstractGameIntegration.createGameTarget(AbstractGameIntegration.createPlayerAPITarget(TEST_PLAYER2), game);

        acceptGame(P1G);
        acceptGame(P2G);
        setup(P3G, P3POSITIONS);
        setup(P1G, P1POSITIONS);
        setup(P2G, P2POSITIONS);

        //  Force turn to P2
        TBGame dbGame = gameRepository.findById(new ObjectId(game.getId())).get();
        dbGame.setCurrentPlayer(TEST_PLAYER2.getId());
        gameRepository.save(dbGame);
        DefaultGroovyMethods.each(cacheManager.getCacheNames(), new Closure<Object>(this, this) {
            void doCall(String it) {
                cacheManager.getCache(it).clear();
            }

            public void doCall() {
                doCall(null);
            }

        });

        fire(P2G, TEST_PLAYER1, new GridCoordinate(7, 7));

        game = spy(P2G, TEST_PLAYER1, new GridCoordinate(7, 8));
        assertEquals(GamePhase.Playing, game.getGamePhase());
        assertEquals("You spied on TEST PLAYER1 at (7,8).", getLastEntry(game.getMaskedPlayersState().getActionLog()).getDescription());
        assertEquals(2, game.getRemainingMoves());
        assertEquals(1, game.getMaskedPlayersState().getSpysRemaining());
        assertEquals(TEST_PLAYER2.getMd5(), game.getCurrentPlayer());
        game = spy(P2G, TEST_PLAYER1, new GridCoordinate(2, 6));
        assertEquals("You spied on TEST PLAYER1 at (2,6).", getLastEntry(game.getMaskedPlayersState().getActionLog()).getDescription());
        assertEquals(1, (int) game.getPlayersScore().get(TEST_PLAYER2.getMd5()));
        assertEquals(0, game.getMaskedPlayersState().getSpysRemaining());
        Assert.assertNotEquals(TEST_PLAYER2.getMd5(), game.getCurrentPlayer());

        //Sample checks, not full
        assertEquals(GridCellState.KnownByHit, game.getMaskedPlayersState().getOpponentGrids().get(TEST_PLAYER1.getMd5()).get(7, 7));
        assertEquals(GridCellState.KnownShip, game.getMaskedPlayersState().getOpponentGrids().get(TEST_PLAYER1.getMd5()).get(8, 7));
        assertEquals(GridCellState.KnownShip, game.getMaskedPlayersState().getOpponentGrids().get(TEST_PLAYER1.getMd5()).get(0, 6));
        assertEquals(GridCellState.KnownEmpty, game.getMaskedPlayersState().getOpponentGrids().get(TEST_PLAYER1.getMd5()).get(7, 8));
        game = getGame(P1G);
        assertEquals(GridCellState.KnownByHit, game.getMaskedPlayersState().getOpponentViews().get(TEST_PLAYER2.getMd5()).get(7, 7));
        assertEquals(GridCellState.KnownShip, game.getMaskedPlayersState().getOpponentViews().get(TEST_PLAYER2.getMd5()).get(8, 7));
        assertEquals(GridCellState.KnownShip, game.getMaskedPlayersState().getOpponentViews().get(TEST_PLAYER2.getMd5()).get(0, 6));
        assertEquals(GridCellState.KnownEmpty, game.getMaskedPlayersState().getOpponentViews().get(TEST_PLAYER2.getMd5()).get(7, 8));
        assertEquals(GridCellState.KnownByHit, game.getMaskedPlayersState().getConsolidatedOpponentView().get(7, 7));
        assertEquals(GridCellState.KnownShip, game.getMaskedPlayersState().getConsolidatedOpponentView().get(8, 7));
        assertEquals(GridCellState.KnownShip, game.getMaskedPlayersState().getConsolidatedOpponentView().get(0, 6));
        assertEquals(GridCellState.KnownEmpty, game.getMaskedPlayersState().getConsolidatedOpponentView().get(7, 8));
    }

    @Test
    public void testRepairForTurnInGame() {
        WebTarget P3 = AbstractGameIntegration.createPlayerAPITarget(TEST_PLAYER3);
        TBMaskedGame game = newGame(P3, STANDARD_PLAYERS_AND_FEATURES);
        assertNotNull(game);
        WebTarget P3G = AbstractGameIntegration.createGameTarget(P3, game);
        WebTarget P1G = AbstractGameIntegration.createGameTarget(AbstractGameIntegration.createPlayerAPITarget(TEST_PLAYER1), game);
        WebTarget P2G = AbstractGameIntegration.createGameTarget(AbstractGameIntegration.createPlayerAPITarget(TEST_PLAYER2), game);

        acceptGame(P1G);
        acceptGame(P2G);
        setup(P3G, P3POSITIONS);
        setup(P1G, P1POSITIONS);
        setup(P2G, P2POSITIONS);

        //  Force turn to P2
        TBGame dbGame = gameRepository.findById(new ObjectId(game.getId())).get();
        dbGame.setCurrentPlayer(TEST_PLAYER2.getId());
        gameRepository.save(dbGame);
        DefaultGroovyMethods.each(cacheManager.getCacheNames(), new Closure<Object>(this, this) {
            void doCall(String it) {
                cacheManager.getCache(it).clear();
            }

            public void doCall() {
                doCall(null);
            }

        });

        fire(P2G, TEST_PLAYER1, new GridCoordinate(7, 7));
        game = fire(P2G, TEST_PLAYER1, new GridCoordinate(7, 0));

        //  Force turn to P1
        dbGame = gameRepository.findById(new ObjectId(game.getId())).get();
        dbGame.setCurrentPlayer(TEST_PLAYER1.getId());
        dbGame.setRemainingMoves(5);
        gameRepository.save(dbGame);
        DefaultGroovyMethods.each(cacheManager.getCacheNames(), new Closure<Object>(this, this) {
            void doCall(String it) {
                cacheManager.getCache(it).clear();
            }

            public void doCall() {
                doCall(null);
            }

        });

        fire(P1G, TEST_PLAYER2, new GridCoordinate(7, 0));
        game = repair(P1G, TEST_PLAYER1, new GridCoordinate(8, 7));
        assertEquals(2, game.getRemainingMoves());
        assertEquals("TEST PLAYER1 repaired their Aircraft Carrier.", getLastEntry(game.getMaskedPlayersState().getActionLog()).getDescription());
        assertEquals(5, DefaultGroovyMethods.find(game.getMaskedPlayersState().getShipStates(), new Closure<Boolean>(this, this) {
            Boolean doCall(ShipState it) {
                return it.getShip().equals(Ship.Carrier);
            }

            public Boolean doCall() {
                return doCall(null);
            }

        }).getHealthRemaining());
        assertEquals(new ArrayList<Boolean>(Arrays.asList(false, false, false, false, false)), DefaultGroovyMethods.find(game.getMaskedPlayersState().getShipStates(), new Closure<Boolean>(this, this) {
            Boolean doCall(ShipState it) {
                return it.getShip().equals(Ship.Carrier);
            }

            public Boolean doCall() {
                return doCall(null);
            }

        }).getShipSegmentHit());
        assertEquals(GridCellState.KnownShip, game.getMaskedPlayersState().getConsolidatedOpponentView().get(7, 7));
        assertEquals(1, game.getMaskedPlayersState().getEmergencyRepairsRemaining());
        assertEquals(2, game.getRemainingMoves());


        game = repair(P1G, TEST_PLAYER1, new GridCoordinate(8, 0));
        assertEquals("TEST PLAYER1 repaired their Cruiser.", getLastEntry(game.getMaskedPlayersState().getActionLog()).getDescription());
        assertEquals(3, DefaultGroovyMethods.find(game.getMaskedPlayersState().getShipStates(), new Closure<Boolean>(this, this) {
            Boolean doCall(ShipState it) {
                return it.getShip().equals(Ship.Cruiser);
            }

            public Boolean doCall() {
                return doCall(null);
            }

        }).getHealthRemaining());
        assertEquals(new ArrayList<Boolean>(Arrays.asList(false, false, false)), DefaultGroovyMethods.find(game.getMaskedPlayersState().getShipStates(), new Closure<Boolean>(this, this) {
            Boolean doCall(ShipState it) {
                return it.getShip().equals(Ship.Cruiser);
            }

            public Boolean doCall() {
                return doCall(null);
            }

        }).getShipSegmentHit());
        assertEquals(GridCellState.KnownShip, game.getMaskedPlayersState().getConsolidatedOpponentView().get(7, 0));
        assertEquals(GamePhase.Playing, game.getGamePhase());
        assertEquals(0, (int) game.getPlayersScore().get(TEST_PLAYER1.getMd5()));
        assertEquals(0, game.getMaskedPlayersState().getEmergencyRepairsRemaining());
        Assert.assertNotEquals(TEST_PLAYER1.getMd5(), game.getCurrentPlayer());
    }

    @Test
    public void testECMForTurnInGame() {
        WebTarget P3 = AbstractGameIntegration.createPlayerAPITarget(TEST_PLAYER3);
        TBMaskedGame game = newGame(P3, STANDARD_PLAYERS_AND_FEATURES);
        assertNotNull(game);
        WebTarget P3G = AbstractGameIntegration.createGameTarget(P3, game);
        WebTarget P1G = AbstractGameIntegration.createGameTarget(AbstractGameIntegration.createPlayerAPITarget(TEST_PLAYER1), game);
        WebTarget P2G = AbstractGameIntegration.createGameTarget(AbstractGameIntegration.createPlayerAPITarget(TEST_PLAYER2), game);

        acceptGame(P1G);
        acceptGame(P2G);
        setup(P3G, P3POSITIONS);
        setup(P1G, P1POSITIONS);
        setup(P2G, P2POSITIONS);

        //  Force turn to P2
        TBGame dbGame = gameRepository.findById(new ObjectId(game.getId())).get();
        dbGame.setCurrentPlayer(TEST_PLAYER2.getId());
        gameRepository.save(dbGame);
        DefaultGroovyMethods.each(cacheManager.getCacheNames(), new Closure<Object>(this, this) {
            void doCall(String it) {
                cacheManager.getCache(it).clear();
            }

            public void doCall() {
                doCall(null);
            }

        });

        fire(P2G, TEST_PLAYER1, new GridCoordinate(7, 7));
        game = fire(P2G, TEST_PLAYER1, new GridCoordinate(7, 0));

        //  Force turn to P1
        dbGame = gameRepository.findById(new ObjectId(game.getId())).get();
        dbGame.setCurrentPlayer(TEST_PLAYER1.getId());
        dbGame.setRemainingMoves(5);
        gameRepository.save(dbGame);
        DefaultGroovyMethods.each(cacheManager.getCacheNames(), new Closure<Object>(this, this) {
            void doCall(String it) {
                cacheManager.getCache(it).clear();
            }

            public void doCall() {
                doCall(null);
            }

        });

        game = fire(P1G, TEST_PLAYER2, new GridCoordinate(7, 0));
        assertEquals(GridCellState.KnownByHit, game.getMaskedPlayersState().getOpponentViews().get(TEST_PLAYER2.getMd5()).get(7, 7));
        assertEquals(GridCellState.KnownByHit, game.getMaskedPlayersState().getOpponentViews().get(TEST_PLAYER2.getMd5()).get(7, 0));
        game = ecm(P1G, TEST_PLAYER1, new GridCoordinate(8, 7));
        assertEquals(2, game.getRemainingMoves());
        assertEquals("TEST PLAYER1 deployed an ECM.", getLastEntry(game.getMaskedPlayersState().getActionLog()).getDescription());
        assertEquals(1, game.getMaskedPlayersState().getEcmsRemaining());
        assertEquals(GridCellState.HiddenHit, game.getMaskedPlayersState().getOpponentViews().get(TEST_PLAYER2.getMd5()).get(7, 7));
        assertEquals(GridCellState.KnownByHit, game.getMaskedPlayersState().getOpponentViews().get(TEST_PLAYER2.getMd5()).get(7, 0));


        game = ecm(P1G, TEST_PLAYER1, new GridCoordinate(6, 0));
        assertEquals("TEST PLAYER1 deployed an ECM.", getLastEntry(game.getMaskedPlayersState().getActionLog()).getDescription());
        assertEquals(GamePhase.Playing, game.getGamePhase());
        assertEquals(GridCellState.HiddenHit, game.getMaskedPlayersState().getOpponentViews().get(TEST_PLAYER2.getMd5()).get(7, 7));
        assertEquals(GridCellState.HiddenHit, game.getMaskedPlayersState().getOpponentViews().get(TEST_PLAYER2.getMd5()).get(7, 0));
        assertEquals(0, (int) game.getPlayersScore().get(TEST_PLAYER1.getMd5()));
        assertEquals(0, game.getMaskedPlayersState().getEcmsRemaining());
        Assert.assertNotEquals(TEST_PLAYER1.getMd5(), game.getCurrentPlayer());
    }

    @Test
    public void testMoveForTurnInGame() {
        WebTarget P3 = AbstractGameIntegration.createPlayerAPITarget(TEST_PLAYER3);
        TBMaskedGame game = newGame(P3, STANDARD_PLAYERS_AND_FEATURES);
        assertNotNull(game);
        WebTarget P3G = AbstractGameIntegration.createGameTarget(P3, game);
        WebTarget P1G = AbstractGameIntegration.createGameTarget(AbstractGameIntegration.createPlayerAPITarget(TEST_PLAYER1), game);
        WebTarget P2G = AbstractGameIntegration.createGameTarget(AbstractGameIntegration.createPlayerAPITarget(TEST_PLAYER2), game);

        acceptGame(P1G);
        acceptGame(P2G);
        setup(P3G, P3POSITIONS);
        setup(P1G, P1POSITIONS);
        setup(P2G, P2POSITIONS);

        //  Force turn to P2
        TBGame dbGame = gameRepository.findById(new ObjectId(game.getId())).get();
        dbGame.setCurrentPlayer(TEST_PLAYER2.getId());
        gameRepository.save(dbGame);
        DefaultGroovyMethods.each(cacheManager.getCacheNames(), new Closure<Object>(this, this) {
            void doCall(String it) {
                cacheManager.getCache(it).clear();
            }

            public void doCall() {
                doCall(null);
            }

        });

        fire(P2G, TEST_PLAYER1, new GridCoordinate(7, 7));
        game = fire(P2G, TEST_PLAYER1, new GridCoordinate(7, 0));

        //  Force turn to P1
        dbGame = gameRepository.findById(new ObjectId(game.getId())).get();
        dbGame.setCurrentPlayer(TEST_PLAYER1.getId());
        dbGame.setRemainingMoves(5);
        gameRepository.save(dbGame);
        DefaultGroovyMethods.each(cacheManager.getCacheNames(), new Closure<Object>(this, this) {
            void doCall(String it) {
                cacheManager.getCache(it).clear();
            }

            public void doCall() {
                doCall(null);
            }

        });

        game = fire(P1G, TEST_PLAYER2, new GridCoordinate(7, 0));
        assertEquals(GridCellState.KnownByHit, game.getMaskedPlayersState().getOpponentViews().get(TEST_PLAYER2.getMd5()).get(7, 7));
        assertEquals(GridCellState.KnownByHit, game.getMaskedPlayersState().getOpponentViews().get(TEST_PLAYER2.getMd5()).get(7, 0));
        assertEquals(new ArrayList<GridCoordinate>(Arrays.asList(new GridCoordinate(5, 7), new GridCoordinate(6, 7), new GridCoordinate(7, 7), new GridCoordinate(8, 7), new GridCoordinate(9, 7))), DefaultGroovyMethods.find(game.getMaskedPlayersState().getShipStates(), new Closure<Boolean>(this, this) {
            Boolean doCall(ShipState it) {
                return it.getShip().equals(Ship.Carrier);
            }

            public Boolean doCall() {
                return doCall(null);
            }

        }).getShipGridCells());
        game = move(P1G, TEST_PLAYER1, new GridCoordinate(8, 7));
        assertEquals(2, game.getRemainingMoves());
        assertEquals("TEST PLAYER1 performed evasive maneuvers.", getLastEntry(game.getMaskedPlayersState().getActionLog()).getDescription());
        assertEquals(1, game.getMaskedPlayersState().getEvasiveManeuversRemaining());
        assertEquals(GridCellState.ObscuredHit, game.getMaskedPlayersState().getOpponentViews().get(TEST_PLAYER2.getMd5()).get(7, 7));
        assertEquals(GridCellState.KnownByHit, game.getMaskedPlayersState().getOpponentViews().get(TEST_PLAYER2.getMd5()).get(7, 0));
        Assert.assertNotEquals(new ArrayList<GridCoordinate>(Arrays.asList(new GridCoordinate(5, 7), new GridCoordinate(6, 7), new GridCoordinate(7, 7), new GridCoordinate(8, 7), new GridCoordinate(9, 7))), DefaultGroovyMethods.find(game.getMaskedPlayersState().getShipStates(), new Closure<Boolean>(this, this) {
            Boolean doCall(ShipState it) {
                return it.getShip().equals(Ship.Carrier);
            }

            public Boolean doCall() {
                return doCall(null);
            }

        }).getShipGridCells());


        assertEquals(new ArrayList<GridCoordinate>(Arrays.asList(new GridCoordinate(7, 0), new GridCoordinate(8, 0), new GridCoordinate(9, 0))), DefaultGroovyMethods.find(game.getMaskedPlayersState().getShipStates(), new Closure<Boolean>(this, this) {
            Boolean doCall(ShipState it) {
                return it.getShip().equals(Ship.Cruiser);
            }

            public Boolean doCall() {
                return doCall(null);
            }

        }).getShipGridCells());
        game = move(P1G, TEST_PLAYER1, new GridCoordinate(7, 0));
        //Assert.assertEquals "TEST PLAYER1 performed evasive maneuvers."
        Assert.assertNotEquals(new ArrayList<GridCoordinate>(Arrays.asList(new GridCoordinate(7, 0), new GridCoordinate(8, 0), new GridCoordinate(9, 0))), DefaultGroovyMethods.find(game.getMaskedPlayersState().getShipStates(), new Closure<Boolean>(this, this) {
            Boolean doCall(ShipState it) {
                return it.getShip().equals(Ship.Cruiser);
            }

            public Boolean doCall() {
                return doCall(null);
            }

        }).getShipGridCells());
        assertEquals(GamePhase.Playing, game.getGamePhase());
        assertEquals(GridCellState.ObscuredHit, game.getMaskedPlayersState().getOpponentViews().get(TEST_PLAYER2.getMd5()).get(7, 7));
        assertEquals(GridCellState.ObscuredHit, game.getMaskedPlayersState().getOpponentViews().get(TEST_PLAYER2.getMd5()).get(7, 0));

        assertEquals(0, (int) game.getPlayersScore().get(TEST_PLAYER1.getMd5()));
        assertEquals(0, game.getMaskedPlayersState().getEvasiveManeuversRemaining());
        Assert.assertNotEquals(TEST_PLAYER1.getMd5(), game.getCurrentPlayer());
    }

    @Test
    public void testCompleteSimpleGameThroughRematch() {
        WebTarget P3 = AbstractGameIntegration.createPlayerAPITarget(TEST_PLAYER3);
        FeaturesAndPlayers players = new FeaturesAndPlayers();
        players.setFeatures(new HashSet<>(Arrays.asList(GameFeature.Grid15x15, GameFeature.SharedIntel, GameFeature.ECMEnabled, GameFeature.EREnabled, GameFeature.EMEnabled, GameFeature.SpyEnabled, GameFeature.CruiseMissileDisabled, GameFeature.PerShip)));
        players.setPlayers(Arrays.asList(TEST_PLAYER2.getMd5(), TEST_PLAYER3.getMd5(), TEST_PLAYER1.getMd5()));
        TBMaskedGame game = newGame(P3, players);
        assertNotNull(game);
        WebTarget P3G = AbstractGameIntegration.createGameTarget(P3, game);
        WebTarget P1G = AbstractGameIntegration.createGameTarget(AbstractGameIntegration.createPlayerAPITarget(TEST_PLAYER1), game);
        WebTarget P2G = AbstractGameIntegration.createGameTarget(AbstractGameIntegration.createPlayerAPITarget(TEST_PLAYER2), game);

        acceptGame(P1G);
        acceptGame(P2G);
        setup(P3G, P3POSITIONS);
        setup(P1G, P1POSITIONS);
        setup(P2G, P2POSITIONS);

        //  Force turn and order
        TBGame dbGame = gameRepository.findById(new ObjectId(game.getId())).get();
        dbGame.setCurrentPlayer(TEST_PLAYER1.getId());
        dbGame.setPlayers(Arrays.asList(TEST_PLAYER1, TEST_PLAYER2, TEST_PLAYER3));
        gameRepository.save(dbGame);
        DefaultGroovyMethods.each(cacheManager.getCacheNames(), new Closure<Object>(this, this) {
            void doCall(String it) {
                cacheManager.getCache(it).clear();
            }

            public void doCall() {
                doCall(null);
            }

        });

        fire(P1G, TEST_PLAYER3, new GridCoordinate(0, 0));
        fire(P1G, TEST_PLAYER3, new GridCoordinate(0, 1));
        fire(P1G, TEST_PLAYER3, new GridCoordinate(0, 2));
        fire(P1G, TEST_PLAYER3, new GridCoordinate(0, 3));
        game = fire(P1G, TEST_PLAYER3, new GridCoordinate(0, 4));
        assertEquals(10, (int) game.getPlayersScore().get(TEST_PLAYER1.getMd5()));

        fire(P2G, TEST_PLAYER3, new GridCoordinate(1, 14));
        fire(P2G, TEST_PLAYER3, new GridCoordinate(0, 14));
        fire(P2G, TEST_PLAYER3, new GridCoordinate(2, 14));
        fire(P2G, TEST_PLAYER3, new GridCoordinate(3, 14));
        game = fire(P2G, TEST_PLAYER3, new GridCoordinate(4, 14));
        assertEquals(7, (int) game.getPlayersScore().get(TEST_PLAYER2.getMd5()));

        fire(P3G, TEST_PLAYER1, new GridCoordinate(12, 12));
        fire(P3G, TEST_PLAYER2, new GridCoordinate(7, 8));
        game = fire(P3G, TEST_PLAYER2, new GridCoordinate(7, 7));
        assertEquals(1, (int) game.getPlayersScore().get(TEST_PLAYER3.getMd5()));

        fire(P1G, TEST_PLAYER3, new GridCoordinate(1, 0));
        fire(P1G, TEST_PLAYER3, new GridCoordinate(1, 1));
        fire(P1G, TEST_PLAYER3, new GridCoordinate(1, 2));
        fire(P1G, TEST_PLAYER3, new GridCoordinate(1, 3));
        game = fire(P1G, TEST_PLAYER3, new GridCoordinate(1, 5));
        assertEquals(19, (int) game.getPlayersScore().get(TEST_PLAYER1.getMd5()));

        fire(P2G, TEST_PLAYER3, new GridCoordinate(2, 0));
        fire(P2G, TEST_PLAYER3, new GridCoordinate(2, 1));
        fire(P2G, TEST_PLAYER3, new GridCoordinate(2, 2));
        fire(P2G, TEST_PLAYER3, new GridCoordinate(2, 3));
        game = fire(P2G, TEST_PLAYER3, new GridCoordinate(2, 4));
        assertEquals(15, (int) game.getPlayersScore().get(TEST_PLAYER2.getMd5()));

        game = fire(P3G, TEST_PLAYER2, new GridCoordinate(7, 9));
        assertEquals(7, (int) game.getPlayersScore().get(TEST_PLAYER3.getMd5()));

        fire(P1G, TEST_PLAYER3, new GridCoordinate(3, 2));
        fire(P1G, TEST_PLAYER3, new GridCoordinate(3, 3));
        game = fire(P1G, TEST_PLAYER3, new GridCoordinate(3, 4));
        assertEquals(27, (int) game.getPlayersScore().get(TEST_PLAYER1.getMd5()));
        Assert.assertFalse(game.getPlayersAlive().get(TEST_PLAYER3.getMd5()));
        Assert.assertTrue(game.getPlayersAlive().get(TEST_PLAYER2.getMd5()));
        Assert.assertTrue(game.getPlayersAlive().get(TEST_PLAYER1.getMd5()));
        spy(P1G, TEST_PLAYER2, new GridCoordinate(2, 2));

        fire(P2G, TEST_PLAYER1, new GridCoordinate(7, 7));
        fire(P2G, TEST_PLAYER1, new GridCoordinate(7, 8));
        fire(P2G, TEST_PLAYER1, new GridCoordinate(7, 6));
        game = fire(P2G, TEST_PLAYER1, new GridCoordinate(6, 7));
        assertEquals(17, (int) game.getPlayersScore().get(TEST_PLAYER2.getMd5()));

        fire(P1G, TEST_PLAYER2, new GridCoordinate(14, 14));
        fire(P1G, TEST_PLAYER2, new GridCoordinate(14, 13));
        game = fire(P1G, TEST_PLAYER2, new GridCoordinate(14, 12));
        assertEquals(35, (int) game.getPlayersScore().get(TEST_PLAYER1.getMd5()));
        repair(P1G, TEST_PLAYER1, new GridCoordinate(7, 7));

        fire(P2G, TEST_PLAYER1, new GridCoordinate(7, 7));
        fire(P2G, TEST_PLAYER1, new GridCoordinate(6, 7));
        game = fire(P2G, TEST_PLAYER1, new GridCoordinate(5, 7));
        assertEquals(20, (int) game.getPlayersScore().get(TEST_PLAYER2.getMd5()));

        fire(P1G, TEST_PLAYER2, new GridCoordinate(0, 0));
        fire(P1G, TEST_PLAYER2, new GridCoordinate(0, 1));
        fire(P1G, TEST_PLAYER2, new GridCoordinate(0, 2));
        fire(P1G, TEST_PLAYER2, new GridCoordinate(0, 3));
        game = fire(P1G, TEST_PLAYER2, new GridCoordinate(0, 4));
        assertEquals(45, (int) game.getPlayersScore().get(TEST_PLAYER1.getMd5()));

        fire(P2G, TEST_PLAYER1, new GridCoordinate(8, 7));
        game = fire(P2G, TEST_PLAYER1, new GridCoordinate(9, 7));
        assertEquals(27, (int) game.getPlayersScore().get(TEST_PLAYER2.getMd5()));

        fire(P1G, TEST_PLAYER2, new GridCoordinate(1, 13));
        fire(P1G, TEST_PLAYER2, new GridCoordinate(0, 14));
        fire(P1G, TEST_PLAYER2, new GridCoordinate(0, 13));
        game = fire(P1G, TEST_PLAYER2, new GridCoordinate(0, 12));
        assertEquals(53, (int) game.getPlayersScore().get(TEST_PLAYER1.getMd5()));

        game = fire(P2G, TEST_PLAYER1, new GridCoordinate(10, 7));
        assertEquals(27, (int) game.getPlayersScore().get(TEST_PLAYER2.getMd5()));

        fire(P1G, TEST_PLAYER2, new GridCoordinate(14, 0));
        fire(P1G, TEST_PLAYER2, new GridCoordinate(14, 1));
        fire(P1G, TEST_PLAYER2, new GridCoordinate(14, 2));
        game = fire(P1G, TEST_PLAYER2, new GridCoordinate(14, 3));
        assertEquals(GamePhase.RoundOver, game.getGamePhase());
        Assert.assertFalse(game.getPlayersAlive().get(TEST_PLAYER2.getMd5()));
        Assert.assertTrue(game.getPlayersAlive().get(TEST_PLAYER1.getMd5()));
        assertEquals(72, (int) game.getPlayersScore().get(TEST_PLAYER1.getMd5()));
        assertEquals("TEST PLAYER1 defeated all challengers!", getLastEntry(game.getMaskedPlayersState().getActionLog()).getDescription());
        assertEquals(TEST_PLAYER1.getMd5(), game.getWinningPlayer());

        TBMaskedGame newGame = rematchGame(P1G);
        assertEquals(GamePhase.Challenged, newGame.getGamePhase());
        Assert.assertNotEquals(game.getId(), newGame.getId());
        P2G = AbstractGameIntegration.createGameTarget(AbstractGameIntegration.createPlayerAPITarget(TEST_PLAYER2), newGame);
        newGame = rejectGame(P2G);
        assertEquals(GamePhase.Declined, newGame.getGamePhase());
    }

    @Test
    public void testCreatingInvalidGames() {
        WebTarget P3 = AbstractGameIntegration.createConnection(TEST_PLAYER3).target(PLAYER_API);
        FeaturesAndPlayers players = new FeaturesAndPlayers();
        players.setFeatures(new HashSet<>(Arrays.asList(GameFeature.Grid20x20, GameFeature.IsolatedIntel, GameFeature.ECMEnabled, GameFeature.SpyDisabled, GameFeature.PerShip)));
        players.setPlayers(Arrays.asList(TEST_PLAYER2.getMd5(), TEST_PLAYER3.getMd5(), TEST_PLAYER1.getMd5()));
        Entity<FeaturesAndPlayers> entity = Entity.entity(players, MediaType.APPLICATION_JSON);
        Response response = P3.path("new").request(MediaType.APPLICATION_JSON).post(entity);
        assertNotNull(response);
        assertEquals(409, response.getStatusInfo().getStatusCode());

        FeaturesAndPlayers players1 = new FeaturesAndPlayers();
        players1.setFeatures(new HashSet<>(Arrays.asList(GameFeature.Grid20x20, GameFeature.IsolatedIntel, GameFeature.ECMEnabled, GameFeature.ECMDisabled, GameFeature.EREnabled, GameFeature.EMDisabled, GameFeature.SpyDisabled, GameFeature.PerShip, GameFeature.CruiseMissileDisabled)));
        players1.setPlayers(Arrays.asList(TEST_PLAYER2.getMd5(), TEST_PLAYER3.getMd5(), TEST_PLAYER1.getMd5()));
        entity = Entity.entity(players1, MediaType.APPLICATION_JSON);


        response = P3.path("new").request(MediaType.APPLICATION_JSON).post(entity);
        assertNotNull(response);
        assertEquals(409, response.getStatusInfo().getStatusCode());
    }

    private TBMaskedGame newGame(WebTarget target, FeaturesAndPlayers featuresAndPlayers) {
        Entity<FeaturesAndPlayers> entity = Entity.entity(featuresAndPlayers, MediaType.APPLICATION_JSON);
        return target.path("new").request(MediaType.APPLICATION_JSON).post(entity, returnedGameClass());
    }

    protected TBMaskedGame setup(final WebTarget target, final List<ShipAndCoordinates> positions) {
        Entity<List<ShipAndCoordinates>> placement = Entity.entity(positions, MediaType.APPLICATION_JSON);
        return target.path("setup").request(MediaType.APPLICATION_JSON).put(placement, returnedGameClass());
    }

    private TBMaskedGame fire(WebTarget target, Player player, GridCoordinate coordinate) {
        return makeMove(player, coordinate, target, "fire");
    }

    private TBMaskedGame cruiseMissile(WebTarget target, Player player, GridCoordinate coordinate) {
        return makeMove(player, coordinate, target, "missile");
    }

    private TBMaskedGame move(WebTarget target, Player player, GridCoordinate coordinate) {
        return makeMove(player, coordinate, target, "move");
    }

    private TBMaskedGame spy(WebTarget target, Player player, GridCoordinate coordinate) {
        return makeMove(player, coordinate, target, "spy");
    }

    private TBMaskedGame repair(WebTarget target, Player player, GridCoordinate coordinate) {
        return makeMove(player, coordinate, target, "repair");
    }

    private TBMaskedGame ecm(WebTarget target, Player player, GridCoordinate coordinate) {
        return makeMove(player, coordinate, target, "ecm");
    }

    private TBMaskedGame makeMove(Player player, GridCoordinate coordinate, WebTarget target, String command) {
        Target t = new Target();
        t.setPlayer(player.getMd5());
        t.setCoordinate(coordinate);
        Entity<Target> placement = Entity.entity(t, MediaType.APPLICATION_JSON);
        return target.path(command).request(MediaType.APPLICATION_JSON).put(placement, returnedGameClass());
    }

    @Override
    public Class<TBMaskedGame> returnedGameClass() {
        return TBMaskedGame.class;
    }

    public Class<TBGame> internalGameClass() {
        return TBGame.class;
    }

    public TBGame newGame() {
        return new TBGame();
    }

    public AbstractGameRepository gameRepository() {
        return gameRepository;
    }

    @Override
    public void testGetMultiplayerGames() {
        //  Do nothing - tested in other ways
    }

    private TBMaskedActionLogEntry getNthLastEntry(final List<TBMaskedActionLogEntry> entries, int offset) {
        return entries.get(entries.size() - offset);
    }

    private TBMaskedActionLogEntry getLastEntry(final List<TBMaskedActionLogEntry> entries) {
        return getNthLastEntry(entries, 1);
    }
}
