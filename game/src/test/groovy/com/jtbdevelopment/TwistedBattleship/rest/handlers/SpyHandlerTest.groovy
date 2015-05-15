package com.jtbdevelopment.TwistedBattleship.rest.handlers

import com.jtbdevelopment.TwistedBattleship.exceptions.NoSpyActionsRemainException
import com.jtbdevelopment.TwistedBattleship.state.GameFeature
import com.jtbdevelopment.TwistedBattleship.state.TBGame
import com.jtbdevelopment.TwistedBattleship.state.TBPlayerState
import com.jtbdevelopment.games.mongo.MongoGameCoreTestCase

/**
 * Date: 5/15/15
 * Time: 6:56 AM
 */
class SpyHandlerTest extends MongoGameCoreTestCase {
    SpyHandler handler = new SpyHandler()

    void testTargetSelf() {
        assertFalse handler.targetSelf()
    }

    void testMovesRequired() {
        TBGame game = new TBGame()
        assert 1 == handler.movesRequired(game)
        game.features.add(GameFeature.PerShip)
        assert 2 == handler.movesRequired(game)
    }

    void testValidatesNoSpiesRemain() {
        TBGame game = new TBGame(
                playerDetails: [
                        (PONE.id): new TBPlayerState(spysRemaining: 1)
                ]
        )
        handler.validateMoveSpecific(PONE, game, PTWO, null)
        game.playerDetails[PONE.id].spysRemaining = 0
        shouldFail(NoSpyActionsRemainException.class, {
            handler.validateMoveSpecific(PONE, game, PTWO, null)
        })
    }
}
