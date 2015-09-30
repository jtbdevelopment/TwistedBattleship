package com.jtbdevelopment.TwistedBattleship.state.grid

import com.jtbdevelopment.TwistedBattleship.state.TBGame
import groovy.transform.CompileStatic
import org.springframework.stereotype.Component

/**
 * Date: 9/29/15
 * Time: 6:46 AM
 */
@Component
@CompileStatic
class ConsolidateGridViews {
    @SuppressWarnings("GrMethodMayBeStatic")
    Grid createConsolidatedView(final TBGame game, final Iterable<Grid> views) {
        int size = game.gridSize
        Grid consolidatedView = new Grid(size)
        (0..size - 1).each {
            int row ->
                (0..size - 1).each {
                    int col ->
                        List<GridCellState> states = views.collect {
                            Grid it ->
                                it.get(row, col)
                        }.sort {
                            GridCellState a, GridCellState b ->
                                b.rank - a.rank // reverse sort
                        }
                        consolidatedView.set(
                                row,
                                col,
                                states[0])
                }
        }
        consolidatedView
    }
}
