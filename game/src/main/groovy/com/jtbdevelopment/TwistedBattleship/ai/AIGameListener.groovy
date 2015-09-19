package com.jtbdevelopment.TwistedBattleship.ai

import com.jtbdevelopment.TwistedBattleship.ai.simple.SimpleAI
import com.jtbdevelopment.TwistedBattleship.ai.simple.SimpleAIPlayerCreator
import com.jtbdevelopment.TwistedBattleship.state.TBGame
import com.jtbdevelopment.games.players.Player
import com.jtbdevelopment.games.publish.GameListener
import com.jtbdevelopment.games.rest.handlers.ChallengeResponseHandler
import com.jtbdevelopment.games.rest.handlers.QuitHandler
import com.jtbdevelopment.games.state.GamePhase
import com.jtbdevelopment.games.state.MultiPlayerGame
import com.jtbdevelopment.games.state.PlayerState
import groovy.transform.CompileStatic
import org.bson.types.ObjectId
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

/**
 * Date: 9/18/15
 * Time: 10:17 PM
 */
@SuppressWarnings("GroovyUnusedDeclaration")
@Component
@CompileStatic
class AIGameListener implements GameListener {
    @Autowired
    ChallengeResponseHandler challengeResponseHandler
    @Autowired
    QuitHandler quitHandler
    @Autowired
    SimpleAIPlayerCreator playerCreator
    @Autowired
    SimpleAI simpleAI

    @Override
    void gameChanged(
            final MultiPlayerGame multiPlayerGame, final Player initiatingPlayer, final boolean initiatingServer) {
        TBGame game = (TBGame) multiPlayerGame
        //  TODO - messages to surrender/quit/decline?

        //  TODO - exceptions
        //  TODO - on initialization
        //  TODO - too fast swamps ui logic - needs to be 1 second apart
        if (initiatingServer) {
            if (game.players.contains(playerCreator.player)) {
                switch (game.gamePhase) {
                    case GamePhase.Challenged:
                        if (game.playerStates[(ObjectId) playerCreator.player.id] == PlayerState.Pending) {
                            challengeResponseHandler.handleAction(playerCreator.player.id, game.id, PlayerState.Accepted)
                        }
                        break
                    case GamePhase.Setup:
                        if (!game.playerDetails[(ObjectId) playerCreator.player.id].setup) {
                            simpleAI.setup(game)
                        }
                        break
                    case GamePhase.Playing:
                        if (game.currentPlayer == playerCreator.player.id) {
                            simpleAI.playOneMove(game)
                        }
                        break
                }
            }
        }
    }
}
