package com.jtbdevelopment.TwistedBattleship.ai.regular;

import com.jtbdevelopment.TwistedBattleship.ai.common.AbstractAIPlayerCreator;
import com.jtbdevelopment.games.dao.AbstractPlayerRepository;
import com.jtbdevelopment.games.mongo.players.MongoPlayer;
import com.jtbdevelopment.games.players.PlayerFactory;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * Date: 9/18/15
 * Time: 6:48 AM
 */
@Component
public class RegularAIPlayerCreator extends AbstractAIPlayerCreator {
    private static final String DISPLAY_NAME_BASE = "Regular AI #";
    private static final String ICON = "images/avatars/robot5.png";

    public RegularAIPlayerCreator(
            final AbstractPlayerRepository<ObjectId, MongoPlayer> playerRepository,
            final PlayerFactory<ObjectId, MongoPlayer> playerFactory) {
        super(playerRepository, playerFactory);
    }

    @PostConstruct
    public void setup() {
        loadOrCreateAIPlayers(DISPLAY_NAME_BASE, ICON);
    }
}
