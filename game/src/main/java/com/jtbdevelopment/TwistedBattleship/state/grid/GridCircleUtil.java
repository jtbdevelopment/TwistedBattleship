package com.jtbdevelopment.TwistedBattleship.state.grid;

import com.jtbdevelopment.TwistedBattleship.state.TBGame;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Date: 5/20/15
 * Time: 7:02 AM
 */
@Component
public class GridCircleUtil {
    public static final Map<Integer, Set<GridCoordinate>> CIRCLE_OFFSETS = new HashMap<Integer, Set<GridCoordinate>>() {{
        put(10, new HashSet<>(Arrays.asList(
                new GridCoordinate(0, 0),
                new GridCoordinate(0, -1),
                new GridCoordinate(0, 1),
                new GridCoordinate(1, 0),
                new GridCoordinate(-1, 0))));
        put(15, new HashSet<>(Arrays.asList(
                new GridCoordinate(0, -2),
                new GridCoordinate(0, 2),
                new GridCoordinate(2, 0),
                new GridCoordinate(-2, 0),
                new GridCoordinate(1, 1),
                new GridCoordinate(1, -1),
                new GridCoordinate(-1, -1),
                new GridCoordinate(-1, 1))));
        put(20, new HashSet<>(Arrays.asList(
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
                new GridCoordinate(1, -2))));
    }};

    public Set<GridCoordinate> computeCircleCoordinates(final TBGame game, final GridCoordinate centerCoordinate) {
        return CIRCLE_OFFSETS.entrySet().stream()
                .filter(e -> e.getKey() <= game.getGridSize())
                .flatMap(e -> e.getValue().stream())
                .collect(Collectors.toList())
                .stream()
                .map(centerCoordinate::add)
                .filter(newCoordinate -> newCoordinate.isValidCoordinate(game))
                .collect(Collectors.toSet());
    }
}
