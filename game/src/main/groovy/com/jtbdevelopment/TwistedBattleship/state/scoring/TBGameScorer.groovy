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
    @Override
    TBGame scoreGame(final TBGame game) {
        //TODO
        return null
    }
}
