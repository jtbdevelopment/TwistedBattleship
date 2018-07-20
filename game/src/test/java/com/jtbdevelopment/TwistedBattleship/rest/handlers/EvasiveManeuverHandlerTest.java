package com.jtbdevelopment.TwistedBattleship.rest.handlers;

import com.jtbdevelopment.TwistedBattleship.exceptions.NoEmergencyManeuverActionsRemainException;
import com.jtbdevelopment.TwistedBattleship.exceptions.NoShipAtCoordinateException;
import com.jtbdevelopment.TwistedBattleship.rest.handlers.helpers.FogCoordinatesGenerator;
import com.jtbdevelopment.TwistedBattleship.rest.handlers.helpers.ShipRelocator;
import com.jtbdevelopment.TwistedBattleship.state.TBActionLogEntry;
import com.jtbdevelopment.TwistedBattleship.state.TBGame;
import com.jtbdevelopment.TwistedBattleship.state.grid.GridCellState;
import com.jtbdevelopment.TwistedBattleship.state.grid.GridCoordinate;
import com.jtbdevelopment.TwistedBattleship.state.ships.Ship;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import static org.mockito.Mockito.mock;

/**
 * Date: 5/21/15
 * Time: 6:41 AM
 */
public class EvasiveManeuverHandlerTest extends AbstractBaseHandlerTest {
    private ShipRelocator shipRelocator = mock(ShipRelocator.class);
    private FogCoordinatesGenerator fogCoordinatesGenerator = mock(FogCoordinatesGenerator.class);

    private EvasiveManeuverHandler handler = new EvasiveManeuverHandler(null, null, null, null, null, null, shipRelocator, fogCoordinatesGenerator);

    @Test
    public void testTargetSelf() {
        Assert.assertTrue(handler.targetSelf());
    }

    @Test
    public void testMovesRequired() {
        TBGame game = new TBGame();
        game.setMovesForSpecials(1);
        Assert.assertEquals(1, handler.movesRequired(game));
        game = new TBGame();
        game.setMovesForSpecials(2);
        Assert.assertEquals(2, handler.movesRequired(game));
    }

    @Test(expected = NoEmergencyManeuverActionsRemainException.class)
    public void testValidatesRepairsRemain() {
        game.getPlayerDetails().get(PONE.getId()).setEvasiveManeuversRemaining(1);
        handler.validateMoveSpecific(PONE, game, PONE, new GridCoordinate(3, 0));
        game.getPlayerDetails().get(PONE.getId()).setEvasiveManeuversRemaining(0);
        handler.validateMoveSpecific(PONE, game, PONE, new GridCoordinate(3, 0));
    }

    @Test(expected = NoShipAtCoordinateException.class)
    public void testValidatesShipExistsAtCoordinate() {
        game.getPlayerDetails().get(PONE.getId()).setEmergencyRepairsRemaining(1);
        handler.validateMoveSpecific(PONE, game, PONE, new GridCoordinate(3, 1));
    }

    @Test
    public void testManeuver() {
        Assert.assertEquals(3, game.getPlayerDetails().get(PONE.getId()).getEvasiveManeuversRemaining());
        game.getPlayerDetails().get(PTWO.getId()).getOpponentGrids().get(PONE.getId()).set(0, 0, GridCellState.KnownShip);
        game.getPlayerDetails().get(PTWO.getId()).getOpponentGrids().get(PONE.getId()).set(1, 0, GridCellState.KnownByHit);
        game.getPlayerDetails().get(PTWO.getId()).getOpponentGrids().get(PONE.getId()).set(2, 0, GridCellState.KnownByOtherHit);
        game.getPlayerDetails().get(PTWO.getId()).getOpponentGrids().get(PONE.getId()).set(1, 1, GridCellState.KnownByMiss);
        game.getPlayerDetails().get(PTWO.getId()).getOpponentGrids().get(PONE.getId()).set(2, 1, GridCellState.KnownEmpty);
        game.getPlayerDetails().get(PTWO.getId()).getOpponentGrids().get(PONE.getId()).set(3, 1, GridCellState.KnownByOtherMiss);
        game.getPlayerDetails().get(PTWO.getId()).getOpponentGrids().get(PONE.getId()).set(5, 5, GridCellState.KnownByOtherMiss);
        game.getPlayerDetails().get(PTWO.getId()).getOpponentGrids().get(PONE.getId()).set(5, 6, GridCellState.KnownEmpty);
        game.getPlayerDetails().get(PTWO.getId()).getOpponentGrids().get(PONE.getId()).set(5, 7, GridCellState.KnownByMiss);
        game.getPlayerDetails().get(PONE.getId()).getOpponentViews().get(PTWO.getId()).set(0, 0, GridCellState.KnownShip);
        game.getPlayerDetails().get(PONE.getId()).getOpponentViews().get(PTWO.getId()).set(1, 0, GridCellState.KnownByHit);
        game.getPlayerDetails().get(PONE.getId()).getOpponentViews().get(PTWO.getId()).set(2, 0, GridCellState.KnownByOtherHit);
        game.getPlayerDetails().get(PONE.getId()).getOpponentViews().get(PTWO.getId()).set(1, 1, GridCellState.KnownByMiss);
        game.getPlayerDetails().get(PONE.getId()).getOpponentViews().get(PTWO.getId()).set(2, 1, GridCellState.KnownEmpty);
        game.getPlayerDetails().get(PONE.getId()).getOpponentViews().get(PTWO.getId()).set(3, 1, GridCellState.KnownByOtherMiss);
        game.getPlayerDetails().get(PONE.getId()).getOpponentViews().get(PTWO.getId()).set(5, 5, GridCellState.KnownByOtherMiss);
        game.getPlayerDetails().get(PONE.getId()).getOpponentViews().get(PTWO.getId()).set(5, 6, GridCellState.KnownEmpty);
        game.getPlayerDetails().get(PONE.getId()).getOpponentViews().get(PTWO.getId()).set(5, 7, GridCellState.KnownByMiss);

        game.getPlayerDetails().get(PTHREE.getId()).getOpponentGrids().get(PONE.getId()).set(0, 0, GridCellState.Unknown);
        game.getPlayerDetails().get(PTHREE.getId()).getOpponentGrids().get(PONE.getId()).set(1, 0, GridCellState.KnownByRehit);
        game.getPlayerDetails().get(PTHREE.getId()).getOpponentGrids().get(PONE.getId()).set(2, 0, GridCellState.KnownByHit);
        game.getPlayerDetails().get(PONE.getId()).getOpponentViews().get(PTHREE.getId()).set(0, 0, GridCellState.Unknown);
        game.getPlayerDetails().get(PONE.getId()).getOpponentViews().get(PTHREE.getId()).set(1, 0, GridCellState.KnownByRehit);
        game.getPlayerDetails().get(PONE.getId()).getOpponentViews().get(PTHREE.getId()).set(2, 0, GridCellState.KnownByHit);
        List<GridCoordinate> initialCoordinates = game.getPlayerDetails().get(PONE.getId()).getShipStates().stream()
                .filter(ss -> ss.getShip().equals(Ship.Carrier))
                .flatMap(ss -> ss.getShipGridCells().stream())
                .collect(Collectors.toList());
        List<GridCoordinate> newCoordinates = Arrays.asList(new GridCoordinate(5, 5), new GridCoordinate(5, 6), new GridCoordinate(5, 7), new GridCoordinate(5, 8), new GridCoordinate(5, 9));
        Mockito.when(shipRelocator.relocateShip(
                game,
                game.getPlayerDetails().get(PONE.getId()),
                game.getPlayerDetails().get(PONE.getId()).getShipStates().stream()
                        .filter(ss -> Ship.Carrier.equals(ss.getShip())).findFirst().get()))
                .thenReturn(newCoordinates);
        HashSet<GridCoordinate> fog = new HashSet<>(initialCoordinates);
        fog.addAll(newCoordinates);
        Mockito.when(fogCoordinatesGenerator.generateFogCoordinates(game, initialCoordinates, newCoordinates)).thenReturn(fog);
        handler.playMove(PONE, game, PONE, new GridCoordinate(1, 0));

        Assert.assertEquals(GridCellState.ObscuredShip, game.getPlayerDetails().get(PTWO.getId()).getOpponentGrids().get(PONE.getId()).get(0, 0));
        Assert.assertEquals(GridCellState.ObscuredHit, game.getPlayerDetails().get(PTWO.getId()).getOpponentGrids().get(PONE.getId()).get(1, 0));
        Assert.assertEquals(GridCellState.ObscuredOtherHit, game.getPlayerDetails().get(PTWO.getId()).getOpponentGrids().get(PONE.getId()).get(2, 0));
        Assert.assertEquals(GridCellState.KnownByMiss, game.getPlayerDetails().get(PTWO.getId()).getOpponentGrids().get(PONE.getId()).get(1, 1));
        Assert.assertEquals(GridCellState.KnownEmpty, game.getPlayerDetails().get(PTWO.getId()).getOpponentGrids().get(PONE.getId()).get(2, 1));
        Assert.assertEquals(GridCellState.KnownByOtherMiss, game.getPlayerDetails().get(PTWO.getId()).getOpponentGrids().get(PONE.getId()).get(3, 1));
        Assert.assertEquals(GridCellState.ObscuredOtherMiss, game.getPlayerDetails().get(PTWO.getId()).getOpponentGrids().get(PONE.getId()).get(5, 5));
        Assert.assertEquals(GridCellState.ObscuredEmpty, game.getPlayerDetails().get(PTWO.getId()).getOpponentGrids().get(PONE.getId()).get(5, 6));
        Assert.assertEquals(GridCellState.ObscuredMiss, game.getPlayerDetails().get(PTWO.getId()).getOpponentGrids().get(PONE.getId()).get(5, 7));
        Assert.assertEquals(GridCellState.ObscuredShip, game.getPlayerDetails().get(PONE.getId()).getOpponentViews().get(PTWO.getId()).get(0, 0));
        Assert.assertEquals(GridCellState.ObscuredHit, game.getPlayerDetails().get(PONE.getId()).getOpponentViews().get(PTWO.getId()).get(1, 0));
        Assert.assertEquals(GridCellState.ObscuredOtherHit, game.getPlayerDetails().get(PONE.getId()).getOpponentViews().get(PTWO.getId()).get(2, 0));
        Assert.assertEquals(GridCellState.KnownByMiss, game.getPlayerDetails().get(PONE.getId()).getOpponentViews().get(PTWO.getId()).get(1, 1));
        Assert.assertEquals(GridCellState.KnownEmpty, game.getPlayerDetails().get(PONE.getId()).getOpponentViews().get(PTWO.getId()).get(2, 1));
        Assert.assertEquals(GridCellState.KnownByOtherMiss, game.getPlayerDetails().get(PONE.getId()).getOpponentViews().get(PTWO.getId()).get(3, 1));
        Assert.assertEquals(GridCellState.ObscuredOtherMiss, game.getPlayerDetails().get(PONE.getId()).getOpponentViews().get(PTWO.getId()).get(5, 5));
        Assert.assertEquals(GridCellState.ObscuredEmpty, game.getPlayerDetails().get(PONE.getId()).getOpponentViews().get(PTWO.getId()).get(5, 6));
        Assert.assertEquals(GridCellState.ObscuredMiss, game.getPlayerDetails().get(PONE.getId()).getOpponentViews().get(PTWO.getId()).get(5, 7));

        Assert.assertEquals(GridCellState.Unknown, game.getPlayerDetails().get(PTHREE.getId()).getOpponentGrids().get(PONE.getId()).get(0, 0));
        Assert.assertEquals(GridCellState.ObscuredRehit, game.getPlayerDetails().get(PTHREE.getId()).getOpponentGrids().get(PONE.getId()).get(1, 0));
        Assert.assertEquals(GridCellState.ObscuredHit, game.getPlayerDetails().get(PTHREE.getId()).getOpponentGrids().get(PONE.getId()).get(2, 0));
        Assert.assertEquals(GridCellState.Unknown, game.getPlayerDetails().get(PONE.getId()).getOpponentViews().get(PTHREE.getId()).get(0, 0));
        Assert.assertEquals(GridCellState.ObscuredRehit, game.getPlayerDetails().get(PONE.getId()).getOpponentViews().get(PTHREE.getId()).get(1, 0));
        Assert.assertEquals(GridCellState.ObscuredHit, game.getPlayerDetails().get(PONE.getId()).getOpponentViews().get(PTHREE.getId()).get(2, 0));

        game.getPlayerDetails().forEach((id, state) -> {
            Assert.assertEquals("100000000000000000000000 performed evasive maneuvers.", getLastEntry(state.getActionLog()).getDescription());
            Assert.assertEquals(TBActionLogEntry.TBActionType.PerformedManeuvers, getLastEntry(state.getActionLog()).getActionType());
        });
        Assert.assertEquals(2, game.getPlayerDetails().get(PONE.getId()).getEvasiveManeuversRemaining());
    }
}
