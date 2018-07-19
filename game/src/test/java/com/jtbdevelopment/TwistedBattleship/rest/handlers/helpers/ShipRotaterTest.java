package com.jtbdevelopment.TwistedBattleship.rest.handlers.helpers;

import com.jtbdevelopment.TwistedBattleship.state.grid.GridCoordinate;
import com.jtbdevelopment.TwistedBattleship.state.ships.Ship;
import com.jtbdevelopment.TwistedBattleship.state.ships.ShipState;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.assertEquals;

/**
 * Date: 5/26/15
 * Time: 6:40 PM
 */
public class ShipRotaterTest {
    private ShipRotater rotater = new ShipRotater();
    private Map<List<GridCoordinate>, List<GridCoordinate>> EXPECTED;

    {
        Map<List<GridCoordinate>, List<GridCoordinate>> map = new HashMap<>();
        map.put(Arrays.asList(new GridCoordinate(0, 1), new GridCoordinate(0, 2)),
                Arrays.asList(new GridCoordinate(-1, 2), new GridCoordinate(0, 2)));
        map.put(Arrays.asList(new GridCoordinate(7, 7), new GridCoordinate(7, 8), new GridCoordinate(7, 9)),
                Arrays.asList(new GridCoordinate(6, 8), new GridCoordinate(7, 8), new GridCoordinate(8, 8)));
        map.put(Arrays.asList(new GridCoordinate(4, 3), new GridCoordinate(4, 4), new GridCoordinate(4, 5), new GridCoordinate(4, 6)),
                Arrays.asList(new GridCoordinate(2, 5), new GridCoordinate(3, 5), new GridCoordinate(4, 5), new GridCoordinate(5, 5)));
        map.put(Arrays.asList(new GridCoordinate(4, 3), new GridCoordinate(4, 4), new GridCoordinate(4, 5), new GridCoordinate(4, 6), new GridCoordinate(4, 7)),
                Arrays.asList(new GridCoordinate(2, 5), new GridCoordinate(3, 5), new GridCoordinate(4, 5), new GridCoordinate(5, 5), new GridCoordinate(6, 5)));
        map.put(Arrays.asList(new GridCoordinate(10, 10), new GridCoordinate(11, 10)),
                Arrays.asList(new GridCoordinate(11, 9), new GridCoordinate(11, 10)));
        map.put(Arrays.asList(new GridCoordinate(9, 10), new GridCoordinate(10, 10), new GridCoordinate(11, 10)),
                Arrays.asList(new GridCoordinate(10, 9), new GridCoordinate(10, 10), new GridCoordinate(10, 11)));
        map.put(Arrays.asList(new GridCoordinate(5, 6), new GridCoordinate(6, 6), new GridCoordinate(7, 6), new GridCoordinate(8, 6)),
                Arrays.asList(new GridCoordinate(7, 4), new GridCoordinate(7, 5), new GridCoordinate(7, 6), new GridCoordinate(7, 7)));
        map.put(Arrays.asList(new GridCoordinate(11, 15), new GridCoordinate(12, 15), new GridCoordinate(13, 15), new GridCoordinate(14, 15), new GridCoordinate(15, 15)),
                Arrays.asList(new GridCoordinate(13, 13), new GridCoordinate(13, 14), new GridCoordinate(13, 15), new GridCoordinate(13, 16), new GridCoordinate(13, 17)));
        EXPECTED = map;
    }

    @Test
    public void testRotateShip() {
        EXPECTED.forEach((input, expected) -> {
            assertEquals(expected, rotater.rotateShip(new ShipState(Ship.Battleship, new TreeSet<>(input))));
        });
    }
}
