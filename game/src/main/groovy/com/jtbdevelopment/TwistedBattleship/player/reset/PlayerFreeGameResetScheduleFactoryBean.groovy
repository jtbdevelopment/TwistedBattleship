package com.jtbdevelopment.TwistedBattleship.player.reset

import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.quartz.CronTriggerFactoryBean
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct

/**
 * Date: 2/12/15
 * Time: 7:02 PM
 */
@Component
@CompileStatic
class PlayerFreeGameResetScheduleFactoryBean extends CronTriggerFactoryBean {
    @Autowired
    PlayerFreeGameResetJobDetailFactoryBean jobDetail

    @PostConstruct
    void setup() {
        setJobDetail(jobDetail.object)
        setCronExpression('0 0 0 * * ?')
        setName('Reset Free Games')
    }
}
