package com.jtbdevelopment.TwistedBattleship.rest.handlers.helpers;

import com.jtbdevelopment.TwistedBattleship.state.grid.GridCoordinate;
import com.jtbdevelopment.TwistedBattleship.state.ships.ShipState;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Date: 5/26/15
 * Time: 6:36 PM
 */
@Component
public class ShipRotater {
    public List<GridCoordinate> rotateShip(final ShipState ship) {
        List<GridCoordinate> cells = ship.getShipGridCells();

        final GridCoordinate rotatePoint = cells.get(cells.size() / 2);
        if (cells.get(0).getRow() == rotatePoint.getRow()) {
            //  Horizontal to Vertical
            //  4,3 4,4 4,5 4,6  ->  2,5 3,5  4,5  5,5
            return cells.stream()
                    .map(current -> new GridCoordinate(
                            current.getRow() + (current.getColumn() - rotatePoint.getColumn()),
                            rotatePoint.getColumn()))
                    .sorted()
                    .collect(Collectors.toList());
        } else {
            //  Vertical to Horizontal
            //  5,6 6,6 7,6 8,6 -> 7,4  7,5  7,6  7,7
            return cells.stream()
                    .map(current -> new GridCoordinate(
                            rotatePoint.getRow(),
                            current.getColumn() + (current.getRow() - rotatePoint.getRow())))
                    .sorted()
                    .collect(Collectors.toList());
        }

    }

}
