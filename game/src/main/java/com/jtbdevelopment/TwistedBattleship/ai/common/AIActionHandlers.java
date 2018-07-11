package com.jtbdevelopment.TwistedBattleship.ai.common;

import com.jtbdevelopment.TwistedBattleship.rest.handlers.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Date: 6/26/16
 * Time: 8:29 PM
 */
@Component
public class AIActionHandlers {
    @Autowired
    private RepairShipHandler repairShipHandler;
    @Autowired
    private EvasiveManeuverHandler evasiveManeuverHandler;
    @Autowired
    private FireAtCoordinateHandler fireAtCoordinateHandler;
    @Autowired
    private SpyHandler spyHandler;
    @Autowired
    private ECMHandler ecmHandler;
    @Autowired
    private CruiseMissileHandler cruiseMissileHandler;

    public RepairShipHandler getRepairShipHandler() {
        return repairShipHandler;
    }

    public EvasiveManeuverHandler getEvasiveManeuverHandler() {
        return evasiveManeuverHandler;
    }

    public FireAtCoordinateHandler getFireAtCoordinateHandler() {
        return fireAtCoordinateHandler;
    }

    public SpyHandler getSpyHandler() {
        return spyHandler;
    }

    public ECMHandler getEcmHandler() {
        return ecmHandler;
    }

    public CruiseMissileHandler getCruiseMissileHandler() {
        return cruiseMissileHandler;
    }
}
