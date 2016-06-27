package com.jtbdevelopment.TwistedBattleship.ai.common

import com.jtbdevelopment.TwistedBattleship.rest.handlers.*
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

/**
 * Date: 6/26/16
 * Time: 8:29 PM
 */
@CompileStatic
@Component
class AIActionHandlers {
    @Autowired
    RepairShipHandler repairShipHandler

    @Autowired
    EvasiveManeuverHandler evasiveManeuverHandler

    @Autowired
    FireAtCoordinateHandler fireAtCoordinateHandler

    @Autowired
    SpyHandler spyHandler

    @Autowired
    ECMHandler ecmHandler

    @Autowired
    CruiseMissileHandler cruiseMissileHandler
}
