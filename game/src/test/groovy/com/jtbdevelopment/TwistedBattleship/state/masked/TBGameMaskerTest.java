package com.jtbdevelopment.TwistedBattleship.state.masked;

import com.jtbdevelopment.TwistedBattleship.state.GameFeature;
import com.jtbdevelopment.TwistedBattleship.state.TBActionLogEntry;
import com.jtbdevelopment.TwistedBattleship.state.TBGame;
import com.jtbdevelopment.TwistedBattleship.state.TBPlayerState;
import com.jtbdevelopment.TwistedBattleship.state.grid.ConsolidateGridViews;
import com.jtbdevelopment.TwistedBattleship.state.grid.Grid;
import com.jtbdevelopment.TwistedBattleship.state.grid.GridCellState;
import com.jtbdevelopment.TwistedBattleship.state.ships.Ship;
import com.jtbdevelopment.TwistedBattleship.state.ships.ShipState;
import com.jtbdevelopment.games.mongo.MongoGameCoreTestCase;
import com.jtbdevelopment.games.players.Player;
import com.jtbdevelopment.games.state.GamePhase;
import org.bson.types.ObjectId;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;

import java.time.Instant;
import java.util.*;

import static org.junit.Assert.*;

/**
 * Date: 4/2/15
 * Time: 6:48 PM
 */
public class TBGameMaskerTest extends MongoGameCoreTestCase {
    private ConsolidateGridViews consolidateGridViews = Mockito.mock(ConsolidateGridViews.class);
    private TBGameMasker masker = new TBGameMasker();

    @Test
    public void testMaskingGame() {
        TBGame game = new TBGame();
        Map<ObjectId, TBPlayerState> map = new HashMap<>();
        TBPlayerState state = new TBPlayerState();
        TBActionLogEntry entry0 = new TBActionLogEntry();


        TBActionLogEntry entry1 = new TBActionLogEntry();


        Map<ObjectId, Grid> map1 = new HashMap<>(2);
        map1.put(PTWO.getId(), new Grid(10));
        map1.put(PTHREE.getId(), new Grid(10));

        Map<ObjectId, Grid> map2 = new HashMap<>(2);
        map2.put(PTWO.getId(), new Grid(10));
        map2.put(PTHREE.getId(), new Grid(10));
        state.setStartingShips(Arrays.asList(Ship.values()));
        state.setScoreFromHits(10);
        state.setScoreFromLiving(20);
        state.setScoreFromSinks(40);
        state.setSpysRemaining(1);
        state.setCruiseMissilesRemaining(5);
        state.setEcmsRemaining(2);
        state.setEvasiveManeuversRemaining(3);
        state.setEmergencyRepairsRemaining(4);
        entry0.setDescription("One");
        entry0.setActionType(TBActionLogEntry.TBActionType.Fired);
        entry1.setDescription("Two");
        entry1.setActionType(TBActionLogEntry.TBActionType.DamagedByECM);
        state.setActionLog(Arrays.asList(entry0, entry1));
        state.setOpponentViews(map1);
        state.setOpponentGrids(map2);
        state.setSetup(true);
        state.setShipStates(Arrays.asList(new ShipState(Ship.Battleship, new TreeSet<>()), new ShipState(Ship.Destroyer, new TreeSet<>()), new ShipState(Ship.Submarine, new TreeSet<>()), new ShipState(Ship.Carrier, new TreeSet<>()), new ShipState(Ship.Cruiser, new TreeSet<>())));
        map.put(PONE.getId(), state);


        state = new TBPlayerState();
        state.setStartingShips(Arrays.asList(Ship.values()));
        state.setScoreFromLiving(30);
        state.setScoreFromSinks(10);
        state.setShipStates(Arrays.asList(new ShipState(Ship.Battleship, 0, new ArrayList<>(), new ArrayList<>()), new ShipState(Ship.Cruiser, 0, new ArrayList<>(), new ArrayList<>())));
        map.put(PTWO.getId(), state);

        state = new TBPlayerState();
        state.setScoreFromHits(10);
        state.setScoreFromSinks(10);
        state.setStartingShips(Arrays.asList(Ship.values()));
        map.put(PTHREE.getId(), state);

        game.setId(new ObjectId());
        game.setRematchTimestamp(Instant.now());
        game.setGamePhase(GamePhase.Playing);
        game.setFeatures(new HashSet<>(Collections.singletonList(GameFeature.Grid10x10)));
        game.setGridSize(10);
        game.setStartingShips(Arrays.asList(Ship.values()));
        game.setPlayers((Arrays.asList(PONE, PTWO, PTHREE)));
        game.setCurrentPlayer(PTHREE.getId());
        game.setMovesForSpecials(-2);
        game.setRemainingMoves(4);
        game.setInitiatingPlayer(PTHREE.getId());
        game.setPlayerDetails(map);
        game.getPlayerDetails().get(PONE.getId()).getOpponentGrids().get(PTWO.getId()).set(4, 0, GridCellState.KnownByHit);
        game.getPlayerDetails().get(PONE.getId()).getOpponentGrids().get(PTHREE.getId()).set(4, 1, GridCellState.KnownByMiss);
        game.getPlayerDetails().get(PONE.getId()).getOpponentViews().get(PTWO.getId()).set(5, 0, GridCellState.KnownByHit);
        game.getPlayerDetails().get(PONE.getId()).getOpponentViews().get(PTHREE.getId()).set(5, 1, GridCellState.KnownByMiss);

        game.getPlayerDetails().get(PONE.getId()).getOpponentViews().get(PTWO.getId()).set(5, 2, GridCellState.Unknown);
        game.getPlayerDetails().get(PONE.getId()).getOpponentViews().get(PTHREE.getId()).set(5, 2, GridCellState.KnownShip);
        game.getPlayerDetails().get(PONE.getId()).getOpponentViews().get(PTWO.getId()).set(5, 3, GridCellState.KnownShip);
        game.getPlayerDetails().get(PONE.getId()).getOpponentViews().get(PTHREE.getId()).set(5, 3, GridCellState.KnownByHit);
        game.getPlayerDetails().get(PONE.getId()).getOpponentViews().get(PTWO.getId()).set(5, 4, GridCellState.KnownByHit);
        game.getPlayerDetails().get(PONE.getId()).getOpponentViews().get(PTHREE.getId()).set(5, 4, GridCellState.KnownByRehit);

        Grid consolidated = new Grid(1);
        masker.setConsolidateGridViews(consolidateGridViews);
        Mockito.when(consolidateGridViews.createConsolidatedView(
                Matchers.eq(game),
                Matchers.eq(new HashSet<>(game.getPlayerDetails().get(PONE.getId()).getOpponentViews().values())))).thenReturn(consolidated);

        final TBMaskedGame maskedGame = masker.maskGameForPlayer(game, PONE);
        assertNotNull(maskedGame);
        assertEquals(game.getGridSize(), maskedGame.getGridSize());
        assertEquals(game.getMovesForSpecials(), maskedGame.getMovesForSpecials());
        assertEquals(game.getStartingShips(), maskedGame.getStartingShips());
        Map<String, Boolean> map3 = new HashMap<>(3);
        map3.put(PONE.getMd5(), true);
        map3.put(PTWO.getMd5(), false);
        map3.put(PTHREE.getMd5(), false);
        assertEquals(map3, maskedGame.getPlayersAlive());
        Map<String, Integer> map4 = new HashMap<>(3);
        map4.put(PONE.getMd5(), 70);
        map4.put(PTWO.getMd5(), 40);
        map4.put(PTHREE.getMd5(), 20);
        assertEquals(map4, maskedGame.getPlayersScore());
        Map<String, Boolean> map5 = new HashMap<>(3);
        map5.put(PONE.getMd5(), true);
        map5.put(PTWO.getMd5(), false);
        map5.put(PTHREE.getMd5(), false);
        assertEquals(map5, maskedGame.getPlayersSetup());

        TBPlayerState playerState = game.getPlayerDetails().get(PONE.getId());
        assertEquals(playerState.getShipStates(), maskedGame.getMaskedPlayersState().getShipStates());
        assertEquals(playerState.getStartingShips(), maskedGame.getMaskedPlayersState().getStartingShips());
        assertEquals(playerState.getAlive(), maskedGame.getMaskedPlayersState().isAlive());
        assertEquals(playerState.getSetup(), maskedGame.getMaskedPlayersState().isSetup());
        assertEquals(playerState.getTotalScore(), maskedGame.getMaskedPlayersState().getTotalScore());
        assertEquals(playerState.getActiveShipsRemaining(), maskedGame.getMaskedPlayersState().getActiveShipsRemaining());
        assertEquals(playerState.getSpysRemaining(), maskedGame.getMaskedPlayersState().getSpysRemaining());
        assertEquals(playerState.getCruiseMissilesRemaining(), maskedGame.getMaskedPlayersState().getCruiseMissilesRemaining());
        assertEquals(playerState.getEvasiveManeuversRemaining(), maskedGame.getMaskedPlayersState().getEvasiveManeuversRemaining());
        assertEquals(playerState.getEcmsRemaining(), maskedGame.getMaskedPlayersState().getEcmsRemaining());
        assertEquals(playerState.getEmergencyRepairsRemaining(), maskedGame.getMaskedPlayersState().getEmergencyRepairsRemaining());
        assertEquals(playerState.getShipStates(), maskedGame.getMaskedPlayersState().getShipStates());
        assertEquals(2, maskedGame.getMaskedPlayersState().getActionLog().size());
        assertEquals(playerState.getActionLog().get(0).getDescription(), maskedGame.getMaskedPlayersState().getActionLog().get(0).getDescription());
        assertEquals(playerState.getActionLog().get(1).getDescription(), maskedGame.getMaskedPlayersState().getActionLog().get(1).getDescription());
        assertEquals(playerState.getActionLog().get(0).getActionType(), maskedGame.getMaskedPlayersState().getActionLog().get(0).getActionType());
        assertEquals(playerState.getActionLog().get(1).getActionType(), maskedGame.getMaskedPlayersState().getActionLog().get(1).getActionType());
        assertEquals(playerState.getActionLog().get(0).getTimestamp().toEpochMilli(), maskedGame.getMaskedPlayersState().getActionLog().get(0).getTimestamp());
        assertEquals(playerState.getActionLog().get(1).getTimestamp().toEpochMilli(), maskedGame.getMaskedPlayersState().getActionLog().get(1).getTimestamp());

        assertEquals(2, maskedGame.getMaskedPlayersState().getOpponentViews().size());
        assertEquals(2, maskedGame.getMaskedPlayersState().getOpponentGrids().size());
        assertEquals(4, maskedGame.getRemainingMoves());
        assertEquals(PTHREE.getMd5(), maskedGame.getCurrentPlayer());
        playerState.getOpponentGrids().forEach((id, grid) -> {
            assertNotNull(grid);
            Player<ObjectId> player = game.getPlayers().stream().filter(p -> p.getId().equals(id)).findFirst().get();
            assertEquals(grid, maskedGame.getMaskedPlayersState().getOpponentGrids().get(player.getMd5()));
        });
        playerState.getOpponentViews().forEach((id, grid) -> {
            assertNotNull(grid);
            Player<ObjectId> player = game.getPlayers().stream().filter(p -> p.getId().equals(id)).findFirst().get();
            assertEquals(grid, maskedGame.getMaskedPlayersState().getOpponentViews().get(player.getMd5()));
        });
        assertSame(consolidated, maskedGame.getMaskedPlayersState().getConsolidatedOpponentView());
        assertEquals(PONE.getMd5(), maskedGame.getWinningPlayer());
    }
}
