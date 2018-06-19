package com.jtbdevelopment.TwistedBattleship.rest.services

import com.jtbdevelopment.TwistedBattleship.ai.AI
import com.jtbdevelopment.TwistedBattleship.exceptions.NotAValidThemeException
import com.jtbdevelopment.TwistedBattleship.player.TBPlayerAttributes
import com.jtbdevelopment.TwistedBattleship.rest.services.messages.FeaturesAndPlayers
import com.jtbdevelopment.TwistedBattleship.state.GameFeature
import com.jtbdevelopment.TwistedBattleship.state.TBGame
import com.jtbdevelopment.TwistedBattleship.state.masked.TBMaskedGame
import com.jtbdevelopment.games.dao.AbstractPlayerRepository
import com.jtbdevelopment.games.dao.StringToIDConverter
import com.jtbdevelopment.games.mongo.players.MongoPlayer
import com.jtbdevelopment.games.players.friendfinder.SourceBasedFriendFinder
import com.jtbdevelopment.games.rest.AbstractMultiPlayerServices
import com.jtbdevelopment.games.rest.handlers.NewGameHandler
import com.jtbdevelopment.games.rest.handlers.PlayerGamesFinderHandler
import com.jtbdevelopment.games.rest.services.AbstractAdminServices
import com.jtbdevelopment.games.rest.services.AbstractGameServices
import com.jtbdevelopment.games.state.masking.AbstractMaskedMultiPlayerGame
import com.jtbdevelopment.games.state.masking.MaskedMultiPlayerGame
import groovy.transform.CompileStatic
import org.bson.types.ObjectId
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.util.StringUtils

import javax.annotation.PostConstruct
import javax.ws.rs.*
import javax.ws.rs.core.MediaType

/**
 * Date: 11/14/14
 * Time: 6:40 AM
 */
@Component
@CompileStatic
class PlayerServices extends AbstractMultiPlayerServices<ObjectId, GameFeature, TBGame, TBMaskedGame, MongoPlayer> {

    @Autowired
    private final NewGameHandler newGameHandler

    private final Map<String, String> aiPlayers
    private final AbstractPlayerRepository<ObjectId, MongoPlayer> playerRepository

    PlayerServices(
            final AbstractGameServices<ObjectId, GameFeature, TBGame, TBMaskedGame, MongoPlayer> gamePlayServices,
            final AbstractPlayerRepository<ObjectId, MongoPlayer> playerRepository,
            final AbstractAdminServices<ObjectId, GameFeature, TBGame, MongoPlayer> adminServices,
            final StringToIDConverter<ObjectId> stringToIDConverter,
            final PlayerGamesFinderHandler<ObjectId, GameFeature, TBGame, TBMaskedGame, MongoPlayer> playerGamesFinderHandler,
            final NewGameHandler<ObjectId, GameFeature, TBGame, TBMaskedGame, MongoPlayer> newGameHandler,
            final List<AI> aiList) {
        super(gamePlayServices, playerRepository, adminServices, stringToIDConverter, playerGamesFinderHandler)
        this.aiPlayers = aiList.collectEntries {
            AI ai ->
                ai.players.collectEntries {
                    [(it.md5): it.displayName]
                }
        }
        this.playerRepository = playerRepository
        this.newGameHandler = newGameHandler
    }

    @PostConstruct
    void setup() {
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("new")
    MaskedMultiPlayerGame createNewGame(final FeaturesAndPlayers featuresAndPlayers) {
        (AbstractMaskedMultiPlayerGame) newGameHandler.handleCreateNewGame(
                (Serializable) playerID.get(),
                featuresAndPlayers.players,
                featuresAndPlayers.features)
    }

    @PUT
    @Path('changeTheme/{newTheme}')
    @Produces(MediaType.APPLICATION_JSON)
    public Object changeTheme(@PathParam('newTheme') String newTheme) {
        MongoPlayer player = playerRepository.findById((ObjectId) playerID.get()).get()
        TBPlayerAttributes playerAttributes = (TBPlayerAttributes) player.gameSpecificPlayerAttributes
        if (StringUtils.isEmpty(newTheme) || !playerAttributes.availableThemes.contains(newTheme)) {
            throw new NotAValidThemeException()
        }
        playerAttributes.theme = newTheme
        return playerRepository.save(player)
    }

    //  TODO - test
    @Override
    Map<String, Set<? super Object>> getFriendsV2() {
        Map<String, Set<? super Object>> friends = super.getFriendsV2()
        ((Map<String, String>) friends[SourceBasedFriendFinder.MASKED_FRIENDS_KEY]).putAll(aiPlayers)
        friends
    }
}
