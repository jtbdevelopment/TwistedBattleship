package com.jtbdevelopment.TwistedBattleship.rest.services;

import com.jtbdevelopment.TwistedBattleship.rest.GameFeatureInfo;
import com.jtbdevelopment.TwistedBattleship.rest.ShipInfo;
import com.jtbdevelopment.TwistedBattleship.state.GameFeature;
import com.jtbdevelopment.TwistedBattleship.state.grid.GridCellState;
import com.jtbdevelopment.TwistedBattleship.state.grid.GridCircleUtil;
import com.jtbdevelopment.TwistedBattleship.state.ships.Ship;
import org.junit.Test;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Date: 4/28/15
 * Time: 6:36 AM
 */
public class PlayerGatewayServiceTest {
    private PlayerGatewayService playerGatewayService = new PlayerGatewayService(null);

    @Test
    public void testGetFeatures() {
        assertEquals(

                Arrays.asList(
                        new GameFeatureInfo(GameFeature.GridSize,
                                Arrays.asList(
                                        new GameFeatureInfo.Detail(GameFeature.Grid10x10),
                                        new GameFeatureInfo.Detail(GameFeature.Grid15x15),
                                        new GameFeatureInfo.Detail(GameFeature.Grid20x20))),
                        new GameFeatureInfo(GameFeature.ActionsPerTurn,
                                Arrays.asList(
                                        new GameFeatureInfo.Detail(GameFeature.PerShip),
                                        new GameFeatureInfo.Detail(GameFeature.Single))),
                        new GameFeatureInfo(GameFeature.FogOfWar,
                                Arrays.asList(
                                        new GameFeatureInfo.Detail(GameFeature.SharedIntel),
                                        new GameFeatureInfo.Detail(GameFeature.IsolatedIntel))),
                        new GameFeatureInfo(GameFeature.StartingShips,
                                Arrays.asList(
                                        new GameFeatureInfo.Detail(GameFeature.StandardShips),
                                        new GameFeatureInfo.Detail(GameFeature.AllCarriers),
                                        new GameFeatureInfo.Detail(GameFeature.AllDestroyers),
                                        new GameFeatureInfo.Detail(GameFeature.AllSubmarines),
                                        new GameFeatureInfo.Detail(GameFeature.AllCruisers),
                                        new GameFeatureInfo.Detail(GameFeature.AllBattleships))),
                        new GameFeatureInfo(GameFeature.ECM,
                                Arrays.asList(
                                        new GameFeatureInfo.Detail(GameFeature.ECMEnabled),
                                        new GameFeatureInfo.Detail(GameFeature.ECMDisabled))),
                        new GameFeatureInfo(GameFeature.EvasiveManeuvers,
                                Arrays.asList(
                                        new GameFeatureInfo.Detail(GameFeature.EMEnabled),
                                        new GameFeatureInfo.Detail(GameFeature.EMDisabled))),
                        new GameFeatureInfo(GameFeature.EmergencyRepairs,
                                Arrays.asList(
                                        new GameFeatureInfo.Detail(GameFeature.EREnabled),
                                        new GameFeatureInfo.Detail(GameFeature.ERDisabled))),
                        new GameFeatureInfo(GameFeature.Spy,
                                Arrays.asList(
                                        new GameFeatureInfo.Detail(GameFeature.SpyEnabled),
                                        new GameFeatureInfo.Detail(GameFeature.SpyDisabled))),
                        new GameFeatureInfo(GameFeature.CruiseMissile,
                                Arrays.asList(
                                        new GameFeatureInfo.Detail(GameFeature.CruiseMissileEnabled),
                                        new GameFeatureInfo.Detail(GameFeature.CruiseMissileDisabled))))
                , playerGatewayService.featuresAndDescriptions());
    }

    @Test
    public void testGetFeaturesAnnotations() throws NoSuchMethodException {
        Method gameServices = PlayerGatewayService.class.getMethod("featuresAndDescriptions");
        assertEquals(3, gameServices.getAnnotations().length);
        assertTrue(gameServices.isAnnotationPresent(Path.class));
        assertEquals("features", gameServices.getAnnotation(Path.class).value());
        assertTrue(gameServices.isAnnotationPresent(GET.class));
        assertTrue(gameServices.isAnnotationPresent(Produces.class));
        assertArrayEquals(Arrays.asList(MediaType.APPLICATION_JSON).toArray(), gameServices.getAnnotation(Produces.class).value());
        Annotation[][] params = gameServices.getParameterAnnotations();
        assertEquals(0, params.length);
    }

    @Test
    public void testGetCircles() {
        assertEquals(GridCircleUtil.CIRCLE_OFFSETS, playerGatewayService.circleSizes());
    }

    @Test
    public void testGetCirclesAnnotations() throws NoSuchMethodException {
        Method gameServices = PlayerGatewayService.class.getMethod("circleSizes");
        assertEquals(3, gameServices.getAnnotations().length);
        assertTrue(gameServices.isAnnotationPresent(Path.class));
        assertEquals("circles", gameServices.getAnnotation(Path.class).value());
        assertTrue(gameServices.isAnnotationPresent(GET.class));
        assertTrue(gameServices.isAnnotationPresent(Produces.class));
        assertArrayEquals(Arrays.asList(MediaType.APPLICATION_JSON).toArray(), gameServices.getAnnotation(Produces.class).value());
        Annotation[][] params = gameServices.getParameterAnnotations();
        assertEquals(0, params.length);
    }

    @Test
    public void testGetShips() {
        List<ShipInfo> ships = Arrays.asList(new ShipInfo(Ship.Battleship), new ShipInfo(Ship.Carrier), new ShipInfo(Ship.Cruiser), new ShipInfo(Ship.Destroyer), new ShipInfo(Ship.Submarine));
        assertTrue(ships.containsAll(playerGatewayService.ships()));
        assertTrue(playerGatewayService.ships().containsAll(ships));
    }

    @Test
    public void testGetShipsAnnotations() throws NoSuchMethodException {
        Method gameServices = PlayerGatewayService.class.getMethod("ships");
        assertEquals(3, gameServices.getAnnotations().length);
        assertTrue(gameServices.isAnnotationPresent(Path.class));
        assertEquals("ships", gameServices.getAnnotation(Path.class).value());
        assertTrue(gameServices.isAnnotationPresent(GET.class));
        assertTrue(gameServices.isAnnotationPresent(Produces.class));
        assertArrayEquals(Arrays.asList(MediaType.APPLICATION_JSON).toArray(), gameServices.getAnnotation(Produces.class).value());
        Annotation[][] params = gameServices.getParameterAnnotations();
        assertEquals(0, params.length);
    }

    @Test
    public void testGetCellStates() {
        assertEquals(Arrays.asList(GridCellState.values()), playerGatewayService.states());
    }

    @Test
    public void testGetStatesAnnotations() throws NoSuchMethodException {
        Method gameServices = PlayerGatewayService.class.getMethod("states");
        assertEquals(3, gameServices.getAnnotations().length);
        assertTrue(gameServices.isAnnotationPresent(Path.class));
        assertEquals("states", gameServices.getAnnotation(Path.class).value());
        assertTrue(gameServices.isAnnotationPresent(GET.class));
        assertTrue(gameServices.isAnnotationPresent(Produces.class));
        assertArrayEquals(Arrays.asList(MediaType.APPLICATION_JSON).toArray(), gameServices.getAnnotation(Produces.class).value());
        Annotation[][] params = gameServices.getParameterAnnotations();
        assertEquals(0, params.length);
    }
}
