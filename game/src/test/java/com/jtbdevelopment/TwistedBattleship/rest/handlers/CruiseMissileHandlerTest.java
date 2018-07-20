package com.jtbdevelopment.TwistedBattleship.rest.handlers;

import com.jtbdevelopment.TwistedBattleship.exceptions.NoCruiseMissileActionsRemaining;
import com.jtbdevelopment.TwistedBattleship.state.GameFeature;
import com.jtbdevelopment.TwistedBattleship.state.TBActionLogEntry;
import com.jtbdevelopment.TwistedBattleship.state.TBGame;
import com.jtbdevelopment.TwistedBattleship.state.TBPlayerState;
import com.jtbdevelopment.TwistedBattleship.state.grid.GridCellState;
import com.jtbdevelopment.TwistedBattleship.state.grid.GridCoordinate;
import com.jtbdevelopment.TwistedBattleship.state.ships.Ship;
import com.jtbdevelopment.TwistedBattleship.state.ships.ShipState;
import com.jtbdevelopment.games.mongo.players.MongoPlayer;
import org.bson.types.ObjectId;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;

import java.util.Arrays;
import java.util.LinkedHashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Date: 5/5/16
 * Time: 8:48 PM
 */
public class CruiseMissileHandlerTest extends AbstractBaseHandlerTest {
    private FireAtCoordinateHandler fireAtCoordinateHandler = Mockito.mock(FireAtCoordinateHandler.class);
    private CruiseMissileHandler handler = new CruiseMissileHandler(null, null, null, null, null, null, fireAtCoordinateHandler);

    @Before
    public void setUp() throws Exception {
        super.setUp();
        game.getPlayerDetails().get(PTWO.getId()).getOpponentGrids().get(PONE.getId()).set(1, 0, GridCellState.KnownByHit);
        game.getPlayerDetails().get(PTWO.getId()).getOpponentGrids().get(PONE.getId()).set(2, 0, GridCellState.KnownByOtherHit);
        game.getPlayerDetails().get(PTWO.getId()).getOpponentGrids().get(PONE.getId()).set(1, 1, GridCellState.KnownByMiss);
        game.getPlayerDetails().get(PONE.getId()).getOpponentViews().get(PTWO.getId()).set(1, 0, GridCellState.KnownByHit);
        game.getPlayerDetails().get(PONE.getId()).getOpponentViews().get(PTWO.getId()).set(2, 0, GridCellState.KnownByOtherHit);
        game.getPlayerDetails().get(PONE.getId()).getOpponentViews().get(PTWO.getId()).set(1, 1, GridCellState.KnownByMiss);

        game.getPlayerDetails().get(PFOUR.getId()).getOpponentGrids().get(PONE.getId()).set(2, 0, GridCellState.KnownByHit);
        game.getPlayerDetails().get(PONE.getId()).getOpponentViews().get(PFOUR.getId()).set(2, 0, GridCellState.KnownByHit);

        ShipState shipState = game.getPlayerDetails().get(PONE.getId()).getShipStates().stream().filter(s -> Ship.Carrier.equals(s.getShip())).findFirst().get();
        shipState.setHealthRemaining(3);
        shipState.setShipSegmentHit(Arrays.asList(false, true, true, false, false));

        when(fireAtCoordinateHandler.playMove(any(MongoPlayer.class), any(TBGame.class), any(MongoPlayer.class), any(GridCoordinate.class))).then((Answer<TBGame>) invocation -> (TBGame) invocation.getArguments()[1]);
    }

    @Test
    public void testTargetSelf() {
        Assert.assertFalse(handler.targetSelf());
    }

    @Test
    public void testMovesRequired() {
        TBGame game = new TBGame();
        game.setMovesForSpecials(1);
        assertEquals(1, handler.movesRequired(game));

        game = new TBGame();
        game.setMovesForSpecials(2);
        assertEquals(2, handler.movesRequired(game));
    }

    @Test(expected = NoCruiseMissileActionsRemaining.class)
    public void testValidatesNoMissilesRemain() {
        TBGame game = new TBGame();
        LinkedHashMap<ObjectId, TBPlayerState> map = new LinkedHashMap<>(1);
        TBPlayerState state = new TBPlayerState();
        state.setCruiseMissilesRemaining(1);
        map.put(PONE.getId(), state);

        game.setPlayerDetails(map);
        handler.validateMoveSpecific(PONE, game, PTWO, null);
        game.getPlayerDetails().get(PONE.getId()).setCruiseMissilesRemaining(0);
        handler.validateMoveSpecific(PONE, game, PTWO, null);
    }

    @Test
    public void testCruiseMissileMissAndSharedIntel() {
        game.getFeatures().add(GameFeature.SharedIntel);

        GridCoordinate coordinate = new GridCoordinate(7, 6);
        assertSame(game, handler.playMove(PTWO, game, PONE, coordinate));
        assertEquals("You fired a cruise missile at 100000000000000000000000 (7,6) and missed.", getLastEntry(game.getPlayerDetails().get(PTWO.getId()).getActionLog()).getDescription());
        assertEquals(TBActionLogEntry.TBActionType.CruiseMissile, getLastEntry(game.getPlayerDetails().get(PTWO.getId()).getActionLog()).getActionType());
        assertEquals("200000000000000000000000 fired a cruise missile at (7,6) and missed.", getLastEntry(game.getPlayerDetails().get(PONE.getId()).getActionLog()).getDescription());
        assertEquals(TBActionLogEntry.TBActionType.CruiseMissile, getLastEntry(game.getPlayerDetails().get(PONE.getId()).getActionLog()).getActionType());
        assertEquals("200000000000000000000000 fired a cruise missile at 100000000000000000000000 (7,6) and missed.", getLastEntry(game.getPlayerDetails().get(PTHREE.getId()).getActionLog()).getDescription());
        assertEquals(TBActionLogEntry.TBActionType.CruiseMissile, getLastEntry(game.getPlayerDetails().get(PTHREE.getId()).getActionLog()).getActionType());
        assertEquals("200000000000000000000000 fired a cruise missile at 100000000000000000000000 (7,6) and missed.", getLastEntry(game.getPlayerDetails().get(PFOUR.getId()).getActionLog()).getDescription());
        assertEquals(TBActionLogEntry.TBActionType.CruiseMissile, getLastEntry(game.getPlayerDetails().get(PFOUR.getId()).getActionLog()).getActionType());
        verify(fireAtCoordinateHandler).playMove(PTWO, game, PONE, coordinate);
    }

    @Test
    public void testCruiseMissileHitAndSharedIntel() {
        game.getFeatures().add(GameFeature.SharedIntel);

        GridCoordinate coordinate = new GridCoordinate(13, 0);
        assertSame(game, handler.playMove(PTWO, game, PONE, coordinate));
        assertEquals("You fired a cruise missile at 100000000000000000000000 (13,0) and hit!", getLastEntry(game.getPlayerDetails().get(PTWO.getId()).getActionLog()).getDescription());
        assertEquals(TBActionLogEntry.TBActionType.CruiseMissile, getLastEntry(game.getPlayerDetails().get(PTWO.getId()).getActionLog()).getActionType());
        assertEquals("200000000000000000000000 fired a cruise missile at (13,0) and hit!", getLastEntry(game.getPlayerDetails().get(PONE.getId()).getActionLog()).getDescription());
        assertEquals(TBActionLogEntry.TBActionType.CruiseMissile, getLastEntry(game.getPlayerDetails().get(PONE.getId()).getActionLog()).getActionType());
        assertEquals("200000000000000000000000 fired a cruise missile at 100000000000000000000000 (13,0) and hit!", getLastEntry(game.getPlayerDetails().get(PTHREE.getId()).getActionLog()).getDescription());
        assertEquals(TBActionLogEntry.TBActionType.CruiseMissile, getLastEntry(game.getPlayerDetails().get(PTHREE.getId()).getActionLog()).getActionType());
        assertEquals("200000000000000000000000 fired a cruise missile at 100000000000000000000000 (13,0) and hit!", getLastEntry(game.getPlayerDetails().get(PTHREE.getId()).getActionLog()).getDescription());
        assertEquals(TBActionLogEntry.TBActionType.CruiseMissile, getLastEntry(game.getPlayerDetails().get(PFOUR.getId()).getActionLog()).getActionType());
        verify(fireAtCoordinateHandler).playMove(PTWO, game, PONE, new GridCoordinate(12, 0));
        verify(fireAtCoordinateHandler).playMove(PTWO, game, PONE, new GridCoordinate(13, 0));
        verify(fireAtCoordinateHandler).playMove(PTWO, game, PONE, new GridCoordinate(14, 0));
    }

    @Test
    public void testCruiseMissileMissAndIsolatedIntel() {
        game.getFeatures().add(GameFeature.IsolatedIntel);

        GridCoordinate coordinate = new GridCoordinate(7, 6);
        assertSame(game, handler.playMove(PTWO, game, PONE, coordinate));
        assertEquals("You fired a cruise missile at 100000000000000000000000 (7,6) and missed.", getLastEntry(game.getPlayerDetails().get(PTWO.getId()).getActionLog()).getDescription());
        assertEquals(TBActionLogEntry.TBActionType.CruiseMissile, getLastEntry(game.getPlayerDetails().get(PTWO.getId()).getActionLog()).getActionType());
        assertEquals("200000000000000000000000 fired a cruise missile at (7,6) and missed.", getLastEntry(game.getPlayerDetails().get(PONE.getId()).getActionLog()).getDescription());
        assertEquals(TBActionLogEntry.TBActionType.CruiseMissile, getLastEntry(game.getPlayerDetails().get(PONE.getId()).getActionLog()).getActionType());
        assertEquals("200000000000000000000000 fired a cruise missile at 100000000000000000000000.", getLastEntry(game.getPlayerDetails().get(PTHREE.getId()).getActionLog()).getDescription());
        assertEquals(TBActionLogEntry.TBActionType.CruiseMissile, getLastEntry(game.getPlayerDetails().get(PTHREE.getId()).getActionLog()).getActionType());
        assertEquals("200000000000000000000000 fired a cruise missile at 100000000000000000000000.", getLastEntry(game.getPlayerDetails().get(PFOUR.getId()).getActionLog()).getDescription());
        assertEquals(TBActionLogEntry.TBActionType.CruiseMissile, getLastEntry(game.getPlayerDetails().get(PFOUR.getId()).getActionLog()).getActionType());
        verify(fireAtCoordinateHandler).playMove(PTWO, game, PONE, coordinate);
    }

    @Test
    public void testCruiseMissileHitAndIsolatedIntel() {
        game.getFeatures().add(GameFeature.IsolatedIntel);

        GridCoordinate coordinate = new GridCoordinate(13, 0);
        assertSame(game, handler.playMove(PTWO, game, PONE, coordinate));
        assertEquals("You fired a cruise missile at 100000000000000000000000 (13,0) and hit!", getLastEntry(game.getPlayerDetails().get(PTWO.getId()).getActionLog()).getDescription());
        assertEquals(TBActionLogEntry.TBActionType.CruiseMissile, getLastEntry(game.getPlayerDetails().get(PTWO.getId()).getActionLog()).getActionType());
        assertEquals("200000000000000000000000 fired a cruise missile at (13,0) and hit!", getLastEntry(game.getPlayerDetails().get(PONE.getId()).getActionLog()).getDescription());
        assertEquals(TBActionLogEntry.TBActionType.CruiseMissile, getLastEntry(game.getPlayerDetails().get(PONE.getId()).getActionLog()).getActionType());
        assertEquals("200000000000000000000000 fired a cruise missile at 100000000000000000000000.", getLastEntry(game.getPlayerDetails().get(PTHREE.getId()).getActionLog()).getDescription());
        assertEquals(TBActionLogEntry.TBActionType.CruiseMissile, getLastEntry(game.getPlayerDetails().get(PTHREE.getId()).getActionLog()).getActionType());
        assertEquals("200000000000000000000000 fired a cruise missile at 100000000000000000000000.", getLastEntry(game.getPlayerDetails().get(PFOUR.getId()).getActionLog()).getDescription());
        assertEquals(TBActionLogEntry.TBActionType.CruiseMissile, getLastEntry(game.getPlayerDetails().get(PFOUR.getId()).getActionLog()).getActionType());
        verify(fireAtCoordinateHandler).playMove(eq(PTWO), eq(game), eq(PONE), eq(new GridCoordinate(12, 0)));
        verify(fireAtCoordinateHandler).playMove(PTWO, game, PONE, new GridCoordinate(13, 0));
        verify(fireAtCoordinateHandler).playMove(PTWO, game, PONE, new GridCoordinate(14, 0));
    }
}
