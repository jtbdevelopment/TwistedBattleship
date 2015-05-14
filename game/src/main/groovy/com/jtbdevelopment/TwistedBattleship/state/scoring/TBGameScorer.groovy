package com.jtbdevelopment.TwistedBattleship.state.scoring

import com.jtbdevelopment.TwistedBattleship.state.TBGame
import com.jtbdevelopment.games.state.scoring.GameScorer
import groovy.transform.CompileStatic
import org.springframework.stereotype.Component

/**
 * Date: 4/27/15
 * Time: 6:25 PM
 */
@Component
@CompileStatic
class TBGameScorer implements GameScorer<TBGame> {
    public static final int SCORE_FOR_HIT = 1
    public static final int SCORE_FOR_SINK = 5
    public static final int SCORE_FOR_VICTORY = 10

    @Override
    TBGame scoreGame(final TBGame game) {
        //TODO
        return null
    }
}
