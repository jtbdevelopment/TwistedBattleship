package com.jtbdevelopment.TwistedBattleship.rest.services

import com.jtbdevelopment.TwistedBattleship.exceptions.NotAValidThemeException
import com.jtbdevelopment.TwistedBattleship.player.TBPlayerAttributes
import com.jtbdevelopment.TwistedBattleship.rest.handlers.PlayerGamesFinderHandler
import com.jtbdevelopment.TwistedBattleship.rest.services.messages.FeaturesAndPlayers
import com.jtbdevelopment.TwistedBattleship.state.GameFeature
import com.jtbdevelopment.TwistedBattleship.state.TBGame
import com.jtbdevelopment.TwistedBattleship.state.masked.TBMaskedGame
import com.jtbdevelopment.games.dao.AbstractPlayerRepository
import com.jtbdevelopment.games.mongo.players.MongoPlayer
import com.jtbdevelopment.games.rest.handlers.NewGameHandler
import groovy.transform.TypeChecked
import org.bson.types.ObjectId

import javax.ws.rs.*
import javax.ws.rs.core.MediaType

/**
 * Date: 4/27/15
 * Time: 7:01 PM
 */
class PlayerServicesTest extends GroovyTestCase {
    PlayerServices playerServices = new PlayerServices()

    void testCreateNewGame() {
        def APLAYER = new ObjectId()
        playerServices.playerID.set(APLAYER)
        def features = [GameFeature.EREnabled, GameFeature.PerShip] as Set
        def players = ["1", "2", "3"]
        FeaturesAndPlayers input = new FeaturesAndPlayers(features: features, players: players)
        TBMaskedGame game = new TBMaskedGame()
        playerServices.newGameHandler = [
                handleCreateNewGame: {
                    ObjectId i, List<String> p, Set<GameFeature> f ->
                        assert i == APLAYER
                        assert p == players
                        assert f == features
                        game
                }
        ] as NewGameHandler
        assert game.is(playerServices.createNewGame(input))
    }

    void testCreateNewGameAnnotations() {
        def gameServices = PlayerServices.getMethod("createNewGame", [FeaturesAndPlayers.class] as Class[])
        assert (gameServices.annotations.size() == 4 ||
                (gameServices.isAnnotationPresent(TypeChecked.TypeCheckingInfo) && gameServices.annotations.size() == 5)
        )
        assert gameServices.isAnnotationPresent(Path.class)
        assert gameServices.getAnnotation(Path.class).value() == "new"
        assert gameServices.isAnnotationPresent(Consumes.class)
        assert gameServices.getAnnotation(Consumes.class).value() == [MediaType.APPLICATION_JSON]
        assert gameServices.isAnnotationPresent(Produces.class)
        assert gameServices.getAnnotation(Produces.class).value() == [MediaType.APPLICATION_JSON]
        assert gameServices.isAnnotationPresent(POST.class)
        def params = gameServices.parameterAnnotations
        assert params.length == 1
        assert params[0].length == 0
    }

    void testGetGames() {
        def APLAYER = new ObjectId()
        def results = [new TBGame(), new TBGame(), new TBGame()]
        playerServices.playerGamesFinderHandler = [
                findGames: {
                    ObjectId it ->
                        assert it == APLAYER
                        return results
                }
        ] as PlayerGamesFinderHandler
        playerServices.playerID.set(APLAYER)
        assert results.is(playerServices.gamesForPlayer())
    }

    void testGamesAnnotations() {
        def gameServices = PlayerServices.getMethod("gamesForPlayer", [] as Class[])
        assert (gameServices.annotations.size() == 3 ||
                (gameServices.isAnnotationPresent(TypeChecked.TypeCheckingInfo) && gameServices.annotations.size() == 4)
        )
        assert gameServices.isAnnotationPresent(Path.class)
        assert gameServices.getAnnotation(Path.class).value() == "games"
        assert gameServices.isAnnotationPresent(Produces.class)
        assert gameServices.getAnnotation(Produces.class).value() == [MediaType.APPLICATION_JSON]
        assert gameServices.isAnnotationPresent(GET.class)
        def params = gameServices.parameterAnnotations
        assert params.length == 0
    }

    void testChangeThemeWithNullThrowsException() {
        def APLAYER = new ObjectId()
        MongoPlayer player = new MongoPlayer()
        playerServices.playerID.set(APLAYER)

        playerServices.playerRepository = [
                findOne: {
                    ObjectId id ->
                        assert APLAYER == id
                        return player
                }
        ] as AbstractPlayerRepository

        shouldFail(NotAValidThemeException.class, {
            playerServices.changeTheme(null)
        })
    }

    void testChangeThemeWithInvalidChoiceThrowsException() {
        def APLAYER = new ObjectId()
        MongoPlayer player = new MongoPlayer()
        player.gameSpecificPlayerAttributes = new TBPlayerAttributes(player: player)
        playerServices.playerID.set(APLAYER)

        playerServices.playerRepository = [
                findOne: {
                    ObjectId id ->
                        assert APLAYER == id
                        return player
                }
        ] as AbstractPlayerRepository

        shouldFail(NotAValidThemeException.class, {
            playerServices.changeTheme('somecrazytheme')
        })
    }

    void testChangingATheme() {
        def goodTheme = 'goodtheme'
        def APLAYER = new ObjectId()
        MongoPlayer player = new MongoPlayer()
        MongoPlayer updatedPlayer = new MongoPlayer()

        def attributes = new TBPlayerAttributes(player: player)
        attributes.availableThemes = ['default', goodTheme, 'another-theme']
        player.gameSpecificPlayerAttributes = attributes

        playerServices.playerID.set(APLAYER)

        playerServices.playerRepository = [
                findOne: {
                    ObjectId id ->
                        assert APLAYER == id
                        return player
                },
                save   : {
                    MongoPlayer p ->
                        assert p.is(player)
                        assert p.gameSpecificPlayerAttributes.theme == goodTheme
                        updatedPlayer
                }
        ] as AbstractPlayerRepository

        assert updatedPlayer.is(playerServices.changeTheme(goodTheme))
    }

    void testChangeThemeAnnotations() {
        def gameServices = PlayerServices.getMethod("changeTheme", [String.class] as Class[])
        assert (gameServices.annotations.size() == 3 ||
                (gameServices.isAnnotationPresent(TypeChecked.TypeCheckingInfo) && gameServices.annotations.size() == 3)
        )
        assert gameServices.isAnnotationPresent(Path.class)
        assert gameServices.getAnnotation(Path.class).value() == "changeTheme/{newTheme}"
        assert gameServices.isAnnotationPresent(Produces.class)
        assert gameServices.getAnnotation(Produces.class).value() == [MediaType.APPLICATION_JSON]
        assert gameServices.isAnnotationPresent(PUT.class)
        def params = gameServices.parameterAnnotations
        assert params.length == 1
        assert params[0].length == 1
        assert params[0][0].annotationType() == PathParam.class
        assert ((PathParam) params[0][0]).value() == "newTheme"
    }
}

