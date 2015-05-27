package com.jtbdevelopment.TwistedBattleship.rest.handlers.helpers

import com.jtbdevelopment.TwistedBattleship.state.grid.GridCoordinate
import com.jtbdevelopment.TwistedBattleship.state.ships.ShipState
import groovy.transform.CompileStatic
import org.springframework.stereotype.Component

/**
 * Date: 5/26/15
 * Time: 6:36 PM
 */
@Component
@CompileStatic
class ShipRotater {
    List<GridCoordinate> rotateShip(final ShipState ship) {
        List<GridCoordinate> cells = ship.shipGridCells
        List<GridCoordinate> initial

        GridCoordinate rotatePoint = cells[(int) (cells.size() / 2)]
        if (cells[0].row == rotatePoint.row) {
            //  Horizontal to Vertical
            //  4,3 4,4 4,5 4,6  ->  2,5 3,5  4,5  5,5
            initial = cells.collect {
                GridCoordinate current ->
                    new GridCoordinate(current.row + (current.column - rotatePoint.column), rotatePoint.column)
            }
        } else {
            //  Vertical to Horizontal
            //  5,6 6,6 7,6 8,6 -> 7,4  7,5  7,6  7,7
            initial = cells.collect {
                GridCoordinate current ->
                    new GridCoordinate(rotatePoint.row, current.column + (current.row - rotatePoint.row))
            }
        }
        initial.sort()
        initial
    }
}
