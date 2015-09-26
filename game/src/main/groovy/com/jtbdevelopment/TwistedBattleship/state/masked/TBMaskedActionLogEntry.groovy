package com.jtbdevelopment.TwistedBattleship.state.masked

import com.jtbdevelopment.TwistedBattleship.state.TBActionLogEntry

/**
 * Date: 9/24/15
 * Time: 7:39 PM
 */
class TBMaskedActionLogEntry implements Serializable {
    long timestamp
    TBActionLogEntry.TBActionType actionType
    String description
}
