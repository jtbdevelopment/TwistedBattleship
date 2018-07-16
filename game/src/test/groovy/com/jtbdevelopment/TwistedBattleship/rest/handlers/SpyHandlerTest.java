package com.jtbdevelopment.TwistedBattleship.rest.handlers;

import com.jtbdevelopment.TwistedBattleship.exceptions.NoSpyActionsRemainException;
import com.jtbdevelopment.TwistedBattleship.state.GameFeature;
import com.jtbdevelopment.TwistedBattleship.state.TBActionLogEntry;
import com.jtbdevelopment.TwistedBattleship.state.TBGame;
import com.jtbdevelopment.TwistedBattleship.state.TBPlayerState;
import com.jtbdevelopment.TwistedBattleship.state.grid.GridCellState;
import com.jtbdevelopment.TwistedBattleship.state.grid.GridCircleUtil;
import com.jtbdevelopment.TwistedBattleship.state.grid.GridCoordinate;
import com.jtbdevelopment.TwistedBattleship.state.ships.Ship;
import org.bson.types.ObjectId;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

import static org.junit.Assert.assertEquals;

/**
 * Date: 5/15/15
 * Time: 6:56 AM
 */
public class SpyHandlerTest extends AbstractBaseHandlerTest {
    private SpyHandler handler = new SpyHandler(null, null, null, null, null, null);

    @Before
    public void setUp() throws Exception {
        super.setUp();
        handler.gridCircleUtil = new GridCircleUtil();
        game.getPlayerDetails().get(PTWO.getId()).getOpponentGrids().get(PONE.getId()).set(1, 0, GridCellState.KnownByHit);
        game.getPlayerDetails().get(PTWO.getId()).getOpponentGrids().get(PONE.getId()).set(2, 0, GridCellState.KnownByOtherHit);
        game.getPlayerDetails().get(PTWO.getId()).getOpponentGrids().get(PONE.getId()).set(1, 1, GridCellState.KnownByMiss);
        game.getPlayerDetails().get(PONE.getId()).getOpponentViews().get(PTWO.getId()).set(1, 0, GridCellState.KnownByHit);
        game.getPlayerDetails().get(PONE.getId()).getOpponentViews().get(PTWO.getId()).set(2, 0, GridCellState.KnownByOtherHit);
        game.getPlayerDetails().get(PONE.getId()).getOpponentViews().get(PTWO.getId()).set(1, 1, GridCellState.KnownByMiss);

        game.getPlayerDetails().get(PFOUR.getId()).getOpponentGrids().get(PONE.getId()).set(2, 0, GridCellState.KnownByHit);
        game.getPlayerDetails().get(PONE.getId()).getOpponentViews().get(PFOUR.getId()).set(2, 0, GridCellState.KnownByHit);

        game.getPlayerDetails().get(PONE.getId()).getShipStates()
                .stream()
                .filter(ss -> Ship.Carrier.equals(ss.getShip()))
                .forEach(ss -> {
                    ss.setHealthRemaining(3);
                    ss.setShipSegmentHit(Arrays.asList(false, true, true, false, false));
                });
    }

    @Test
    public void testTargetSelf() {
        Assert.assertFalse(handler.targetSelf());
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

    @Test(expected = NoSpyActionsRemainException.class)
    public void testValidatesNoSpiesRemain() {
        TBGame game = new TBGame();
        Map<ObjectId, TBPlayerState> map = new HashMap<>();
        TBPlayerState state = new TBPlayerState();
        state.setSpysRemaining(1);
        map.put(PONE.getId(), state);
        game.setPlayerDetails(map);
        handler.validateMoveSpecific(PONE, game, PTWO, null);
        game.getPlayerDetails().get(PONE.getId()).setSpysRemaining(0);
        handler.validateMoveSpecific(PONE, game, PTWO, null);
    }

    @Test
    public void testSpyWithIsolatedIntel() {
        game.getFeatures().add(GameFeature.IsolatedIntel);

        TBGame g = handler.playMove(PTWO, game, PONE, new GridCoordinate(2, 1));

        assertEquals(game, g);
        coreSpyAsserts();
        IntStream.range(0, 15).forEach(row -> {
            final int start = row < 5 ? 4 : 0;
            IntStream.range(0, 15).forEach(col -> {
                if (start <= col) {
                    assertEquals(GridCellState.Unknown, game.getPlayerDetails().get(PTWO.getId()).getOpponentGrids().get(PONE.getId()).get(row, col));
                    assertEquals(GridCellState.Unknown, game.getPlayerDetails().get(PONE.getId()).getOpponentViews().get(PTWO.getId()).get(row, col));
                }

                if (row == 2 && col == 0) {
                    assertEquals(GridCellState.KnownByHit, game.getPlayerDetails().get(PFOUR.getId()).getOpponentGrids().get(PONE.getId()).get(row, col));
                    assertEquals(GridCellState.KnownByHit, game.getPlayerDetails().get(PONE.getId()).getOpponentViews().get(PFOUR.getId()).get(row, col));
                } else {
                    assertEquals(GridCellState.Unknown, game.getPlayerDetails().get(PFOUR.getId()).getOpponentGrids().get(PONE.getId()).get(row, col));
                    assertEquals(GridCellState.Unknown, game.getPlayerDetails().get(PONE.getId()).getOpponentViews().get(PFOUR.getId()).get(row, col));
                }

                assertEquals(GridCellState.Unknown, game.getPlayerDetails().get(PTHREE.getId()).getOpponentGrids().get(PONE.getId()).get(row, col));
                assertEquals(GridCellState.Unknown, game.getPlayerDetails().get(PONE.getId()).getOpponentViews().get(PTHREE.getId()).get(row, col));
            });
        });
        assertEquals(0, game.getPlayerDetails().get(PTHREE.getId()).getActionLog().size());
        assertEquals(0, game.getPlayerDetails().get(PFOUR.getId()).getActionLog().size());
    }

    @Test
    public void testSpyWithSharedIntel() {
        game.getFeatures().add(GameFeature.SharedIntel);

        TBGame g = handler.playMove(PTWO, game, PONE, new GridCoordinate(2, 1));

        assertEquals(game, g);
        coreSpyAsserts();
        IntStream.range(0, 15).forEach(row -> {
            final int start = row < 5 ? 4 : 0;
            IntStream.range(0, 15).forEach(col -> {
                if (start <= col) {
                    assertEquals(GridCellState.Unknown, game.getPlayerDetails().get(PTWO.getId()).getOpponentGrids().get(PONE.getId()).get(row, col));
                    assertEquals(GridCellState.Unknown, game.getPlayerDetails().get(PONE.getId()).getOpponentViews().get(PTWO.getId()).get(row, col));
                }

                if (row == 1 && col == 0) {
                    assertEquals(GridCellState.KnownByOtherHit, game.getPlayerDetails().get(PFOUR.getId()).getOpponentGrids().get(PONE.getId()).get(row, col));
                    assertEquals(GridCellState.KnownByOtherHit, game.getPlayerDetails().get(PONE.getId()).getOpponentViews().get(PFOUR.getId()).get(row, col));
                    assertEquals(GridCellState.KnownByOtherHit, game.getPlayerDetails().get(PTHREE.getId()).getOpponentGrids().get(PONE.getId()).get(row, col));
                    assertEquals(GridCellState.KnownByOtherHit, game.getPlayerDetails().get(PONE.getId()).getOpponentViews().get(PTHREE.getId()).get(row, col));
                } else if (row == 1 && col == 1) {
                    assertEquals(GridCellState.KnownEmpty, game.getPlayerDetails().get(PFOUR.getId()).getOpponentGrids().get(PONE.getId()).get(row, col));
                    assertEquals(GridCellState.KnownEmpty, game.getPlayerDetails().get(PONE.getId()).getOpponentViews().get(PFOUR.getId()).get(row, col));
                    assertEquals(GridCellState.KnownEmpty, game.getPlayerDetails().get(PTHREE.getId()).getOpponentGrids().get(PONE.getId()).get(row, col));
                    assertEquals(GridCellState.KnownEmpty, game.getPlayerDetails().get(PONE.getId()).getOpponentViews().get(PTHREE.getId()).get(row, col));
                } else {
                    assertEquals(game.getPlayerDetails().get(PTWO.getId()).getOpponentGrids().get(PONE.getId()).get(row, col), game.getPlayerDetails().get(PTHREE.getId()).getOpponentGrids().get(PONE.getId()).get(row, col));
                    assertEquals(game.getPlayerDetails().get(PONE.getId()).getOpponentViews().get(PTWO.getId()).get(row, col), game.getPlayerDetails().get(PONE.getId()).getOpponentViews().get(PTHREE.getId()).get(row, col));
                    if (row == 2 && col == 0) {
                        assertEquals(GridCellState.KnownByHit, game.getPlayerDetails().get(PFOUR.getId()).getOpponentGrids().get(PONE.getId()).get(row, col));
                        assertEquals(GridCellState.KnownByHit, game.getPlayerDetails().get(PONE.getId()).getOpponentViews().get(PFOUR.getId()).get(row, col));
                    } else {
                        assertEquals(game.getPlayerDetails().get(PTWO.getId()).getOpponentGrids().get(PONE.getId()).get(row, col), game.getPlayerDetails().get(PFOUR.getId()).getOpponentGrids().get(PONE.getId()).get(row, col));
                        assertEquals(game.getPlayerDetails().get(PONE.getId()).getOpponentViews().get(PTWO.getId()).get(row, col), game.getPlayerDetails().get(PONE.getId()).getOpponentViews().get(PFOUR.getId()).get(row, col));
                    }

                }
            });
        });
        assertEquals("200000000000000000000000 spied on 100000000000000000000000 at (2,1).", getLastEntry(game.getPlayerDetails().get(PTHREE.getId()).getActionLog()).getDescription());
        assertEquals("200000000000000000000000 spied on 100000000000000000000000 at (2,1).", getLastEntry(game.getPlayerDetails().get(PFOUR.getId()).getActionLog()).getDescription());
        assertEquals(TBActionLogEntry.TBActionType.Spied, getLastEntry(game.getPlayerDetails().get(PFOUR.getId()).getActionLog()).getActionType());
        assertEquals(TBActionLogEntry.TBActionType.Spied, getLastEntry(game.getPlayerDetails().get(PTHREE.getId()).getActionLog()).getActionType());
    }

    private void coreSpyAsserts() {
        assertEquals(3, game.getPlayerDetails().get(PTHREE.getId()).getSpysRemaining());
        assertEquals(3, game.getPlayerDetails().get(PFOUR.getId()).getSpysRemaining());
        assertEquals(3, game.getPlayerDetails().get(PONE.getId()).getSpysRemaining());
        assertEquals(2, game.getPlayerDetails().get(PTWO.getId()).getSpysRemaining());
        assertEquals("200000000000000000000000 spied on you at (2,1).", getLastEntry(game.getPlayerDetails().get(PONE.getId()).getActionLog()).getDescription());
        assertEquals(TBActionLogEntry.TBActionType.Spied, getLastEntry(game.getPlayerDetails().get(PONE.getId()).getActionLog()).getActionType());
        assertEquals("You spied on 100000000000000000000000 at (2,1).", getLastEntry(game.getPlayerDetails().get(PTWO.getId()).getActionLog()).getDescription());
        assertEquals(TBActionLogEntry.TBActionType.Spied, getLastEntry(game.getPlayerDetails().get(PTWO.getId()).getActionLog()).getActionType());
        assertEquals(GridCellState.Unknown, game.getPlayerDetails().get(PTWO.getId()).getOpponentGrids().get(PONE.getId()).get(4, 0));
        assertEquals(GridCellState.KnownEmpty, game.getPlayerDetails().get(PTWO.getId()).getOpponentGrids().get(PONE.getId()).get(4, 1));
        assertEquals(GridCellState.Unknown, game.getPlayerDetails().get(PTWO.getId()).getOpponentGrids().get(PONE.getId()).get(4, 2));
        assertEquals(GridCellState.Unknown, game.getPlayerDetails().get(PTWO.getId()).getOpponentGrids().get(PONE.getId()).get(4, 3));
        assertEquals(GridCellState.KnownShip, game.getPlayerDetails().get(PTWO.getId()).getOpponentGrids().get(PONE.getId()).get(3, 0));
        assertEquals(GridCellState.KnownEmpty, game.getPlayerDetails().get(PTWO.getId()).getOpponentGrids().get(PONE.getId()).get(3, 1));
        assertEquals(GridCellState.KnownEmpty, game.getPlayerDetails().get(PTWO.getId()).getOpponentGrids().get(PONE.getId()).get(3, 2));
        assertEquals(GridCellState.Unknown, game.getPlayerDetails().get(PTWO.getId()).getOpponentGrids().get(PONE.getId()).get(3, 3));
        assertEquals(GridCellState.KnownByOtherHit, game.getPlayerDetails().get(PTWO.getId()).getOpponentGrids().get(PONE.getId()).get(2, 0));
        assertEquals(GridCellState.KnownEmpty, game.getPlayerDetails().get(PTWO.getId()).getOpponentGrids().get(PONE.getId()).get(2, 1));
        assertEquals(GridCellState.KnownEmpty, game.getPlayerDetails().get(PTWO.getId()).getOpponentGrids().get(PONE.getId()).get(2, 2));
        assertEquals(GridCellState.KnownEmpty, game.getPlayerDetails().get(PTWO.getId()).getOpponentGrids().get(PONE.getId()).get(2, 3));
        assertEquals(GridCellState.KnownByHit, game.getPlayerDetails().get(PTWO.getId()).getOpponentGrids().get(PONE.getId()).get(1, 0));
        assertEquals(GridCellState.KnownByMiss, game.getPlayerDetails().get(PTWO.getId()).getOpponentGrids().get(PONE.getId()).get(1, 1));
        assertEquals(GridCellState.KnownEmpty, game.getPlayerDetails().get(PTWO.getId()).getOpponentGrids().get(PONE.getId()).get(1, 2));
        assertEquals(GridCellState.Unknown, game.getPlayerDetails().get(PTWO.getId()).getOpponentGrids().get(PONE.getId()).get(1, 3));
        assertEquals(GridCellState.Unknown, game.getPlayerDetails().get(PTWO.getId()).getOpponentGrids().get(PONE.getId()).get(0, 0));
        assertEquals(GridCellState.KnownEmpty, game.getPlayerDetails().get(PTWO.getId()).getOpponentGrids().get(PONE.getId()).get(0, 1));
        assertEquals(GridCellState.Unknown, game.getPlayerDetails().get(PTWO.getId()).getOpponentGrids().get(PONE.getId()).get(0, 2));
        assertEquals(GridCellState.Unknown, game.getPlayerDetails().get(PTWO.getId()).getOpponentGrids().get(PONE.getId()).get(0, 3));

        assertEquals(GridCellState.Unknown, game.getPlayerDetails().get(PONE.getId()).getOpponentViews().get(PTWO.getId()).get(4, 0));
        assertEquals(GridCellState.KnownEmpty, game.getPlayerDetails().get(PONE.getId()).getOpponentViews().get(PTWO.getId()).get(4, 1));
        assertEquals(GridCellState.Unknown, game.getPlayerDetails().get(PONE.getId()).getOpponentViews().get(PTWO.getId()).get(4, 2));
        assertEquals(GridCellState.Unknown, game.getPlayerDetails().get(PONE.getId()).getOpponentViews().get(PTWO.getId()).get(4, 3));
        assertEquals(GridCellState.KnownShip, game.getPlayerDetails().get(PONE.getId()).getOpponentViews().get(PTWO.getId()).get(3, 0));
        assertEquals(GridCellState.KnownEmpty, game.getPlayerDetails().get(PONE.getId()).getOpponentViews().get(PTWO.getId()).get(3, 1));
        assertEquals(GridCellState.KnownEmpty, game.getPlayerDetails().get(PONE.getId()).getOpponentViews().get(PTWO.getId()).get(3, 2));
        assertEquals(GridCellState.Unknown, game.getPlayerDetails().get(PONE.getId()).getOpponentViews().get(PTWO.getId()).get(3, 3));
        assertEquals(GridCellState.KnownByOtherHit, game.getPlayerDetails().get(PONE.getId()).getOpponentViews().get(PTWO.getId()).get(2, 0));
        assertEquals(GridCellState.KnownEmpty, game.getPlayerDetails().get(PONE.getId()).getOpponentViews().get(PTWO.getId()).get(2, 1));
        assertEquals(GridCellState.KnownEmpty, game.getPlayerDetails().get(PONE.getId()).getOpponentViews().get(PTWO.getId()).get(2, 2));
        assertEquals(GridCellState.KnownEmpty, game.getPlayerDetails().get(PONE.getId()).getOpponentViews().get(PTWO.getId()).get(2, 3));
        assertEquals(GridCellState.KnownByHit, game.getPlayerDetails().get(PONE.getId()).getOpponentViews().get(PTWO.getId()).get(1, 0));
        assertEquals(GridCellState.KnownByMiss, game.getPlayerDetails().get(PONE.getId()).getOpponentViews().get(PTWO.getId()).get(1, 1));
        assertEquals(GridCellState.KnownEmpty, game.getPlayerDetails().get(PONE.getId()).getOpponentViews().get(PTWO.getId()).get(1, 2));
        assertEquals(GridCellState.Unknown, game.getPlayerDetails().get(PONE.getId()).getOpponentViews().get(PTWO.getId()).get(1, 3));
        assertEquals(GridCellState.Unknown, game.getPlayerDetails().get(PONE.getId()).getOpponentViews().get(PTWO.getId()).get(0, 0));
        assertEquals(GridCellState.KnownEmpty, game.getPlayerDetails().get(PONE.getId()).getOpponentViews().get(PTWO.getId()).get(0, 1));
        assertEquals(GridCellState.Unknown, game.getPlayerDetails().get(PONE.getId()).getOpponentViews().get(PTWO.getId()).get(0, 2));
        assertEquals(GridCellState.Unknown, game.getPlayerDetails().get(PONE.getId()).getOpponentViews().get(PTWO.getId()).get(0, 3));
    }
}
