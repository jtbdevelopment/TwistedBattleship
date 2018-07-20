package com.jtbdevelopment.TwistedBattleship.ai.common;

import com.jtbdevelopment.TwistedBattleship.rest.handlers.*;
import org.springframework.stereotype.Component;

/**
 * Date: 6/26/16
 * Time: 8:29 PM
 */
@Component
public class AIActionHandlers {
    private final RepairShipHandler repairShipHandler;
    private final EvasiveManeuverHandler evasiveManeuverHandler;
    private final FireAtCoordinateHandler fireAtCoordinateHandler;
    private final SpyHandler spyHandler;
    private final ECMHandler ecmHandler;
    private final CruiseMissileHandler cruiseMissileHandler;

    public AIActionHandlers(
            final RepairShipHandler repairShipHandler,
            final EvasiveManeuverHandler evasiveManeuverHandler,
            final FireAtCoordinateHandler fireAtCoordinateHandler,
            final SpyHandler spyHandler,
            final ECMHandler ecmHandler,
            final CruiseMissileHandler cruiseMissileHandler) {
        this.repairShipHandler = repairShipHandler;
        this.evasiveManeuverHandler = evasiveManeuverHandler;
        this.fireAtCoordinateHandler = fireAtCoordinateHandler;
        this.spyHandler = spyHandler;
        this.ecmHandler = ecmHandler;
        this.cruiseMissileHandler = cruiseMissileHandler;
    }

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
