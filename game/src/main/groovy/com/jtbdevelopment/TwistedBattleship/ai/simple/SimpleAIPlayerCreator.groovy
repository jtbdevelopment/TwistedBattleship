package com.jtbdevelopment.TwistedBattleship.ai.simple

import com.jtbdevelopment.TwistedBattleship.ai.AI
import com.jtbdevelopment.games.dao.AbstractPlayerRepository
import com.jtbdevelopment.games.players.Player
import com.jtbdevelopment.games.players.PlayerFactory
import groovy.transform.CompileStatic
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct

/**
 * Date: 9/18/15
 * Time: 6:48 AM
 */
@Component
@CompileStatic
class SimpleAIPlayerCreator {
    private static final String DISPLAY_NAME_BASE = "Simple AI #"
    List<Player> players

    private static Logger logger = LoggerFactory.getLogger(SimpleAIPlayerCreator.class)
    @Autowired
    AbstractPlayerRepository playerRepository
    @Autowired
    PlayerFactory playerFactory

    @PostConstruct
    void loadOrCreateSystemPlayers() {
        logger.info('Checking for simple system players.')
        players = (1..AI.PLAYERS_TO_MAKE).collect {
            String name = DISPLAY_NAME_BASE + it
            Player player = (Player) playerRepository.findByDisplayName(name)
            //  Need icon
            if (player == null) {
                logger.info("Making system id for " + name)
                player = playerFactory.newSystemPlayer()
                player.displayName = name
                player.sourceId = name
                player = playerRepository.save(player)
            }
            player
        }
        logger.info("Completed")
    }
}
