package com.jtbdevelopment.TwistedBattleship.rest.handlers.helpers;

import com.jtbdevelopment.TwistedBattleship.state.TBGame;
import com.jtbdevelopment.TwistedBattleship.state.grid.GridCoordinate;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import static org.junit.Assert.assertEquals;

/**
 * Date: 5/27/15
 * Time: 6:38 PM
 */
public class FogCoordinatesGeneratorTest {
    private FogCoordinatesGenerator fogCoordinatesGenerator = new FogCoordinatesGenerator();
    private TBGame game;

    @Test
    public void testNormalCase() {
        fogCoordinatesGenerator.setGenerator(new Random() {
            public int nextInt(int max) {

                assert 2 == max;
                return 1;
            }
        });
        Set<GridCoordinate> fogged = fogCoordinatesGenerator.generateFogCoordinates(game,
                Arrays.asList(new GridCoordinate(5, 5), new GridCoordinate(6, 5), new GridCoordinate(7, 5)),
                Arrays.asList(new GridCoordinate(7, 5), new GridCoordinate(8, 5), new GridCoordinate(9, 5)));

        assertEquals(
                new HashSet<>(
                        Arrays.asList(
                                new GridCoordinate(5, 5),
                                new GridCoordinate(6, 5),
                                new GridCoordinate(7, 5),
                                new GridCoordinate(7, 5),
                                new GridCoordinate(8, 5),
                                new GridCoordinate(9, 5),
                                new GridCoordinate(7, 6),
                                new GridCoordinate(8, 6),
                                new GridCoordinate(9, 6),
                                new GridCoordinate(7, 7),
                                new GridCoordinate(8, 7),
                                new GridCoordinate(9, 7),
                                new GridCoordinate(8, 5),
                                new GridCoordinate(9, 5),
                                new GridCoordinate(9, 5),
                                new GridCoordinate(8, 6),
                                new GridCoordinate(9, 6),
                                new GridCoordinate(9, 6),
                                new GridCoordinate(8, 7),
                                new GridCoordinate(9, 7),
                                new GridCoordinate(9, 7),
                                new GridCoordinate(9, 5),
                                new GridCoordinate(10, 5),
                                new GridCoordinate(11, 5),
                                new GridCoordinate(9, 6),
                                new GridCoordinate(10, 6),
                                new GridCoordinate(11, 6),
                                new GridCoordinate(9, 7),
                                new GridCoordinate(10, 7),
                                new GridCoordinate(11, 7))),
                fogged);
    }

    @Test
    public void testOffEdgeCase() {
        fogCoordinatesGenerator.setGenerator(new Random() {
            public int nextInt(int max) {

                assert 2 == max;
                return 1;
            }
        });
        Set<GridCoordinate> fogged = fogCoordinatesGenerator.generateFogCoordinates(game,
                Arrays.asList(new GridCoordinate(5, 5), new GridCoordinate(6, 5), new GridCoordinate(7, 5)),
                Arrays.asList(new GridCoordinate(13, 5), new GridCoordinate(14, 5), new GridCoordinate(15, 5)));

        assertEquals(
                new HashSet<>(
                        Arrays.asList(
                                new GridCoordinate(5, 5),
                                new GridCoordinate(6, 5),
                                new GridCoordinate(7, 5),
                                new GridCoordinate(13, 5),
                                new GridCoordinate(14, 5),
                                new GridCoordinate(15, 5),
                                new GridCoordinate(13, 6),
                                new GridCoordinate(14, 6),
                                new GridCoordinate(13, 7),
                                new GridCoordinate(14, 7))),
                fogged);
    }

    @Before
    public void setup() {
        game = new TBGame();
        game.setGridSize(15);

    }
}
