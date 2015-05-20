package com.jtbdevelopment.TwistedBattleship.state.grid

import com.jtbdevelopment.TwistedBattleship.state.TBGame
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

/**
 * Date: 5/20/15
 * Time: 7:02 AM
 */
@Component
@CompileStatic
class GridCircleUtil {
    @Autowired
    GridSizeUtil gridSizeUtil

    final static Map<Integer, List<GridCoordinate>> CIRCLE_OFFSETS = [
            (10): [
                    new GridCoordinate(0, -1),
                    new GridCoordinate(0, 0),
                    new GridCoordinate(0, 1),
                    new GridCoordinate(1, 0),
                    new GridCoordinate(-1, 0),
            ],  // 5, of 100 = 5%
            (15): [
                    new GridCoordinate(0, -2),
                    new GridCoordinate(0, 2),
                    new GridCoordinate(2, 0),
                    new GridCoordinate(-2, 0),
                    new GridCoordinate(1, 1),
                    new GridCoordinate(1, -1),
                    new GridCoordinate(-1, -1),
                    new GridCoordinate(-1, 1),
            ],  // 13, of 225 = 5.7%
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
            ],  // 25 of 400 = 6.25%
    ]

    Collection<GridCoordinate> computeCircleCoordinates(
            final TBGame game, final GridCoordinate centerCoordinate) {
        Collection<GridCoordinate> coordinates = CIRCLE_OFFSETS.findAll {
            it.key <= game.gridSize
        }.collectMany {
            int listSize, List<GridCoordinate> adjustments ->
                adjustments.collect {
                    GridCoordinate adjustment ->
                        centerCoordinate.add(adjustment)
                }
        }.findAll {
            GridCoordinate it -> gridSizeUtil.isValidCoordinate(game, it)
        }
        coordinates
    }
}
