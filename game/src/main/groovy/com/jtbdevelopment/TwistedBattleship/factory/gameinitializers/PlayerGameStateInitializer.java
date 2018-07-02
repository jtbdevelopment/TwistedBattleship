package com.jtbdevelopment.TwistedBattleship.factory.gameinitializers;

import com.jtbdevelopment.TwistedBattleship.state.GameFeature;
import com.jtbdevelopment.TwistedBattleship.state.TBGame;
import com.jtbdevelopment.TwistedBattleship.state.TBPlayerState;
import com.jtbdevelopment.TwistedBattleship.state.grid.Grid;
import com.jtbdevelopment.TwistedBattleship.state.grid.GridCoordinate;
import com.jtbdevelopment.TwistedBattleship.state.ships.Ship;
import com.jtbdevelopment.TwistedBattleship.state.ships.ShipState;
import com.jtbdevelopment.games.factory.GameInitializer;
import com.jtbdevelopment.games.players.Player;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Date: 4/6/15
 * Time: 6:34 PM
 */
@Component
public class PlayerGameStateInitializer implements GameInitializer<TBGame> {
    @Override
    public void initializeGame(final TBGame game) {
        final int specialMoves = game.getPlayers().size() - 1;


        final List<ShipState> defaultShipPlacements = new LinkedList<>();

        for (int shipIndex = 0; shipIndex < game.getStartingShips().size(); ++shipIndex) {
            Ship ship = game.getStartingShips().get(shipIndex);

            SortedSet<GridCoordinate> coordinates = new TreeSet<>();
            for (int gridIndex = 1; gridIndex <= ship.getGridSize(); ++gridIndex) {
                coordinates.add(new GridCoordinate(shipIndex, gridIndex));
            }
            defaultShipPlacements.add(new ShipState(ship, coordinates));
        }

        game.getPlayers().forEach(p -> {
            Set<Player<ObjectId>> opponents = new HashSet<>(game.getPlayers());
            opponents.remove(p);
            TBPlayerState state = new TBPlayerState();
            state.setStartingShips(game.getStartingShips());
            state.setShipStates(defaultShipPlacements);
            state.setEcmsRemaining(game.getFeatures().contains(GameFeature.ECMEnabled) ? specialMoves : 0);
            state.setCruiseMissilesRemaining(game.getFeatures().contains(GameFeature.CruiseMissileEnabled) ? 1 : 0);
            state.setEmergencyRepairsRemaining(game.getFeatures().contains(GameFeature.EREnabled) ? specialMoves : 0);
            state.setEvasiveManeuversRemaining(game.getFeatures().contains(GameFeature.EMEnabled) ? specialMoves : 0);
            state.setSpysRemaining(game.getFeatures().contains(GameFeature.SpyEnabled) ? specialMoves : 0);
            state.setOpponentGrids(opponents.stream().collect(Collectors.toMap(Player::getId, o -> new Grid(game.getGridSize()))));
            state.setOpponentViews(opponents.stream().collect(Collectors.toMap(Player::getId, o -> new Grid(game.getGridSize()))));
            game.getPlayerDetails().put(p.getId(), state);
        });
    }

    public final int getOrder() {
        return LATE_ORDER;
    }

}
