package com.jtbdevelopment.TwistedBattleship.state

/**
 * Date: 4/1/15
 * Time: 7:11 PM
 */
class GameFeaturesTest extends GroovyTestCase {
    void testGroupedFeatures() {
        assert GameFeatures.groupedFeatures == [
                (GameFeatures.GridSize): [GameFeatures.Grid10x10, GameFeatures.Grid15x15, GameFeatures.Grid20x20],
                (GameFeatures.FogOfWar): [GameFeatures.SharedIntel, GameFeatures.IsolatedIntel],
                (GameFeatures.ECM): [GameFeatures.ECMEnabled, GameFeatures.ECMDisabled],
                (GameFeatures.Spy): [GameFeatures.SpyEnabled, GameFeatures.SpyDisabled],
                (GameFeatures.EmergencyRepairs): [GameFeatures.EREnabled, GameFeatures.ERDisabled],
                (GameFeatures.EvasiveManeuvers): [GameFeatures.EMEnabled, GameFeatures.EMDisabled],
                (GameFeatures.Critical): [GameFeatures.CriticalEnabled, GameFeatures.CriticalDisabled],
                (GameFeatures.ActionsPerTurn): [GameFeatures.PerShip, GameFeatures.Single],
        ]
    }
}
