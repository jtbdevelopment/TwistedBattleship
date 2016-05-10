package com.jtbdevelopment.TwistedBattleship.state

import java.time.ZoneId
import java.time.ZonedDateTime

/**
 * Date: 9/24/15
 * Time: 6:59 PM
 */
class TBActionLogEntry implements Serializable {
    private static final ZoneId GMT = ZoneId.of("GMT")
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

    ZonedDateTime timestamp = ZonedDateTime.now(GMT)
    TBActionType actionType
    String description
}
