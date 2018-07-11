package com.jtbdevelopment.TwistedBattleship.rest.handlers.helpers;

import com.jtbdevelopment.TwistedBattleship.state.TBGame;
import com.jtbdevelopment.TwistedBattleship.state.grid.GridCoordinate;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Date: 5/27/15
 * Time: 6:36 PM
 */
@Component
public class FogCoordinatesGenerator {

    private static final List<List<Integer>> FOG_OFFSET =
            Arrays.asList(
                    Arrays.asList(-1, 0, -1),
                    Arrays.asList(0, 1, 2),
                    Arrays.asList(-2, -1, 0));
    private Random generator;

    public FogCoordinatesGenerator() {
        generator = new Random();
    }

    //  testing
    FogCoordinatesGenerator(final Random generator) {
        this.generator = generator;
    }

    public Set<GridCoordinate> generateFogCoordinates(final TBGame game, final List<GridCoordinate> initialCoordinates, final List<GridCoordinate> newCoordinates) {
        final Set<GridCoordinate> fogCoordinates = new HashSet<>();
        fogCoordinates.addAll(newCoordinates);
        fogCoordinates.addAll(initialCoordinates);
        List<Integer> fogRows = FOG_OFFSET.get(generator.nextInt(FOG_OFFSET.size() - 1));
        final List<Integer> fogCols = FOG_OFFSET.get(generator.nextInt(FOG_OFFSET.size() - 1));
        fogRows.forEach((rowOffset) ->
                fogCols.forEach((colOffset) ->
                        fogCoordinates.addAll(
                                newCoordinates.stream()
                                        .map(newCoordinate -> newCoordinate.add(rowOffset, colOffset))
                                        .filter(fogCoordinate -> fogCoordinate.isValidCoordinate(game))
                                        .collect(Collectors.toList()))));
        return fogCoordinates;
    }
}
