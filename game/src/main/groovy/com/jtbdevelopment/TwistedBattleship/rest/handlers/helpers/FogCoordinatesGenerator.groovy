package com.jtbdevelopment.TwistedBattleship.rest.handlers.helpers

import com.jtbdevelopment.TwistedBattleship.state.TBGame
import com.jtbdevelopment.TwistedBattleship.state.grid.GridCoordinate
import com.jtbdevelopment.TwistedBattleship.state.grid.GridSizeUtil
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

/**
 * Date: 5/27/15
 * Time: 6:36 PM
 */
@CompileStatic
@Component
class FogCoordinatesGenerator {
    Random generator = new Random()

    @Autowired
    GridSizeUtil gridSizeUtil

    private static final List<List<Integer>> FOG_OFFSET = [
            [-1, 0, -1],
            [0, 1, 2],
            [-2, -1, 0]
    ]

    Set<GridCoordinate> generateFogCoordinates(
            final TBGame game,
            final List<GridCoordinate> initialCoordinates,
            final List<GridCoordinate> newCoordinates) {
        Set<GridCoordinate> fogCoordinates = [] as Set
        fogCoordinates.addAll(newCoordinates)
        fogCoordinates.addAll(initialCoordinates)
        List<Integer> fogRows = FOG_OFFSET[generator.nextInt(FOG_OFFSET.size() - 1)]
        List<Integer> fogCols = FOG_OFFSET[generator.nextInt(FOG_OFFSET.size() - 1)]
        fogRows.each {
            Integer rowOffset ->
                fogCols.each {
                    Integer colOffset ->
                        fogCoordinates.addAll(
                                newCoordinates.collect {
                                    GridCoordinate newCoordinate ->
                                        newCoordinate.add(rowOffset, colOffset)
                                }.findAll {
                                    GridCoordinate fogCoordinate ->
                                        gridSizeUtil.isValidCoordinate(game, fogCoordinate)
                                }
                        )
                }
        }
        fogCoordinates
    }
}
