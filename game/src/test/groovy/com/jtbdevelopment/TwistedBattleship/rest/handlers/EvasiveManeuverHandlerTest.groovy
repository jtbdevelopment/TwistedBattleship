package com.jtbdevelopment.TwistedBattleship.rest.handlers

import com.jtbdevelopment.TwistedBattleship.exceptions.NoEmergencyManeuverActionsRemainException
import com.jtbdevelopment.TwistedBattleship.exceptions.NoShipAtCoordinateException
import com.jtbdevelopment.TwistedBattleship.rest.handlers.helpers.FogCoordinatesGenerator
import com.jtbdevelopment.TwistedBattleship.rest.handlers.helpers.ShipRelocator
import com.jtbdevelopment.TwistedBattleship.state.GameFeature
import com.jtbdevelopment.TwistedBattleship.state.TBGame
import com.jtbdevelopment.TwistedBattleship.state.TBPlayerState
import com.jtbdevelopment.TwistedBattleship.state.grid.GridCellState
import com.jtbdevelopment.TwistedBattleship.state.grid.GridCoordinate
import com.jtbdevelopment.TwistedBattleship.state.ships.Ship
import com.jtbdevelopment.TwistedBattleship.state.ships.ShipState

/**
 * Date: 5/21/15
 * Time: 6:41 AM
 */
class EvasiveManeuverHandlerTest extends AbstractBaseHandlerTest {
    EvasiveManeuverHandler handler = new EvasiveManeuverHandler()

    void testTargetSelf() {
        assert handler.targetSelf()
    }

    void testMovesRequired() {
        TBGame game = new TBGame()
        assert 1 == handler.movesRequired(game)
        game.features.add(GameFeature.PerShip)
        assert 2 == handler.movesRequired(game)
    }

    void testValidatesRepairsRemain() {
        game.playerDetails[PONE.id].evasiveManeuversRemaining = 1
        handler.validateMoveSpecific(PONE, game, PONE, new GridCoordinate(3, 0))
        game.playerDetails[PONE.id].evasiveManeuversRemaining = 0
        shouldFail(NoEmergencyManeuverActionsRemainException.class, {
            handler.validateMoveSpecific(PONE, game, PONE, new GridCoordinate(3, 0))
        })
    }

    void testValidatesShipExistsAtCoordinate() {
        game.playerDetails[PONE.id].emergencyRepairsRemaining = 1
        shouldFail(NoShipAtCoordinateException.class, {
            handler.validateMoveSpecific(PONE, game, PONE, new GridCoordinate(3, 1))
        })
    }

    //  No need to really test isolated vs shared
    void testManeuver() {
        assert 3 == game.playerDetails[PONE.id].evasiveManeuversRemaining
        game.playerDetails[PTWO.id].opponentGrids[PONE.id].set(0, 0, GridCellState.KnownShip)
        game.playerDetails[PTWO.id].opponentGrids[PONE.id].set(1, 0, GridCellState.KnownByHit)
        game.playerDetails[PTWO.id].opponentGrids[PONE.id].set(2, 0, GridCellState.KnownByOtherHit)
        game.playerDetails[PTWO.id].opponentGrids[PONE.id].set(1, 1, GridCellState.KnownByMiss)
        game.playerDetails[PTWO.id].opponentGrids[PONE.id].set(2, 1, GridCellState.KnownEmpty)
        game.playerDetails[PTWO.id].opponentGrids[PONE.id].set(3, 1, GridCellState.KnownByOtherMiss)
        game.playerDetails[PTWO.id].opponentGrids[PONE.id].set(5, 5, GridCellState.KnownByOtherMiss)
        game.playerDetails[PTWO.id].opponentGrids[PONE.id].set(5, 6, GridCellState.KnownEmpty)
        game.playerDetails[PTWO.id].opponentGrids[PONE.id].set(5, 7, GridCellState.KnownByMiss)
        game.playerDetails[PONE.id].opponentViews[PTWO.id].set(0, 0, GridCellState.KnownShip)
        game.playerDetails[PONE.id].opponentViews[PTWO.id].set(1, 0, GridCellState.KnownByHit)
        game.playerDetails[PONE.id].opponentViews[PTWO.id].set(2, 0, GridCellState.KnownByOtherHit)
        game.playerDetails[PONE.id].opponentViews[PTWO.id].set(1, 1, GridCellState.KnownByMiss)
        game.playerDetails[PONE.id].opponentViews[PTWO.id].set(2, 1, GridCellState.KnownEmpty)
        game.playerDetails[PONE.id].opponentViews[PTWO.id].set(3, 1, GridCellState.KnownByOtherMiss)
        game.playerDetails[PONE.id].opponentViews[PTWO.id].set(5, 5, GridCellState.KnownByOtherMiss)
        game.playerDetails[PONE.id].opponentViews[PTWO.id].set(5, 6, GridCellState.KnownEmpty)
        game.playerDetails[PONE.id].opponentViews[PTWO.id].set(5, 7, GridCellState.KnownByMiss)

        game.playerDetails[PTHREE.id].opponentGrids[PONE.id].set(0, 0, GridCellState.Unknown)
        game.playerDetails[PTHREE.id].opponentGrids[PONE.id].set(1, 0, GridCellState.KnownByRehit)
        game.playerDetails[PTHREE.id].opponentGrids[PONE.id].set(2, 0, GridCellState.KnownByHit)
        game.playerDetails[PONE.id].opponentViews[PTHREE.id].set(0, 0, GridCellState.Unknown)
        game.playerDetails[PONE.id].opponentViews[PTHREE.id].set(1, 0, GridCellState.KnownByRehit)
        game.playerDetails[PONE.id].opponentViews[PTHREE.id].set(2, 0, GridCellState.KnownByHit)
        def initialCoordinates = game.playerDetails[PONE.id].shipStates[Ship.Carrier].shipGridCells
        def newCoordinates = [new GridCoordinate(5, 5), new GridCoordinate(5, 6), new GridCoordinate(5, 7), new GridCoordinate(5, 8), new GridCoordinate(5, 9)]
        handler.shipRelocator = [
                relocateShip: {
                    TBGame g, TBPlayerState ps, ShipState ss ->
                        assert game.is(g)
                        assert game.playerDetails[PONE.id].is(ps)
                        assert game.playerDetails[PONE.id].shipStates[Ship.Carrier].is(ss)
                        newCoordinates
                }
        ] as ShipRelocator
        handler.fogCoordinatesGenerator = [
                generateFogCoordinates: {
                    TBGame g, List<GridCoordinate> o, List<GridCoordinate> n ->
                        assert game.is(g)
                        assert o.is(initialCoordinates)
                        assert n.is(newCoordinates)
                        return (o + n) as Set
                }
        ] as FogCoordinatesGenerator
        handler.playMove(PONE, game, PONE, new GridCoordinate(1, 0))

        assert GridCellState.ObscuredShip == game.playerDetails[PTWO.id].opponentGrids[PONE.id].get(0, 0)
        assert GridCellState.ObscuredHit == game.playerDetails[PTWO.id].opponentGrids[PONE.id].get(1, 0)
        assert GridCellState.ObscuredOtherHit == game.playerDetails[PTWO.id].opponentGrids[PONE.id].get(2, 0)
        assert GridCellState.KnownByMiss == game.playerDetails[PTWO.id].opponentGrids[PONE.id].get(1, 1)
        assert GridCellState.KnownEmpty == game.playerDetails[PTWO.id].opponentGrids[PONE.id].get(2, 1)
        assert GridCellState.KnownByOtherMiss == game.playerDetails[PTWO.id].opponentGrids[PONE.id].get(3, 1)
        assert GridCellState.ObscuredOtherMiss == game.playerDetails[PTWO.id].opponentGrids[PONE.id].get(5, 5)
        assert GridCellState.ObscuredEmpty == game.playerDetails[PTWO.id].opponentGrids[PONE.id].get(5, 6)
        assert GridCellState.ObscuredMiss == game.playerDetails[PTWO.id].opponentGrids[PONE.id].get(5, 7)
        assert GridCellState.ObscuredShip == game.playerDetails[PONE.id].opponentViews[PTWO.id].get(0, 0)
        assert GridCellState.ObscuredHit == game.playerDetails[PONE.id].opponentViews[PTWO.id].get(1, 0)
        assert GridCellState.ObscuredOtherHit == game.playerDetails[PONE.id].opponentViews[PTWO.id].get(2, 0)
        assert GridCellState.KnownByMiss == game.playerDetails[PONE.id].opponentViews[PTWO.id].get(1, 1)
        assert GridCellState.KnownEmpty == game.playerDetails[PONE.id].opponentViews[PTWO.id].get(2, 1)
        assert GridCellState.KnownByOtherMiss == game.playerDetails[PONE.id].opponentViews[PTWO.id].get(3, 1)
        assert GridCellState.ObscuredOtherMiss == game.playerDetails[PONE.id].opponentViews[PTWO.id].get(5, 5)
        assert GridCellState.ObscuredEmpty == game.playerDetails[PONE.id].opponentViews[PTWO.id].get(5, 6)
        assert GridCellState.ObscuredMiss == game.playerDetails[PONE.id].opponentViews[PTWO.id].get(5, 7)

        assert GridCellState.Unknown == game.playerDetails[PTHREE.id].opponentGrids[PONE.id].get(0, 0)
        assert GridCellState.ObscuredRehit == game.playerDetails[PTHREE.id].opponentGrids[PONE.id].get(1, 0)
        assert GridCellState.ObscuredHit == game.playerDetails[PTHREE.id].opponentGrids[PONE.id].get(2, 0)
        assert GridCellState.Unknown == game.playerDetails[PONE.id].opponentViews[PTHREE.id].get(0, 0)
        assert GridCellState.ObscuredRehit == game.playerDetails[PONE.id].opponentViews[PTHREE.id].get(1, 0)
        assert GridCellState.ObscuredHit == game.playerDetails[PONE.id].opponentViews[PTHREE.id].get(2, 0)

        game.playerDetails.each { assert "1 performed evasive maneuvers." == it.value.lastActionMessage }
        assert 2 == game.playerDetails[PONE.id].evasiveManeuversRemaining
    }

}
