package com.jtbdevelopment.TwistedBattleship.rest.handlers;

import com.jtbdevelopment.TwistedBattleship.exceptions.NoECMActionsRemainException;
import com.jtbdevelopment.TwistedBattleship.state.TBActionLogEntry;
import com.jtbdevelopment.TwistedBattleship.state.TBGame;
import com.jtbdevelopment.TwistedBattleship.state.TBPlayerState;
import com.jtbdevelopment.TwistedBattleship.state.grid.GridCellState;
import com.jtbdevelopment.TwistedBattleship.state.grid.GridCircleUtil;
import com.jtbdevelopment.TwistedBattleship.state.grid.GridCoordinate;
import org.bson.types.ObjectId;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Date: 5/20/15
 * Time: 6:38 PM
 */
public class ECMHandlerTest extends AbstractBaseHandlerTest {
    private GridCircleUtil gridCircleUtil = new GridCircleUtil();
    private ECMHandler handler = new ECMHandler(null, null, null, null, null, null, gridCircleUtil);

    @Test
    public void testTargetSelf() {
        assert handler.targetSelf();
    }

    @SuppressWarnings("Duplicates")
    @Test
    public void testMovesRequired() {
        TBGame game = new TBGame();
        game.setMovesForSpecials(1);
        assertEquals(1, handler.movesRequired(game));
        game = new TBGame();
        game.setMovesForSpecials(2);
        assertEquals(2, handler.movesRequired(game));
    }

    @Test(expected = NoECMActionsRemainException.class)
    public void testValidatesECMsRemain() {
        game.getPlayerDetails().get(PONE.getId()).setEcmsRemaining(1);
        handler.validateMoveSpecific(PONE, game, PONE, new GridCoordinate(3, 0));
        game.getPlayerDetails().get(PONE.getId()).setEcmsRemaining(0);
        handler.validateMoveSpecific(PONE, game, PONE, new GridCoordinate(3, 0));
    }

    @Test
    public void testAnECM() {
        game.getPlayerDetails().get(PTWO.getId()).getOpponentGrids().get(PONE.getId()).set(1, 0, GridCellState.KnownByHit);
        game.getPlayerDetails().get(PTWO.getId()).getOpponentGrids().get(PONE.getId()).set(2, 0, GridCellState.KnownByOtherHit);
        game.getPlayerDetails().get(PTWO.getId()).getOpponentGrids().get(PONE.getId()).set(1, 1, GridCellState.KnownByMiss);
        game.getPlayerDetails().get(PTWO.getId()).getOpponentGrids().get(PONE.getId()).set(5, 5, GridCellState.KnownByMiss);
        game.getPlayerDetails().get(PTWO.getId()).getOpponentGrids().get(PONE.getId()).set(5, 6, GridCellState.KnownByHit);
        game.getPlayerDetails().get(PTWO.getId()).getOpponentGrids().get(PONE.getId()).set(5, 7, GridCellState.KnownByRehit);
        game.getPlayerDetails().get(PTWO.getId()).getOpponentGrids().get(PONE.getId()).set(5, 8, GridCellState.KnownByOtherHit);
        game.getPlayerDetails().get(PONE.getId()).getOpponentViews().get(PTWO.getId()).set(1, 0, GridCellState.KnownByHit);
        game.getPlayerDetails().get(PONE.getId()).getOpponentViews().get(PTWO.getId()).set(2, 0, GridCellState.KnownByOtherHit);
        game.getPlayerDetails().get(PONE.getId()).getOpponentViews().get(PTWO.getId()).set(1, 1, GridCellState.KnownByMiss);
        game.getPlayerDetails().get(PONE.getId()).getOpponentViews().get(PTWO.getId()).set(5, 5, GridCellState.KnownByMiss);
        game.getPlayerDetails().get(PONE.getId()).getOpponentViews().get(PTWO.getId()).set(5, 6, GridCellState.KnownByHit);
        game.getPlayerDetails().get(PONE.getId()).getOpponentViews().get(PTWO.getId()).set(5, 7, GridCellState.KnownByRehit);
        game.getPlayerDetails().get(PONE.getId()).getOpponentViews().get(PTWO.getId()).set(5, 8, GridCellState.KnownByOtherHit);

        game.getPlayerDetails().get(PFOUR.getId()).getOpponentGrids().get(PONE.getId()).set(2, 0, GridCellState.KnownByHit);
        game.getPlayerDetails().get(PONE.getId()).getOpponentViews().get(PFOUR.getId()).set(2, 0, GridCellState.KnownByHit);

        game.getPlayerDetails().get(PONE.getId()).getActionLog().add(new TBActionLogEntry("You fired at " + PTWO.getDisplayName() + " " + new GridCoordinate(1, 0) + " something", TBActionLogEntry.TBActionType.Fired));
        game.getPlayerDetails().get(PTWO.getId()).getActionLog().add(new TBActionLogEntry("You fired at " + PONE.getDisplayName() + " " + new GridCoordinate(1, 0) + " something", TBActionLogEntry.TBActionType.Fired));
        game.getPlayerDetails().get(PTWO.getId()).getActionLog().add(new TBActionLogEntry("You fired at " + PONE.getDisplayName() + " " + new GridCoordinate(0, 0) + " something", TBActionLogEntry.TBActionType.Fired));
        game.getPlayerDetails().get(PTWO.getId()).getActionLog().add(new TBActionLogEntry("You fired at " + PONE.getDisplayName() + " " + new GridCoordinate(0, 0) + " something", TBActionLogEntry.TBActionType.Spied));
        game.getPlayerDetails().get(PTWO.getId()).getActionLog().add(new TBActionLogEntry("You X at " + PONE.getDisplayName() + " " + new GridCoordinate(0, 0) + " something", TBActionLogEntry.TBActionType.Fired));
        game.getPlayerDetails().get(PTWO.getId()).getActionLog().add(new TBActionLogEntry("You fired at " + PONE.getDisplayName() + " " + new GridCoordinate(10, 10) + " something", TBActionLogEntry.TBActionType.Fired));

        assertSame(game, handler.playMove(PONE, game, PONE, new GridCoordinate(1, 0)));
        assertEquals(GridCellState.KnownByMiss, game.getPlayerDetails().get(PTWO.getId()).getOpponentGrids().get(PONE.getId()).get(5, 5));
        assertEquals(GridCellState.KnownByHit, game.getPlayerDetails().get(PTWO.getId()).getOpponentGrids().get(PONE.getId()).get(5, 6));
        assertEquals(GridCellState.KnownByRehit, game.getPlayerDetails().get(PTWO.getId()).getOpponentGrids().get(PONE.getId()).get(5, 7));
        assertEquals(GridCellState.KnownByOtherHit, game.getPlayerDetails().get(PTWO.getId()).getOpponentGrids().get(PONE.getId()).get(5, 8));
        assertEquals(GridCellState.KnownByMiss, game.getPlayerDetails().get(PONE.getId()).getOpponentViews().get(PTWO.getId()).get(5, 5));
        assertEquals(GridCellState.KnownByHit, game.getPlayerDetails().get(PONE.getId()).getOpponentViews().get(PTWO.getId()).get(5, 6));
        assertEquals(GridCellState.KnownByRehit, game.getPlayerDetails().get(PONE.getId()).getOpponentViews().get(PTWO.getId()).get(5, 7));
        assertEquals(GridCellState.KnownByOtherHit, game.getPlayerDetails().get(PONE.getId()).getOpponentViews().get(PTWO.getId()).get(5, 8));
        assertEquals(2, game.getPlayerDetails().get(PONE.getId()).getEcmsRemaining());
        assertEquals(getLastEntry(game.getPlayerDetails().get(PONE.getId()).getActionLog()).getDescription(), "100000000000000000000000 deployed an ECM.");
        assertEquals(getLastEntry(game.getPlayerDetails().get(PONE.getId()).getActionLog()).getActionType(), TBActionLogEntry.TBActionType.UsedECM);

        assertEquals(TBActionLogEntry.TBActionType.Fired, game.getPlayerDetails().get(PONE.getId()).getActionLog().get(0).getActionType());
        assertEquals("You fired at " + PTWO.getDisplayName() + " " + new GridCoordinate(1, 0) + " something", game.getPlayerDetails().get(PONE.getId()).getActionLog().get(0).getDescription());
        assertEquals(TBActionLogEntry.TBActionType.DamagedByECM, game.getPlayerDetails().get(PTWO.getId()).getActionLog().get(0).getActionType());
        assertEquals("Log damaged by ECM.", game.getPlayerDetails().get(PTWO.getId()).getActionLog().get(0).getDescription());
        assertEquals(TBActionLogEntry.TBActionType.DamagedByECM, game.getPlayerDetails().get(PTWO.getId()).getActionLog().get(1).getActionType());
        assertEquals("Log damaged by ECM.", game.getPlayerDetails().get(PTWO.getId()).getActionLog().get(1).getDescription());
        assertEquals(TBActionLogEntry.TBActionType.Spied, game.getPlayerDetails().get(PTWO.getId()).getActionLog().get(2).getActionType());
        assertEquals("You fired at " + PONE.getDisplayName() + " " + new GridCoordinate(0, 0) + " something", game.getPlayerDetails().get(PTWO.getId()).getActionLog().get(2).getDescription());
        assertEquals(TBActionLogEntry.TBActionType.Fired, game.getPlayerDetails().get(PTWO.getId()).getActionLog().get(3).getActionType());
        assertEquals("You X at " + PONE.getDisplayName() + " " + new GridCoordinate(0, 0) + " something", game.getPlayerDetails().get(PTWO.getId()).getActionLog().get(3).getDescription());
        assertEquals(TBActionLogEntry.TBActionType.Fired, game.getPlayerDetails().get(PTWO.getId()).getActionLog().get(4).getActionType());
        assertEquals("You fired at " + PONE.getDisplayName() + " " + new GridCoordinate(10, 10) + " something", game.getPlayerDetails().get(PTWO.getId()).getActionLog().get(4).getDescription());

        game.getPlayerDetails().entrySet().stream().filter(d -> !PONE.getId().equals(d.getKey()))
                .forEach((e) -> {
                    ObjectId id = e.getKey();
                    TBPlayerState state = e.getValue();
                    assertEquals("100000000000000000000000 deployed an ECM.", getLastEntry(state.getActionLog()).getDescription());
                    assertEquals(TBActionLogEntry.TBActionType.UsedECM, getLastEntry(state.getActionLog()).getActionType());
                    if (!PTWO.getId().equals(id)) {
                        assertTrue(state.getOpponentGrids().get(PONE.getId()).stream()
                                .filter(gc -> gc.getRow() == 5)
                                .filter(gc -> gc.getColumn() >= 5)
                                .filter(gc -> gc.getColumn() <= 8)
                                .allMatch(gc -> state.getOpponentGrids().get(PONE.getId()).get(gc).equals(GridCellState.Unknown)));
                        assertTrue(game.getPlayerDetails().get(PONE.getId()).getOpponentViews().get(id).stream()
                                .filter(gc -> gc.getRow() == 5)
                                .filter(gc -> gc.getColumn() >= 5)
                                .filter(gc -> gc.getColumn() <= 8)
                                .allMatch(gc -> game.getPlayerDetails().get(PONE.getId()).getOpponentViews().get(id).get(gc).equals(GridCellState.Unknown)));
                    }
                });
    }
}
