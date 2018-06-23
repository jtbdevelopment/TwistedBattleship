package com.jtbdevelopment.TwistedBattleship.state.ships;

import com.jtbdevelopment.TwistedBattleship.exceptions.NotAllShipsSetupException;
import com.jtbdevelopment.TwistedBattleship.exceptions.ShipPlacementsNotValidException;
import com.jtbdevelopment.TwistedBattleship.state.TBGame;
import com.jtbdevelopment.TwistedBattleship.state.grid.GridCoordinate;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Date: 4/30/15
 * Time: 8:44 PM
 */
@Component
public class ShipPlacementValidator {
    public void validateShipPlacementsForGame(final TBGame game, final List<ShipState> states) {
        List<Ship> ships = states.stream().map(ShipState::getShip).sorted().collect(Collectors.toList());
        if (!ships.equals(game.getStartingShips())) {
            throw new NotAllShipsSetupException();
        }


        //  TODO - cleanup this code a bit
        final Set<GridCoordinate> used = new HashSet<>();
        states.forEach(state -> {
            if (state.getShipGridCells().size() != state.getShip().getGridSize()) {
                throw new ShipPlacementsNotValidException();
            }

            List<GridCoordinate> shipGridCells = state.getShipGridCells();
            shipGridCells.forEach(coordinate -> {
                if (used.contains(coordinate)) {
                    throw new ShipPlacementsNotValidException();
                }

                used.add(coordinate);

                if (!coordinate.isValidCoordinate(game)) {
                    throw new ShipPlacementsNotValidException();
                }
            });
            Collections.sort(shipGridCells);
            List<Integer> rows = shipGridCells.stream().map(GridCoordinate::getRow).sorted().collect(Collectors.toList());
            List<Integer> columns = shipGridCells.stream().map(GridCoordinate::getColumn).sorted().collect(Collectors.toList());
            if (new HashSet<>(rows).size() == 1) {
                for (int i = 1; i < (columns.size()); i++) {
                    if (columns.get(i) - columns.get(i - 1) != 1) {
                        throw new ShipPlacementsNotValidException();
                    }
                }
            } else if (new HashSet<>(columns).size() == 1) {
                for (int i = 1; i < (rows.size()); i++) {
                    if (rows.get(i) - rows.get(i - 1) != 1) {
                        throw new ShipPlacementsNotValidException();
                    }
                }
            } else {
                throw new ShipPlacementsNotValidException();
            }
        });
    }

}
