package com.jtbdevelopment.TwistedBattleship.state;

import java.io.Serializable;
import java.time.Instant;

/**
 * Date: 9/24/15
 * Time: 6:59 PM
 */
public class TBActionLogEntry implements Serializable {
    private Instant timestamp = Instant.now();
    private TBActionType actionType;
    private String description;

    public TBActionLogEntry() {

    }

    public TBActionLogEntry(final TBActionType actionType, final String description) {
        this.description = description;
        this.actionType = actionType;
    }

    public TBActionLogEntry(final String description, final TBActionType actionType) {
        this.description = description;
        this.actionType = actionType;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    @SuppressWarnings("unused")
    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public TBActionType getActionType() {
        return actionType;
    }

    public void setActionType(TBActionType actionType) {
        this.actionType = actionType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public enum TBActionType {
        Begin, Fired, Spied, Repaired, UsedECM, DamagedByECM, PerformedManeuvers, CruiseMissile, Sunk, Defeated, Victory
    }
}
