package com.jtbdevelopment.TwistedBattleship.ai.common

import com.jtbdevelopment.TwistedBattleship.ai.AI
import com.jtbdevelopment.games.dao.AbstractPlayerRepository
import com.jtbdevelopment.games.mongo.players.MongoPlayer
import com.jtbdevelopment.games.players.Player
import com.jtbdevelopment.games.players.PlayerFactory
import groovy.transform.CompileStatic
import org.bson.types.ObjectId
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired

/**
 * Date: 9/18/15
 * Time: 6:48 AM
 */
@CompileStatic
class AbstractAIPlayerCreator {
    protected static Logger logger = LoggerFactory.getLogger(AbstractAIPlayerCreator.class)

    List<Player> players

    @Autowired
    AbstractPlayerRepository<ObjectId, MongoPlayer> playerRepository
    @Autowired
    PlayerFactory<ObjectId, MongoPlayer> playerFactory

    protected void loadOrCreateAIPlayers(final String baseName, final String icon) {
        logger.info('Checking for ' + baseName + ' system players.')
        players = (1..AI.PLAYERS_TO_MAKE).collect {
            String name = baseName + it
            Player player = (Player) playerRepository.findByDisplayName(name)
            if (player == null) {
                logger.info("Making system id for " + name)
                player = playerFactory.newSystemPlayer()
                player.displayName = name
                player.sourceId = name
                player.imageUrl = icon
                player = playerRepository.save(player)
            }
            player
        }
        logger.info("Completed")
    }
}
