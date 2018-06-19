package com.jtbdevelopment.TwistedBattleship.state

import java.time.Instant

/**
 * Date: 9/24/15
 * Time: 6:59 PM
 */
class TBActionLogEntry implements Serializable {
    enum TBActionType {
        Begin,
        Fired,
        Spied,
        Repaired,
        UsedECM,
        DamagedByECM,
        PerformedManeuvers,
        CruiseMissile,

        Sunk,
        Defeated,
        Victory
    }

    Instant timestamp = Instant.now()
    TBActionType actionType
    String description
}
