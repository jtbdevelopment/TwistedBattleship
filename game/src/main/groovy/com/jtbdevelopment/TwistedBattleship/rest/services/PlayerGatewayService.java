package com.jtbdevelopment.TwistedBattleship.rest.services;

import com.jtbdevelopment.TwistedBattleship.rest.GameFeatureInfo;
import com.jtbdevelopment.TwistedBattleship.rest.ShipInfo;
import com.jtbdevelopment.TwistedBattleship.state.GameFeature;
import com.jtbdevelopment.TwistedBattleship.state.TBGame;
import com.jtbdevelopment.TwistedBattleship.state.grid.GridCellState;
import com.jtbdevelopment.TwistedBattleship.state.grid.GridCircleUtil;
import com.jtbdevelopment.TwistedBattleship.state.grid.GridCoordinate;
import com.jtbdevelopment.TwistedBattleship.state.masked.TBMaskedGame;
import com.jtbdevelopment.TwistedBattleship.state.ships.Ship;
import com.jtbdevelopment.games.mongo.players.MongoPlayer;
import com.jtbdevelopment.games.rest.services.AbstractPlayerGatewayService;
import com.jtbdevelopment.games.rest.services.AbstractPlayerServices;
import groovy.lang.Closure;
import org.bson.types.ObjectId;
import org.codehaus.groovy.runtime.DefaultGroovyMethods;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Date: 11/14/14
 * Time: 6:36 AM
 */
@Path("/")
@Component
public class PlayerGatewayService extends AbstractPlayerGatewayService<ObjectId, GameFeature, TBGame, TBMaskedGame, MongoPlayer> {
    public PlayerGatewayService(final AbstractPlayerServices<ObjectId, GameFeature, TBGame, TBMaskedGame, MongoPlayer> playerServices) {
        super(playerServices);
    }

    @GET
    @Path("features")
    @Produces(MediaType.APPLICATION_JSON)
    public List<GameFeatureInfo> featuresAndDescriptions() {
        Map<GameFeature, List<GameFeature>> groupedFeatures = GameFeature.getGroupedFeatures();
        return groupedFeatures.keySet().stream()
                .sorted()
                .map(group -> new GameFeatureInfo(
                        group,
                        groupedFeatures.get(group).stream().map(GameFeatureInfo.Detail::new).collect(Collectors.toList())))
                .collect(Collectors.toList());
    }

    @GET
    @Path("circles")
    @Produces(MediaType.APPLICATION_JSON)
    public Map<Integer, Set<GridCoordinate>> circleSizes() {
        return GridCircleUtil.getCIRCLE_OFFSETS();
    }

    @GET
    @Path("ships")
    @Produces(MediaType.APPLICATION_JSON)
    public List<ShipInfo> ships() {
        return DefaultGroovyMethods.collect(Ship.values(), new Closure<ShipInfo>(this, this) {
            public ShipInfo doCall(Ship it) {
                return new ShipInfo(it);
            }

        });
    }

    @GET
    @Path("states")
    @Produces(MediaType.APPLICATION_JSON)
    public List<GridCellState> states() {
        return DefaultGroovyMethods.toList(GridCellState.values());
    }

}
