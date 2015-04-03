package com.jtbdevelopment.TwistedBattleship.player.reset

import org.quartz.JobDetail
import org.quartz.JobKey
import org.quartz.impl.triggers.CronTriggerImpl

/**
 * Date: 2/12/15
 * Time: 7:09 PM
 */
class PlayerFreeGameResetScheduleFactoryBeanTest extends GroovyTestCase {

    void testInitializes() {
        def detail = [
                getKey: {
                    return new JobKey('name', 'group')
                }
        ] as JobDetail
        def job = [
                getObject: {
                    return detail
                }
        ] as PlayerFreeGameResetJobDetailFactoryBean
        PlayerFreeGameResetScheduleFactoryBean factoryBean = new PlayerFreeGameResetScheduleFactoryBean()
        factoryBean.jobDetail = job
        factoryBean.setup()
        factoryBean.afterPropertiesSet()
        CronTriggerImpl impl = (CronTriggerImpl) factoryBean.object

        assert impl.jobDataMap["jobDetail"].is(detail)
        assert impl.cronExpression == '0 0 0 * * ?'
    }
}
