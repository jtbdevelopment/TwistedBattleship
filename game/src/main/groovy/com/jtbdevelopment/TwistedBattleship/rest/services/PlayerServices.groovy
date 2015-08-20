package com.jtbdevelopment.TwistedBattleship.rest.services

import com.jtbdevelopment.TwistedBattleship.exceptions.NotAValidThemeException
import com.jtbdevelopment.TwistedBattleship.player.TBPlayerAttributes
import com.jtbdevelopment.TwistedBattleship.rest.handlers.PlayerGamesFinderHandler
import com.jtbdevelopment.TwistedBattleship.rest.services.messages.FeaturesAndPlayers
import com.jtbdevelopment.games.players.Player
import com.jtbdevelopment.games.rest.handlers.NewGameHandler
import com.jtbdevelopment.games.rest.services.AbstractPlayerServices
import com.jtbdevelopment.games.state.masking.AbstractMaskedMultiPlayerGame
import com.jtbdevelopment.games.state.masking.MaskedMultiPlayerGame
import groovy.transform.CompileStatic
import org.bson.types.ObjectId
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.util.StringUtils

import javax.ws.rs.*
import javax.ws.rs.core.MediaType

/**
 * Date: 11/14/14
 * Time: 6:40 AM
 */
@Component
@CompileStatic
class PlayerServices extends AbstractPlayerServices<ObjectId> {

    @Autowired
    NewGameHandler newGameHandler
    @Autowired
    PlayerGamesFinderHandler playerGamesFinderHandler

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

    @GET
    @Path("games")
    @Produces(MediaType.APPLICATION_JSON)
    public List gamesForPlayer() {
        playerGamesFinderHandler.findGames((ObjectId) playerID.get())
    }

    @PUT
    @Path('changeTheme/{newTheme}')
    @Produces(MediaType.APPLICATION_JSON)
    public Object changeTheme(@PathParam('newTheme') String newTheme) {
        Player player = playerRepository.findOne((ObjectId) playerID.get())
        TBPlayerAttributes playerAttributes = (TBPlayerAttributes) player.gameSpecificPlayerAttributes
        if (StringUtils.isEmpty(newTheme) || !playerAttributes.availableThemes.contains(newTheme)) {
            throw new NotAValidThemeException()
        }
        playerAttributes.theme = newTheme
        return playerRepository.save(player)
    }
}
