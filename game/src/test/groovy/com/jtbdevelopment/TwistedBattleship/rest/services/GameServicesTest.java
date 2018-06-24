package com.jtbdevelopment.TwistedBattleship.rest.services;

import com.jtbdevelopment.TwistedBattleship.rest.Target;
import com.jtbdevelopment.TwistedBattleship.rest.handlers.*;
import com.jtbdevelopment.TwistedBattleship.rest.services.messages.ShipAndCoordinates;
import com.jtbdevelopment.TwistedBattleship.state.grid.GridCoordinate;
import com.jtbdevelopment.TwistedBattleship.state.masked.TBMaskedGame;
import com.jtbdevelopment.TwistedBattleship.state.ships.Ship;
import com.jtbdevelopment.TwistedBattleship.state.ships.ShipState;
import com.jtbdevelopment.games.mongo.MongoGameCoreTestCase;
import org.bson.types.ObjectId;
import org.codehaus.groovy.runtime.DefaultGroovyMethods;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.lang.reflect.Method;
import java.util.*;

import static org.junit.Assert.*;

/**
 * Date: 5/5/15
 * Time: 6:38 PM
 */
public class GameServicesTest extends MongoGameCoreTestCase {
    private FireAtCoordinateHandler fireAtCoordinateHandler = Mockito.mock(FireAtCoordinateHandler.class);
    private SpyHandler spyHandler = Mockito.mock(SpyHandler.class);
    private CruiseMissileHandler cruiseMissileHandler = Mockito.mock(CruiseMissileHandler.class);
    private RepairShipHandler repairShipHandler = Mockito.mock(RepairShipHandler.class);
    private EvasiveManeuverHandler evasiveManeuverHandler = Mockito.mock(EvasiveManeuverHandler.class);
    private SetupShipsHandler setupShipsHandler = Mockito.mock(SetupShipsHandler.class);
    private ECMHandler ecmHandler = Mockito.mock(ECMHandler.class);
    private GameServices services = new GameServices(null, null, null, null, null);

    @Test
    public void testActionAnnotations() {
        Map<String, List<Object>> map = new HashMap<>();
        map.put("setupShips", Arrays.asList("setup", Collections.singletonList(List.class)));
        map.put("fire", Arrays.asList("fire", Collections.singletonList(Target.class)));
        map.put("cruiseMissile", Arrays.asList("missile", Collections.singletonList(Target.class)));
        map.put("spy", Arrays.asList("spy", Collections.singletonList(Target.class)));
        map.put("repair", Arrays.asList("repair", Collections.singletonList(Target.class)));
        map.put("ecm", Arrays.asList("ecm", Collections.singletonList(Target.class)));
        map.put("move", Arrays.asList("move", Collections.singletonList(Target.class)));

        map.forEach((method, details) -> {
            Method m;
            try {
                m = GameServices.class.getMethod(method, DefaultGroovyMethods.asType(details.get(1), Class[].class));
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
            assertEquals(4, DefaultGroovyMethods.size(m.getAnnotations()));
            assert m.isAnnotationPresent(PUT.class);
            assert m.isAnnotationPresent(Produces.class);
            assertArrayEquals(Collections.singletonList(MediaType.APPLICATION_JSON).toArray(), m.getAnnotation(Produces.class).value());
            assert m.isAnnotationPresent(Path.class);
            assert m.getAnnotation(Path.class).value().equals(details.get(0));
            assert m.isAnnotationPresent(Consumes.class);
            assertArrayEquals(Collections.singletonList(MediaType.APPLICATION_JSON).toArray(), m.getAnnotation(Consumes.class).value());
        });
    }

    @Test
    public void testSetupShips() {
        ShipAndCoordinates coordinates = new ShipAndCoordinates();
        coordinates.setShip(Ship.Cruiser);
        coordinates.setCoordinates(Arrays.asList(new GridCoordinate(0, 10), new GridCoordinate(0, 9), new GridCoordinate(0, 11)));

        ShipAndCoordinates coordinates1 = new ShipAndCoordinates();
        coordinates1.setShip(Ship.Destroyer);
        coordinates1.setCoordinates(new ArrayList<>(Arrays.asList(new GridCoordinate(1, 10), new GridCoordinate(1, 9), new GridCoordinate(1, 10))));


        List<ShipAndCoordinates> input = Arrays.asList(coordinates, coordinates1);
        final TBMaskedGame maskedGame = new TBMaskedGame();
        ObjectId gameId = new ObjectId();
        services.getPlayerID().set(PONE.getId());
        services.getGameID().set(gameId);
        services.setupShipsHandler = setupShipsHandler;

        Mockito.when(setupShipsHandler.handleAction(Matchers.eq(PONE.getId()), Matchers.eq(gameId), Matchers.isA(List.class))).then(new Answer<TBMaskedGame>() {
            @Override
            public TBMaskedGame answer(InvocationOnMock invocation) {
                List<ShipState> ss = (List<ShipState>) invocation.getArguments()[2];
                assertEquals(2, ss.size());
                ShipState cruiser = ss.stream().filter(s -> Ship.Cruiser.equals(s.getShip())).findFirst().get();
                Assert.assertNotNull(cruiser);
                ShipState destroyer = ss.stream().filter(s -> Ship.Destroyer.equals(s.getShip())).findFirst().get();
                Assert.assertNotNull(destroyer);
                assertEquals(3, cruiser.getHealthRemaining());
                assertEquals(Ship.Cruiser, cruiser.getShip());
                assertEquals(Arrays.asList(false, false, false), cruiser.getShipSegmentHit());
                assertEquals(Arrays.asList(new GridCoordinate(0, 9), new GridCoordinate(0, 10), new GridCoordinate(0, 11)), cruiser.getShipGridCells());

                assertEquals(2, destroyer.getHealthRemaining());
                assertEquals(Ship.Destroyer, destroyer.getShip());
                assertEquals(Arrays.asList(false, false), destroyer.getShipSegmentHit());
                assertEquals(Arrays.asList(new GridCoordinate(1, 9), new GridCoordinate(1, 10)), destroyer.getShipGridCells());
                return maskedGame;
            }

        });
        assert DefaultGroovyMethods.is(maskedGame, services.setupShips(input));
    }

    @Test
    public void testFire() {
        TBMaskedGame maskedGame = new TBMaskedGame();
        Target target = new Target();
        target.setPlayer(PONE.getMd5());
        target.setCoordinate(new GridCoordinate(10, 5));
        ObjectId gameId = new ObjectId();
        services.getPlayerID().set(PONE.getId());
        services.getGameID().set(gameId);
        services.fireAtCoordinateHandler = fireAtCoordinateHandler;
        Mockito.when(fireAtCoordinateHandler.handleAction(PONE.getId(), gameId, target)).thenReturn(maskedGame);
        assertSame(maskedGame, services.fire(target));
    }

    @Test
    public void testMissile() {
        TBMaskedGame maskedGame = new TBMaskedGame();
        Target target = new Target();
        target.setPlayer(PONE.getMd5());
        target.setCoordinate(new GridCoordinate(10, 5));
        ObjectId gameId = new ObjectId();
        services.getPlayerID().set(PONE.getId());
        services.getGameID().set(gameId);
        services.cruiseMissileHandler = cruiseMissileHandler;
        Mockito.when(cruiseMissileHandler.handleAction(PONE.getId(), gameId, target)).thenReturn(maskedGame);
        assertSame(maskedGame, services.cruiseMissile(target));
    }

    @Test
    public void testSpy() {
        TBMaskedGame maskedGame = new TBMaskedGame();
        Target target = new Target();
        target.setPlayer(PONE.getMd5());
        target.setCoordinate(new GridCoordinate(10, 5));
        ObjectId gameId = new ObjectId();
        services.getPlayerID().set(PONE.getId());
        services.getGameID().set(gameId);
        services.spyHandler = spyHandler;
        Mockito.when(spyHandler.handleAction(PONE.getId(), gameId, target)).thenReturn(maskedGame);
        assertSame(maskedGame, services.spy(target));
    }

    @Test
    public void testRepair() {
        TBMaskedGame maskedGame = new TBMaskedGame();
        Target target = new Target();
        target.setPlayer(PONE.getMd5());
        target.setCoordinate(new GridCoordinate(10, 5));
        ObjectId gameId = new ObjectId();
        services.getPlayerID().set(PONE.getId());
        services.getGameID().set(gameId);
        services.repairShipHandler = repairShipHandler;
        Mockito.when(repairShipHandler.handleAction(PONE.getId(), gameId, target)).thenReturn(maskedGame);
        assertSame(maskedGame, services.repair(target));
    }

    @Test
    public void testECM() {
        TBMaskedGame maskedGame = new TBMaskedGame();
        maskedGame.setId("x");
        Target target = new Target();
        target.setPlayer(PONE.getMd5());
        target.setCoordinate(new GridCoordinate(10, 5));
        ObjectId gameId = new ObjectId();
        services.getPlayerID().set(PONE.getId());
        services.getGameID().set(gameId);
        services.ecmHandler = ecmHandler;
        Mockito.when(ecmHandler.handleAction(PONE.getId(), gameId, target)).thenReturn(maskedGame);
        assertSame(maskedGame, services.ecm(target));
    }

    @Test
    public void testMove() {
        TBMaskedGame maskedGame = new TBMaskedGame();
        Target target = new Target();
        target.setPlayer(PONE.getMd5());
        target.setCoordinate(new GridCoordinate(10, 5));
        ObjectId gameId = new ObjectId();
        services.getPlayerID().set(PONE.getId());
        services.getGameID().set(gameId);
        services.evasiveManeuverHandler = evasiveManeuverHandler;
        Mockito.when(evasiveManeuverHandler.handleAction(Matchers.eq(PONE.getId()), Matchers.eq(gameId), Matchers.eq(target))).thenReturn(maskedGame);
        assertSame(maskedGame, services.move(target));
    }
}
