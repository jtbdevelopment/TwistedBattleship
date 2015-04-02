package com.jtbdevelopment.TwistedBattleship.state

/**
 * Date: 4/2/15
 * Time: 5:23 PM
 */
class TBPlayerStateTest extends GroovyTestCase {
    TBPlayerState state = new TBPlayerState()

    void testInitialSettings() {
        assert [:] == state.shipHealthRemaining
        assert 0 == state.scoreFromLiving
        assert 0 == state.scoreFromSinks
        assert 0 == state.scoreFromHits
        assert 0 == state.totalScore
        assert 0 == state.activeShipsRemaining
        assert 0 == state.spysRemaining
        assert 0 == state.ecmsRemaining
        assert 0 == state.evasiveManeuversRemaining
        assert 0 == state.emergencyRepairsRemaining
        assertNull state.playerGrid
        assert [:] == state.opponentGrids
        assert [:] == state.shipPositions
    }

    void testGetTotalScore() {
        state.scoreFromLiving = 5
        assert 5 == state.getTotalScore()
        state.scoreFromHits = 10
        assert 15 == state.totalScore
        state.scoreFromSinks = 20
        assert 35 == state.totalScore
        state.scoreFromLiving = 30
        assert 60 == state.totalScore
    }
}
