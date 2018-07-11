package com.jtbdevelopment.TwistedBattleship.state.masked;

import com.jtbdevelopment.TwistedBattleship.state.grid.Grid;
import com.jtbdevelopment.TwistedBattleship.state.ships.Ship;
import com.jtbdevelopment.TwistedBattleship.state.ships.ShipState;

import java.util.*;

/**
 * Date: 4/30/15
 * Time: 6:59 AM
 */
public class TBMaskedPlayerState {
    private boolean setup;
    private boolean alive;
    private List<TBMaskedActionLogEntry> actionLog = new LinkedList<>();
    private List<Ship> startingShips = new ArrayList<>();
    private List<ShipState> shipStates = new ArrayList<>();
    private int activeShipsRemaining;
    private int spysRemaining = 0;
    private int cruiseMissilesRemaining = 0;
    private int evasiveManeuversRemaining = 0;
    private int ecmsRemaining = 0;
    private int emergencyRepairsRemaining = 0;
    private int totalScore;
    private Map<String, Grid> opponentGrids = new HashMap<>();
    private Map<String, Grid> opponentViews = new HashMap<>();
    private Grid consolidatedOpponentView;

    public boolean isSetup() {
        return setup;
    }

    public void setSetup(boolean setup) {
        this.setup = setup;
    }

    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    public List<TBMaskedActionLogEntry> getActionLog() {
        return actionLog;
    }

    public void setActionLog(List<TBMaskedActionLogEntry> actionLog) {
        this.actionLog = actionLog;
    }

    public List<Ship> getStartingShips() {
        return startingShips;
    }

    public void setStartingShips(List<Ship> startingShips) {
        this.startingShips = startingShips;
    }

    public List<ShipState> getShipStates() {
        return shipStates;
    }

    public void setShipStates(List<ShipState> shipStates) {
        this.shipStates = shipStates;
    }

    public int getActiveShipsRemaining() {
        return activeShipsRemaining;
    }

    public void setActiveShipsRemaining(int activeShipsRemaining) {
        this.activeShipsRemaining = activeShipsRemaining;
    }

    public int getSpysRemaining() {
        return spysRemaining;
    }

    public void setSpysRemaining(int spysRemaining) {
        this.spysRemaining = spysRemaining;
    }

    public int getCruiseMissilesRemaining() {
        return cruiseMissilesRemaining;
    }

    public void setCruiseMissilesRemaining(int cruiseMissilesRemaining) {
        this.cruiseMissilesRemaining = cruiseMissilesRemaining;
    }

    public int getEvasiveManeuversRemaining() {
        return evasiveManeuversRemaining;
    }

    public void setEvasiveManeuversRemaining(int evasiveManeuversRemaining) {
        this.evasiveManeuversRemaining = evasiveManeuversRemaining;
    }

    public int getEcmsRemaining() {
        return ecmsRemaining;
    }

    public void setEcmsRemaining(int ecmsRemaining) {
        this.ecmsRemaining = ecmsRemaining;
    }

    public int getEmergencyRepairsRemaining() {
        return emergencyRepairsRemaining;
    }

    public void setEmergencyRepairsRemaining(int emergencyRepairsRemaining) {
        this.emergencyRepairsRemaining = emergencyRepairsRemaining;
    }

    public int getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(int totalScore) {
        this.totalScore = totalScore;
    }

    public Map<String, Grid> getOpponentGrids() {
        return opponentGrids;
    }

    public void setOpponentGrids(Map<String, Grid> opponentGrids) {
        this.opponentGrids = opponentGrids;
    }

    public Map<String, Grid> getOpponentViews() {
        return opponentViews;
    }

    public void setOpponentViews(Map<String, Grid> opponentViews) {
        this.opponentViews = opponentViews;
    }

    public Grid getConsolidatedOpponentView() {
        return consolidatedOpponentView;
    }

    public void setConsolidatedOpponentView(Grid consolidatedOpponentView) {
        this.consolidatedOpponentView = consolidatedOpponentView;
    }
}
