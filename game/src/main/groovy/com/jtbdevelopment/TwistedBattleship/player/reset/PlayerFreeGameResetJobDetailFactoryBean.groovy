package com.jtbdevelopment.TwistedBattleship.player.reset

import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.quartz.MethodInvokingJobDetailFactoryBean
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct

/**
 * Date: 2/12/15
 * Time: 6:56 PM
 */
@Component
@CompileStatic
class PlayerFreeGameResetJobDetailFactoryBean extends MethodInvokingJobDetailFactoryBean {
    @Autowired
    PlayerFreeGameReset reset

    @PostConstruct
    void setup() {
        setTargetObject(reset)
        setTargetMethod('resetFreeGames')
    }
}
