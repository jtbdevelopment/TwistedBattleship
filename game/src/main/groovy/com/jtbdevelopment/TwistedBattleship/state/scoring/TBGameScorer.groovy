package com.jtbdevelopment.TwistedBattleship.state.scoring

import com.jtbdevelopment.TwistedBattleship.player.TBPlayerAttributes
import com.jtbdevelopment.TwistedBattleship.state.TBGame
import com.jtbdevelopment.TwistedBattleship.state.TBPlayerState
import com.jtbdevelopment.games.dao.AbstractPlayerRepository
import com.jtbdevelopment.games.players.SystemPlayer
import com.jtbdevelopment.games.publish.PlayerPublisher
import com.jtbdevelopment.games.state.scoring.GameScorer
import groovy.transform.CompileStatic
import org.bson.types.ObjectId
import org.springframework.beans.factory.annotation.Autowired
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

    @Autowired
    AbstractPlayerRepository playerRepository

    @Autowired
    PlayerPublisher playerPublisher

    @Override
    TBGame scoreGame(final TBGame game) {
        game.playerDetails.findAll {
            it.value.alive
        }.each {
            it.value.scoreFromLiving += SCORE_FOR_VICTORY
        }

        def nonSystemPlayers = game.players.findAll { !(it in SystemPlayer) }
        def updatedPlayers = nonSystemPlayers.collect {
            def player = playerRepository.findOne(it.id)
            TBPlayerAttributes attributes = (TBPlayerAttributes) player.gameSpecificPlayerAttributes
            TBPlayerState state = game.playerDetails[(ObjectId) player.id]
            if (state.alive) {
                attributes.wins += 1
                attributes.currentWinStreak += 1

            } else {
                attributes.losses += 1
                attributes.currentWinStreak = 0
            }
            attributes.highestScore = Math.max(attributes.highestScore, state.totalScore)
            player
        }
        updatedPlayers.each {
            playerPublisher.publish(playerRepository.save(it))
        }

        game
    }
}
