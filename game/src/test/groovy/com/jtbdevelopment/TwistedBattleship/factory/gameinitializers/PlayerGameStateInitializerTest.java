package com.jtbdevelopment.TwistedBattleship.factory.gameinitializers;

import com.jtbdevelopment.TwistedBattleship.state.GameFeature;
import com.jtbdevelopment.TwistedBattleship.state.TBGame;
import com.jtbdevelopment.TwistedBattleship.state.TBPlayerState;
import com.jtbdevelopment.TwistedBattleship.state.grid.GridCellState;
import com.jtbdevelopment.TwistedBattleship.state.grid.GridCoordinate;
import com.jtbdevelopment.TwistedBattleship.state.ships.Ship;
import com.jtbdevelopment.games.factory.GameInitializer;
import com.jtbdevelopment.games.mongo.MongoGameCoreTestCase;
import com.jtbdevelopment.games.players.Player;
import org.bson.types.ObjectId;
import org.junit.Assert;
import org.junit.Test;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.Assert.assertEquals;

/**
 * Date: 4/6/15
 * Time: 6:34 PM
 */
public class PlayerGameStateInitializerTest extends MongoGameCoreTestCase {
    private PlayerGameStateInitializer initializer = new PlayerGameStateInitializer();

    @Test
    public void testGetOrder() {
        assertEquals(GameInitializer.LATE_ORDER, initializer.getOrder());
    }

    @Test
    public void testInitializeGame() {
        TBGame game = new TBGame();
        game.setFeatures(new HashSet<>(Arrays.asList(GameFeature.Grid15x15, GameFeature.Single, GameFeature.ECMEnabled, GameFeature.EREnabled, GameFeature.SpyEnabled, GameFeature.EMEnabled, GameFeature.CruiseMissileEnabled)));
        game.setPlayers(Arrays.asList(PONE, PTWO, PTHREE));
        game.setGridSize(12);
        game.setStartingShips(Arrays.asList(Ship.values()));

        assertEquals(new LinkedHashMap(), game.getPlayerDetails());
        initializer.initializeGame(game);
        assertEquals(3, game.getPlayerDetails().size());
        for (int index = 0; index < game.getStartingShips().size(); ++index) {
            Ship ship = game.getStartingShips().get(index);
            int finalIndex = index;
            List<GridCoordinate> expectedCoordinates = IntStream.range(1, ship.getGridSize() + 1).mapToObj(col -> new GridCoordinate(finalIndex, col)).collect(Collectors.toList());
            game.getPlayerDetails().forEach((id, state) -> {
                assertEquals(ship, state.getShipStates().get(finalIndex).getShip());
                assertEquals(expectedCoordinates, state.getShipStates().get(finalIndex).getShipGridCells());
            });
        }

        game.getPlayers().forEach(p -> validatePlayerStates(game, p, 2));
    }

    @Test
    public void testInitializeGameNoOptionalFeatures() {
        TBGame game = new TBGame();
        game.setFeatures(new HashSet<>(Arrays.asList(GameFeature.Grid20x20)));
        game.setPlayers(Arrays.asList(PONE, PTWO, PTHREE));
        game.setGridSize(12);
        game.setStartingShips(Arrays.asList(Ship.values()));

        assertEquals(new LinkedHashMap(), game.getPlayerDetails());
        initializer.initializeGame(game);
        assertEquals(3, game.getPlayerDetails().size());
        game.getPlayers().forEach(p -> validatePlayerStates(game, p, 0));
    }

    private void validatePlayerStates(final TBGame game, final Player<ObjectId> p, final int expectedSpecials) {

        Set<ObjectId> opponentIds = game.getPlayers().stream()
                .map(Player::getId)
                .filter(id -> !p.getId().equals(id))
                .collect(Collectors.toSet());
        TBPlayerState playerState = game.getPlayerDetails().get(p.getId());
        Assert.assertNotNull(playerState);
        assertEquals(expectedSpecials, playerState.getEcmsRemaining());
        assertEquals(expectedSpecials, playerState.getEvasiveManeuversRemaining());
        assertEquals(expectedSpecials, playerState.getEmergencyRepairsRemaining());
        assertEquals(expectedSpecials, playerState.getSpysRemaining());
        assertEquals(((expectedSpecials > 0) ? 1 : 0), playerState.getCruiseMissilesRemaining());
        assertEquals(game.getStartingShips(), playerState.getStartingShips());
        assertEquals(opponentIds, playerState.getOpponentViews().keySet());
        assertEquals(opponentIds, playerState.getOpponentGrids().keySet());
        game.getPlayerDetails()
                .values()
                .stream()
                .map(TBPlayerState::getOpponentGrids)
                .flatMap(grids -> grids.values().stream())
                .forEach(grid -> grid.stream().forEach(coordinate -> assertEquals(GridCellState.Unknown, grid.get(coordinate))));

        game.getPlayerDetails()
                .values()
                .stream()
                .map(TBPlayerState::getOpponentViews)
                .flatMap(grids -> grids.values().stream())
                .forEach(grid -> grid.stream().forEach(coordinate -> assertEquals(GridCellState.Unknown, grid.get(coordinate))));
    }
}
