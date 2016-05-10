package com.jtbdevelopment.TwistedBattleship.rest.handlers

import com.jtbdevelopment.TwistedBattleship.exceptions.NoCruiseMissileActionsRemaining
import com.jtbdevelopment.TwistedBattleship.state.GameFeature
import com.jtbdevelopment.TwistedBattleship.state.TBActionLogEntry
import com.jtbdevelopment.TwistedBattleship.state.TBGame
import com.jtbdevelopment.TwistedBattleship.state.TBPlayerState
import com.jtbdevelopment.TwistedBattleship.state.grid.GridCellState
import com.jtbdevelopment.TwistedBattleship.state.grid.GridCoordinate
import com.jtbdevelopment.TwistedBattleship.state.ships.Ship
import com.jtbdevelopment.games.players.Player
import org.bson.types.ObjectId

/**
 * Date: 5/5/16
 * Time: 8:48 PM
 */
class CruiseMissileHandlerTest extends AbstractBaseHandlerTest {
    CruiseMissileHandler handler = new CruiseMissileHandler();

    List<Object> calls = [];
    FireAtCoordinateHandler fireAtCoordinateHandler = [
            playMove: {
                Player<ObjectId> player, TBGame game, Player<ObjectId> targetted, GridCoordinate coordinate ->
                    calls += [
                            Player    : player,
                            Game      : game,
                            Target    : targetted,
                            Coordinate: coordinate
                    ]
                    game
            }
    ] as FireAtCoordinateHandler;


    @Override
    protected void setUp() throws Exception {
        super.setUp()
        calls.clear()
        game.playerDetails[PTWO.id].opponentGrids[PONE.id].set(1, 0, GridCellState.KnownByHit)
        game.playerDetails[PTWO.id].opponentGrids[PONE.id].set(2, 0, GridCellState.KnownByOtherHit)
        game.playerDetails[PTWO.id].opponentGrids[PONE.id].set(1, 1, GridCellState.KnownByMiss)
        game.playerDetails[PONE.id].opponentViews[PTWO.id].set(1, 0, GridCellState.KnownByHit)
        game.playerDetails[PONE.id].opponentViews[PTWO.id].set(2, 0, GridCellState.KnownByOtherHit)
        game.playerDetails[PONE.id].opponentViews[PTWO.id].set(1, 1, GridCellState.KnownByMiss)

        game.playerDetails[PFOUR.id].opponentGrids[PONE.id].set(2, 0, GridCellState.KnownByHit)
        game.playerDetails[PONE.id].opponentViews[PFOUR.id].set(2, 0, GridCellState.KnownByHit)

        game.playerDetails[PONE.id].shipStates.find { it.ship == Ship.Carrier }.healthRemaining = 3
        game.playerDetails[PONE.id].shipStates.find {
            it.ship == Ship.Carrier
        }.shipSegmentHit = [false, true, true, false, false]

        handler.fireAtCoordinateHandler = fireAtCoordinateHandler
    }

    void testTargetSelf() {
        assertFalse handler.targetSelf()
    }

    void testMovesRequired() {
        TBGame game = new TBGame(movesForSpecials: 1)
        assert 1 == handler.movesRequired(game)
        game = new TBGame(movesForSpecials: 2)
        assert 2 == handler.movesRequired(game)
    }

    void testValidatesNoMissilesRemain() {
        TBGame game = new TBGame(
                playerDetails: [
                        (PONE.id): new TBPlayerState(cruiseMissilesRemaining: 1)
                ]
        )
        handler.validateMoveSpecific(PONE, game, PTWO, null)
        game.playerDetails[PONE.id].cruiseMissilesRemaining = 0
        shouldFail(NoCruiseMissileActionsRemaining.class, {
            handler.validateMoveSpecific(PONE, game, PTWO, null)
        })
    }

    void testCruiseMissileMissAndSharedIntel() {
        game.features.add(GameFeature.SharedIntel)

        def coordinate = new GridCoordinate(7, 6)
        assert game.is(handler.playMove(PTWO, game, PONE, coordinate))
        assert "You fired a cruise missile at 1 (7,6) and missed." == game.playerDetails[PTWO.id].actionLog[-1].description
        assert TBActionLogEntry.TBActionType.CruiseMissile == game.playerDetails[PTWO.id].actionLog[-1].actionType
        assert "2 fired a cruise missile at (7,6) and missed." == game.playerDetails[PONE.id].actionLog[-1].description
        assert TBActionLogEntry.TBActionType.CruiseMissile == game.playerDetails[PONE.id].actionLog[-1].actionType
        assert "2 fired a cruise missile at 1 (7,6) and missed." == game.playerDetails[PTHREE.id].actionLog[-1].description
        assert TBActionLogEntry.TBActionType.CruiseMissile == game.playerDetails[PTHREE.id].actionLog[-1].actionType
        assert "2 fired a cruise missile at 1 (7,6) and missed." == game.playerDetails[PFOUR.id].actionLog[-1].description
        assert TBActionLogEntry.TBActionType.CruiseMissile == game.playerDetails[PFOUR.id].actionLog[-1].actionType
        assert [[Player: PTWO, Game: game, Target: PONE, Coordinate: coordinate]] == calls
    }

    void testCruiseMissileMissAndIsolatedIntel() {
        game.features.add(GameFeature.IsolatedIntel)

        def coordinate = new GridCoordinate(7, 6)
        assert game.is(handler.playMove(PTWO, game, PONE, coordinate))
        assert "You fired a cruise missile at 1 (7,6) and missed." == game.playerDetails[PTWO.id].actionLog[-1].description
        assert TBActionLogEntry.TBActionType.CruiseMissile == game.playerDetails[PTWO.id].actionLog[-1].actionType
        assert "2 fired a cruise missile at (7,6) and missed." == game.playerDetails[PONE.id].actionLog[-1].description
        assert TBActionLogEntry.TBActionType.CruiseMissile == game.playerDetails[PONE.id].actionLog[-1].actionType
        assert "2 fired a cruise missile at 1." == game.playerDetails[PTHREE.id].actionLog[-1].description
        assert TBActionLogEntry.TBActionType.CruiseMissile == game.playerDetails[PTHREE.id].actionLog[-1].actionType
        assert "2 fired a cruise missile at 1." == game.playerDetails[PFOUR.id].actionLog[-1].description
        assert TBActionLogEntry.TBActionType.CruiseMissile == game.playerDetails[PFOUR.id].actionLog[-1].actionType
        assert [[Player: PTWO, Game: game, Target: PONE, Coordinate: coordinate]] == calls
    }
}
