package com.jtbdevelopment.TwistedBattleship.rest.handlers;

import com.jtbdevelopment.TwistedBattleship.exceptions.CannotRepairADestroyedShipException;
import com.jtbdevelopment.TwistedBattleship.exceptions.NoRepairActionsRemainException;
import com.jtbdevelopment.TwistedBattleship.exceptions.NoShipAtCoordinateException;
import com.jtbdevelopment.TwistedBattleship.state.TBActionLogEntry;
import com.jtbdevelopment.TwistedBattleship.state.TBGame;
import com.jtbdevelopment.TwistedBattleship.state.grid.GridCellState;
import com.jtbdevelopment.TwistedBattleship.state.grid.GridCoordinate;
import com.jtbdevelopment.TwistedBattleship.state.ships.Ship;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

/**
 * Date: 5/19/15
 * Time: 6:39 AM
 */
public class RepairShipHandlerTest extends AbstractBaseHandlerTest {
    private RepairShipHandler handler = new RepairShipHandler(null, null, null, null, null, null);

    @Test
    public void testTargetSelf() {
        Assert.assertTrue(handler.targetSelf());
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

    @Test(expected = NoRepairActionsRemainException.class)
    public void testValidatesRepairsRemain() {
        game.getPlayerDetails().get(PONE.getId()).setEmergencyRepairsRemaining(1);
        handler.validateMoveSpecific(PONE, game, PONE, new GridCoordinate(3, 0));
        game.getPlayerDetails().get(PONE.getId()).setEmergencyRepairsRemaining(0);
        handler.validateMoveSpecific(PONE, game, PONE, new GridCoordinate(3, 0));
    }

    @Test(expected = NoShipAtCoordinateException.class)
    public void testValidatesShipExistsAtCoordinate() {
        game.getPlayerDetails().get(PONE.getId()).setEmergencyRepairsRemaining(1);
        handler.validateMoveSpecific(PONE, game, PONE, new GridCoordinate(3, 1));
    }

    @Test
    public void testRepair() {
        game.getPlayerDetails().get(PONE.getId()).setEmergencyRepairsRemaining(1);
        game.getPlayerDetails().get(PONE.getId()).getShipStates()
                .stream()
                .filter(ss -> Ship.Carrier.equals(ss.getShip()))
                .forEach(ss -> {
                    ss.setHealthRemaining(2);
                    ss.setShipSegmentHit(Arrays.asList(false, true, true, true, false));
                });
        game.getPlayerDetails().get(PTWO.getId()).getOpponentGrids().get(PONE.getId()).set(0, 0, GridCellState.KnownShip);
        game.getPlayerDetails().get(PTWO.getId()).getOpponentGrids().get(PONE.getId()).set(1, 0, GridCellState.KnownByHit);
        game.getPlayerDetails().get(PTWO.getId()).getOpponentGrids().get(PONE.getId()).set(2, 0, GridCellState.KnownByRehit);
        game.getPlayerDetails().get(PTWO.getId()).getOpponentGrids().get(PONE.getId()).set(3, 0, GridCellState.KnownByOtherHit);
        game.getPlayerDetails().get(PTWO.getId()).getOpponentGrids().get(PONE.getId()).set(4, 0, GridCellState.Unknown);
        game.getPlayerDetails().get(PONE.getId()).getOpponentViews().get(PTWO.getId()).set(0, 0, GridCellState.KnownShip);
        game.getPlayerDetails().get(PONE.getId()).getOpponentViews().get(PTWO.getId()).set(1, 0, GridCellState.KnownByHit);
        game.getPlayerDetails().get(PONE.getId()).getOpponentViews().get(PTWO.getId()).set(2, 0, GridCellState.KnownByRehit);
        game.getPlayerDetails().get(PONE.getId()).getOpponentViews().get(PTWO.getId()).set(3, 0, GridCellState.KnownByOtherHit);
        game.getPlayerDetails().get(PONE.getId()).getOpponentViews().get(PTWO.getId()).set(4, 0, GridCellState.Unknown);

        game.getPlayerDetails().get(PTHREE.getId()).getOpponentGrids().get(PONE.getId()).set(3, 0, GridCellState.KnownByHit);
        game.getPlayerDetails().get(PONE.getId()).getOpponentViews().get(PTHREE.getId()).set(3, 0, GridCellState.KnownByHit);
        game.getPlayerDetails().get(PONE.getId()).getOpponentViews().get(PTHREE.getId()).set(4, 0, GridCellState.HiddenHit);

        assertEquals(game, handler.playMove(PONE, game, PONE, new GridCoordinate(4, 0)));

        assertEquals(GridCellState.Unknown, game.getPlayerDetails().get(PONE.getId()).getOpponentViews().get(PTHREE.getId()).get(4, 0));

        assertEquals(GridCellState.KnownShip, game.getPlayerDetails().get(PTWO.getId()).getOpponentGrids().get(PONE.getId()).get(0, 0));
        assertEquals(GridCellState.KnownShip, game.getPlayerDetails().get(PTWO.getId()).getOpponentGrids().get(PONE.getId()).get(1, 0));
        assertEquals(GridCellState.KnownShip, game.getPlayerDetails().get(PTWO.getId()).getOpponentGrids().get(PONE.getId()).get(2, 0));
        assertEquals(GridCellState.KnownShip, game.getPlayerDetails().get(PTWO.getId()).getOpponentGrids().get(PONE.getId()).get(3, 0));
        assertEquals(GridCellState.Unknown, game.getPlayerDetails().get(PTWO.getId()).getOpponentGrids().get(PONE.getId()).get(4, 0));
        assertEquals(GridCellState.KnownShip, game.getPlayerDetails().get(PONE.getId()).getOpponentViews().get(PTWO.getId()).get(0, 0));
        assertEquals(GridCellState.KnownShip, game.getPlayerDetails().get(PONE.getId()).getOpponentViews().get(PTWO.getId()).get(1, 0));
        assertEquals(GridCellState.KnownShip, game.getPlayerDetails().get(PONE.getId()).getOpponentViews().get(PTWO.getId()).get(2, 0));
        assertEquals(GridCellState.KnownShip, game.getPlayerDetails().get(PONE.getId()).getOpponentViews().get(PTWO.getId()).get(3, 0));
        assertEquals(GridCellState.Unknown, game.getPlayerDetails().get(PONE.getId()).getOpponentViews().get(PTWO.getId()).get(4, 0));
        assertEquals(GridCellState.KnownShip, game.getPlayerDetails().get(PTHREE.getId()).getOpponentGrids().get(PONE.getId()).get(3, 0));
        assertEquals(GridCellState.KnownShip, game.getPlayerDetails().get(PONE.getId()).getOpponentViews().get(PTHREE.getId()).get(3, 0));
        game.getPlayerDetails().forEach((key, value) -> {
            assertEquals("100000000000000000000000 repaired their Aircraft Carrier.", getLastEntry(value.getActionLog()).getDescription());
            assertEquals(TBActionLogEntry.TBActionType.Repaired, getLastEntry(value.getActionLog()).getActionType());

        });
        assertEquals(0, game.getPlayerDetails().get(PONE.getId()).getEmergencyRepairsRemaining());
        game.getPlayerDetails().get(PONE.getId()).getShipStates().stream()
                .filter(ss -> Ship.Carrier.equals(ss.getShip()))
                .forEach(ss -> {
                    assertEquals(5, ss.getHealthRemaining());
                    assertEquals(Arrays.asList(false, false, false, false, false), ss.getShipSegmentHit());
                });
    }

    @Test(expected = CannotRepairADestroyedShipException.class)
    public void testRepairOnDestroyedShip() {
        game.getPlayerDetails().get(PONE.getId()).setEmergencyRepairsRemaining(1);
        game.getPlayerDetails().get(PONE.getId()).getShipStates()
                .stream()
                .filter(ss -> Ship.Carrier.equals(ss.getShip()))
                .forEach(ss -> {
                    ss.setHealthRemaining(0);
                    ss.setShipSegmentHit(Arrays.asList(true, true, true, true, true));
                });
        game.getPlayerDetails().get(PTWO.getId()).getOpponentGrids().get(PONE.getId()).set(0, 0, GridCellState.KnownShip);
        game.getPlayerDetails().get(PTWO.getId()).getOpponentGrids().get(PONE.getId()).set(1, 0, GridCellState.KnownByHit);
        game.getPlayerDetails().get(PTWO.getId()).getOpponentGrids().get(PONE.getId()).set(2, 0, GridCellState.KnownByRehit);
        game.getPlayerDetails().get(PTWO.getId()).getOpponentGrids().get(PONE.getId()).set(3, 0, GridCellState.KnownByOtherHit);
        game.getPlayerDetails().get(PTWO.getId()).getOpponentGrids().get(PONE.getId()).set(4, 0, GridCellState.Unknown);
        game.getPlayerDetails().get(PONE.getId()).getOpponentViews().get(PTWO.getId()).set(0, 0, GridCellState.KnownShip);
        game.getPlayerDetails().get(PONE.getId()).getOpponentViews().get(PTWO.getId()).set(1, 0, GridCellState.KnownByHit);
        game.getPlayerDetails().get(PONE.getId()).getOpponentViews().get(PTWO.getId()).set(2, 0, GridCellState.KnownByRehit);
        game.getPlayerDetails().get(PONE.getId()).getOpponentViews().get(PTWO.getId()).set(3, 0, GridCellState.KnownByOtherHit);
        game.getPlayerDetails().get(PONE.getId()).getOpponentViews().get(PTWO.getId()).set(4, 0, GridCellState.Unknown);

        game.getPlayerDetails().get(PTHREE.getId()).getOpponentGrids().get(PONE.getId()).set(3, 0, GridCellState.KnownByHit);
        game.getPlayerDetails().get(PONE.getId()).getOpponentViews().get(PTHREE.getId()).set(3, 0, GridCellState.KnownByHit);

        handler.validateMoveSpecific(PONE, game, PONE, new GridCoordinate(4, 0));

    }
}
