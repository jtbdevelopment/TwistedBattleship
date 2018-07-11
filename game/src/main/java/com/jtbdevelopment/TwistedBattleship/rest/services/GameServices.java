package com.jtbdevelopment.TwistedBattleship.rest.services;

import com.jtbdevelopment.TwistedBattleship.rest.Target;
import com.jtbdevelopment.TwistedBattleship.rest.handlers.*;
import com.jtbdevelopment.TwistedBattleship.rest.services.messages.ShipAndCoordinates;
import com.jtbdevelopment.TwistedBattleship.state.GameFeature;
import com.jtbdevelopment.TwistedBattleship.state.TBGame;
import com.jtbdevelopment.TwistedBattleship.state.masked.TBMaskedGame;
import com.jtbdevelopment.TwistedBattleship.state.ships.ShipState;
import com.jtbdevelopment.games.mongo.players.MongoPlayer;
import com.jtbdevelopment.games.rest.AbstractMultiPlayerGameServices;
import com.jtbdevelopment.games.rest.handlers.*;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * Date: 11/11/14
 * Time: 9:42 PM
 */
@Component
public class GameServices extends AbstractMultiPlayerGameServices<ObjectId, GameFeature, TBGame, TBMaskedGame, MongoPlayer> {
    @Autowired
    protected SetupShipsHandler setupShipsHandler;
    @Autowired
    protected FireAtCoordinateHandler fireAtCoordinateHandler;
    @Autowired
    protected SpyHandler spyHandler;
    @Autowired
    protected RepairShipHandler repairShipHandler;
    @Autowired
    protected ECMHandler ecmHandler;
    @Autowired
    protected EvasiveManeuverHandler evasiveManeuverHandler;
    @Autowired
    protected CruiseMissileHandler cruiseMissileHandler;

    public GameServices(final GameGetterHandler<ObjectId, GameFeature, TBGame, TBMaskedGame, MongoPlayer> gameGetterHandler, final DeclineRematchOptionHandler<ObjectId, GameFeature, TBGame, TBMaskedGame, MongoPlayer> declineRematchOptionHandler, final ChallengeResponseHandler<ObjectId, GameFeature, TBGame, TBMaskedGame, MongoPlayer> responseHandler, final ChallengeToRematchHandler<ObjectId, GameFeature, TBGame, TBMaskedGame, MongoPlayer> rematchHandler, final QuitHandler<ObjectId, GameFeature, TBGame, TBMaskedGame, MongoPlayer> quitHandler) {
        super(gameGetterHandler, declineRematchOptionHandler, responseHandler, rematchHandler, quitHandler);
    }

    @PUT
    @Path("setup")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Object setupShips(final List<ShipAndCoordinates> ships) {
        List<ShipState> shipStates = ships.stream()
                .map(s ->
                        new ShipState(s.getShip(),
                                new TreeSet<>(s.getCoordinates())))
                .collect(Collectors.toList());
        return setupShipsHandler.handleAction(getPlayerID().get(), getGameID().get(), shipStates);
    }

    @PUT
    @Path("fire")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Object fire(final Target target) {
        return fireAtCoordinateHandler.handleAction(getPlayerID().get(), getGameID().get(), target);
    }

    @PUT
    @Path("missile")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Object cruiseMissile(final Target target) {
        return cruiseMissileHandler.handleAction(getPlayerID().get(), getGameID().get(), target);
    }

    @PUT
    @Path("spy")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Object spy(final Target target) {
        return spyHandler.handleAction(getPlayerID().get(), getGameID().get(), target);
    }

    @PUT
    @Path("repair")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Object repair(final Target target) {
        return repairShipHandler.handleAction(getPlayerID().get(), getGameID().get(), target);
    }

    @PUT
    @Path("ecm")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Object ecm(final Target target) {
        return ecmHandler.handleAction(getPlayerID().get(), getGameID().get(), target);
    }

    @PUT
    @Path("move")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Object move(final Target target) {
        return evasiveManeuverHandler.handleAction(getPlayerID().get(), getGameID().get(), target);
    }
}
