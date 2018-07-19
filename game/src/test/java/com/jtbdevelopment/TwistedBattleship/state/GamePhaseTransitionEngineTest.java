package com.jtbdevelopment.TwistedBattleship.state;

import com.jtbdevelopment.TwistedBattleship.TBSCoreTestCase;
import com.jtbdevelopment.TwistedBattleship.state.grid.Grid;
import com.jtbdevelopment.TwistedBattleship.state.grid.GridCellState;
import com.jtbdevelopment.TwistedBattleship.state.grid.GridCoordinate;
import com.jtbdevelopment.TwistedBattleship.state.ships.Ship;
import com.jtbdevelopment.TwistedBattleship.state.ships.ShipState;
import com.jtbdevelopment.games.state.GamePhase;
import com.jtbdevelopment.games.state.scoring.GameScorer;
import org.bson.types.ObjectId;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;

import static org.junit.Assert.assertEquals;

/**
 * Date: 4/22/15
 * Time: 9:12 PM
 */
public class GamePhaseTransitionEngineTest extends TBSCoreTestCase {

    private GameScorer<TBGame> gameScorer = Mockito.mock(GameScorer.class);
    private GamePhaseTransitionEngine engine = new GamePhaseTransitionEngine(gameScorer);

    @Test
    public void testEvaluateSetupPhaseWithAllPlayersSetup() {
        final TBGame game = new TBGame();
        game.setGamePhase(GamePhase.Setup);
        game.setStartingShips(Arrays.asList(Ship.values()));
        LinkedHashMap<ObjectId, TBPlayerState> map = new LinkedHashMap<>(3);
        TBPlayerState state = new TBPlayerState();
        state.setSetup(true);
        map.put(PONE.getId(), state);
        state = new TBPlayerState();
        state.setSetup(true);
        map.put(PFOUR.getId(), state);
        state = new TBPlayerState();
        state.setSetup(true);
        map.put(PTWO.getId(), state);
        game.setPlayerDetails(map);
        game.getPlayerDetails().forEach((p, s) -> s.setStartingShips(Arrays.asList(Ship.values())));
        TBGame result = engine.evaluateGame(game);
        assertEquals(GamePhase.Playing, result.getGamePhase());

        game.getPlayerDetails().values().forEach((ps) -> {
            TBActionLogEntry lastEntry = getLastEntry(ps.getActionLog());
            assertEquals(TBActionLogEntry.TBActionType.Begin, lastEntry.getActionType());
            assertEquals("Game ready to play.", lastEntry.getDescription());
        });
    }

    @Test
    public void testEvaluateSetupPhaseWithSomePlayersSetup() {
        TBGame game = new TBGame();
        game.setGamePhase(GamePhase.Setup);
        LinkedHashMap<ObjectId, TBPlayerState> map = new LinkedHashMap<>(3);
        TBPlayerState state = new TBPlayerState();
        state.setSetup(true);
        map.put(PONE.getId(), state);
        state = new TBPlayerState();
        state.setSetup(false);
        map.put(PFOUR.getId(), state);
        state = new TBPlayerState();
        state.setSetup(true);
        map.put(PTHREE.getId(), state);
        game.setPlayerDetails(map);
        TBGame result = engine.evaluateGame(game);
        assertEquals(GamePhase.Setup, result.getGamePhase());
        game.getPlayerDetails().values().forEach((ps) -> assertEquals(0, ps.getActionLog().size()));
    }

    @Test
    public void testEvaluateSetupPhaseWithNoPlayersSetup() {
        TBGame game = new TBGame();
        game.setGamePhase(GamePhase.Setup);
        LinkedHashMap<ObjectId, TBPlayerState> map = new LinkedHashMap<>(3);
        TBPlayerState state = new TBPlayerState();
        state.setSetup(false);
        map.put(PONE.getId(), state);
        state = new TBPlayerState();
        state.setSetup(false);
        map.put(PFOUR.getId(), state);
        state = new TBPlayerState();
        state.setSetup(false);
        map.put(PTWO.getId(), state);
        game.setPlayerDetails(map);
        TBGame result = engine.evaluateGame(game);
        assertEquals(GamePhase.Setup, result.getGamePhase());
        game.getPlayerDetails().values().forEach((ps) -> assertEquals(0, ps.getActionLog().size()));
    }

    @Test
    public void testEvaluatePlayingPhaseOnePlayerAlive() {
        TBGame game = new TBGame();
        game.setId(new ObjectId());
        game.setGamePhase(GamePhase.Playing);
        LinkedHashMap<ObjectId, TBPlayerState> map = new LinkedHashMap<>(3);
        TBPlayerState state = new TBPlayerState();
        state.setShipStates(Collections.singletonList(new ShipState(Ship.Battleship, 0, new ArrayList<>(), new ArrayList<>())));
        map.put(PONE.getId(), state);
        state = new TBPlayerState();
        state.setShipStates(new ArrayList<>());
        map.put(PFOUR.getId(), state);
        state = new TBPlayerState();
        state.setShipStates(Collections.singletonList(new ShipState(Ship.Submarine, 1, Arrays.asList(new GridCoordinate(0, 0), new GridCoordinate(0, 1), new GridCoordinate(0, 2)), Arrays.asList(false, true, true))));
        map.put(PTWO.getId(), state);
        game.setPlayerDetails(map);
        game.getPlayerDetails().get(PONE.getId()).getOpponentGrids().put(PTWO.getId(), new Grid(10));
        game.getPlayerDetails().get(PONE.getId()).getOpponentGrids().get(PTWO.getId()).set(0, 2, GridCellState.KnownByHit);
        game.getPlayerDetails().get(PTWO.getId()).getOpponentViews().put(PONE.getId(), new Grid(10));
        game.getPlayerDetails().get(PTWO.getId()).getOpponentViews().get(PONE.getId()).set(0, 2, GridCellState.KnownByHit);
        game.setPlayers(Arrays.asList(PONE, PFOUR, PTWO));
        Mockito.when(gameScorer.scoreGame(game)).thenReturn(game);

        TBGame result = engine.evaluateGame(game);
        assertEquals(GamePhase.RoundOver, result.getGamePhase());
        game.getPlayerDetails().values().forEach((ps) -> {
            TBActionLogEntry lastEntry = getLastEntry(ps.getActionLog());
            assertEquals(TBActionLogEntry.TBActionType.Victory, lastEntry.getActionType());
            assertEquals("200000000000000000000000 defeated all challengers!", lastEntry.getDescription());
        });
        assertEquals(GridCellState.RevealedShip, game.getPlayerDetails().get(PONE.getId()).getOpponentGrids().get(PTWO.getId()).get(0, 0));
        assertEquals(GridCellState.RevealedShip, game.getPlayerDetails().get(PTWO.getId()).getOpponentViews().get(PONE.getId()).get(0, 0));
        assertEquals(GridCellState.HiddenHit, game.getPlayerDetails().get(PONE.getId()).getOpponentGrids().get(PTWO.getId()).get(0, 1));
        assertEquals(GridCellState.HiddenHit, game.getPlayerDetails().get(PTWO.getId()).getOpponentViews().get(PONE.getId()).get(0, 1));
        assertEquals(GridCellState.KnownByHit, game.getPlayerDetails().get(PONE.getId()).getOpponentGrids().get(PTWO.getId()).get(0, 2));
        assertEquals(GridCellState.KnownByHit, game.getPlayerDetails().get(PTWO.getId()).getOpponentViews().get(PONE.getId()).get(0, 2));
        Mockito.verify(gameScorer).scoreGame(game);
    }

    @Test
    public void testEvaluatePlayingPhaseMultiplePlayerAlive() {
        TBGame game = new TBGame();
        game.setGamePhase(GamePhase.Playing);
        LinkedHashMap<ObjectId, TBPlayerState> map = new LinkedHashMap<>(3);
        TBPlayerState state = new TBPlayerState();
        state.setShipStates(Collections.singletonList(new ShipState(Ship.Battleship, 2, new ArrayList<>(), new ArrayList<>())));
        map.put(PONE.getId(), state);
        state = new TBPlayerState();
        state.setShipStates(new ArrayList<>());
        map.put(PFOUR.getId(), state);
        state = new TBPlayerState();
        state.setShipStates(Collections.singletonList(new ShipState(Ship.Submarine, 1, new ArrayList<>(), new ArrayList<>())));
        map.put(PTWO.getId(), state);
        game.setPlayerDetails(map);

        TBGame result = engine.evaluateGame(game);
        assertEquals(GamePhase.Playing, result.getGamePhase());
        game.getPlayerDetails().values().forEach((ps) -> assertEquals(0, ps.getActionLog().size()));
    }

}
