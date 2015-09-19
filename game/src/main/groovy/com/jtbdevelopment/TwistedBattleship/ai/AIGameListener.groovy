package com.jtbdevelopment.TwistedBattleship.ai

import com.jtbdevelopment.TwistedBattleship.dao.GameRepository
import com.jtbdevelopment.TwistedBattleship.state.TBGame
import com.jtbdevelopment.TwistedBattleship.state.TBPlayerState
import com.jtbdevelopment.games.players.Player
import com.jtbdevelopment.games.publish.GameListener
import com.jtbdevelopment.games.rest.handlers.ChallengeResponseHandler
import com.jtbdevelopment.games.rest.handlers.QuitHandler
import com.jtbdevelopment.games.state.GamePhase
import com.jtbdevelopment.games.state.MultiPlayerGame
import com.jtbdevelopment.games.state.PlayerState
import groovy.transform.CompileStatic
import org.bson.types.ObjectId
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct
import java.util.concurrent.LinkedBlockingDeque
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

/**
 * Date: 9/18/15
 * Time: 10:17 PM
 */
@SuppressWarnings("GroovyUnusedDeclaration")
@Component
@CompileStatic
class AIGameListener implements GameListener {
    private static Logger logger = LoggerFactory.getLogger(AIGameListener.class)

    @Autowired
    ChallengeResponseHandler challengeResponseHandler
    @Autowired
    QuitHandler quitHandler
    @Autowired
    GameRepository gameRepository

    @Autowired
    List<AI> aiList

    private Map<Player, AI> playerAIMap
    private Map<ObjectId, AI> playerIDAIMap
    private List<Player> aiPlayers
    private Set<ObjectId> aiIDs

    @PostConstruct
    public void setup() {
        playerAIMap = aiList.collectEntries {
            AI ai ->
                [(ai.player): ai]
        }
        aiPlayers = playerAIMap.keySet().toList()
        playerIDAIMap = aiList.collectEntries {
            AI ai ->
                [((ObjectId) ai.player.id): ai]
        }
        aiIDs = playerIDAIMap.keySet()
    }

    private final Map<ObjectId, Integer> problemGames = [:]
    //  TODO - config
    private int maxAttempts = 10
    private final ThreadPoolExecutor executor = new ThreadPoolExecutor(
            3,
            25,
            60,
            TimeUnit.SECONDS,
            new LinkedBlockingDeque<Runnable>())

    //  TODO - messages to surrender/quit/decline?
    //  TODO - on initialization
    private class Worker implements Runnable {
        private TBGame game

        Worker(final TBGame game) {
            this.game = game
        }

        //  Only ever handle one AI action at a time, even if multiple AIs or multiple actions allowed
        @Override
        void run() {
            if(!game.players.find{ Player p -> aiPlayers.contains(p) }) {
                return
            }
            //  Too fast and UI can't tell difference between updates since only looks at second level
            sleep(1000)
            logger.debug('AI Playing ' + game.id)
            try {
                game = gameRepository.findOne(game.id)
                switch (game.gamePhase) {
                    case GamePhase.Challenged:
                        ObjectId aiToAccept = game.playerStates.find {
                            ObjectId id, PlayerState state ->
                                aiIDs.contains(id) && state == PlayerState.Pending
                        }?.key
                        if (aiToAccept) {
                            challengeResponseHandler.handleAction(aiToAccept, game.id, PlayerState.Accepted)
                        }
                        break
                    case GamePhase.Setup:
                        ObjectId aiToSetup = game.playerDetails.find {
                            ObjectId id, TBPlayerState state ->
                                aiIDs.contains(id) && !state.setup
                        }?.key
                        if (aiToSetup) {
                            playerIDAIMap[aiToSetup].setup(game)
                        }
                        break
                    case GamePhase.Playing:
                        if (aiIDs.contains(game.currentPlayer)) {
                            playerIDAIMap[game.currentPlayer].playOneMove(game)
                        }
                        break
                }
                problemGames.remove(game.id)
                logger.debug('AI Playing Completed ' + game.id)
            } catch (Exception ex) {
                logger.warn('There was a problem playing ', ex)
                if (problemGames.getOrDefault(game.id, 0)) {
                    int attempts = problemGames[game.id]
                    if (attempts < maxAttempts) {
                        ++attempts
                        problemGames[game.id] = attempts
                        logger.info('Re-queueing for another attempt ' + game.id)
                        executor.execute(new Worker(game))
                    } else {
                        //  TODO - quit it?
                        logger.warn('Game has run out of attempts ' + game.id)
                        problemGames[game.id]
                    }
                }
            }
        }
    }

    @Override
    void gameChanged(
            final MultiPlayerGame multiPlayerGame, final Player initiatingPlayer, final boolean initiatingServer) {
        TBGame game = (TBGame) multiPlayerGame
        if (initiatingServer) {
            executor.execute(new Worker(game))
        }
    }
}
