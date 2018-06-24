package com.jtbdevelopment.TwistedBattleship.rest.services;

import com.jtbdevelopment.TwistedBattleship.state.GameFeature;
import com.jtbdevelopment.TwistedBattleship.state.TBGame;
import com.jtbdevelopment.games.dao.AbstractGameRepository;
import com.jtbdevelopment.games.dao.AbstractPlayerRepository;
import com.jtbdevelopment.games.dao.StringToIDConverter;
import com.jtbdevelopment.games.mongo.players.MongoPlayer;
import com.jtbdevelopment.games.rest.services.AbstractAdminServices;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Component;

/**
 * Date: 11/27/2014
 * Time: 6:34 PM
 */
@Component
public class AdminServices extends AbstractAdminServices<ObjectId, GameFeature, TBGame, MongoPlayer> {
    public AdminServices(
            final AbstractPlayerRepository<ObjectId, MongoPlayer> playerRepository,
            final AbstractGameRepository<ObjectId, GameFeature, TBGame> gameRepository,
            final StringToIDConverter<ObjectId> stringToIDConverter) {
        super(playerRepository, gameRepository, stringToIDConverter);
    }
}
