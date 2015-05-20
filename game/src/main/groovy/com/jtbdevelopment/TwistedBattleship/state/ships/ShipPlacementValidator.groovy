package com.jtbdevelopment.TwistedBattleship.state.ships

import com.jtbdevelopment.TwistedBattleship.exceptions.NotAllShipsSetupException
import com.jtbdevelopment.TwistedBattleship.exceptions.ShipPlacementsNotValidException
import com.jtbdevelopment.TwistedBattleship.state.TBGame
import com.jtbdevelopment.TwistedBattleship.state.grid.GridCoordinate
import com.jtbdevelopment.TwistedBattleship.state.grid.GridSizeUtil
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

/**
 * Date: 4/30/15
 * Time: 8:44 PM
 */
@CompileStatic
@Component
class ShipPlacementValidator {
    @Autowired
    GridSizeUtil gridSizeUtil

    public void validateShipPlacementsForGame(final TBGame game, Map<Ship, ShipState> shipState) {
        if (shipState.size() != Ship.values().size()) {
            throw new NotAllShipsSetupException()
        }

        //  TODO - cleanup this code a bit
        Set<GridCoordinate> used = [] as Set
        shipState.each {
            Ship ship, ShipState state ->
                if (state.shipGridCells.size() != ship.gridSize) {
                    throw new ShipPlacementsNotValidException()
                }

                state.shipGridCells.each {
                    GridCoordinate coordinate ->
                        if (used.contains(coordinate)) {
                            throw new ShipPlacementsNotValidException()
                        }
                        used.add(coordinate)

                        if (!gridSizeUtil.isValidCoordinate(game, coordinate)) {
                            throw new ShipPlacementsNotValidException()
                        }
                }

                List<Integer> rows = state.shipGridCells.collect { it.row }.sort()
                List<Integer> cols = state.shipGridCells.collect { it.column }.sort()
                if ((rows as Set).size() == 1) {
                    (0..ship.gridSize - 2).each {
                        int i ->
                            if ((cols[i + 1] - cols[i]) != 1) {
                                throw new ShipPlacementsNotValidException()
                            }
                    }
                } else if ((cols as Set).size() == 1) {
                    (0..ship.gridSize - 2).each {
                        int i ->
                            if ((rows[i + 1] - rows[i]) != 1) {
                                throw new ShipPlacementsNotValidException()
                            }
                    }
                } else {
                    throw new ShipPlacementsNotValidException()
                }
        }
    }
}
