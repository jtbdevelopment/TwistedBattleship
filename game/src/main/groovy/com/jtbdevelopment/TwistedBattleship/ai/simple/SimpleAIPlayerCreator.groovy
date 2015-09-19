package com.jtbdevelopment.TwistedBattleship.ai.simple

import com.jtbdevelopment.games.dao.AbstractPlayerRepository
import com.jtbdevelopment.games.mongo.players.MongoPlayer
import com.jtbdevelopment.games.mongo.players.MongoSystemPlayer
import com.jtbdevelopment.games.players.Player
import com.jtbdevelopment.games.players.PlayerFactory
import com.jtbdevelopment.games.players.SystemPlayer
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
    private static final String DISPLAY_NAME = "Simple AI"
    Player player

    private static Logger logger = LoggerFactory.getLogger(SimpleAIPlayerCreator.class)
    @Autowired
    AbstractPlayerRepository playerRepository
    @Autowired
    PlayerFactory playerFactory

    @PostConstruct
    void loadOrCreateSystemPlayers() {
        logger.info('Checking for system player.')
        player = (Player) playerRepository.findByDisplayName(DISPLAY_NAME)
        //  Need icon
        if (player == null) {
            logger.info("Making system id for " + DISPLAY_NAME)
            player = playerFactory.newSystemPlayer()
            player.displayName = DISPLAY_NAME
            player.sourceId = DISPLAY_NAME
            player = playerRepository.save(player)
        }
        logger.info("Completed")
    }
}
