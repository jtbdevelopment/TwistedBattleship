package com.jtbdevelopment.TwistedBattleship.state

/**
 * Date: 4/1/15
 * Time: 7:11 PM
 */
class GameFeatureTest extends GroovyTestCase {
    void testGroupedFeatures() {
        assert GameFeature.groupedFeatures == [
                (GameFeature.GridSize)      : [GameFeature.Grid10x10, GameFeature.Grid15x15, GameFeature.Grid20x20],
                (GameFeature.ActionsPerTurn): [GameFeature.PerShip, GameFeature.Single],
                (GameFeature.FogOfWar)      : [GameFeature.SharedIntel, GameFeature.IsolatedIntel],
                (GameFeature.ECM)           : [GameFeature.ECMEnabled, GameFeature.ECMDisabled],
                (GameFeature.Spy)           : [GameFeature.SpyEnabled, GameFeature.SpyDisabled],
                (GameFeature.EmergencyRepairs): [GameFeature.EREnabled, GameFeature.ERDisabled],
                (GameFeature.EvasiveManeuvers): [GameFeature.EMEnabled, GameFeature.EMDisabled],
//                (GameFeature.Critical)      : [GameFeature.CriticalEnabled, GameFeature.CriticalDisabled],
        ]
    }
}
