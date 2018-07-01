package com.jtbdevelopment.TwistedBattleship.rest.handlers.helpers;

import com.jtbdevelopment.TwistedBattleship.state.TBGame;
import com.jtbdevelopment.TwistedBattleship.state.grid.GridCoordinate;
import com.jtbdevelopment.TwistedBattleship.state.ships.ShipState;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Date: 5/26/15
 * Time: 7:02 PM
 */
@Component
public class ShipRelocatorCalculator {
    public List<GridCoordinate> relocateShip(
            final TBGame game,
            final ShipState state,
            final List<GridCoordinate> initialCoordinates,
            final Set<GridCoordinate> otherShipCoordinates,
            final boolean moveRows,
            final List<Integer> adjustmentSequenceToTry) {
        return adjustmentSequenceToTry.stream().map(move -> {
            GridCoordinate adjustment = moveRows ? new GridCoordinate(move, 0) : new GridCoordinate(0, move);

            List<GridCoordinate> attempt = initialCoordinates.stream()
                    .map(c -> c.add(adjustment))
                    .collect(Collectors.toList());

            Optional<GridCoordinate> invalid =
                    attempt.stream()
                            .filter(c -> !c.isValidCoordinate(game) || otherShipCoordinates.contains(c))
                            .findFirst();

            if (invalid.isPresent() || state.getShipGridCells().equals(attempt)) {
                return null;
            } else {
                Collections.sort(attempt);
                return attempt;
            }
        }).filter(Objects::nonNull).findFirst().orElse(null);
    }
}
