package com.jtbdevelopment.TwistedBattleship.rest.handlers.helpers

import com.jtbdevelopment.TwistedBattleship.state.TBGame
import com.jtbdevelopment.TwistedBattleship.state.grid.GridCoordinate

/**
 * Date: 5/27/15
 * Time: 6:38 PM
 */
class FogCoordinatesGeneratorTest extends GroovyTestCase {
    FogCoordinatesGenerator fogCoordinatesGenerator = new FogCoordinatesGenerator()

    TBGame game = new TBGame(gridSize: 15)

    void testNormalCase() {
        fogCoordinatesGenerator.generator = [
                nextInt: {
                    int max ->
                        assert 2 == max
                        return 1
                }
        ] as Random
        Set<GridCoordinate> fogged = fogCoordinatesGenerator.generateFogCoordinates(
                game,
                [new GridCoordinate(5, 5), new GridCoordinate(6, 5), new GridCoordinate(7, 5)],
                [new GridCoordinate(7, 5), new GridCoordinate(8, 5), new GridCoordinate(9, 5)]
        )

        assert [
                new GridCoordinate(5, 5), new GridCoordinate(6, 5), new GridCoordinate(7, 5),

                new GridCoordinate(7, 5), new GridCoordinate(8, 5), new GridCoordinate(9, 5),

                new GridCoordinate(7, 6), new GridCoordinate(8, 6), new GridCoordinate(9, 6),
                new GridCoordinate(7, 7), new GridCoordinate(8, 7), new GridCoordinate(9, 7),

                new GridCoordinate(8, 5), new GridCoordinate(9, 5), new GridCoordinate(9, 5),
                new GridCoordinate(8, 6), new GridCoordinate(9, 6), new GridCoordinate(9, 6),
                new GridCoordinate(8, 7), new GridCoordinate(9, 7), new GridCoordinate(9, 7),

                new GridCoordinate(9, 5), new GridCoordinate(10, 5), new GridCoordinate(11, 5),
                new GridCoordinate(9, 6), new GridCoordinate(10, 6), new GridCoordinate(11, 6),
                new GridCoordinate(9, 7), new GridCoordinate(10, 7), new GridCoordinate(11, 7),
        ] as Set == fogged
    }

    void testOffEdgeCase() {
        fogCoordinatesGenerator.generator = [
                nextInt: {
                    int max ->
                        assert 2 == max
                        return 1
                }
        ] as Random
        Set<GridCoordinate> fogged = fogCoordinatesGenerator.generateFogCoordinates(
                game,
                [new GridCoordinate(5, 5), new GridCoordinate(6, 5), new GridCoordinate(7, 5)],
                [new GridCoordinate(13, 5), new GridCoordinate(14, 5), new GridCoordinate(15, 5)]
        )

        assert [
                new GridCoordinate(5, 5), new GridCoordinate(6, 5), new GridCoordinate(7, 5),

                new GridCoordinate(13, 5), new GridCoordinate(14, 5), new GridCoordinate(15, 5),

                new GridCoordinate(13, 6), new GridCoordinate(14, 6),
                new GridCoordinate(13, 7), new GridCoordinate(14, 7),

                new GridCoordinate(14, 5), new GridCoordinate(14, 5),
                new GridCoordinate(14, 6), new GridCoordinate(14, 6),
                new GridCoordinate(14, 7), new GridCoordinate(14, 7),
        ] as Set == fogged
    }
}
