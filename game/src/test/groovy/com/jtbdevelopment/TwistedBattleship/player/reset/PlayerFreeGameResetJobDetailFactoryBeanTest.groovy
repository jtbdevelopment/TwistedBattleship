package com.jtbdevelopment.TwistedBattleship.player.reset
/**
 * Date: 2/12/15
 * Time: 7:00 PM
 */
class PlayerFreeGameResetJobDetailFactoryBeanTest extends GroovyTestCase {
    void testInitialization() {
        def reset = [] as PlayerFreeGameReset

        PlayerFreeGameResetJobDetailFactoryBean factoryBean = new PlayerFreeGameResetJobDetailFactoryBean()
        factoryBean.reset = reset
        factoryBean.setup()
        assert factoryBean.targetMethod == 'resetFreeGames'
        assert factoryBean.targetObject.is(reset)
    }
}
