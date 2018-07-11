package com.jtbdevelopment.TwistedBattleship.ai.common;

import com.jtbdevelopment.TwistedBattleship.rest.handlers.SetupShipsHandler;
import com.jtbdevelopment.TwistedBattleship.state.TBGame;
import com.jtbdevelopment.TwistedBattleship.state.grid.GridCoordinate;
import com.jtbdevelopment.TwistedBattleship.state.ships.ShipState;
import com.jtbdevelopment.games.mongo.players.MongoPlayer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Date: 9/29/15
 * Time: 6:50 AM
 */
@Component
public class RandomizedSetup {
    @Autowired
    private SetupShipsHandler setupShipsHandler;
    private Random random = new Random();

    public void setup(final TBGame game, final MongoPlayer player) {
        final Set<GridCoordinate> used = new HashSet<>();
        List<ShipState> shipStates = game.getStartingShips()
                .stream()
                .map(ship -> {
                    boolean horizontal = random.nextInt(100) < 50;
                    TreeSet<GridCoordinate> set = new TreeSet<>();
                    if (horizontal) {
                        boolean ok = false;
                        while (!ok) {
                            final int row = random.nextInt(game.getGridSize());
                            int startCol = random.nextInt(game.getGridSize() - ship.getGridSize());
                            set.clear();
                            for (int col = startCol; col <= startCol + ship.getGridSize(); ++col) {
                                set.add(new GridCoordinate(row, col));
                            }
                            ok = set.stream().noneMatch(used::contains);
                        }

                    } else {
                        boolean ok = false;
                        while (!ok) {
                            int startRow = random.nextInt(game.getGridSize() - ship.getGridSize());
                            final int col = random.nextInt(game.getGridSize());
                            set.clear();
                            for (int row = startRow; row <= startRow + ship.getGridSize(); ++row) {
                                set.add(new GridCoordinate(row, col));
                            }
                            ok = set.stream().noneMatch(used::contains);
                        }

                    }

                    used.addAll(set);
                    return new ShipState(ship, set);
                })
                .collect(Collectors.toList());
        setupShipsHandler.handleAction(player.getId(), game.getId(), shipStates);
    }
}
