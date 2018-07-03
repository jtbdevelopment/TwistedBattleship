package com.jtbdevelopment.TwistedBattleship.state.masked;

import com.jtbdevelopment.TwistedBattleship.state.TBActionLogEntry;

import java.io.Serializable;

/**
 * Date: 9/24/15
 * Time: 7:39 PM
 */
public class TBMaskedActionLogEntry implements Serializable {
    private long timestamp;
    private TBActionLogEntry.TBActionType actionType;
    private String description;

    public TBMaskedActionLogEntry(long timestamp, TBActionLogEntry.TBActionType actionType, String description) {
        this.timestamp = timestamp;
        this.actionType = actionType;
        this.description = description;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public TBActionLogEntry.TBActionType getActionType() {
        return actionType;
    }

    public void setActionType(TBActionLogEntry.TBActionType actionType) {
        this.actionType = actionType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
