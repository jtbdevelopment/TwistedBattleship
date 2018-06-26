package com.jtbdevelopment.TwistedBattleship.rest.services;

import com.jtbdevelopment.TwistedBattleship.exceptions.NotAValidThemeException;
import com.jtbdevelopment.TwistedBattleship.player.TBPlayerAttributes;
import com.jtbdevelopment.TwistedBattleship.rest.services.messages.FeaturesAndPlayers;
import com.jtbdevelopment.TwistedBattleship.state.GameFeature;
import com.jtbdevelopment.TwistedBattleship.state.TBGame;
import com.jtbdevelopment.TwistedBattleship.state.masked.TBMaskedGame;
import com.jtbdevelopment.games.dao.AbstractPlayerRepository;
import com.jtbdevelopment.games.mongo.players.MongoPlayer;
import com.jtbdevelopment.games.rest.handlers.NewGameHandler;
import org.bson.types.ObjectId;
import org.codehaus.groovy.runtime.DefaultGroovyMethods;
import org.junit.Test;
import org.mockito.Mockito;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;

import static org.junit.Assert.*;

/**
 * Date: 4/27/15
 * Time: 7:01 PM
 */
public class PlayerServicesTest {
    private AbstractPlayerRepository<ObjectId, MongoPlayer> playerRepository = Mockito.mock(AbstractPlayerRepository.class);
    private NewGameHandler<ObjectId, GameFeature, TBGame, TBMaskedGame, MongoPlayer> newGameHandler = Mockito.mock(NewGameHandler.class);
    private PlayerServices playerServices = new PlayerServices(null, playerRepository, null, null, null, newGameHandler, Collections.emptyList());

    @Test
    public void testCreateNewGame() {
        ObjectId APLAYER = new ObjectId();
        playerServices.getPlayerID().set(APLAYER);
        Set<GameFeature> features = new HashSet<>(Arrays.asList(GameFeature.EREnabled, GameFeature.PerShip));
        List<String> players = Arrays.asList("1", "2", "3");
        FeaturesAndPlayers input = new FeaturesAndPlayers();
        input.setFeatures(features);
        input.setPlayers(players);
        TBMaskedGame game = new TBMaskedGame();
        Mockito.when(newGameHandler.handleCreateNewGame(APLAYER, players, features)).thenReturn(game);
        assertSame(game, playerServices.createNewGame(input));
    }

    @Test
    public void testCreateNewGameAnnotations() throws NoSuchMethodException {
        Method gameServices = PlayerServices.class.getMethod("createNewGame", new Class[]{FeaturesAndPlayers.class});
        assertEquals(4, DefaultGroovyMethods.size(gameServices.getAnnotations()));
        assertTrue(gameServices.isAnnotationPresent(Path.class));
        assertEquals("new", gameServices.getAnnotation(Path.class).value());
        assertTrue(gameServices.isAnnotationPresent(Consumes.class));
        assertArrayEquals(Collections.singletonList(MediaType.APPLICATION_JSON).toArray(), gameServices.getAnnotation(Consumes.class).value());
        assertTrue(gameServices.isAnnotationPresent(Produces.class));
        assertArrayEquals(Collections.singletonList(MediaType.APPLICATION_JSON).toArray(),
                gameServices.getAnnotation(Produces.class).value());
        assertTrue(gameServices.isAnnotationPresent(POST.class));
        Annotation[][] params = gameServices.getParameterAnnotations();
        assertEquals(1, params.length);
        assertEquals(0, params[0].length);
    }

    @Test(expected = NotAValidThemeException.class)
    public void testChangeThemeWithNullThrowsException() {
        ObjectId APLAYER = new ObjectId();
        MongoPlayer player = new MongoPlayer();
        playerServices.getPlayerID().set(APLAYER);

        Mockito.when(playerRepository.findById(APLAYER)).thenReturn(Optional.of(player));

        playerServices.changeTheme(null);
    }

    @Test(expected = NotAValidThemeException.class)
    public void testChangeThemeWithInvalidChoiceThrowsException() {
        ObjectId APLAYER = new ObjectId();
        MongoPlayer player = new MongoPlayer();
        TBPlayerAttributes attributes = new TBPlayerAttributes();
        attributes.setPlayer(player);
        player.setGameSpecificPlayerAttributes(attributes);
        playerServices.getPlayerID().set(APLAYER);

        Mockito.when(playerRepository.findById(APLAYER)).thenReturn(Optional.of(player));

        playerServices.changeTheme("somecrazytheme");
    }

    @Test
    public void testChangingATheme() {
        String goodTheme = "goodtheme";
        ObjectId APLAYER = new ObjectId();
        MongoPlayer player = new MongoPlayer();
        MongoPlayer updatedPlayer = new MongoPlayer();

        TBPlayerAttributes attributes = new TBPlayerAttributes();
        attributes.setPlayer(player);
        attributes.setAvailableThemes(new HashSet<>(Arrays.asList("default", goodTheme, "another-theme")));
        player.setGameSpecificPlayerAttributes(attributes);

        playerServices.getPlayerID().set(APLAYER);

        Mockito.when(playerRepository.findById(APLAYER)).thenReturn(Optional.of(player));
        Mockito.when(playerRepository.save(player)).thenReturn(updatedPlayer);
        assertSame(updatedPlayer, playerServices.changeTheme(goodTheme));
        assertEquals(goodTheme, ((TBPlayerAttributes) player.getGameSpecificPlayerAttributes()).getTheme());
    }

    @Test
    public void testChangeThemeAnnotations() throws NoSuchMethodException {
        Method gameServices = PlayerServices.class.getMethod("changeTheme", new Class[]{String.class});
        assertEquals(3, gameServices.getAnnotations().length);
        assertTrue(gameServices.isAnnotationPresent(Path.class));
        assertEquals("changeTheme/{newTheme}", gameServices.getAnnotation(Path.class).value());
        assertTrue(gameServices.isAnnotationPresent(Produces.class));
        assertArrayEquals(Collections.singletonList(MediaType.APPLICATION_JSON).toArray(),
                gameServices.getAnnotation(Produces.class).value());
        assertTrue(gameServices.isAnnotationPresent(PUT.class));
        Annotation[][] params = gameServices.getParameterAnnotations();
        assertEquals(1, params.length);
        assertEquals(1, params[0].length);
        assertEquals(PathParam.class, params[0][0].annotationType());
        assertEquals("newTheme", ((PathParam) params[0][0]).value());
    }
}
