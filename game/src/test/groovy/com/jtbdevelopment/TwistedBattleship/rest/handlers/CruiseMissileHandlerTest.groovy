package com.jtbdevelopment.TwistedBattleship.rest.handlers

import com.jtbdevelopment.TwistedBattleship.exceptions.NoCruiseMissileActionsRemaining
import com.jtbdevelopment.TwistedBattleship.state.TBGame
import com.jtbdevelopment.TwistedBattleship.state.TBPlayerState
import com.jtbdevelopment.TwistedBattleship.state.grid.GridCellState
import com.jtbdevelopment.TwistedBattleship.state.ships.Ship

/**
 * Date: 5/5/16
 * Time: 8:48 PM
 */
class CruiseMissileHandlerTest extends AbstractBaseHandlerTest {
    CruiseMissileHandler handler = new CruiseMissileHandler();

    @Override
    protected void setUp() throws Exception {
        super.setUp()
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

}
