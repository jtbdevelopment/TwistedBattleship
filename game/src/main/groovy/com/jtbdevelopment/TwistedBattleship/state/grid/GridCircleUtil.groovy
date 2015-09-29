package com.jtbdevelopment.TwistedBattleship.state.grid

import com.jtbdevelopment.TwistedBattleship.state.TBGame
import groovy.transform.CompileStatic
import org.springframework.stereotype.Component

/**
 * Date: 5/20/15
 * Time: 7:02 AM
 */
@Component
@CompileStatic
class GridCircleUtil {
    //  Details are cumulative - use entries for 10 + 15 to get circle for grid size 20
    final static Map<Integer, Set<GridCoordinate>> CIRCLE_OFFSETS = [
            (10): [
                    new GridCoordinate(0, 0),
                    new GridCoordinate(0, -1),
                    new GridCoordinate(0, 1),
                    new GridCoordinate(1, 0),
                    new GridCoordinate(-1, 0),
            ] as Set,  // 5, of 100 = 5%
            (15): [
                    new GridCoordinate(0, -2),
                    new GridCoordinate(0, 2),
                    new GridCoordinate(2, 0),
                    new GridCoordinate(-2, 0),
                    new GridCoordinate(1, 1),
                    new GridCoordinate(1, -1),
                    new GridCoordinate(-1, -1),
                    new GridCoordinate(-1, 1),
            ] as Set,  // 13, of 225 = 5.7%
            (20): [
                    new GridCoordinate(2, 2),
                    new GridCoordinate(2, -2),
                    new GridCoordinate(-2, -2),
                    new GridCoordinate(-2, 2),
                    new GridCoordinate(2, 1),
                    new GridCoordinate(1, 2),
                    new GridCoordinate(2, -1),
                    new GridCoordinate(-1, 2),
                    new GridCoordinate(-2, -1),
                    new GridCoordinate(-1, -2),
                    new GridCoordinate(-2, 1),
                    new GridCoordinate(1, -2),
            ] as Set,  // 25 of 400 = 6.25%
    ]

    @SuppressWarnings("GrMethodMayBeStatic")
    Set<GridCoordinate> computeCircleCoordinates(
            final TBGame game, final GridCoordinate centerCoordinate) {
        return new HashSet(CIRCLE_OFFSETS.findAll {
            it.key <= game.gridSize
        }.collectMany {
            int listSize, Set<GridCoordinate> adjustments ->
                adjustments.collect {
                    GridCoordinate adjustment ->
                        centerCoordinate.add(adjustment)
                }
        }.findAll {
            GridCoordinate it -> it.isValidCoordinate(game)
        })
    }
}
