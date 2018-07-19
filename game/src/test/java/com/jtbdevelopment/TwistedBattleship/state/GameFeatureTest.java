package com.jtbdevelopment.TwistedBattleship.state;

import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * Date: 4/1/15
 * Time: 7:11 PM
 */
public class GameFeatureTest {
    @Test
    public void testGroupedFeatures() {
        Map<GameFeature, List<GameFeature>> map = new HashMap<>();
        map.put(GameFeature.GridSize, Arrays.asList(GameFeature.Grid10x10, GameFeature.Grid15x15, GameFeature.Grid20x20));
        map.put(GameFeature.ActionsPerTurn, Arrays.asList(GameFeature.PerShip, GameFeature.Single));
        map.put(GameFeature.FogOfWar, Arrays.asList(GameFeature.SharedIntel, GameFeature.IsolatedIntel));
        map.put(GameFeature.StartingShips, Arrays.asList(GameFeature.StandardShips, GameFeature.AllCarriers, GameFeature.AllDestroyers, GameFeature.AllSubmarines, GameFeature.AllCruisers, GameFeature.AllBattleships));
        map.put(GameFeature.ECM, Arrays.asList(GameFeature.ECMEnabled, GameFeature.ECMDisabled));
        map.put(GameFeature.Spy, Arrays.asList(GameFeature.SpyEnabled, GameFeature.SpyDisabled));
        map.put(GameFeature.CruiseMissile, Arrays.asList(GameFeature.CruiseMissileEnabled, GameFeature.CruiseMissileDisabled));
        map.put(GameFeature.EmergencyRepairs, Arrays.asList(GameFeature.EREnabled, GameFeature.ERDisabled));
        map.put(GameFeature.EvasiveManeuvers, Arrays.asList(GameFeature.EMEnabled, GameFeature.EMDisabled));
        assertEquals(map, GameFeature.getGroupedFeatures());
    }

}
