package com.jtbdevelopment.TwistedBattleship.ai.common;

import com.jtbdevelopment.TwistedBattleship.ai.AI;
import com.jtbdevelopment.games.dao.AbstractPlayerRepository;
import com.jtbdevelopment.games.mongo.players.MongoPlayer;
import com.jtbdevelopment.games.players.PlayerFactory;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;

/**
 * Date: 9/18/15
 * Time: 6:48 AM
 */
public class AbstractAIPlayerCreator {
    private static Logger logger = LoggerFactory.getLogger(AbstractAIPlayerCreator.class);
    private List<MongoPlayer> players;
    private final AbstractPlayerRepository<ObjectId, MongoPlayer> playerRepository;
    private final PlayerFactory<ObjectId, MongoPlayer> playerFactory;

    public AbstractAIPlayerCreator(
            final AbstractPlayerRepository<ObjectId, MongoPlayer> playerRepository,
            final PlayerFactory<ObjectId, MongoPlayer> playerFactory) {
        this.playerRepository = playerRepository;
        this.playerFactory = playerFactory;
    }

    protected void loadOrCreateAIPlayers(final String baseName, final String icon) {
        logger.info("Checking for " + baseName + " system players.");
        players = new LinkedList<>();
        for (int count = 1; count <= AI.PLAYERS_TO_MAKE; ++count) {
            String name = baseName + count;
            MongoPlayer player = playerRepository.findByDisplayName(name);
            if (player == null) {
                logger.info("Making system id for " + name);
                player = playerFactory.newSystemPlayer();
                player.setDisplayName(name);
                player.setSourceId(name);
                player.setImageUrl(icon);
                player = playerRepository.save(player);
            }
            players.add(player);
        }
        logger.info("Completed");
    }

    public List<MongoPlayer> getPlayers() {
        return players;
    }

}
